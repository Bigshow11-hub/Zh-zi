package com.zhuzi.app.data

import android.content.Context

/** Niveau de la mascotte, selon les mots découverts : la tige de bambou qu'elle tient grandit. */
data class Level(val number: Int, val from: Int, val name: T3)

val levels = listOf(
    Level(1, 0, T3("Pousse", "Sprout", "Росток")),
    Level(2, 10, T3("Jeune tige", "Young shoot", "Побег")),
    Level(3, 30, T3("Bambou", "Bamboo", "Бамбук")),
    Level(4, 60, T3("Bosquet", "Grove", "Роща")),
    Level(5, 100, T3("Forêt de bambous", "Bamboo forest", "Бамбуковый лес")),
    Level(6, 180, T3("Montagne de bambous", "Bamboo mountain", "Бамбуковая гора")),
    Level(7, 280, T3("Maître du bambou", "Bamboo master", "Мастер бамбука")),
)

fun levelFor(known: Int): Level = levels.last { known >= it.from }

/** Le niveau suivant, ou null au dernier niveau. */
fun nextLevel(known: Int): Level? = levels.firstOrNull { it.from > known }

/** Ce qui permet de débloquer un accessoire. */
data class AvatarStats(
    val known: Int,
    val streak: Int,
    val lessonsMastered: Int,
    val listeningPassed: Boolean,
)

enum class Accessory(val label: T3, val condition: T3, val unlocked: (AvatarStats) -> Boolean) {
    NONE(T3("Aucun", "None", "Нет"), T3("", "", ""), { true }),
    SCARF(T3("Écharpe rouge", "Red scarf", "Красный шарф"),
        T3("3 jours de série", "3-day streak", "Серия 3 дня"), { it.streak >= 3 }),
    STRAW_HAT(T3("Chapeau de paille", "Straw hat", "Соломенная шляпа"),
        T3("20 mots découverts", "20 words discovered", "20 слов открыто"), { it.known >= 20 }),
    HEADPHONES(T3("Casque audio", "Headphones", "Наушники"),
        T3("Un quiz d'écoute réussi", "A listening quiz passed", "Пройден тест на слух"), { it.listeningPassed }),
    SCHOLAR_CAP(T3("Bonnet de lettré", "Scholar's cap", "Шапка учёного"),
        T3("Une leçon maîtrisée", "One lesson mastered", "Освоен один урок"), { it.lessonsMastered >= 1 }),
    JADE_CROWN(T3("Couronne de jade", "Jade crown", "Нефритовая корона"),
        T3("100 mots découverts", "100 words discovered", "100 слов открыто"), { it.known >= 100 }),
}

/** Couleurs de tenue (pull à capuche) et fonds proposés. */
val outfitColors = listOf(0xFF5B5CF0, 0xFF8B7BF7, 0xFF2BB59A, 0xFFFF7A66, 0xFF4C8DF6, 0xFF3A3D5C)
val backgrounds = listOf(
    0xFFE4E1FF to 0xFFF6F4FF, // lavande
    0xFFD5F5EA to 0xFFF2FCF8, // menthe
    0xFFFFE2D6 to 0xFFFFF6F1, // pêche
    0xFFD6E8FF to 0xFFF2F8FF, // ciel
    0xFF2A2C55 to 0xFF16172F, // nuit
)

/** Les choix de l'utilisateur, gardés sur le téléphone. */
data class AvatarLook(val outfit: Int, val accessory: Accessory, val background: Int)

object AvatarPrefs {
    private fun prefs(c: Context) = c.getSharedPreferences("settings", Context.MODE_PRIVATE)

    fun get(c: Context): AvatarLook {
        val p = prefs(c)
        return AvatarLook(
            outfit = p.getInt("avatar_outfit", 0).coerceIn(0, outfitColors.size - 1),
            accessory = Accessory.entries.firstOrNull { it.name == p.getString("avatar_acc", null) } ?: Accessory.NONE,
            background = p.getInt("avatar_bg", 0).coerceIn(0, backgrounds.size - 1),
        )
    }

    fun set(c: Context, look: AvatarLook) {
        prefs(c).edit()
            .putInt("avatar_outfit", look.outfit)
            .putString("avatar_acc", look.accessory.name)
            .putInt("avatar_bg", look.background)
            .apply()
    }
}
