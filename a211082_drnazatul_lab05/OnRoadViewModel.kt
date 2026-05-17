package com.example.a211082_drnazatul_lab05

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a211082_drnazatul_lab05.data.OnRoadUiState
import com.example.a211082_drnazatul_lab05.data.PlaceEntity
import com.example.a211082_drnazatul_lab05.data.PlaceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.compose.runtime.setValue

class OnRoadViewModel(
    private val repository: PlaceRepository
) : ViewModel() {

    val savedPlaces = repository.allPlaces.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    var uiState by mutableStateOf(OnRoadUiState())
        private set

    fun updateDestination(newDest: String, startRouting: Boolean = false) {
        uiState = uiState.copy(
            destination = newDest,
            isRouting = startRouting
        )
    }

    fun setRouting(status: Boolean) {
        uiState = uiState.copy(isRouting = status)
    }

    fun addPlace(name: String) {
        viewModelScope.launch {
            repository.insert(name)
        }
    }

    fun deletePlace(place: PlaceEntity) {
        viewModelScope.launch {
            repository.delete(place)
        }
    }
}