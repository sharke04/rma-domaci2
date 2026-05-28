package rs.edu.raf.rma.showtime.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch

class QuizViewModel(
    private val generator: QuizGenerator,
) : ViewModel() {

    private val _state = MutableStateFlow(QuizContract.UiState())
    val state = _state.asStateFlow()

    private fun setState(reducer: QuizContract.UiState.() -> QuizContract.UiState) {
        _state.getAndUpdate(reducer)
    }

    private val events = MutableSharedFlow<QuizContract.UiEvent>()

    fun setEvent(event: QuizContract.UiEvent) {
        viewModelScope.launch { events.emit(event) }
    }

    private var timerJob: Job? = null

    init {
        observeEvents()
        generateQuestions()
    }

    private fun observeEvents() {
        viewModelScope.launch {
            events.collect { event ->
                when (event) {
                    is QuizContract.UiEvent.AnswerSelected -> recordAnswer(event.answerIndex)
                    QuizContract.UiEvent.BackPressed -> setState { copy(showAbandonDialog = true) }
                    QuizContract.UiEvent.AbandonConfirmed -> abandon()
                    QuizContract.UiEvent.AbandonDismissed -> setState { copy(showAbandonDialog = false) }
                }
            }
        }
    }

    private fun generateQuestions() {
        viewModelScope.launch {
            try {
                val questions = generator.generate()
                setState {
                    copy(
                        phase = QuizContract.Phase.Active,
                        questions = questions,
                        answers = List<Int?>(questions.size) { null },
                    )
                }
                startTimer()
            } catch (e: Exception) {
                setState { copy(error = e.message) }
            }
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            var remaining = 60
            while (remaining > 0 && _state.value.phase == QuizContract.Phase.Active) {
                delay(1000)
                remaining--
                setState { copy(timeRemaining = remaining) }
            }
            if (_state.value.phase == QuizContract.Phase.Active) {
                finishQuiz()
            }
        }
    }

    private fun abandon() {
        timerJob?.cancel()
        timerJob = null
        setState { copy(navigateUp = true) }
    }

    private fun finishQuiz() {
        timerJob?.cancel()
        timerJob = null
        setState { copy(phase = QuizContract.Phase.Result, timeAtFinish = timeRemaining) }
    }

    private fun recordAnswer(answerIndex: Int) {
        val currentIdx = _state.value.currentIndex
        if (_state.value.phase != QuizContract.Phase.Active) return
        if (_state.value.answers.getOrNull(currentIdx) != null) return

        val isCorrect = answerIndex == _state.value.questions[currentIdx].correctAnswerIndex
        val newAnswers = _state.value.answers.toMutableList().also { it[currentIdx] = answerIndex }
        setState {
            copy(
                answers = newAnswers,
                correctCount = if (isCorrect) correctCount + 1 else correctCount,
            )
        }

        viewModelScope.launch {
            delay(800)
            if (currentIdx == _state.value.questions.size - 1) {
                finishQuiz()
            } else {
                setState { copy(currentIndex = currentIdx + 1) }
            }
        }
    }
}
