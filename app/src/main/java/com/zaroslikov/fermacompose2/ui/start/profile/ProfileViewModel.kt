package com.zaroslikov.fermacompose2.ui.start.profile

import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.table.profile.DomainProfileTable
import com.zaroslikov.domain.repository.BookmarkRepository
import com.zaroslikov.domain.repository.ExpensesRepository
import com.zaroslikov.domain.repository.ProfileRepository
import com.zaroslikov.domain.repository.SaleRepository
import com.zaroslikov.domain.repository.SettingsRepository
import com.zaroslikov.domain.repository.WriteOffRepository
import com.zaroslikov.fermacompose2.base.viewModel.BaseViewModel2
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val expensesRepository: ExpensesRepository,
    private val saleRepository: SaleRepository,
    private val writeOffRepository: WriteOffRepository,
    private val profileRepository: ProfileRepository,
    private val bookmarkRepository: BookmarkRepository
) : BaseViewModel2<ProfileState, ProfileIntent, ProfileReduce>(
    ProfileState(),
    ProfileReduce()
) {

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            sendIntent(ProfileIntent.LoadingChanged(true))
            val currencyList = profileRepository.getAllCurrenciesSorted().first()
            if (currencyList.isNotEmpty()) {
                val profileFinanceList = currencyList.mapIndexed { index, suffix ->
                    val income = saleRepository.getIncomeAllProject(suffix).first()
                    val expenses = expensesRepository.getExpensesAllProject(suffix).first()
                    val ownNeed = writeOffRepository.getOwnNeedAllProject(suffix).first()
                    val scrap = writeOffRepository.getScrapAllProject(suffix).first()
                    val (incubIncome, incubExpenses, bredEggs, hatchedEggs) =
                        bookmarkRepository.getFinanceIncubatorAllProject(suffix).first()

                    val isProjectFinanceActive =
                        income > 0 || expenses > 0 || ownNeed > 0 || scrap > 0

                    val isIncubatorFinanceActive =
                        incubIncome > 0 || incubExpenses > 0 || bredEggs > 0 || hatchedEggs > 0


                    ProfileFinance(
                        currentBalance = (income + incubIncome) - (expenses + incubExpenses),
                        projectFinance = if (isProjectFinanceActive) ProjectFinance(
                            income = income,
                            expenses = expenses,
                            ownNeed = ownNeed,
                            scrap = scrap,
                        ) else null,
                        incubatorFinance = if (isIncubatorFinanceActive) IncubatorFinance(
                            income = incubIncome,
                            expenses = incubExpenses,
                            bredEggs = bredEggs.toDouble(),
                            hatchedEggs = hatchedEggs.toDouble(),
                        ) else null,
                        priceSuffix = suffix,
                        isSelected = index == 0
                    )
                }
                updateState { state ->
                    state.copy(
                        currencyList = profileFinanceList,
                        currentFinanceCurrency = profileFinanceList.firstOrNull { it.isSelected }
                            ?: ProfileFinance()
                    )
                }
            }
            sendIntent(ProfileIntent.LoadingChanged(false))
        }
    }

    fun onIntent(intent: ProfileIntent) {
        sendIntent(intent)
        when (intent) {
            is ProfileIntent.NameChanged -> updateNameProfile(intent.value)
            else -> Unit
        }
    }

    private fun updateNameProfile(name: String) {
        viewModelScope.launch {
            profileRepository.updateProfile(DomainProfileTable(id = 1, name = name))
        }
    }
}
