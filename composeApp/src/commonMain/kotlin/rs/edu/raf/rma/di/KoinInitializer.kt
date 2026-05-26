package rs.edu.raf.rma.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import rs.edu.raf.rma.core.auth.di.authModule
import rs.edu.raf.rma.core.db.di.databaseModule
import rs.edu.raf.rma.networking.di.networkingModule
import rs.edu.raf.rma.passwords.di.passwordsModule
import rs.edu.raf.rma.posts.di.postsModule
import rs.edu.raf.rma.posts.splash.di.splashModule
import rs.edu.raf.rma.showtime.accounts.di.accountsModule
import rs.edu.raf.rma.showtime.movies.di.moviesModule
import rs.edu.raf.rma.showtime.welcome.di.welcomeModule

fun initKoin(config: KoinAppDeclaration? = null): KoinApplication {
    return startKoin {
        config?.invoke(this)
        modules(
            databaseModule(),
            networkingModule,
            authModule,
            splashModule,
            passwordsModule,
            postsModule,
            moviesModule,
            accountsModule,
            welcomeModule,
        )
    }
}
