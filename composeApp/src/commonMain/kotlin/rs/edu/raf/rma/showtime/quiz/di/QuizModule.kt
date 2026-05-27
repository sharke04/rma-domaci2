package rs.edu.raf.rma.showtime.quiz.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import rs.edu.raf.rma.showtime.quiz.QuizGenerator
import rs.edu.raf.rma.showtime.quiz.QuizViewModel

val quizModule = module {
    single { QuizGenerator(get()) }
    viewModelOf(::QuizViewModel)
}
