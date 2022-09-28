package com.example.noteappunittesting.feature_note.domain.use_case

import com.example.noteappunittesting.feature_note.domain.model.Note
import com.example.noteappunittesting.feature_note.domain.repository.NoteRepo

class DeleteNoteUseCase(
    private val repo: NoteRepo
) {

    suspend operator fun invoke(note: Note) = repo.deleteNote(note)

}