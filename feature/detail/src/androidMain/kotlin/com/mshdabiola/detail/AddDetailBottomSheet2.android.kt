package com.mshdabiola.detail

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.mshdabiola.designsystem.drawable.SynIcons
import com.mshdabiola.model.AppConstant
import com.mshdabiola.model.NoteBg
import org.jetbrains.compose.resources.stringResource
import synapse.feature.detail.generated.resources.Res
import synapse.feature.detail.generated.resources.modules_designsystem_add_image
import synapse.feature.detail.generated.resources.modules_designsystem_checkboxes
import synapse.feature.detail.generated.resources.modules_designsystem_drawing
import synapse.feature.detail.generated.resources.modules_designsystem_recording
import synapse.feature.detail.generated.resources.modules_designsystem_take_photo

@OptIn(markerClass = [androidx.compose.material3.ExperimentalMaterial3Api::class])
@androidx.compose.runtime.Composable
actual fun AddBottomSheet2(
    currentColor: Int,
    currentImage: Int,
    isNoteCheck: Boolean,
    saveImage: (String) -> Unit,
    saveVoice: (String, String) -> Unit,
    getPhotoUri: () -> String,
    changeToCheckBoxes: () -> Unit,
    onDrawing: () -> Unit,
    onDismiss: () -> Unit,
    show: Boolean,
    isVoiceSupport: Boolean,
)  {
    val background = if (currentImage != -1) {
        Color( NoteBg.noteBgs [currentImage].fgColor)
    } else {
        if (currentColor != -1) {
            Color(AppConstant.noteColors[currentColor])
        } else {
            MaterialTheme.colorScheme.surface
        }
    }

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            it?.let {
                saveImage(it.toString())
            }
        },
    )
    val snapPictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = {
            if (it) {
                saveImage(getPhotoUri())
                // navigateToEdit(-3, "image text", photoId)
            }
        },
    )

    val voiceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            it.data?.let { intent ->
                val strArr = intent.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val audiouri = intent.data

                if (audiouri != null) {
                    saveVoice(audiouri.toString(), strArr?.joinToString() ?: "")
                }
            }
        },
    )

    val audioPermission =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = {
                if (it) {
                    voiceLauncher.launch(
                        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(
                                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,
                            )
                            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speck Now Now")
                            putExtra("android.speech.extra.GET_AUDIO_FORMAT", "audio/AMR")
                            putExtra("android.speech.extra.GET_AUDIO", true)
                        },
                    )
                }
            },
        )

    val context = LocalContext.current

    if (show) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            containerColor = background,

            ) {
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = SynIcons.PhotoCamera,
                        contentDescription = "",
                    )
                },
                label = { Text(text = stringResource(Res.string.modules_designsystem_take_photo)) },
                selected = false,
                onClick = {
                    snapPictureLauncher.launch(Uri.parse(getPhotoUri()))
                    onDismiss()
                },
                colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = background),
                modifier = androidx.compose.ui.Modifier.testTag("detail:take_photo"),
            )

            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = SynIcons.Image,
                        contentDescription = "",
                    )
                },
                label = { Text(text = stringResource(Res.string.modules_designsystem_add_image)) },
                selected = false,
                onClick = {
                    imageLauncher.launch(
                        PickVisualMediaRequest(
                            mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly,
                        ),
                    )
                    onDismiss()
                },
                colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = background),
                modifier = androidx.compose.ui.Modifier.testTag("detail:add_image"),
            )
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = SynIcons.Brush,
                        contentDescription = "",
                    )
                },
                label = { Text(text = stringResource(Res.string.modules_designsystem_drawing)) },
                selected = false,
                onClick = {
                    onDismiss()
                    onDrawing()
                },
                colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = background),
                modifier = androidx.compose.ui.Modifier.testTag("detail:drawing"),

                )
            if (isVoiceSupport) {
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = SynIcons.KeyboardVoice,
                            contentDescription = "",
                        )
                    },
                    label = { Text(text = stringResource(Res.string.modules_designsystem_recording)) },
                    selected = false,
                    onClick = {
                        onDismiss()
                        if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) ==
                            PackageManager.PERMISSION_GRANTED
                        ) {
                            voiceLauncher.launch(
                                Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                    putExtra(
                                        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,
                                    )
                                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Speck Now Now")
                                    putExtra(
                                        "android.speech.extra.GET_AUDIO_FORMAT",
                                        "audio/AMR",
                                    )
                                    putExtra("android.speech.extra.GET_AUDIO", true)
                                },
                            )
                        } else {
                            audioPermission.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = background),
                    modifier = androidx.compose.ui.Modifier.testTag("detail:recording"),
                )
            }
            if (!isNoteCheck) {
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = SynIcons.CheckBox,
                            contentDescription = "",
                        )
                    },
                    label = { Text(text = stringResource(Res.string.modules_designsystem_checkboxes)) },
                    selected = false,
                    onClick = {
                        onDismiss()
                        changeToCheckBoxes()
                    },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = background),
                    modifier = androidx.compose.ui.Modifier.testTag("detail:checkboxes"),
                )
            }
        }
    }
}
