package rs.edu.raf.rma.showtime.quiz

sealed class QuizQuestion {
    abstract val imageUrl: String?
    abstract val answers: List<String>
    abstract val correctAnswerIndex: Int

    data class GuessTheMovie(
        override val imageUrl: String,
        override val answers: List<String>,
        override val correctAnswerIndex: Int,
    ) : QuizQuestion()

    data class GuessTheYear(
        override val imageUrl: String?,
        val title: String,
        override val answers: List<String>,
        override val correctAnswerIndex: Int,
    ) : QuizQuestion()

    data class GuessTheActor(
        override val imageUrl: String?,
        val title: String,
        override val answers: List<String>,
        override val correctAnswerIndex: Int,
    ) : QuizQuestion()
}
