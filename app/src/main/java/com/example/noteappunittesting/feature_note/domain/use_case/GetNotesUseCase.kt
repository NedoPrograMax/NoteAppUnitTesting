package com.example.noteappunittesting.feature_note.domain.use_case

import android.util.Log
import com.example.noteappunittesting.feature_note.domain.model.Note
import com.example.noteappunittesting.feature_note.domain.repository.NoteRepo
import com.example.noteappunittesting.feature_note.domain.util.NoteOrder
import com.example.noteappunittesting.feature_note.domain.util.OrderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetNotesUseCase(
    private val repo: NoteRepo,
) {
    operator fun invoke(
        noteOrder: NoteOrder = NoteOrder.Date(OrderType.Descending)
    ): Flow<List<Note>> {
        return repo.getNotes().map { notes->

           sortByOrder(noteOrder, notes)

        }
    }

    private fun sortByOrder(noteOrder: NoteOrder, notes: List<Note>): List<Note>{
        return when(noteOrder.orderType){
            is OrderType.Ascending ->{
                when(noteOrder){
                    is NoteOrder.Title -> notes.sortedBy { it.title.lowercase() }
                    is NoteOrder.Date -> notes.sortedBy { it.timeStamp }
                    is NoteOrder.Color -> notes.sortedBy { it.color }
                }
            }
            is OrderType.Descending ->{
                when(noteOrder){
                    is NoteOrder.Title -> notes.sortedByDescending { it.title.lowercase() }
                    is NoteOrder.Date -> notes.sortedByDescending { it.timeStamp }
                    is NoteOrder.Color -> notes.sortedByDescending { it.color }
                }
            }
        }
    }
}