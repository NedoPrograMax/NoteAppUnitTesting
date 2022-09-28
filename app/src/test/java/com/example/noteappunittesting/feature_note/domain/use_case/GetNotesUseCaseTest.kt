package com.example.noteappunittesting.feature_note.domain.use_case

import com.example.noteappunittesting.feature_note.date.repository.FakeNoteRepo
import com.example.noteappunittesting.feature_note.domain.model.Note
import com.example.noteappunittesting.feature_note.domain.util.NoteOrder
import com.example.noteappunittesting.feature_note.domain.util.OrderType
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class GetNotesUseCaseTest{
    private lateinit var getNotes: GetNotesUseCase
    private lateinit var fakeNoteRepo: FakeNoteRepo

    @Before
    fun setUp(){
        fakeNoteRepo = FakeNoteRepo()
        getNotes = GetNotesUseCase(fakeNoteRepo)

        val notesToInsert = mutableListOf<Note>()
        ('a'..'z').forEachIndexed { index, c ->
            notesToInsert.add(
                Note(
                    title = c.toString(),
                    content = c.toString(),
                    timeStamp = index.toLong(),
                    color = index,
                )
            )
        }

        notesToInsert.shuffle()
        runBlocking {
            notesToInsert.forEach { fakeNoteRepo.insertNote(it) }
        }
    }

    @Test
    fun `Order notes by title ascending, correct order`() = runBlocking{
        val notes = getNotes(NoteOrder.Title(OrderType.Ascending)).first()

        for(i in 0..notes.size -2){
            assertThat(notes[i].title).isLessThan(notes[i+1].title)
        }
    }

    @Test
    fun `Order notes by title descending, correct order`() = runBlocking{
        val notes = getNotes(NoteOrder.Title(OrderType.Descending)).first()

        for(i in 0..notes.size -2){
            assertThat(notes[i].title).isGreaterThan(notes[i+1].title)
        }
    }

    @Test
    fun `Order notes by date ascending, correct order`() = runBlocking{
        val notes = getNotes(NoteOrder.Date(OrderType.Ascending)).first()

        for(i in 0..notes.size -2){
            assertThat(notes[i].timeStamp).isLessThan(notes[i+1].timeStamp)
        }
    }

    @Test
    fun `Order notes by date descending, correct order`() = runBlocking{
        val notes = getNotes(NoteOrder.Date(OrderType.Descending)).first()

        for(i in 0..notes.size -2){
            assertThat(notes[i].timeStamp).isGreaterThan(notes[i+1].timeStamp)
        }
    }

    @Test
    fun `Order notes by color ascending, correct order`() = runBlocking{
        val notes = getNotes(NoteOrder.Color(OrderType.Ascending)).first()

        for(i in 0..notes.size -2){
            assertThat(notes[i].color).isLessThan(notes[i+1].color)
        }
    }

    @Test
    fun `Order notes by color descending, correct order`() = runBlocking{
        val notes = getNotes(NoteOrder.Color(OrderType.Descending)).first()

        for(i in 0..notes.size -2){
            assertThat(notes[i].color).isGreaterThan(notes[i+1].color)
        }
    }


}