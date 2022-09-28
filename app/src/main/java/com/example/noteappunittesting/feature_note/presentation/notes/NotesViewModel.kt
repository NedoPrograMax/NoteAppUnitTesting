package com.example.noteappunittesting.feature_note.presentation.notes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteappunittesting.feature_note.domain.model.Note
import com.example.noteappunittesting.feature_note.domain.use_case.NoteUseCases
import com.example.noteappunittesting.feature_note.domain.util.NoteOrder
import com.example.noteappunittesting.feature_note.domain.util.OrderType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases
): ViewModel() {

    private val _state = MutableStateFlow(NotesState())
    val state: StateFlow<NotesState> get() = _state

    private var recentlyDeletedNote: Note? = null

    private var getNotesJob: Job? = null

    init {
        getNotes(NoteOrder.Date(OrderType.Descending))
    }

    fun onEvent(event: NotesEvent){
        when(event){
            is NotesEvent.Order ->{
                viewModelScope.launch {
                    if (_state.value.noteOrder::class == event.noteOrder::class &&
                        _state.value.noteOrder.orderType == event.noteOrder.orderType){
                        return@launch
                    }
                    getNotes(event.noteOrder)
                }
            }
            is NotesEvent.DeleteNote ->{
                viewModelScope.launch {
                    noteUseCases.deleteNoteUseCase(event.note)
                    recentlyDeletedNote = event.note
                }
            }
            is NotesEvent.RestoreNote ->{
                viewModelScope.launch {
                    noteUseCases.addNoteUseCase(recentlyDeletedNote ?: return@launch)
                    recentlyDeletedNote = null
                }
            }
            is NotesEvent.ToggleOrderSection ->{
                viewModelScope.launch {
                    _state.emit(_state.value.copy(
                        isOrderSectionVisible = !_state.value.isOrderSectionVisible
                    ))
                }
            }
        }
    }

    private fun getNotes(noteOrder: NoteOrder) {
        getNotesJob?.cancel()
        getNotesJob = noteUseCases.getNotesUseCase(noteOrder)
                .onEach { notes->

                _state.emit(_state.value.copy(
                    notes = notes,
                    noteOrder = noteOrder,
                ))
            }
                .launchIn(viewModelScope)
    }

}