package com.example.noteappunittesting.feature_note.domain.use_case

import android.util.Log
import com.example.noteappunittesting.feature_note.domain.model.InvalidNoteException
import com.example.noteappunittesting.feature_note.domain.model.Note
import com.example.noteappunittesting.feature_note.domain.repository.NoteRepo
import kotlin.jvm.Throws

class AddNoteUseCase(private val repo: NoteRepo) {

    @Throws(InvalidNoteException::class)
    suspend operator fun invoke(note: Note){
        if(note.title.isBlank()){
            throw InvalidNoteException("The title of the note can't be empty")
        }
        if(note.content.isBlank()){
            throw InvalidNoteException("The content of the note can't be empty")
        }

        repo.insertNote(note)
    }
}