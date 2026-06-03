package rs.edu.raf.rma.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import rs.edu.raf.rma.core.auth.di.authModule
import rs.edu.raf.rma.core.db.di.databaseModule
import rs.edu.raf.rma.networking.di.networkingModule
import rs.edu.raf.rma.showtime.accounts.di.accountsModule
import rs.edu.raf.rma.showtime.favourites.di.favouritesModule
import rs.edu.raf.rma.showtime.movies.di.moviesModule
import rs.edu.raf.rma.showtime.watchlist.di.watchlistModule
import rs.edu.raf.rma.showtime.quiz.di.quizModule
import rs.edu.raf.rma.showtime.welcome.di.welcomeModule

fun initKoin(config: KoinAppDeclaration? = null): KoinApplication {
    return startKoin {
        config?.invoke(this)
        modules(
            databaseModule(),
            networkingModule,
            authModule,
            moviesModule,
            favouritesModule,
            watchlistModule,
            accountsModule,
            welcomeModule,
            quizModule,
        )
    }
}
