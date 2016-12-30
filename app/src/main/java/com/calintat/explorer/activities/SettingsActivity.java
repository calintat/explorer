package com.calintat.explorer.activities;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.calintat.explorer.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {

            toolbar.setNavigationIcon(R.drawable.ic_back);

            toolbar.setNavigationOnClickListener(v -> finish());

            toolbar.setTitle(R.string.navigation_settings);
        }

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.fragment, new SettingsFragment()).commit();

        setSupportActionBar(toolbar);
    }

    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences);
        }
    }
}