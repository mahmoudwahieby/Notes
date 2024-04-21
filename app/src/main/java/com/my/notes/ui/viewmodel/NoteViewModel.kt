package com.my.notes.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.my.notes.data.Note
import com.my.notes.data.NoteDao
import com.my.notes.ui.NoteState
import com.my.notes.ui.NotesEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteViewModel(
    private val dao: NoteDao
): ViewModel() {
    private val isSortByDate = MutableStateFlow(true)

    @OptIn(ExperimentalCoroutinesApi::class)
    private var notes = isSortByDate.flatMapLatest { sort ->
        if (sort) {
            dao.getNotesOrderByDate()
        } else {
            dao.getNotesOrderByTitle()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val _state = MutableStateFlow(NoteState())
    val state = combine(_state, isSortByDate, notes) { state, isSortByDate, notes ->
        state.copy(
            notes = notes
        )
    }.stateIn(viewModelScope, SharingStarted. WhileSubscribed(5000), NoteState())

    fun onEvent(event: NotesEvent) {
        when (event) {
            is NotesEvent.DeleteNote -> {
                viewModelScope.launch {
                    dao.deleteNote(event.note)
                }
            }
            is NotesEvent.SaveNote -> {
                val note = Note(
                    title = state.value.title.value,
                    description = state.value.description.value,
                    date = System.currentTimeMillis()
                )

                viewModelScope.launch {
                    dao.upsertNote(note)
                }

                _state.update {
                    it.copy(
                        title = mutableStateOf(""),
                        description = mutableStateOf("")
                    )
                }
            }
            NotesEvent.SortNotes -> {
                isSortByDate.value = !isSortByDate.value
            }
        }
    }
}