package com.calintat.explorer.activities

import android.app.FragmentTransaction
import android.os.Bundle
import android.preference.PreferenceFragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar

import com.calintat.explorer.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        val toolbar = findViewById(R.id.toolbar) as Toolbar

        toolbar.setNavigationIcon(R.drawable.ic_back)

        toolbar.setNavigationOnClickListener { finish() }

        toolbar.setTitle(R.string.navigation_settings)

        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.fragment, SettingsFragment()).commit()

        setSupportActionBar(toolbar)
    }

    class SettingsFragment : PreferenceFragment() {

        override fun onCreate(savedInstanceState: Bundle?) {

            super.onCreate(savedInstanceState)

            addPreferencesFromResource(R.xml.preferences)
        }
    }
}