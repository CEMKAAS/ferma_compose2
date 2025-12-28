package com.zaroslikov.data.di

import com.zaroslikov.data.room.repository.AddRepositoryImpl
import com.zaroslikov.data.room.repository.AnimalCountRepositoryImpl
import com.zaroslikov.data.room.repository.AnimalRepositoryImpl
import com.zaroslikov.data.room.repository.AnimalSizeRepositoryImpl
import com.zaroslikov.data.room.repository.AnimalVaccinationRepositoryImpl
import com.zaroslikov.data.room.repository.AnimalWeightRepositoryImpl
import com.zaroslikov.data.room.repository.ExpensesAnimalRepositoryImpl
import com.zaroslikov.data.room.repository.ExpensesRepositoryImpl
import com.zaroslikov.data.room.repository.FinanceRepositoryImpl
import com.zaroslikov.data.room.repository.IncubatorRepositoryImpl
import com.zaroslikov.data.room.repository.IncubatorTableRepositoryImpl
import com.zaroslikov.data.room.repository.NoteRepositoryImpl
import com.zaroslikov.data.room.repository.ProjectRepositoryImpl
import com.zaroslikov.data.room.repository.SaleRepositoryImpl
import com.zaroslikov.data.room.repository.SettingsRepositoryImpl
import com.zaroslikov.data.room.repository.WarehouseRepositoryImpl
import com.zaroslikov.data.room.repository.WriteOffRepositoryImpl
import com.zaroslikov.domain.repository.AddRepository
import com.zaroslikov.domain.repository.AnimalCountRepository
import com.zaroslikov.domain.repository.AnimalRepository
import com.zaroslikov.domain.repository.AnimalSizeRepository
import com.zaroslikov.domain.repository.AnimalVaccinationRepository
import com.zaroslikov.domain.repository.AnimalWeightRepository
import com.zaroslikov.domain.repository.ExpensesAnimalRepository
import com.zaroslikov.domain.repository.ExpensesRepository
import com.zaroslikov.domain.repository.FinanceRepository
import com.zaroslikov.domain.repository.IncubatorRepository
import com.zaroslikov.domain.repository.IncubatorTableRepository
import com.zaroslikov.domain.repository.NoteRepository
import com.zaroslikov.domain.repository.ProjectRepository
import com.zaroslikov.domain.repository.SaleRepository
import com.zaroslikov.domain.repository.SettingsRepository
import com.zaroslikov.domain.repository.WarehouseRepository
import com.zaroslikov.domain.repository.WriteOffRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RoomRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAddRepository(addRepositoryImpl: AddRepositoryImpl): AddRepository

    @Binds
    @Singleton
    abstract fun bindAnimalCountRepository(animalCountRepositoryImpl: AnimalCountRepositoryImpl): AnimalCountRepository

    @Binds
    @Singleton
    abstract fun bindAnimalRepository(animalRepositoryImpl: AnimalRepositoryImpl): AnimalRepository

    @Binds
    @Singleton
    abstract fun bindAnimalSizeRepository(animalSizeRepositoryImpl: AnimalSizeRepositoryImpl): AnimalSizeRepository

    @Binds
    @Singleton
    abstract fun bindAnimalVaccinationRepository(animalVaccinationRepositoryImpl: AnimalVaccinationRepositoryImpl): AnimalVaccinationRepository

    @Binds
    @Singleton
    abstract fun bindAnimalWeightRepository(animalWeightRepositoryImpl: AnimalWeightRepositoryImpl): AnimalWeightRepository

    @Binds
    @Singleton
    abstract fun bindExpensesAnimalRepository(expensesRepositoryImpl: ExpensesAnimalRepositoryImpl): ExpensesAnimalRepository

    @Binds
    @Singleton
    abstract fun bindExpensesRepository(expensesRepositoryImpl: ExpensesRepositoryImpl): ExpensesRepository

    @Binds
    @Singleton
    abstract fun bindFinanceRepository(financeRepositoryImpl: FinanceRepositoryImpl): FinanceRepository

    @Binds
    @Singleton
    abstract fun bindIncubatorRepository(incubatorRepositoryImpl: IncubatorRepositoryImpl): IncubatorRepository

    @Binds
    @Singleton
    abstract fun bindNoteRepository(noteRepositoryImpl: NoteRepositoryImpl): NoteRepository

    @Binds
    @Singleton
    abstract fun bindProjectRepository(projectRepositoryImpl: ProjectRepositoryImpl): ProjectRepository

    @Binds
    @Singleton
    abstract fun bindSaleRepository(saleRepositoryImpl: SaleRepositoryImpl): SaleRepository

    @Binds
    @Singleton
    abstract fun bindWarehouseRepository(warehouseRepositoryImpl: WarehouseRepositoryImpl): WarehouseRepository

    @Binds
    @Singleton
    abstract fun bindWriteOffRepository(writeOffRepositoryImpl: WriteOffRepositoryImpl): WriteOffRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(settingsRepositoryImpl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindIncubatorTableRepository(incubatorTableRepositoryImpl: IncubatorTableRepositoryImpl): IncubatorTableRepository
}