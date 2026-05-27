package rs.edu.raf.rma.showtime.quiz

interface QuizContract {
    enum class Phase { Loading, Active, Result }

    data class UiState(
        val phase: Phase = Phase.Loading,
        val questions: List<QuizQuestion> = emptyList(),
        val currentIndex: Int = 0,
        val answers: List<Int?> = emptyList(),
        val timeRemaining: Int = 60,
        val showAbandonDialog: Boolean = false,
        val navigateUp: Boolean = false,
        val error: String? = null,
    ) {
        val correctCount get() = answers.zip(questions).count { (a, q) -> a != null && a == q.correctAnswerIndex }
        val incorrectCount get() = answers.count { it != null } - correctCount
        val score get() = correctCount * 10
        val timeUsed get() = 60 - timeRemaining
    }

    sealed interface UiEvent {
        data class AnswerSelected(val answerIndex: Int) : UiEvent
        data object BackPressed : UiEvent
        data object AbandonConfirmed : UiEvent
        data object AbandonDismissed : UiEvent
    }

}
