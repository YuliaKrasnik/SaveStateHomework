package com.android.homework.savestatehomework

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.homework.savestatehomework.ui.ListFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ListFragment.newInstance())
                .commitNow()
        }
    }
}