package com.android.homework.savestatehomework.ui

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.homework.savestatehomework.R
import com.android.homework.savestatehomework.db.AppDatabase
import com.android.homework.savestatehomework.db.ContactsDao
import com.android.homework.savestatehomework.db.model.Contact

class ListFragment : Fragment() {
    companion object {
        fun newInstance() = ListFragment()
        private const val PERMISSION_REQUEST_CODE_READ_CONTACT: Int = 1
    }

    private lateinit var db: AppDatabase
    private lateinit var contactsDao: ContactsDao
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentLayout = inflater.inflate(R.layout.fragment_list, container, false)

        context?.let { initDb(it) }

        recyclerView = fragmentLayout.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        return fragmentLayout
    }

    private fun initDb(context: Context) {
        db = AppDatabase.getAppDatabase(context)
        contactsDao = db.contactsDao()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (isDbEmpty()) {
            getContactsFromPhone()
        } else {
            getContactsFromDb()
        }
    }

    private fun getContactsFromDb() {
        val listContacts = contactsDao.getContacts()
        showList(listContacts)
        showToast("Контакты из базы данных приложения")
    }

    private fun isDbEmpty() = contactsDao.getAnyContact() == null

    private fun getContactsFromPhone() {
        if (checkPermissionForContacts()) {
            showContactsFromPhone()
        } else {
            requestPermissions(
                arrayOf(android.Manifest.permission.READ_CONTACTS),
                PERMISSION_REQUEST_CODE_READ_CONTACT
            )
        }
    }

    private fun showContactsFromPhone() {
        val listContacts = readContactsFromPhone()
        showList(listContacts)
        saveListContactsInDb(listContacts)
        showToast("Контакты из телефона")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE_READ_CONTACT) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showContactsFromPhone()
            } else {
                showToast("Необходимо разрешение, чтобы прочитать контакты")
            }
        }
    }

    private fun readContactsFromPhone(): List<Contact> {
        val listContacts = mutableListOf<Contact>()
        val cursor = context?.contentResolver?.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        cursor?.let {
            cursor.use {
                while (it.moveToNext()) {
                    val id = it.getString(it.getColumnIndex(ContactsContract.Contacts._ID))
                    val fullName =
                        it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    val phone =
                        it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    val contact = Contact(id, fullName, phone)
                    listContacts.add(contact)
                }
            }
        }
        return listContacts
    }

    private fun saveListContactsInDb(contacts: List<Contact>) = contactsDao.insert(contacts)

    private fun showToast(text: String) = Toast.makeText(context, text, Toast.LENGTH_LONG).show()

    private fun checkPermissionForContacts() = (ContextCompat.checkSelfPermission(
        context!!,
        android.Manifest.permission.READ_CONTACTS
    ) == PackageManager.PERMISSION_GRANTED)

    private fun showList(list: List<Contact>) {
        val listAdapter = ListAdapter()
        recyclerView.adapter = listAdapter
        listAdapter.updateItems(list)
    }

}