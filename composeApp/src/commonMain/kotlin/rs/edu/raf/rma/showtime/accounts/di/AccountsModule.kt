package rs.edu.raf.rma.showtime.accounts.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import rs.edu.raf.rma.showtime.accounts.AccountsViewModel

val accountsModule = module {
    viewModelOf(::AccountsViewModel)
}
