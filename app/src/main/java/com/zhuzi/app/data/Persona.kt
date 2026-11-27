package com.zhuzi.app.data

import java.time.LocalDate
import java.time.LocalTime

/** Un texte en trois langues. */
data class T3(val fr: String, val en: String, val ru: String) {
    fun get(lang: Lang): String = when (lang) {
        Lang.FR -> fr
        Lang.EN -> en
        Lang.RU -> ru
        Lang.BN -> Bn.t(en)
    }
}

/** Un proverbe chinois : caractères, pinyin, traduction et ce qu'il veut dire. */
data class Proverb(val hanzi: String, val pinyin: String, val meaning: T3)

/** Proverbes sur l'apprentissage, la patience… et le bambou. */
val proverbs = listOf(
    Proverb("学而时习之", "xué ér shí xí zhī", T3(
        "Apprendre, puis réviser souvent. (Confucius)",
        "Learn, then review often. (Confucius)",
        "Учиться и постоянно повторять. (Конфуций)")),
    Proverb("熟能生巧", "shú néng shēng qiǎo", T3(
        "C'est en forgeant qu'on devient forgeron.",
        "Practice makes perfect.",
        "Повторение — мать учения.")),
    Proverb("胸有成竹", "xiōng yǒu chéng zhú", T3(
        "Avoir déjà le bambou en tête : savoir exactement où l'on va.",
        "Have the whole bamboo in mind: know exactly where you're going.",
        "Иметь бамбук в душе: точно знать, куда идёшь.")),
    Proverb("千里之行，始于足下", "qiān lǐ zhī xíng, shǐ yú zú xià", T3(
        "Un voyage de mille lieues commence par un pas.",
        "A journey of a thousand miles begins with a single step.",
        "Путь в тысячу ли начинается с первого шага.")),
    Proverb("滴水穿石", "dī shuǐ chuān shí", T3(
        "La goutte d'eau finit par percer la pierre.",
        "Dripping water wears through stone.",
        "Капля камень точит.")),
    Proverb("温故而知新", "wēn gù ér zhī xīn", T3(
        "Revoir l'ancien pour comprendre le nouveau.",
        "Review the old to understand the new.",
        "Повторяя старое, познаёшь новое.")),
    Proverb("活到老，学到老", "huó dào lǎo, xué dào lǎo", T3(
        "On apprend à tout âge.",
        "You're never too old to learn.",
        "Век живи — век учись.")),
    Proverb("有志者事竟成", "yǒu zhì zhě shì jìng chéng", T3(
        "Qui veut, peut.",
        "Where there's a will, there's a way.",
        "Кто хочет — тот добьётся.")),
    Proverb("一步一个脚印", "yí bù yí gè jiǎoyìn", T3(
        "Un pas, une empreinte : avancer sûrement.",
        "One step, one footprint: steady progress.",
        "Шаг за шагом, оставляя след.")),
    Proverb("万事开头难", "wàn shì kāi tóu nán", T3(
        "Le plus dur, c'est de commencer.",
        "Every beginning is hard.",
        "Лиха беда начало.")),
    Proverb("竹报平安", "zhú bào píng'ān", T3(
        "Le bambou annonce la paix : tout va bien.",
        "The bamboo brings word of peace: all is well.",
        "Бамбук приносит весть о мире: всё хорошо.")),
    Proverb("学无止境", "xué wú zhǐ jìng", T3(
        "Apprendre n'a pas de fin.",
        "Learning has no end.",
        "Учению нет предела.")),
)

/** Le proverbe du jour (change chaque jour). */
fun proverbOfDay(date: LocalDate = LocalDate.now()): Proverb =
    proverbs[(date.toEpochDay() % proverbs.size).toInt()]

/** Salutation selon l'heure, en chinois et dans la langue choisie. */
data class Greeting(val hanzi: String, val pinyin: String, val local: T3)

fun greetingNow(time: LocalTime = LocalTime.now()): Greeting = when (time.hour) {
    in 5..11 -> Greeting("早上好", "zǎoshang hǎo", T3("Bonjour", "Good morning", "Доброе утро"))
    in 12..17 -> Greeting("下午好", "xiàwǔ hǎo", T3("Bon après-midi", "Good afternoon", "Добрый день"))
    else -> Greeting("晚上好", "wǎnshang hǎo", T3("Bonsoir", "Good evening", "Добрый вечер"))
}

/** Une phrase d'accroche pour chaque leçon. */
val lessonIntros: Map<Int, T3> by lazy { hsk1Intros + hsk2Intros + hsk3Intros + hsk3bIntros }

private val hsk1Intros: Map<Int, T3> = mapOf(
    1 to T3("Les premiers mots pour saluer, remercier et s'excuser.",
        "Your first words to greet, thank and apologize.",
        "Первые слова: поздороваться, поблагодарить, извиниться."),
    2 to T3("Je, tu, il… et tous les gens autour de toi.",
        "I, you, he… and all the people around you.",
        "Я, ты, он… и все люди вокруг тебя."),
    3 to T3("Papa, maman, la maison et ce qu'on y trouve.",
        "Mom, dad, home and everything in it.",
        "Мама, папа, дом и всё, что в нём."),
    4 to T3("Compter jusqu'à dix et demander « combien ? ».",
        "Count to ten and ask \"how many?\".",
        "Считать до десяти и спрашивать «сколько?»."),
    5 to T3("Hier, aujourd'hui, demain… et l'heure qu'il est.",
        "Yesterday, today, tomorrow… and what time it is.",
        "Вчера, сегодня, завтра… и который час."),
    6 to T3("Au restaurant comme au marché : manger, boire, acheter.",
        "At the restaurant or the market: eat, drink, buy.",
        "В ресторане и на рынке: есть, пить, покупать."),
    7 to T3("Les verbes de tous les jours, pour raconter ta journée.",
        "Everyday verbs to talk about your day.",
        "Глаголы на каждый день, чтобы рассказать о своём дне."),
    8 to T3("Où ? Ici, là-bas, devant, derrière : se repérer.",
        "Where? Here, there, in front, behind: find your way.",
        "Где? Здесь, там, впереди, сзади: ориентируемся."),
    9 to T3("Grand, petit, chaud, froid : décrire le monde.",
        "Big, small, hot, cold: describe the world.",
        "Большой, маленький, жарко, холодно: описываем мир."),
    10 to T3("Les petits mots qui font tenir les phrases.",
        "The little words that hold sentences together.",
        "Маленькие слова, на которых держится фраза."),
    11 to T3("Livres, écrans, avions : les objets du quotidien.",
        "Books, screens, planes: everyday things.",
        "Книги, экраны, самолёты: вещи вокруг нас."),
)

/** Encouragements après une bonne réponse (en chinois + traduction), et après une erreur. */
val cheers = listOf(
    Greeting("很好！", "hěn hǎo!", T3("Très bien !", "Very good!", "Очень хорошо!")),
    Greeting("太好了！", "tài hǎo le!", T3("Génial !", "Great!", "Отлично!")),
    Greeting("不错！", "búcuò!", T3("Pas mal du tout !", "Not bad at all!", "Неплохо!")),
    Greeting("对了！", "duì le!", T3("C'est ça !", "That's right!", "Верно!")),
)

val nudges = listOf(
    T3("Presque ! Regarde bien la réponse.", "Almost! Take a good look at the answer.", "Почти! Посмотри на ответ."),
    T3("Pas tout à fait. Ça viendra.", "Not quite. It'll come.", "Не совсем. Всё придёт."),
    T3("Une erreur, c'est un pas de plus.", "A mistake is one more step.", "Ошибка — это ещё один шаг."),
)
