package rs.edu.raf.rma.showtime.watchlist.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import rs.edu.raf.rma.showtime.watchlist.WatchlistRepository
import rs.edu.raf.rma.showtime.watchlist.WatchlistRepositoryImpl
import rs.edu.raf.rma.showtime.watchlist.WatchlistViewModel

val watchlistModule = module {
    single { WatchlistRepositoryImpl(db = get(), showtimeApi = get()) } bind WatchlistRepository::class
    viewModelOf(::WatchlistViewModel)
}
