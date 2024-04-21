package com.my.notes.ui

import android.provider.ContactsContract.CommonDataKinds.Note

sealed interface NotesEvent {
    object SortNotes: NotesEvent

    data class DeleteNote(val note: Note): NotesEvent

    data class SaveNote(val title: String, val description: String): NotesEvent
}