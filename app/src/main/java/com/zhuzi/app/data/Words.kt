package com.zhuzi.app.data

/** Un mot à apprendre : caractères, prononciation (pinyin) et traduction. */
data class Word(val hanzi: String, val pinyin: String, val french: String)

/** Premiers mots pour démarrer. La liste grandira au fil des leçons. */
val starterWords = listOf(
    Word("你好", "nǐ hǎo", "bonjour"),
    Word("谢谢", "xièxie", "merci"),
    Word("再见", "zàijiàn", "au revoir"),
    Word("我", "wǒ", "je, moi"),
    Word("你", "nǐ", "tu, toi"),
    Word("是", "shì", "être"),
    Word("不", "bù", "ne… pas"),
    Word("水", "shuǐ", "eau"),
    Word("茶", "chá", "thé"),
    Word("竹子", "zhúzi", "bambou"),
)
