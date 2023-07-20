package com.created.team201.presentation.createStudy

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.created.domain.model.CreateStudy
import com.created.domain.model.Period
import com.created.domain.repository.CreateStudyRepository
import com.created.team201.data.datasource.remote.CreateStudyRemoteDataSourceImpl
import com.created.team201.data.remote.NetworkServiceModule
import com.created.team201.data.repository.CreateStudyRepositoryImpl
import com.created.team201.presentation.createStudy.model.CreateStudyUiModel
import com.created.team201.presentation.createStudy.model.PeriodUiModel
import com.created.team201.util.NonNullLiveData
import com.created.team201.util.NonNullMutableLiveData
import kotlinx.coroutines.launch

class CreateStudyViewModel(
    private val createStudyRepository: CreateStudyRepository,
) : ViewModel() {
    private val name: NonNullMutableLiveData<String> = NonNullMutableLiveData("")
    private val introduction: NonNullMutableLiveData<String> = NonNullMutableLiveData("")

    private val _peopleCount: NonNullMutableLiveData<Int> = NonNullMutableLiveData(0)
    val peopleCount: NonNullLiveData<Int>
        get() = _peopleCount

    private val _startDate: NonNullMutableLiveData<String> = NonNullMutableLiveData("")
    val startDate: NonNullLiveData<String>
        get() = _startDate

    private val _period: NonNullMutableLiveData<Int> = NonNullMutableLiveData(0)
    val period: NonNullLiveData<Int>
        get() = _period

    private val _cycle: NonNullMutableLiveData<PeriodUiModel> =
        NonNullMutableLiveData(PeriodUiModel(0, 0))
    val cycle: NonNullLiveData<PeriodUiModel>
        get() = _cycle

    private val _isEnableCreateStudy: MediatorLiveData<Boolean> =
        MediatorLiveData<Boolean>().apply {
            addSourceList(name, introduction, peopleCount, startDate, period, cycle) {
                isInitializeCreateStudyInformation()
            }
        }
    val isEnableCreateStudy: LiveData<Boolean>
        get() = _isEnableCreateStudy

    private val _isSuccessCreateStudy: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val isSuccessCreateStudy: LiveData<Boolean>
        get() = _isSuccessCreateStudy

    fun setName(name: String) {
        this.name.value = name
    }

    fun setIntroduction(introduction: String) {
        this.introduction.value = introduction
    }

    fun setPeopleCount(peopleCount: Int) {
        _peopleCount.value = peopleCount
    }

    fun setStartDate(startDate: String) {
        _startDate.value = startDate
    }

    fun setCycle(date: Int, type: Int) {
        val cycle = PeriodUiModel(date, type)
        _cycle.value = cycle
    }

    fun setPeriod(period: Int) {
        _period.value = period
    }

    private fun getCreateStudy(): CreateStudyUiModel = CreateStudyUiModel(
        name.value,
        peopleCount.value,
        startDate.value,
        period.value,
        cycle.value,
        introduction.value,
    )

    fun createStudy() {
        viewModelScope.launch {
            runCatching {
                createStudyRepository.createStudy(getCreateStudy().toDomain())
            }.onSuccess {
                _isSuccessCreateStudy.value = true
            }.onFailure {
                _isSuccessCreateStudy.value = false
            }
        }
    }

    private fun isInitializeCreateStudyInformation(): Boolean =
        name.value.isNotEmpty() && peopleCount.value.isNotZero() && startDate.value.isNotEmpty() && period.value.isNotZero() && cycle.value.date.isNotZero() && introduction.value.isNotEmpty()

    private fun CreateStudyUiModel.toDomain(): CreateStudy =
        CreateStudy(name, peopleCount, startDate, period, cycle.toDomain(), introduction)

    private fun PeriodUiModel.toDomain(): Period = Period(date, type)

    private fun String.isNotEmpty(): Boolean = isEmpty().not()

    private fun Int.isNotZero(): Boolean = this != 0

    private fun <T> MediatorLiveData<T>.addSourceList(
        vararg liveDataArgument: LiveData<*>,
        onChanged: () -> T,
    ) {
        liveDataArgument.forEach {
            this.addSource(it) {
                value = onChanged()
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = CreateStudyRepositoryImpl(
                    CreateStudyRemoteDataSourceImpl(
                        NetworkServiceModule.createStudyService,
                    ),
                )
                return CreateStudyViewModel(repository) as T
            }
        }
    }
}
