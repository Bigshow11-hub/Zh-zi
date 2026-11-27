package com.zhuzi.app.data

/** Les deux parties de l'examen blanc, comme au HSK : écoute (听力) puis lecture (阅读). */
enum class ExamPart { LISTENING, READING }

/** Les sortes de questions. */
enum class ExamKind {
    /** On entend un mot, on choisit ses caractères. */
    HEAR_WORD,
    /** On entend une phrase, on choisit sa traduction. */
    HEAR_SENTENCE,
    /** On lit un mot, on choisit son sens. */
    READ_WORD,
    /** On lit une phrase à trou, on choisit le mot qui manque. */
    FILL_BLANK,
}

/**
 * Une question : ce qu'on entend (audio) ou lit (prompt), les 4 réponses proposées,
 * l'indice de la bonne, et le mot testé (pour la révision s'il est raté).
 */
data class ExamQuestion(
    val part: ExamPart,
    val kind: ExamKind,
    val word: Word,
    val audio: String?,
    val prompt: String?,
    val promptPinyin: String?,
    val choices: List<String>,
    val answer: Int,
    /** Réponses en caractères chinois (police pinceau). */
    val hanziChoices: Boolean,
)

/** Fabrique un examen blanc de 20 questions (10 écoute, 10 lecture) sur les mots d'un niveau. */
object ExamBuilder {
    const val PASS = 60

    fun build(level: Int, lang: Lang, seed: Long = System.currentTimeMillis()): List<ExamQuestion> {
        val rnd = kotlin.random.Random(seed)
        val pool = allLessons.filter { it.level == level }.flatMap { it.words }
        val withExample = pool.filter { ex -> examples[ex.hanzi]?.let { it.hanzi.contains(ex.hanzi) } == true }
        val picked = mutableSetOf<String>()
        fun take(from: List<Word>, n: Int): List<Word> {
            val out = from.filter { it.hanzi !in picked }.shuffled(rnd).take(n)
            picked += out.map { it.hanzi }
            return out
        }
        fun others(w: Word, n: Int, sameLength: Boolean = false): List<Word> {
            val base = pool.filter { it.hanzi != w.hanzi && it.meaning(lang) != w.meaning(lang) }
            val near = if (sameLength) base.filter { it.hanzi.length == w.hanzi.length } else base
            return (if (near.size >= n) near else base).shuffled(rnd).take(n)
        }
        fun <T> shuffleWithAnswer(right: T, wrong: List<T>): Pair<List<T>, Int> {
            val all = (wrong + right).shuffled(rnd)
            return all to all.indexOf(right)
        }
        val qs = mutableListOf<ExamQuestion>()
        // Écoute 1 : un mot → ses caractères
        take(pool, 5).forEach { w ->
            val (c, a) = shuffleWithAnswer(w.hanzi, others(w, 3, sameLength = true).map { it.hanzi })
            qs += ExamQuestion(ExamPart.LISTENING, ExamKind.HEAR_WORD, w, w.hanzi, null, null, c, a, true)
        }
        // Écoute 2 : une phrase → sa traduction
        take(withExample, 5).forEach { w ->
            val ex = examples.getValue(w.hanzi)
            val wrong = withExample.filter { it.hanzi != w.hanzi }.shuffled(rnd).take(3).map { examples.getValue(it.hanzi).meaning(lang) }
            val (c, a) = shuffleWithAnswer(ex.meaning(lang), wrong)
            qs += ExamQuestion(ExamPart.LISTENING, ExamKind.HEAR_SENTENCE, w, ex.hanzi, null, null, c, a, false)
        }
        // Lecture 1 : un mot → son sens
        take(pool, 5).forEach { w ->
            val (c, a) = shuffleWithAnswer(w.meaning(lang), others(w, 3).map { it.meaning(lang) })
            qs += ExamQuestion(ExamPart.READING, ExamKind.READ_WORD, w, null, w.hanzi, null, c, a, false)
        }
        // Lecture 2 : une phrase à trou → le mot qui manque
        take(withExample, 5).forEach { w ->
            val ex = examples.getValue(w.hanzi)
            val blank = ex.hanzi.replaceFirst(w.hanzi, "（" + "　".repeat(w.hanzi.length) + "）")
            val (c, a) = shuffleWithAnswer(w.hanzi, others(w, 3, sameLength = true).map { it.hanzi })
            qs += ExamQuestion(ExamPart.READING, ExamKind.FILL_BLANK, w, null, blank, null, c, a, true)
        }
        return qs
    }
}
