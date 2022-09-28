package com.example.noteappunittesting.feature_note.presentation.edit_note

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.noteappunittesting.R
import com.example.noteappunittesting.core.util.TestTags
import com.example.noteappunittesting.feature_note.domain.model.Note
import com.example.noteappunittesting.feature_note.presentation.edit_note.components.TransparentHintTextField
import com.example.noteappunittesting.feature_note.presentation.util.saveImageFile
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun EditNoteScreen(
    navController: NavController,
    noteColor: Int,
    viewModel: EditNoteViewModel = hiltViewModel(),

    ) {
    val titleState by viewModel.noteTitle.collectAsState()
    val contentState by viewModel.noteContent.collectAsState()
    val colorState by viewModel.noteColor.collectAsState()
    val scaffoldState = rememberScaffoldState()
    val noteBackgroundAnimatable = remember {
        Animatable(
            Color(if (noteColor != -1) noteColor else colorState)
        )
    }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val imageState by viewModel.noteImage.collectAsState()

    val launcher =
        rememberLauncherForActivityResult(contract =
        ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                viewModel.onEvent(EditNoteEvent.UpdateImage(result.data?.data.toString()))
            }
        }
    val pickIntent =
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

    LaunchedEffect(key1 = Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is EditNoteViewModel.UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
                is EditNoteViewModel.UiEvent.SaveNote -> {
                    navController.navigateUp()
                }
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        val imagePath = saveImageFile(imageState.toUri(), context)

                        viewModel.onEvent(EditNoteEvent.UpdateImage(imagePath))
                        viewModel.onEvent(EditNoteEvent.SaveNote)
                    }
                },
                backgroundColor = MaterialTheme.colors.primary,
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = "Save")
            }
        },
        scaffoldState = scaffoldState
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(noteBackgroundAnimatable.value)
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Note.noteColors.forEach { color ->
                    val colorInt = color.toArgb()
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .shadow(15.dp, CircleShape)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = 3.dp,
                                color = if (viewModel.noteColor.value == colorInt) Color.Black
                                else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable {
                                scope.launch {
                                    noteBackgroundAnimatable.animateTo(
                                        targetValue = Color(colorInt),
                                        animationSpec = tween(
                                            durationMillis = 750
                                        )
                                    )
                                }
                                viewModel.onEvent(EditNoteEvent.ChangeColor(colorInt))
                            }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            TransparentHintTextField(
                text = titleState.text,
                hint = titleState.hint,
                onValueChange = {
                    viewModel.onEvent(EditNoteEvent.EnteredTitle(it))
                },
                onFocusChange = {
                    viewModel.onEvent(EditNoteEvent.FocusTitle(it))
                },
                isHintVisible = titleState.isHintVisible,
                singleLine = true,
                testTag = TestTags.TITLE_SECTION,
                textStyle = MaterialTheme.typography.h5
            )

            Spacer(modifier = Modifier.height(16.dp))

            AsyncImage(
                model = imageState,
                contentDescription = "Note Image",
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_launcher_background),
                modifier = Modifier
                    .fillMaxHeight(0.25f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { launcher.launch(pickIntent) }
            )

            Spacer(modifier = Modifier.height(4.dp))

            TransparentHintTextField(
                text = contentState.text,
                hint = contentState.hint,
                onValueChange = {
                    viewModel.onEvent(EditNoteEvent.EnteredContent(it))
                },
                onFocusChange = {
                    viewModel.onEvent(EditNoteEvent.FocusContent(it))
                },
                isHintVisible = contentState.isHintVisible,
                textStyle = MaterialTheme.typography.body1,
                testTag = TestTags.CONTENT_SECTION,
                modifier = Modifier.fillMaxHeight(),
            )
        }
    }
}