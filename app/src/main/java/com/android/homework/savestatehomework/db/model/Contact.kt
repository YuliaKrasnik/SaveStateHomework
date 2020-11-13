package com.android.homework.savestatehomework.db.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Contact(@PrimaryKey @NonNull val id: String, val name: String, val phone: String)