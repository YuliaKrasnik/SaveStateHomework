package com.android.homework.savestatehomework.db

import androidx.annotation.Nullable
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.android.homework.savestatehomework.db.model.Contact

@Dao
interface ContactsDao {
    @Insert
    fun insert(contacts: List<Contact>)

    @Query("SELECT * FROM contact LIMIT 1")
    @Nullable
    fun getAnyContact(): Contact?

/*    @Query("DELETE from contact")
    fun deleteAll()*/

    @Query("SELECT * FROM contact")
    fun getContacts(): List<Contact>
}