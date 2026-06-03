package rs.edu.raf.rma.showtime.quiz.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import rs.edu.raf.rma.showtime.quiz.QuizGenerator
import rs.edu.raf.rma.showtime.quiz.QuizResultsRepository
import rs.edu.raf.rma.showtime.quiz.QuizResultsRepositoryImpl
import rs.edu.raf.rma.showtime.quiz.QuizViewModel

val quizModule = module {
    single { QuizGenerator(get(), get()) }
    single { QuizResultsRepositoryImpl(db = get(), authStore = get()) } bind QuizResultsRepository::class
    viewModelOf(::QuizViewModel)
}
