package com.example.noteappunittesting.feature_note.presentation.edit_note

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.focus.FocusState
import com.example.noteappunittesting.feature_note.domain.model.Note

sealed class EditNoteEvent{
    data class EnteredTitle(val value: String): EditNoteEvent()
    data class FocusTitle(val focusState: FocusState): EditNoteEvent()
    data class EnteredContent(val value: String): EditNoteEvent()
    data class FocusContent(val focusState: FocusState): EditNoteEvent()
    data class ChangeColor(val color: Int): EditNoteEvent()
    object SaveNote : EditNoteEvent()
    data class UpdateImage(val uri: String): EditNoteEvent()
}
