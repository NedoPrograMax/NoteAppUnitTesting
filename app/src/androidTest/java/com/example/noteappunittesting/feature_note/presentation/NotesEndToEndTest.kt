package com.example.noteappunittesting.feature_note.presentation

import androidx.activity.compose.setContent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.noteappunittesting.core.util.TestTags
import com.example.noteappunittesting.di.AppModule
import com.example.noteappunittesting.feature_note.presentation.edit_note.EditNoteScreen
import com.example.noteappunittesting.feature_note.presentation.notes.NotesScreen
import com.example.noteappunittesting.feature_note.presentation.util.Screen
import com.example.noteappunittesting.ui.theme.NoteAppUnitTestingTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(AppModule::class)
class NotesEndToEndTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
        composeRule.activity.setContent {
            NoteAppUnitTestingTheme {
                val navController = rememberNavController()
                NavHost(navController = navController,
                    startDestination = Screen.NotesScreen.route) {
                    composable(route = Screen.NotesScreen.route) {
                        NotesScreen(navController)
                    }
                    composable(route = Screen.EditNoteScreen.route
                            + "?noteId={noteId}&noteColor={noteColor}",
                        arguments = listOf(
                            navArgument(
                                name = "noteId"
                            ) {
                                type = NavType.IntType
                                defaultValue = -1
                            },
                            navArgument(
                                name = "noteColor"
                            ) {
                                type = NavType.IntType
                                defaultValue = -1
                            },
                        )
                    ) {
                        val noteColor = it.arguments?.getInt("noteColor") ?: -1
                        EditNoteScreen(
                            navController,
                            noteColor = noteColor,
                        )
                    }
                }
            }
        }
    }

    @Test
    fun saveNewNote_editAfterwards() {
        // Click on FAB to add note screen
        composeRule.onNodeWithContentDescription("Add").performClick()

        // Enter text in title and content text fields than saving the note
        composeRule.onNodeWithTag(TestTags.TITLE_SECTION).performTextInput("test-title")
        composeRule.onNodeWithTag(TestTags.CONTENT_SECTION).performTextInput("test-content")
        composeRule.onNodeWithContentDescription("Save").performClick()

        // Make sure there is the note and click on note to edit it
        composeRule.onNodeWithText("test-title").assertIsDisplayed()
        composeRule.onNodeWithText("test-title").performClick()

      // Make sure all fields are the same and adding text to title and saving
        composeRule.onNodeWithTag(TestTags.TITLE_SECTION).assertTextEquals("test-title")
        composeRule.onNodeWithTag(TestTags.CONTENT_SECTION).assertTextEquals("test-content")
        composeRule.onNodeWithTag(TestTags.TITLE_SECTION).performTextInput("2")
        composeRule.onNodeWithContentDescription("Save").performClick()

        // Make sure the update was applied to the list
        composeRule.onNodeWithText("2").assertIsDisplayed()

    }

    @Test
    fun saveNewNotes_orderByTitleDescending(){
        for(i in 1..3){
            // Click on FAB to add note screen
            composeRule.onNodeWithContentDescription("Add").performClick()

            // Enter text in title and content text fields than saving the note
            composeRule.onNodeWithTag(TestTags.TITLE_SECTION).performTextInput("$i")
            composeRule.onNodeWithTag(TestTags.CONTENT_SECTION).performTextInput("$i")
            composeRule.onNodeWithContentDescription("Save").performClick()
        }

        composeRule.onNodeWithText("1").assertIsDisplayed()
        composeRule.onNodeWithText("2").assertIsDisplayed()
        composeRule.onNodeWithText("3").assertIsDisplayed()

        composeRule.onNodeWithContentDescription("Sort").performClick()
        composeRule.onNodeWithContentDescription("Title").performClick()
        composeRule.onNodeWithContentDescription("Descending").performClick()

        composeRule.onAllNodesWithTag(TestTags.NOTE_ITEM)[0].assertTextContains("3")
        composeRule.onAllNodesWithTag(TestTags.NOTE_ITEM)[1].assertTextContains("2")
        composeRule.onAllNodesWithTag(TestTags.NOTE_ITEM)[2].assertTextContains("1")

    }
}