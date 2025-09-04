package com.mshdabiola.main

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import com.mshdabiola.main.model.MainState
import com.mshdabiola.main.model.SelectState
import com.mshdabiola.model.note.NoteCategory
import com.mshdabiola.model.note.NoteDisplayCategory
import com.mshdabiola.model.note.NotePad
import com.mshdabiola.model.testtag.ArchiveAppBarTestTags
import com.mshdabiola.model.testtag.LabelAppBarTestTags
import com.mshdabiola.model.testtag.MainAppBarTestTags
import com.mshdabiola.model.testtag.MainScreenTestTags
import com.mshdabiola.model.testtag.ReminderAppBarTestTags
import com.mshdabiola.model.testtag.SelectAppBarTestTags
import com.mshdabiola.model.testtag.SelectTrashAppBarTestTags
import com.mshdabiola.model.testtag.TrashAppBarTestTags
import com.mshdabiola.ui.SharedTransitionContainer
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalMaterial3Api::class)
class MainScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun createNotePad(id: Long, title: String, isPinned: Boolean = false, color: Int = 0): NotePad {
        return NotePad(
            id = id,
            title = title,
            labels = emptyList(),
            isPin = isPinned,
            color = color
        )
    }
    private fun createSelectState(selectedIds: Set<Long>, isAllPin: Boolean = false) = SelectState(
        setOfSelected = selectedIds,
        isAllPin = isAllPin,
        colorIndex = -1, // Default, can be customized if needed for specific tests
        notificationUiState = null // Default
    )

    @Composable
    private fun rememberTestSearchBarState(): SearchBarState = rememberSearchBarState()

    @Test
    fun mainScreen_loadingState_displaysLoadingIndicator() {
        composeTestRule.setContent {
            SharedTransitionContainer {
                MainScreen(
                    mainState = MainState.Loading,
                    searchBarState = rememberTestSearchBarState()
                )
            }
        }
        composeTestRule.onNodeWithTag(MainScreenTestTags.MAIN_LOADING_STATE).assertIsDisplayed()
        composeTestRule.onNodeWithTag(MainScreenTestTags.MAIN_SCAFFOLD_SUCCESS).assertDoesNotExist()
    }

    @Test
    fun mainScreen_viewState_empty_displaysEmptyStateAndCorrectAppBar() {
        val emptyViewState = MainState.ViewState(
            pinNotePads = emptyList(),
            unPinNotePads = emptyList(),
            noteDisplayCategory = NoteDisplayCategory(noteCategory = NoteCategory.NOTE),
            isGrid = false,
            selectState = null
        )
        composeTestRule.setContent {
            SharedTransitionContainer {
                MainScreen(
                    mainState = emptyViewState,
                    searchBarState = rememberTestSearchBarState()
                )
            }
        }
        composeTestRule.onNodeWithTag(MainScreenTestTags.MAIN_SCAFFOLD_SUCCESS).assertIsDisplayed()
        composeTestRule.onNodeWithTag(MainAppBarTestTags.TOP_SEARCH_BAR_ROOT).assertIsDisplayed() // NOTE category default AppBar
        composeTestRule.onNodeWithTag(MainScreenTestTags.MAIN_EMPTY_STATE_VIEW).assertIsDisplayed()
        composeTestRule.onNodeWithTag(MainScreenTestTags.MAIN_NOTES_GRID).assertIsDisplayed()
    }

    @Test
    fun mainScreen_viewState_onlyUnpinnedNotes_displaysCorrectly() {
        val unpinnedNotes = listOf(createNotePad(1, "Unpinned 1"))
        val viewState = MainState.ViewState(
            pinNotePads = emptyList(),
            unPinNotePads = unpinnedNotes,
            noteDisplayCategory = NoteDisplayCategory(noteCategory = NoteCategory.NOTE),
            isGrid = false,
            selectState = null
        )
        composeTestRule.setContent {
            SharedTransitionContainer {
                MainScreen(
                    mainState = viewState,
                    searchBarState = rememberTestSearchBarState()
                )
            }
        }
        composeTestRule.onNodeWithTag(MainScreenTestTags.MAIN_SCAFFOLD_SUCCESS).assertIsDisplayed()
        composeTestRule.onNodeWithTag(MainScreenTestTags.MAIN_NOTES_GRID).assertIsDisplayed()
        composeTestRule.onNodeWithTag(MainScreenTestTags.MAIN_PINNED_SECTION_HEADER).assertDoesNotExist()
//        composeTestRule.onNodeWithTag(MainScreenTestTags.MAIN_OTHERS_SECTION_HEADER).assertDoesNotExist()
        composeTestRule.onNodeWithTag(MainScreenTestTags.MAIN_NOTE_CARD_UNPINNED_PREFIX + "1").assertIsDisplayed()
    }

    @Test
    fun mainScreen_viewState_onlyPinnedNotes_displaysCorrectly() {
        val pinnedNotes = listOf(createNotePad(1, "Pinned 1", isPinned = true))
        val viewState = MainState.ViewState(
            pinNotePads = pinnedNotes,
            unPinNotePads = emptyList(),
            noteDisplayCategory = NoteDisplayCategory(noteCategory = NoteCategory.NOTE),
            isGrid = false,
            selectState = null
        )
        composeTestRule.setContent {
            SharedTransitionContainer {
                MainScreen(
                    mainState = viewState,
                    searchBarState = rememberTestSearchBarState()
                )
            }
        }
        composeTestRule.onNodeWithTag(MainScreenTestTags.MAIN_SCAFFOLD_SUCCESS).assertIsDisplayed()
        composeTestRule.onNodeWithTag(MainScreenTestTags.MAIN_NOTES_GRID).assertIsDisplayed()
        composeTestRule.onNodeWithTag(MainScreenTestTags.MAIN_PINNED_SECTION_HEADER).assertIsDisplayed()
//        composeTestRule.onNodeWithTag(MainScreenTestTags.MAIN_OTHERS_SECTION_HEADER).assertDoesNotExist()
        composeTestRule.onNodeWithTag(MainScreenTestTags.MAIN_NOTE_CARD_PINNED_PREFIX + "1").assertIsDisplayed()
    }

    @Test
    fun mainScreen_viewState_pinnedAndUnpinnedNotes_displaysCorrectly() {
        val pinnedNotes = listOf(createNotePad(1, "Pinned 1", isPinned = true))
        val unpinnedNotes = listOf(createNotePad(2, "Unpinned 2"))
        val viewState = MainState.ViewState(
            pinNotePads = pinnedNotes,
            unPinNotePads = unpinnedNotes,
            noteDisplayCategory = NoteDisplayCategory(noteCategory = NoteCategory.NOTE),
            isGrid = false,
            selectState = null
        )
        composeTestRule.setContent {
            SharedTransitionContainer {
                MainScreen(
                    mainState = viewState,
                    searchBarState = rememberTestSearchBarState()
                )
            }
        }
        composeTestRule.onNodeWithTag(MainScreenTestTags.MAIN_SCAFFOLD_SUCCESS).assertIsDisplayed()
        composeTestRule.onNodeWithTag(MainScreenTestTags.MAIN_NOTES_GRID).assertIsDisplayed()
        composeTestRule.onNodeWithTag(MainScreenTestTags.MAIN_PINNED_SECTION_HEADER).assertIsDisplayed()
        composeTestRule.onNodeWithTag(MainScreenTestTags.MAIN_NOTE_CARD_PINNED_PREFIX + "1").assertIsDisplayed()
//        composeTestRule.onNodeWithTag(MainScreenTestTags.MAIN_OTHERS_SECTION_HEADER).assertIsDisplayed()
        composeTestRule.onNodeWithTag(MainScreenTestTags.MAIN_NOTE_CARD_UNPINNED_PREFIX + "2").assertIsDisplayed()
    }

    // --- Tests for different AppBars in Normal Mode ---

    @Test
    fun mainScreen_viewState_noteCategoryNOTE_showsMainAppBar() {
        val viewState = MainState.ViewState(
            noteDisplayCategory = NoteDisplayCategory(noteCategory = NoteCategory.NOTE),
        )
        composeTestRule.setContent {
            SharedTransitionContainer {
                MainScreen(mainState = viewState, searchBarState = rememberTestSearchBarState())
            }
        }
        composeTestRule.onNodeWithTag(MainAppBarTestTags.TOP_SEARCH_BAR_ROOT).assertIsDisplayed()
    }

    @Test
    fun mainScreen_viewState_noteCategoryREMINDER_showsReminderAppBar() {
        val viewState = MainState.ViewState(
            noteDisplayCategory = NoteDisplayCategory(noteCategory = NoteCategory.REMINDER),
        )
        composeTestRule.setContent {
            SharedTransitionContainer {
                MainScreen(mainState = viewState, searchBarState = rememberTestSearchBarState())
            }
        }
        composeTestRule.onNodeWithTag(ReminderAppBarTestTags.APP_BAR_ROOT).assertIsDisplayed()
    }

    @Test
    fun mainScreen_viewState_noteCategoryLABEL_showsLabelAppBar() {
        val labelName = "Test Label"
        val viewState = MainState.ViewState(
            noteDisplayCategory = NoteDisplayCategory(1,noteCategory = NoteCategory.LABEL),
            labelName = labelName
        )
        composeTestRule.setContent {
            SharedTransitionContainer {
                MainScreen(mainState = viewState, searchBarState = rememberTestSearchBarState())
            }
        }
        composeTestRule.onNodeWithTag(LabelAppBarTestTags.APP_BAR_ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithText(labelName).assertIsDisplayed() // Check label name in app bar
    }

    @Test
    fun mainScreen_viewState_noteCategoryTRASH_showsTrashAppBar() {
        val viewState = MainState.ViewState(
            noteDisplayCategory = NoteDisplayCategory(noteCategory = NoteCategory.TRASH),
        )
        composeTestRule.setContent {
            SharedTransitionContainer {
                MainScreen(mainState = viewState, searchBarState = rememberTestSearchBarState())
            }
        }
        composeTestRule.onNodeWithTag(TrashAppBarTestTags.ROOT_APP_BAR).assertIsDisplayed()
    }

    @Test
    fun mainScreen_viewState_noteCategoryARCHIVE_showsArchiveAppBar() {
        val viewState = MainState.ViewState(
            noteDisplayCategory = NoteDisplayCategory(noteCategory = NoteCategory.ARCHIVE),
        )
        composeTestRule.setContent {
            SharedTransitionContainer {
                MainScreen(mainState = viewState, searchBarState = rememberTestSearchBarState())
            }
        }
        composeTestRule.onNodeWithTag(ArchiveAppBarTestTags.SCREEN_ROOT).assertIsDisplayed()
    }

    // --- Tests for Selection Mode AppBars ---
    @Test
    fun mainScreen_viewState_selectionMode_noteCategoryNOTE_showsSelectAppBar() {
        val selectState = createSelectState(setOf(1L))
        val viewState = MainState.ViewState(
            noteDisplayCategory = NoteDisplayCategory(noteCategory = NoteCategory.NOTE),
            selectState = selectState,
            unPinNotePads = listOf(createNotePad(1L, "Note 1")) // Need at least one note for selection to be meaningful
        )
        composeTestRule.setContent {
            SharedTransitionContainer {
                MainScreen(mainState = viewState, searchBarState = rememberTestSearchBarState())
            }
        }
        composeTestRule.onNodeWithTag(SelectAppBarTestTags.ROOT_APP_BAR).assertIsDisplayed()
        composeTestRule.onNodeWithText(selectState.setOfSelected.size.toString()).assertIsDisplayed() // Title check
    }

    @Test
    fun mainScreen_viewState_selectionMode_noteCategoryTRASH_showsSelectTrashAppBar() {
        val selectState = createSelectState(setOf(1L))
        val viewState = MainState.ViewState(
            noteDisplayCategory = NoteDisplayCategory(noteCategory = NoteCategory.TRASH),
            selectState = selectState,
            unPinNotePads = listOf(createNotePad(1L, "Trashed Note 1"))
        )
        composeTestRule.setContent {
            SharedTransitionContainer {
                MainScreen(mainState = viewState, searchBarState = rememberTestSearchBarState())
            }
        }
        composeTestRule.onNodeWithTag(SelectTrashAppBarTestTags.ROOT_APP_BAR).assertIsDisplayed()
        composeTestRule.onNodeWithText(selectState.setOfSelected.size.toString()).assertIsDisplayed()
    }

    // --- Tests for Note Interactions ---
    @Test
    fun mainScreen_noteClick_normalMode_callsNavigateToNoteEditor() {
        var navigatedNoteId: Long? = null
        var navigatedColorIndex: Int? = null
        var navigatedBackground: Int? = null
        val note = createNotePad(1L, "Clickable Note", color = 123)
        val viewState = MainState.ViewState(
            unPinNotePads = listOf(note),
            noteDisplayCategory = NoteDisplayCategory(noteCategory = NoteCategory.NOTE),
            selectState = null // Normal mode
        )

        composeTestRule.setContent {
            SharedTransitionContainer {
                MainScreen(
                    mainState = viewState,
                    searchBarState = rememberTestSearchBarState(),
                    navigateToNoteEditor = { id, color, bg ->
                        navigatedNoteId = id
                        navigatedColorIndex = color
                        navigatedBackground = bg
                    }
                )
            }
        }
        composeTestRule.onNodeWithTag(MainScreenTestTags.MAIN_NOTE_CARD_UNPINNED_PREFIX + note.id).performClick()
        assertEquals(note.id, navigatedNoteId)
        assertEquals(note.color, navigatedColorIndex)
        // Background is derived from color in NoteCard, assuming it's the same for this test
        // If NoteCard transforms color to background, this check might need adjustment or be more lenient.
        // For now, let's assume background passed is same as color for simplicity of MainScreen -> NoteCard contract.
        assertEquals(note.color, navigatedBackground)

    }

    @Test
    fun mainScreen_noteLongClick_normalMode_callsOnNoteSelected() {
        var selectedNoteId: Long? = null
        val note = createNotePad(1L, "Selectable Note")
        val viewState = MainState.ViewState(
            unPinNotePads = listOf(note),
            noteDisplayCategory = NoteDisplayCategory(noteCategory = NoteCategory.NOTE),
            selectState = null // Normal mode, long click initiates selection
        )

        composeTestRule.setContent {
            SharedTransitionContainer {
                MainScreen(
                    mainState = viewState,
                    searchBarState = rememberTestSearchBarState(),
                    onNoteSelected = { id -> selectedNoteId = id }
                )
            }
        }
        composeTestRule.onNodeWithTag(MainScreenTestTags.MAIN_NOTE_CARD_UNPINNED_PREFIX + note.id)
            .performTouchInput {
                longClick()
            }
        assertEquals(note.id, selectedNoteId)
    }

    @Test
    fun mainScreen_noteClick_selectionMode_callsOnNoteSelected() {
        var selectedNoteId: Long? = null
        val note = createNotePad(1L, "Selectable Note in Selection Mode")
        val initialSelectState = createSelectState(setOf(2L)) // Another note is already selected
        val viewState = MainState.ViewState(
            unPinNotePads = listOf(note, createNotePad(2L, "Other note")),
            noteDisplayCategory = NoteDisplayCategory(noteCategory = NoteCategory.NOTE),
            selectState = initialSelectState // Selection mode active
        )

        composeTestRule.setContent {
            SharedTransitionContainer {
                MainScreen(
                    mainState = viewState,
                    searchBarState = rememberTestSearchBarState(),
                    onNoteSelected = { id -> selectedNoteId = id }
                )
            }
        }
        composeTestRule.onNodeWithTag(MainScreenTestTags.MAIN_NOTE_CARD_UNPINNED_PREFIX + note.id).performClick()
        assertEquals(note.id, selectedNoteId)
    }
}
