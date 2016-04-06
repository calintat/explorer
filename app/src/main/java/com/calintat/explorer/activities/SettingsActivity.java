package com.calintat.explorer.activities;

import android.app.FragmentTransaction;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.calintat.explorer.R;

public class SettingsActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);

        if(toolbar!=null)
        {
            toolbar.setNavigationIcon(R.drawable.ic_back);

            toolbar.setNavigationOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    finish();
                }
            });

        }

        FragmentTransaction fragmentTransaction=getFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.content_frame,new SettingsFragment()).commit();

        setSupportActionBar(toolbar);
    }

    public static class SettingsFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences);
        }
    }
}