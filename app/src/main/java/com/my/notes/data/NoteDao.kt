package com.my.notes.data

import android.provider.ContactsContract
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Upsert
    suspend fun upsertNote(note: Note)

    @Delete
    suspend fun deleteNote(note: ContactsContract.CommonDataKinds.Note)

    @Query("SELECT * FROM note ORDER BY date")
    fun getNotesOrderByDate(): Flow<List<Note>>

    @Query("SELECT * FROM note ORDER BY title ASC")
    fun getNotesOrderByTitle(): Flow<List<Note>>
}