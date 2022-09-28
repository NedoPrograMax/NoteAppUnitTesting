package com.example.noteappunittesting.feature_note.domain.repository

import com.example.noteappunittesting.feature_note.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepo {

    fun getNotes(): Flow<List<Note>>

    suspend fun getNoteById(id: Int): Note?

    suspend fun insertNote(note: Note)

    suspend fun deleteNote(note: Note)
}