package com.example.noteappunittesting.feature_note.date.repository

import com.example.noteappunittesting.feature_note.domain.model.Note
import com.example.noteappunittesting.feature_note.domain.repository.NoteRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeNoteRepo: NoteRepo {

    private val notes = mutableListOf<Note>()


    override fun getNotes(): Flow<List<Note>> {
        return flow{emit(notes)}
    }

    override suspend fun getNoteById(id: Int): Note? {
      return notes.find{it.id == id}
    }

    override suspend fun insertNote(note: Note) {
       notes.add(note)
    }

    override suspend fun deleteNote(note: Note) {
        notes.remove(note)
    }
}