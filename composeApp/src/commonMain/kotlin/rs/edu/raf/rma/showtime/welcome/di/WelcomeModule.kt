package rs.edu.raf.rma.showtime.welcome.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import rs.edu.raf.rma.showtime.welcome.WelcomeViewModel

val welcomeModule = module {
    viewModelOf(::WelcomeViewModel)
}
