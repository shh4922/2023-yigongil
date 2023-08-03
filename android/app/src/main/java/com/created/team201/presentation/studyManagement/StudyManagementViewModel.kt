package com.created.team201.presentation.studyManagement

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.created.domain.model.CreateTodo
import com.created.domain.model.PageIndex
import com.created.domain.model.Role
import com.created.domain.model.Round
import com.created.domain.model.RoundDetail
import com.created.domain.model.StudyDetail
import com.created.domain.model.StudyMember
import com.created.domain.model.Todo
import com.created.domain.repository.StudyManagementRepository
import com.created.team201.data.datasource.remote.StudyManagementDataSourceImpl
import com.created.team201.data.mapper.toDomain
import com.created.team201.data.remote.NetworkServiceModule
import com.created.team201.data.repository.StudyManagementRepositoryImpl
import com.created.team201.presentation.home.model.TodoUiModel
import com.created.team201.presentation.studyManagement.adapter.OptionalTodoViewType
import com.created.team201.presentation.studyManagement.model.OptionalTodoUiModel
import com.created.team201.presentation.studyManagement.model.RoundUiModel
import com.created.team201.presentation.studyManagement.model.StudyManagementInformationUiModel
import com.created.team201.presentation.studyManagement.model.StudyMemberUiModel
import com.created.team201.presentation.studyManagement.model.StudyRoundDetailUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StudyManagementViewModel(
    private val repository: StudyManagementRepository,
) : ViewModel() {
    private val rounds: MutableLiveData<List<RoundUiModel>> = MutableLiveData()

    private val _studyRounds: MutableLiveData<List<StudyRoundDetailUiModel>> = MutableLiveData()
    val studyRounds: LiveData<List<StudyRoundDetailUiModel>> get() = _studyRounds

    private val _state: MutableLiveData<StudyManagementState> = MutableLiveData()
    val state: LiveData<StudyManagementState> get() = _state

    private val _currentRound: MutableLiveData<Int> = MutableLiveData(0)
    val currentRound: LiveData<Int> get() = _currentRound

    lateinit var studyInformation: StudyManagementInformationUiModel

    fun initStudyManagement(studyId: Long, roleIndex: Int) {
        initStatus(roleIndex)
        viewModelScope.launch {
            fetchStudyInformation(studyId)
            initStudyRounds(studyId)
        }
    }

    private fun initStatus(roleIndex: Int) {
        _state.value = StudyManagementState.getRoleStatus(Role.valueOf(roleIndex))
    }

    private suspend fun fetchStudyInformation(studyId: Long) {
        withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            runCatching {
                repository.getStudyDetail(studyId)
            }.onSuccess {
                studyInformation = it.toUiModel()
                rounds.postValue(it.rounds.map { round: Round -> round.toUiModel() })
            }.onFailure {
                Log.e(LOG_ERROR, it.message.toString())
            }
        }
    }

    private suspend fun initStudyRounds(studyId: Long) {
        val rounds = rounds.value ?: listOf()
        _currentRound.postValue(studyInformation.currentRound)

        withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            for (round in rounds) {
                getStudyRoundDetail(studyId, round.id)
                    .onSuccess {
                        val studyRounds = studyRounds.value ?: listOf()
                        _studyRounds.postValue(studyRounds + it.toUiModel())
                    }.onFailure {
                        Log.e(LOG_ERROR, it.message.toString())
                    }
            }
        }
    }

    fun updateCurrentPage(pageIndex: PageIndex) {
        _currentRound.value = pageIndex.toRound()
    }

    private suspend fun getStudyRoundDetail(studyId: Long, roundId: Long): Result<RoundDetail> {
        return runCatching {
            repository.getRoundDetail(studyId, roundId)
        }
    }

    fun updateTodoContent(
        currentItemId: Int,
        todo: TodoUiModel,
        todoContent: String,
        studyId: Long,
    ) {
        val isNecessary =
            studyRounds.value?.get(currentItemId)?.necessaryTodo?.todoId == todo.todoId
        val newTodo = todo.copy(content = todoContent)
        val studyDetails = studyRounds.value ?: listOf()
        updateNecessaryTodoContent(studyDetails, todo.todoId, todoContent)
        patchTodo(newTodo, isNecessary, studyId)
    }

    fun updateTodo(currentItemId: Int, todoId: Long, isDone: Boolean, studyId: Long) {
        val studyDetails = studyRounds.value ?: listOf()
        val isNecessary = studyRounds.value?.get(currentItemId)?.necessaryTodo?.todoId == todoId
        val studyRound: StudyRoundDetailUiModel
        val todo: TodoUiModel
        when (isNecessary) {
            true -> {
                updateNecessaryTodoCheck(studyDetails, todoId, isDone)
                studyRound = studyDetails.find { it.necessaryTodo.todoId == todoId }!!
                todo = studyRound.necessaryTodo.copy(isDone = isDone)
            }

            false -> {
                updateOptionalTodoCheck(studyDetails, todoId, isDone)
                studyRound =
                    studyDetails.find { it.optionalTodos.any { it.todo.todoId == todoId } }!!
                todo =
                    studyRound.optionalTodos.find { it.todo.todoId == todoId }!!.todo.copy(isDone = isDone)
            }
        }
        patchTodo(todo, isNecessary, studyId)
    }

    private fun patchTodo(
        todo: TodoUiModel,
        isNecessary: Boolean,
        studyId: Long,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                repository.patchTodo(
                    studyId = studyId,
                    todo = todo.toDomain(),
                    isNecessary = isNecessary,
                )
            }.onFailure {
                Log.e(LOG_ERROR, it.message.toString())
            }
        }
    }

    private fun updateNecessaryTodoContent(
        studyDetails: List<StudyRoundDetailUiModel>,
        id: Long,
        content: String,
    ) {
        _studyRounds.value = studyDetails.map { studyDetailUiModel ->
            studyDetailUiModel.takeIf { it.necessaryTodo.todoId != id } ?: studyDetailUiModel.copy(
                necessaryTodo = studyDetailUiModel.necessaryTodo.copy(content = content),
            )
        }
    }

    private fun updateNecessaryTodoCheck(
        studyDetails: List<StudyRoundDetailUiModel>,
        id: Long,
        isDone: Boolean,
    ) {
        _studyRounds.value = studyDetails.map { studyDetailUiModel ->
            studyDetailUiModel.takeIf { it.necessaryTodo.todoId != id } ?: studyDetailUiModel.copy(
                necessaryTodo = studyDetailUiModel.necessaryTodo.copy(isDone = isDone),
            )
        }
    }

    private fun updateOptionalTodoCheck(
        studyDetails: List<StudyRoundDetailUiModel>,
        id: Long,
        isDone: Boolean,
    ) {
        _studyRounds.value = studyDetails.map { studyDetailUiModel ->
            studyDetailUiModel.takeIf { todoUiModel -> !todoUiModel.optionalTodos.any { it.todo.todoId == id } }
                ?: studyDetailUiModel.copy(
                    optionalTodos = studyDetailUiModel.optionalTodos.map {
                        it.takeUnless { it.todo.todoId == id }
                            ?: it.copy(it.todo.copy(isDone = isDone))
                    },
                )
        }
    }

    fun addOptionalTodo(studyId: Long, currentPage: Int, todoContent: String) {
        val currentStudyRounds = studyRounds.value ?: listOf()
        val currentRound = currentStudyRounds[currentPage]

        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                repository.createTodo(studyId, CreateTodo(false, currentRound.id, todoContent))
            }.onSuccess { result ->
                val todoId = result.getOrDefault(DEFAULT_TODO_ID)
                val newOptionalTodos = getNewOptionalTodos(currentRound, todoId, todoContent)
                val newRound = currentRound.copy(optionalTodos = newOptionalTodos)
                val updatedStudyRounds = currentStudyRounds.map { studyRoundDetailUiModel ->
                    studyRoundDetailUiModel.takeIf { it.id != currentRound.id } ?: newRound
                }
                _studyRounds.postValue(updatedStudyRounds)
            }.onFailure {
                Log.e(LOG_ERROR, it.message.toString())
            }
        }
    }

    private fun getNewOptionalTodos(
        currentStudy: StudyRoundDetailUiModel,
        todoId: Long,
        todoContent: String,
    ): MutableList<OptionalTodoUiModel> {
        val newOptionalTodos = currentStudy.optionalTodos.toMutableList()
        newOptionalTodos.addAll(
            listOf(
                OptionalTodoUiModel(
                    TodoUiModel(todoId, todoContent, false),
                    OptionalTodoViewType.DISPLAY.viewType,
                ),
            ),
        )
        return newOptionalTodos
    }

    private fun StudyDetail.toUiModel(): StudyManagementInformationUiModel =
        StudyManagementInformationUiModel(
            name = name,
            numberOfCurrentMembers = numberOfCurrentMembers,
            numberOfMaximumMembers = numberOfMaximumMembers,
            role = role,
            startAt = startAt,
            totalRoundCount = totalRoundCount,
            periodOfRound = periodOfRound,
            currentRound = currentRound,
            introduction = introduction,
            rounds = rounds.map { it.toUiModel() },
        )

    private fun RoundDetail.toUiModel(): StudyRoundDetailUiModel = StudyRoundDetailUiModel(
        id = id,
        masterId = masterId,
        role = role,
        necessaryTodo = necessaryTodo.toUiModel(),
        optionalTodos = optionalTodos.map { it.toOptionalTodoUiModel() },
        studyMembers = members.map { it.toUiModel(masterId) },

    )

    private fun Todo.toUiModel(): TodoUiModel = TodoUiModel(
        todoId = todoId,
        content = if (content == null) "" else content,
        isDone = isDone,
    )

    private fun StudyMember.toUiModel(masterId: Long): StudyMemberUiModel = StudyMemberUiModel(
        id = id,
        isMaster = id == masterId,
        nickname = nickname,
        profileImageUrl = profileImageUrl,
        isDone = isDone,

    )

    private fun Round.toUiModel(): RoundUiModel = RoundUiModel(id = id, number = number)

    private fun Todo.toOptionalTodoUiModel(): OptionalTodoUiModel = OptionalTodoUiModel(
        todo = this.toUiModel(),
        viewType = OptionalTodoViewType.DISPLAY.viewType,
    )

    companion object {
        private const val DEFAULT_TODO_ID = 0L
        private const val LOG_ERROR = "ERROR"

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = StudyManagementRepositoryImpl(
                    StudyManagementDataSourceImpl(
                        NetworkServiceModule.studyManagementService,
                    ),
                )
                return StudyManagementViewModel(repository) as T
            }
        }
    }
}
