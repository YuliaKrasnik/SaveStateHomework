package com.android.homework.savestatehomework.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.android.homework.savestatehomework.db.model.Contact
import io.reactivex.Single

@Dao
interface ContactsDao {
    @Insert
    fun insert(contacts: List<Contact>)

    @Query("SELECT * FROM contact")
    fun getContacts(): Single<List<Contact>>
}