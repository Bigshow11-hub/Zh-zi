package com.zhuzi.app.data

import android.content.Context

/** Une étape à fêter : clé unique, gros repère affiché (nombre ou caractère), titre et texte. */
data class Milestone(val key: String, val badge: String, val title: T3, val body: T3)

/**
 * Les étapes : séries de jours, niveaux de la mascotte, leçons maîtrisées.
 * Chaque étape n'est fêtée qu'une fois (gardé sur le téléphone).
 */
object Celebrations {
    private val streaks = listOf(3, 7, 14, 30, 50, 100, 200, 365)
    private val masteredSteps = listOf(1, 3, 5, 11)

    private fun prefs(context: Context) = context.getSharedPreferences("celebrations", Context.MODE_PRIVATE)

    /** Toutes les étapes déjà atteintes, de la plus ancienne à la plus récente. */
    fun reached(streak: Int, known: Int, mastered: Int): List<Milestone> {
        val out = mutableListOf<Milestone>()
        levels.drop(1).filter { known >= it.from }.forEach { l ->
            out += Milestone(
                "level${l.number}", "${l.number}",
                T3("Niveau ${l.number} : ${l.name.fr} !", "Level ${l.number}: ${l.name.en}!", "Уровень ${l.number}: ${l.name.ru}!"),
                T3("Ton panda grandit : ${l.from} mots découverts. Son bambou pousse avec toi.",
                    "Your panda is growing: ${l.from} words discovered. Its bamboo grows with you.",
                    "Твой панда растёт: открыто слов — ${l.from}. Его бамбук растёт вместе с тобой."),
            )
        }
        masteredSteps.filter { mastered >= it }.forEach { n ->
            out += Milestone(
                "mastered$n", "$n",
                if (n == 1) T3("Première leçon maîtrisée !", "First lesson mastered!", "Первый урок освоен!")
                else T3("$n leçons maîtrisées !", "$n lessons mastered!", "Освоено уроков: $n!"),
                if (n >= 11) T3("Tout le HSK 1 est maîtrisé. Bravo, c'est un vrai cap !",
                    "All of HSK 1 is mastered. Well done, that's a real milestone!",
                    "Весь HSK 1 освоен. Браво, это настоящий рубеж!")
                else T3("Sens, écrit et écoute réussis à plus de 80 %. Le sceau 记 est à toi.",
                    "Meaning, writing and listening passed above 80%. The 记 seal is yours.",
                    "Смысл, письмо и аудирование пройдены более чем на 80 %. Печать 记 твоя."),
            )
        }
        streaks.filter { streak >= it }.forEach { d ->
            out += Milestone(
                "streak$d", "$d",
                T3("$d jours d'affilée !", "$d days in a row!", "$d дней подряд!"),
                when {
                    d < 7 -> T3("Le bambou pousse un peu chaque jour. Continue demain !",
                        "Bamboo grows a little every day. Keep going tomorrow!",
                        "Бамбук растёт понемногу каждый день. Продолжай завтра!")
                    d < 30 -> T3("Une vraie habitude est en train de naître. 加油 !",
                        "A real habit is taking root. 加油!",
                        "Рождается настоящая привычка. 加油!")
                    else -> T3("Une régularité de maître. Ton chinois te dit merci : 谢谢 !",
                        "Master-level consistency. Your Chinese says thank you: 谢谢!",
                        "Постоянство мастера. Твой китайский говорит спасибо: 谢谢!")
                },
            )
        }
        return out
    }

    /**
     * L'étape à fêter maintenant (la plus récente pas encore vue), ou null.
     * La toute première fois, les étapes déjà atteintes sont notées sans fête, pour ne pas en montrer dix d'un coup.
     */
    fun pending(context: Context, streak: Int, known: Int, mastered: Int): Milestone? {
        val p = prefs(context)
        val all = reached(streak, known, mastered)
        if (!p.getBoolean("init", false)) {
            p.edit().putBoolean("init", true).putStringSet("shown", all.map { it.key }.toSet()).apply()
            return null
        }
        val shown = p.getStringSet("shown", emptySet()) ?: emptySet()
        val fresh = all.filter { it.key !in shown }
        if (fresh.isEmpty()) return null
        // les autres étapes nouvelles du même coup sont marquées aussi : une seule fête à la fois
        p.edit().putStringSet("shown", shown + fresh.map { it.key }).apply()
        return fresh.last()
    }
}
