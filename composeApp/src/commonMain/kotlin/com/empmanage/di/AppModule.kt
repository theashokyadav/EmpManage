package com.empmanage.di

import com.empmanage.data.local.db.AppDatabase
import com.empmanage.data.local.db.EmployeeDao
import com.empmanage.data.local.db.createDatabase
import com.empmanage.data.repository.EmployeeRepositoryImpl
import com.empmanage.domain.repository.EmployeeRepository
import org.koin.core.module.Module
import org.koin.dsl.module

val domainModule: Module = module {
}

val dataModule: Module = module {
    single<AppDatabase> { createDatabase() }
    single<EmployeeDao> { get<AppDatabase>().employeeDao() }
    single<EmployeeRepository> { EmployeeRepositoryImpl(get()) }
}

val viewModelModule: Module = module {
}

val commonModules = listOf(domainModule, dataModule, viewModelModule)
