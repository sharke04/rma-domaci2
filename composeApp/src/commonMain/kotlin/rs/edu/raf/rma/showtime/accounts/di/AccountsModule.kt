package rs.edu.raf.rma.showtime.accounts.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import rs.edu.raf.rma.showtime.accounts.details.AccountDetailsViewModel
import rs.edu.raf.rma.showtime.accounts.registration.AccountRegistrationViewModel

val accountsModule = module {
    viewModelOf(::AccountRegistrationViewModel)
    viewModelOf(::AccountDetailsViewModel)
}
