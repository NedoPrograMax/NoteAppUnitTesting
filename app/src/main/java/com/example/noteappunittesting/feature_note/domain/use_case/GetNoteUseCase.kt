package com.example.noteappunittesting.feature_note.domain.use_case

import com.example.noteappunittesting.feature_note.domain.model.Note
import com.example.noteappunittesting.feature_note.domain.repository.NoteRepo

open class GetNoteUseCase(
    private val repo: NoteRepo
) {
    suspend operator fun invoke(id: Int): Note?= repo.getNoteById(id)
}