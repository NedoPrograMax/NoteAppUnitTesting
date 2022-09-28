package com.example.noteappunittesting.feature_note.presentation.edit_note

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.toArgb
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteappunittesting.feature_note.domain.model.InvalidNoteException
import com.example.noteappunittesting.feature_note.domain.model.Note
import com.example.noteappunittesting.feature_note.domain.use_case.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditNoteViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    savedStateHandle: SavedStateHandle,
): ViewModel() {

    private val _noteTitle = MutableStateFlow(NoteTextFieldState(
        hint = "Enter title..."
    ))
    val noteTitle: StateFlow<NoteTextFieldState> get() = _noteTitle

    private val _noteContent = MutableStateFlow(NoteTextFieldState(
        hint = "Enter content..."
    ))
    val noteContent: StateFlow<NoteTextFieldState> get() = _noteContent

    private val _noteColor = MutableStateFlow(Note.noteColors.random().toArgb())
    val noteColor: StateFlow<Int> get() = _noteColor

    private val _noteImage = MutableStateFlow<String>("")
    val noteImage: StateFlow<String> get() = _noteImage

    private val _event = MutableSharedFlow<UiEvent>()
    val event: SharedFlow<UiEvent> get() = _event

    private var currentNoteId: Int? = null

    init {
        savedStateHandle.get<Int>("noteId")?.let {noteId->
            if(noteId != -1){
                viewModelScope.launch {
                    noteUseCases.getNoteUseCase(noteId)?.also { note->
                        currentNoteId = note.id
                        _noteTitle.emit(
                            noteTitle.value.copy(
                                text = note.title,
                                isHintVisible = false
                            )
                        )

                        _noteContent.emit(
                            noteContent.value.copy(
                                text = note.content,
                                isHintVisible = false
                            )
                        )

                        _noteColor.emit(note.color)

                        _noteImage.emit(note.imageUri)
                    }
                }
            }
        }
    }

    fun onEvent(event: EditNoteEvent){
        when(event){
            is EditNoteEvent.EnteredTitle -> {
                viewModelScope.launch {
                    _noteTitle.emit(
                        noteTitle.value.copy(
                            text = event.value
                        )
                    )
                }
            }
                is EditNoteEvent.FocusTitle -> {
                    viewModelScope.launch {
                        _noteTitle.emit(
                            noteTitle.value.copy(
                                isHintVisible = !event.focusState.isFocused &&
                                        noteTitle.value.text.isBlank()
                            )
                        )
                    }
                }

            is EditNoteEvent.EnteredContent -> {
                viewModelScope.launch {
                    _noteContent.emit(
                        noteContent.value.copy(
                            text = event.value
                        )
                    )
                }
            }
            is EditNoteEvent.FocusContent -> {
                viewModelScope.launch {
                    _noteContent.emit(
                        noteContent.value.copy(
                            isHintVisible = !event.focusState.isFocused &&
                                    noteContent.value.text.isBlank()
                        )
                    )
                }
            }

            is EditNoteEvent.ChangeColor ->{
                viewModelScope.launch {
                    _noteColor.emit(
                        event.color
                    )
                }
            }

            is EditNoteEvent.SaveNote ->{
                viewModelScope.launch {
                    try {

                        noteUseCases.addNoteUseCase(
                            Note(
                                title = noteTitle.value.text,
                                content = noteContent.value.text,
                                timeStamp = System.currentTimeMillis(),
                                color = noteColor.value,
                                imageUri = _noteImage.value,
                                id = currentNoteId,
                            )
                        )
                        _event.emit(UiEvent.SaveNote)
                    }catch (e:InvalidNoteException){
                        _event.emit(UiEvent.ShowSnackbar(e.message ?: "Couldn't save note"))
                    }
                }
            }

            is EditNoteEvent.UpdateImage ->{
                viewModelScope.launch {
                    _noteImage.emit(event.uri)
                }
            }

        }
    }

    sealed class UiEvent{
        data class  ShowSnackbar(val message: String): UiEvent()
        object SaveNote: UiEvent()
    }
}