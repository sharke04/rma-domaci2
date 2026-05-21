package rs.edu.raf.rma.showtime.movies.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import rs.edu.raf.rma.premiere.details.MovieDetailsViewModel
import rs.edu.raf.rma.premiere.list.MoviesListViewModel

val moviesModule = module {
    viewModelOf(::MoviesListViewModel)
    viewModelOf(::MovieDetailsViewModel)
}