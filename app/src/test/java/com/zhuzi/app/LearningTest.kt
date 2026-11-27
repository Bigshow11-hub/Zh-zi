package com.zhuzi.app

import android.content.Context
import com.zhuzi.app.data.Srs
import com.zhuzi.app.data.Streak
import com.zhuzi.app.data.Word
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.time.LocalDate

/** Tests de la révision espacée (boîtes) et de la série de jours. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LearningTest {
    private lateinit var context: Context
    private val ni = Word("你", "nǐ", "tu", "you", "ты")
    private val hao = Word("好", "hǎo", "bien", "good", "хорошо")
    private fun today() = LocalDate.now().toEpochDay()

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        listOf("srs", "streak", "activity").forEach { context.getSharedPreferences(it, Context.MODE_PRIVATE).edit().clear().commit() }
    }

    @Test
    fun nouveauMotEnBoite1PourDemain() {
        val srs = Srs(context)
        assertEquals(0, srs.boxOf(ni))
        srs.learn(ni)
        assertEquals(1, srs.boxOf(ni))
        assertTrue(srs.dueWords(listOf(ni)).isEmpty())
        assertEquals(1, srs.dueTomorrow(listOf(ni)))
    }

    @Test
    fun motSuMonteEtMotOublieRetombe() {
        val srs = Srs(context)
        srs.learn(ni)
        srs.answer(ni, knew = true); assertEquals(2, srs.boxOf(ni))
        srs.answer(ni, knew = true); assertEquals(3, srs.boxOf(ni))
        srs.answer(ni, knew = false); assertEquals(1, srs.boxOf(ni))
        repeat(10) { srs.answer(ni, knew = true) }
        assertEquals(6, srs.boxOf(ni)) // jamais au-delà de la boîte 6
    }

    @Test
    fun learnNeRemetPasAZero() {
        val srs = Srs(context)
        srs.learn(ni); srs.answer(ni, true); srs.answer(ni, true)
        srs.learn(ni)
        assertEquals(3, srs.boxOf(ni))
    }

    @Test
    fun motsDejaConnusEtalesEtBienRetenus() {
        val srs = Srs(context)
        srs.markKnown(ni, 0); srs.markKnown(hao, 10)
        assertEquals(2, srs.masteredCount(listOf(ni, hao)))
        assertTrue(srs.dueWords(listOf(ni, hao)).isEmpty())
    }

    @Test
    fun ecritureDAilleursVueParLaCopieEnMemoire() {
        val srs = Srs(context)
        Srs(context).learn(hao) // autre instance (comme le bouton de la notification)
        assertEquals(1, srs.boxOf(hao))
        assertEquals(1, srs.knownCount(listOf(ni, hao)))
    }

    @Test
    fun serieCommenceEtTient() {
        val s = Streak(context)
        assertEquals(0, s.current())
        assertFalse(s.activeToday())
        s.markActive(); s.markActive()
        assertEquals(1, s.current())
        assertTrue(s.activeToday())
    }

    @Test
    fun serieContinueLendemainEtCasseApresUnJourSansEtude() {
        val prefs = context.getSharedPreferences("streak", Context.MODE_PRIVATE)
        // Étudié hier, série de 4 : aujourd'hui elle passe à 5
        prefs.edit().putLong("last_day", today() - 1).putInt("count", 4).commit()
        assertEquals(4, Streak(context).current())
        Streak(context).markActive()
        assertEquals(5, Streak(context).current())
        // Dernière étude il y a 2 jours : la série est cassée, et repart à 1
        prefs.edit().putLong("last_day", today() - 2).putInt("count", 9).commit()
        assertEquals(0, Streak(context).current())
        Streak(context).markActive()
        assertEquals(1, Streak(context).current())
    }
}
