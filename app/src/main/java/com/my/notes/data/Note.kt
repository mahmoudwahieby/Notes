package com.my.notes.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(

    val title: String,
    val description: String,
    val date: Long,

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

)
