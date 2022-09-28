package com.example.noteappunittesting.feature_note.domain.use_case

import com.example.noteappunittesting.feature_note.domain.model.Note
import com.example.noteappunittesting.feature_note.domain.repository.NoteRepo
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class GetNoteUseCaseTest{

    @Test
    fun `Mockito does smth`(){
        val note = Note(
            title = "sample note",
            content = "sample content",
            color = 12,
            timeStamp = 123123,
        )
        val noteRepo : NoteRepo = mock()
        runBlocking {
            whenever(noteRepo.getNoteById(1)).thenReturn(note)
            GetNoteUseCase(noteRepo).invoke(1)
            verify(noteRepo).getNoteById(1)
        }
    }
}
