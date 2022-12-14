package com.example.noteappunittesting.feature_note.presentation.util

sealed class Screen(val route: String){
    object NotesScreen: Screen("notes_screen")
    object EditNoteScreen: Screen("edit_note_screen")
}