/*
package com.example.sbma_project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sbma_project.database.Run
import com.example.sbma_project.database.RunDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class RunViewModel(
    private val dao: RunDao
): ViewModel(){
    private val _sortType = MutableStateFlow(SortType.DATE)

    private val _runs = _sortType
        .flatMapLatest { sortType ->
            when(sortType){
                SortType.DATE -> dao.getRunsOrderedByDate()
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _state = MutableStateFlow(RunState())
    private val state = combine(_state,_sortType,_runs){ state, sortType, runs ->
        state.copy(
            //runs = runs,
            sortType = sortType,

        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RunState())

    fun onEvent(event: RunEvent){
        when(event) {
            is RunEvent.DeleteRun ->{
                viewModelScope.launch {
                    dao.deleteRun(event.run)
                }
            }

            RunEvent.SavaRun -> {
                val date = state.value.date
                val avgSpeed = state.value.avgSpeed
                val distance = state.value.distance
                val steps = state.value.steps
                val stepLength = state.value.stepLength
                val time = state.value.time

                if(
                    date.isBlank() //||
//                    avgSpeed.isBlank() ||
//                    distance.isBlank() ||
//                    steps.isBlank() ||
//                    stepLength.isBlank() ||
//                    time.isBlank()
                    ){
                    return
                }
                val run = Run(
                    date = date,
                    avgSpeed = avgSpeed,
                    distance = distance,
                    steps = steps,
                    stepLength = stepLength,
                    time = time,
                )
                viewModelScope.launch {
                    dao.upsertRun(run)
                }
                _state.update { it.copy(
                    //isAddingContact = false,
                    date = "",
                    avgSpeed = 0,
                    distance = 0,
                    steps = 0,
                    stepLength = 0.0,
                    time = 0.0,
                ) }
            }

            is RunEvent.SortRuns -> {
                _sortType.value = event.sortType
            }
        }
    }
}*/
