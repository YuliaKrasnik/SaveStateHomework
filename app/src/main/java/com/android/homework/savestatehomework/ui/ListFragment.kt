package com.android.homework.savestatehomework.ui

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
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
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class ListFragment : Fragment() {
    companion object {
        fun newInstance() = ListFragment()
        private const val PERMISSION_REQUEST_CODE_READ_CONTACT: Int = 1
        private const val TAG_DB = "tag_db"
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
        getContacts()
    }

    private fun getContacts() {
        contactsDao.getContacts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<List<Contact>>() {
                    override fun onSuccess(listContacts: List<Contact>) {
                        if (listContacts.isNotEmpty()) {
                            showList(listContacts)
                            showToast("Контакты из базы данных приложения")
                        } else {
                            getContactsFromPhone()
                        }
                    }

                    override fun onError(e: Throwable) {
                        e.message?.let { Log.e(TAG_DB, it) }
                    }
                })
    }

    private fun saveListContactsInDb(list: List<Contact>) =
            Completable.fromAction { contactsDao.insert(list) }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe()

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