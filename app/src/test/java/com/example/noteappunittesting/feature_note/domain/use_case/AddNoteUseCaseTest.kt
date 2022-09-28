package com.example.noteappunittesting.feature_note.domain.use_case

import com.example.noteappunittesting.feature_note.date.repository.FakeNoteRepo
import com.example.noteappunittesting.feature_note.domain.model.InvalidNoteException
import com.example.noteappunittesting.feature_note.domain.model.Note
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class AddNoteUseCaseTest{
    private lateinit var addNoteUseCase: AddNoteUseCase
    private lateinit var fakeNoteRepo: FakeNoteRepo

    @Before
    fun setUp(){
        fakeNoteRepo = FakeNoteRepo()
        addNoteUseCase = AddNoteUseCase(fakeNoteRepo)
    }

    @Test
    fun `Adding note with blank title to the DB, getting an InvalidNoteException`(){
        val note = Note(
            title = "",
            content = "A",
            timeStamp = 1,
            color = 1,
        )
        try {
            runBlocking {
                addNoteUseCase(note)
            }
        }catch (e: Exception){
            assertThat(e::class).isEqualTo(InvalidNoteException::class)
        }
    }

    @Test
    fun `Adding note with blank content to the DB, getting an InvalidNoteException`(){
        val note = Note(
            title = "1",
            content = "",
            timeStamp = 1,
            color = 1,
        )
        try {
            runBlocking {
                addNoteUseCase(note)
            }
        }catch (e: Exception){
            assertThat(e::class).isEqualTo(InvalidNoteException::class)
        }
    }

    @Test
    fun `Adding note to the DB, correct`(){
        val note = Note(
            title = "1",
            content = "1",
            timeStamp = 1,
            color = 1,
        )
            runBlocking {
                addNoteUseCase(note)
            }
        runBlocking {
            val notes = fakeNoteRepo.getNotes().first()
            assertThat(notes).contains(note)
        }

    }
}