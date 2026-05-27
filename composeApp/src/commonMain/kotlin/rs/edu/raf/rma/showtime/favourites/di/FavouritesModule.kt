package rs.edu.raf.rma.showtime.favourites.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import rs.edu.raf.rma.showtime.favourites.FavouritesRepository
import rs.edu.raf.rma.showtime.favourites.FavouritesRepositoryImpl
import rs.edu.raf.rma.showtime.favourites.FavouritesViewModel

val favouritesModule = module {
    single { FavouritesRepositoryImpl(db = get(), showtimeApi = get()) } bind FavouritesRepository::class
    viewModelOf(::FavouritesViewModel)
}
