package com.zhuzi.app.data

/** Les langues de l'appli (traductions et interface). */
enum class Lang(val code: String, val nativeName: String) {
    FR("fr", "Français"),
    EN("en", "English"),
    RU("ru", "Русский"),
}

/** Un mot à apprendre : caractères, prononciation (pinyin) et traductions. */
data class Word(
    val hanzi: String,
    val pinyin: String,
    val french: String,
    val english: String,
    val russian: String,
) {
    fun meaning(lang: Lang): String = when (lang) {
        Lang.FR -> french
        Lang.EN -> english
        Lang.RU -> russian
    }
}

/** Une leçon : un thème (titre long et court, en français, anglais, russe), un caractère symbole et ses mots. */
data class Lesson(
    val id: Int,
    val title: String,
    val short: String,
    val symbol: String,
    val en: Pair<String, String>,
    val ru: Pair<String, String>,
    val words: List<Word>,
) {
    fun title(lang: Lang): String = when (lang) {
        Lang.FR -> title
        Lang.EN -> en.first
        Lang.RU -> ru.first
    }

    fun short(lang: Lang): String = when (lang) {
        Lang.FR -> short
        Lang.EN -> en.second
        Lang.RU -> ru.second
    }
}

private fun w(hanzi: String, pinyin: String, french: String, english: String, russian: String) =
    Word(hanzi, pinyin, french, english, russian)

/** Les mots du HSK 1, rangés par thème. */
val hsk1Lessons = listOf(
    Lesson(1, "Bonjour et politesse", "Politesse", "好",
        en = "Greetings and politeness" to "Politeness", ru = "Приветствие и вежливость" to "Вежливость", words = listOf(
        w("你好", "nǐ hǎo", "bonjour", "hello", "привет"),
        w("谢谢", "xièxie", "merci", "thank you", "спасибо"),
        w("不客气", "bú kèqi", "de rien", "you're welcome", "не за что"),
        w("对不起", "duìbuqǐ", "pardon, désolé", "sorry", "извините"),
        w("没关系", "méi guānxi", "ce n'est pas grave", "it doesn't matter", "ничего страшного"),
        w("再见", "zàijiàn", "au revoir", "goodbye", "до свидания"),
        w("请", "qǐng", "s'il vous plaît, inviter", "please, to invite", "пожалуйста, приглашать"),
        w("喂", "wèi", "allô", "hello (on the phone)", "алло"),
        w("高兴", "gāoxìng", "content, heureux", "happy, glad", "радостный, довольный"),
        w("认识", "rènshi", "connaître (quelqu'un)", "to know (someone)", "знать (кого-то)"),
        w("叫", "jiào", "s'appeler", "to be called", "звать(ся)"),
        w("名字", "míngzi", "nom, prénom", "name", "имя"),
    )),
    Lesson(2, "Les personnes", "Personnes", "人",
        en = "People" to "People", ru = "Люди" to "Люди", words = listOf(
        w("我", "wǒ", "je, moi", "I, me", "я"),
        w("你", "nǐ", "tu, toi", "you", "ты"),
        w("他", "tā", "il, lui", "he, him", "он"),
        w("她", "tā", "elle", "she, her", "она"),
        w("我们", "wǒmen", "nous", "we, us", "мы"),
        w("人", "rén", "personne, gens", "person, people", "человек, люди"),
        w("谁", "shéi", "qui ?", "who?", "кто?"),
        w("先生", "xiānsheng", "monsieur", "Mr., sir", "господин"),
        w("小姐", "xiǎojiě", "mademoiselle", "Miss", "девушка, госпожа"),
        w("老师", "lǎoshī", "professeur", "teacher", "учитель"),
        w("学生", "xuésheng", "élève, étudiant", "student", "студент, ученик"),
        w("同学", "tóngxué", "camarade de classe", "classmate", "одноклассник"),
        w("朋友", "péngyou", "ami", "friend", "друг"),
        w("医生", "yīshēng", "médecin", "doctor", "врач"),
    )),
    Lesson(3, "La famille et la maison", "Famille", "家",
        en = "Family and home" to "Family", ru = "Семья и дом" to "Семья", words = listOf(
        w("爸爸", "bàba", "papa", "dad", "папа"),
        w("妈妈", "māma", "maman", "mom", "мама"),
        w("儿子", "érzi", "fils", "son", "сын"),
        w("女儿", "nǚ'ér", "fille (enfant)", "daughter", "дочь"),
        w("家", "jiā", "maison, famille", "home, family", "дом, семья"),
        w("狗", "gǒu", "chien", "dog", "собака"),
        w("猫", "māo", "chat", "cat", "кошка"),
        w("爱", "ài", "aimer", "to love", "любить"),
        w("喜欢", "xǐhuan", "aimer bien", "to like", "нравиться"),
        w("住", "zhù", "habiter", "to live (somewhere)", "жить (где-то)"),
        w("桌子", "zhuōzi", "table", "table", "стол"),
        w("椅子", "yǐzi", "chaise", "chair", "стул"),
        w("东西", "dōngxi", "chose, objet", "thing, object", "вещь"),
    )),
    Lesson(4, "Les nombres", "Nombres", "十",
        en = "Numbers" to "Numbers", ru = "Числа" to "Числа", words = listOf(
        w("一", "yī", "un", "one", "один"),
        w("二", "èr", "deux", "two", "два"),
        w("三", "sān", "trois", "three", "три"),
        w("四", "sì", "quatre", "four", "четыре"),
        w("五", "wǔ", "cinq", "five", "пять"),
        w("六", "liù", "six", "six", "шесть"),
        w("七", "qī", "sept", "seven", "семь"),
        w("八", "bā", "huit", "eight", "восемь"),
        w("九", "jiǔ", "neuf", "nine", "девять"),
        w("十", "shí", "dix", "ten", "десять"),
        w("几", "jǐ", "combien (petit nombre)", "how many (small number)", "сколько (немного)"),
        w("多少", "duōshao", "combien", "how many, how much", "сколько"),
        w("岁", "suì", "an (âge)", "year (of age)", "год (возраста)"),
        w("个", "gè", "spécificatif général", "general measure word", "общее счётное слово"),
        w("本", "běn", "spécificatif des livres", "measure word for books", "счётное слово для книг"),
        w("块", "kuài", "yuan (argent)", "yuan (money)", "юань (деньги)"),
        w("些", "xiē", "quelques", "some", "несколько"),
    )),
    Lesson(5, "Le temps", "Le temps", "天",
        en = "Time" to "Time", ru = "Время" to "Время", words = listOf(
        w("今天", "jīntiān", "aujourd'hui", "today", "сегодня"),
        w("明天", "míngtiān", "demain", "tomorrow", "завтра"),
        w("昨天", "zuótiān", "hier", "yesterday", "вчера"),
        w("年", "nián", "année", "year", "год"),
        w("月", "yuè", "mois, lune", "month, moon", "месяц, луна"),
        w("号", "hào", "jour du mois, numéro", "day of the month, number", "число (дата), номер"),
        w("星期", "xīngqī", "semaine", "week", "неделя"),
        w("上午", "shàngwǔ", "matin", "morning", "утро"),
        w("中午", "zhōngwǔ", "midi", "noon", "полдень"),
        w("下午", "xiàwǔ", "après-midi", "afternoon", "после полудня"),
        w("现在", "xiànzài", "maintenant", "now", "сейчас"),
        w("时候", "shíhou", "moment", "moment, time", "момент, время"),
        w("点", "diǎn", "heure (sur l'horloge)", "o'clock", "час (на часах)"),
        w("分钟", "fēnzhōng", "minute", "minute", "минута"),
    )),
    Lesson(6, "Manger, boire, acheter", "Manger", "吃",
        en = "Eat, drink, buy" to "Food", ru = "Есть, пить, покупать" to "Еда", words = listOf(
        w("吃", "chī", "manger", "to eat", "есть"),
        w("喝", "hē", "boire", "to drink", "пить"),
        w("茶", "chá", "thé", "tea", "чай"),
        w("水", "shuǐ", "eau", "water", "вода"),
        w("菜", "cài", "plat, légume", "dish, vegetable", "блюдо, овощи"),
        w("米饭", "mǐfàn", "riz cuit", "cooked rice", "варёный рис"),
        w("水果", "shuǐguǒ", "fruit", "fruit", "фрукты"),
        w("苹果", "píngguǒ", "pomme", "apple", "яблоко"),
        w("杯子", "bēizi", "verre, tasse", "cup, glass", "чашка, стакан"),
        w("饭店", "fàndiàn", "restaurant, hôtel", "restaurant, hotel", "ресторан, гостиница"),
        w("钱", "qián", "argent", "money", "деньги"),
        w("买", "mǎi", "acheter", "to buy", "покупать"),
        w("商店", "shāngdiàn", "magasin", "shop", "магазин"),
    )),
    Lesson(7, "Les actions du quotidien", "Actions", "看",
        en = "Everyday actions" to "Actions", ru = "Повседневные действия" to "Действия", words = listOf(
        w("看", "kàn", "regarder, lire", "to look, to read", "смотреть, читать"),
        w("看见", "kànjiàn", "voir", "to see", "видеть"),
        w("听", "tīng", "écouter", "to listen", "слушать"),
        w("说", "shuō", "parler, dire", "to speak, to say", "говорить"),
        w("读", "dú", "lire (à voix haute)", "to read (aloud)", "читать (вслух)"),
        w("写", "xiě", "écrire", "to write", "писать"),
        w("做", "zuò", "faire", "to do, to make", "делать"),
        w("工作", "gōngzuò", "travailler, travail", "to work, job", "работать, работа"),
        w("学习", "xuéxí", "étudier", "to study", "учиться"),
        w("睡觉", "shuìjiào", "dormir", "to sleep", "спать"),
        w("打电话", "dǎ diànhuà", "téléphoner", "to make a phone call", "звонить по телефону"),
        w("开", "kāi", "ouvrir, conduire", "to open, to drive", "открывать, водить"),
        w("坐", "zuò", "s'asseoir, prendre (transport)", "to sit, to take (transport)", "сидеть, ехать на"),
        w("来", "lái", "venir", "to come", "приходить"),
        w("去", "qù", "aller", "to go", "идти, ехать"),
        w("回", "huí", "revenir, rentrer", "to return", "возвращаться"),
    )),
    Lesson(8, "Les lieux et les directions", "Lieux", "中",
        en = "Places and directions" to "Places", ru = "Места и направления" to "Места", words = listOf(
        w("中国", "Zhōngguó", "Chine", "China", "Китай"),
        w("北京", "Běijīng", "Pékin", "Beijing", "Пекин"),
        w("学校", "xuéxiào", "école", "school", "школа"),
        w("医院", "yīyuàn", "hôpital", "hospital", "больница"),
        w("这", "zhè", "ceci, ce", "this", "это, этот"),
        w("那", "nà", "cela, ce…là", "that", "то, тот"),
        w("哪", "nǎ", "quel ?", "which?", "какой?"),
        w("哪儿", "nǎr", "où ?", "where?", "где?"),
        w("里", "lǐ", "dans, à l'intérieur", "in, inside", "в, внутри"),
        w("上", "shàng", "sur, monter", "on, up", "на, вверх"),
        w("下", "xià", "sous, descendre", "under, down", "под, вниз"),
        w("前面", "qiánmiàn", "devant", "in front", "впереди"),
        w("后面", "hòumiàn", "derrière", "behind", "сзади"),
        w("在", "zài", "être à, se trouver", "to be at, in", "находиться, в"),
    )),
    Lesson(9, "Décrire et la météo", "Décrire", "大",
        en = "Describing and weather" to "Describing", ru = "Описание и погода" to "Описание", words = listOf(
        w("大", "dà", "grand", "big", "большой"),
        w("小", "xiǎo", "petit", "small", "маленький"),
        w("多", "duō", "beaucoup", "many, much", "много"),
        w("少", "shǎo", "peu", "few, little", "мало"),
        w("好", "hǎo", "bon, bien", "good, well", "хороший, хорошо"),
        w("很", "hěn", "très", "very", "очень"),
        w("太", "tài", "trop", "too (much)", "слишком"),
        w("冷", "lěng", "froid", "cold", "холодный"),
        w("热", "rè", "chaud", "hot", "жаркий, горячий"),
        w("漂亮", "piàoliang", "joli, beau", "pretty, beautiful", "красивый"),
        w("天气", "tiānqì", "temps (météo)", "weather", "погода"),
        w("下雨", "xià yǔ", "pleuvoir", "to rain", "идёт дождь"),
    )),
    Lesson(10, "Questions et petits mots", "Questions", "吗",
        en = "Questions and small words" to "Questions", ru = "Вопросы и частицы" to "Вопросы", words = listOf(
        w("是", "shì", "être", "to be", "быть, являться"),
        w("不", "bù", "ne… pas", "not", "не"),
        w("有", "yǒu", "avoir, il y a", "to have, there is", "иметь, есть"),
        w("没有", "méiyǒu", "ne pas avoir", "not to have", "не иметь, нет"),
        w("吗", "ma", "particule de question", "question particle", "вопросительная частица"),
        w("呢", "ne", "et… ? (particule)", "and…? (particle)", "а…? (частица)"),
        w("什么", "shénme", "quoi ?", "what?", "что?"),
        w("怎么", "zěnme", "comment ?", "how?", "как?"),
        w("怎么样", "zěnmeyàng", "comment ça va ? / qu'en penses-tu ?", "how is it? / how about?", "как дела? / как насчёт?"),
        w("的", "de", "particule de possession", "possessive particle", "притяжательная частица"),
        w("了", "le", "particule d'action finie", "completed-action particle", "частица завершённости"),
        w("都", "dōu", "tous, tout", "all, both", "все, всё"),
        w("和", "hé", "et, avec", "and, with", "и, с"),
        w("会", "huì", "savoir faire", "can (know how to)", "уметь"),
        w("能", "néng", "pouvoir", "can, to be able to", "мочь"),
        w("想", "xiǎng", "vouloir, penser à", "to want, to think of", "хотеть, думать о"),
        w("一点儿", "yìdiǎnr", "un peu", "a little", "немного"),
    )),
    Lesson(11, "Objets et transports", "Objets", "书",
        en = "Objects and transport" to "Objects", ru = "Предметы и транспорт" to "Предметы", words = listOf(
        w("书", "shū", "livre", "book", "книга"),
        w("字", "zì", "caractère (écrit)", "character (written)", "иероглиф"),
        w("汉语", "Hànyǔ", "langue chinoise", "Chinese language", "китайский язык"),
        w("衣服", "yīfu", "vêtement", "clothes", "одежда"),
        w("电脑", "diànnǎo", "ordinateur", "computer", "компьютер"),
        w("电视", "diànshì", "télévision", "television", "телевизор"),
        w("电影", "diànyǐng", "film", "film, movie", "фильм"),
        w("飞机", "fēijī", "avion", "airplane", "самолёт"),
        w("出租车", "chūzūchē", "taxi", "taxi", "такси"),
    )),
)
