package com.empmanage.di

import com.empmanage.data.local.db.AppDatabase
import com.empmanage.data.local.db.EmployeeDao
import com.empmanage.data.local.db.createDatabase
import com.empmanage.data.repository.EmployeeRepositoryImpl
import com.empmanage.domain.repository.EmployeeRepository
import com.empmanage.domain.usecase.AddEmployeeUseCase
import com.empmanage.domain.usecase.TopNSalaryUseCase
import com.empmanage.domain.usecase.UpdateEmployeeUseCase
import com.empmanage.domain.util.DuplicateIndex
import com.empmanage.domain.util.NameIndex
import com.empmanage.presentation.viewmodel.EmployeeFormViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val domainModule: Module = module {
    single { DuplicateIndex() }
    single { NameIndex() }
    single { AddEmployeeUseCase(get(), get(), get()) }
    single { UpdateEmployeeUseCase(get(), get(), get()) }
    single { TopNSalaryUseCase() }
}

val dataModule: Module = module {
    single<AppDatabase> { createDatabase() }
    single<EmployeeDao> { get<AppDatabase>().employeeDao() }
    single<EmployeeRepository> { EmployeeRepositoryImpl(get()) }
}

val viewModelModule: Module = module {
    viewModelOf(::EmployeeFormViewModel)
}

val commonModules = listOf(domainModule, dataModule, viewModelModule)
