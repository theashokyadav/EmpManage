package com.empmanage.di

import org.koin.core.module.Module
import org.koin.dsl.module

val domainModule: Module = module {
}

val dataModule: Module = module {
}

val viewModelModule: Module = module {
}

val commonModules = listOf(domainModule, dataModule, viewModelModule)
