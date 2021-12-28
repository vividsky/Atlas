package com.example.atlas;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PreferenceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
    }

    public static class UserPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        Toolbar toolbar;
        DrawerLayout mDrawerLayout;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.user_preference_main);
            Preference sortBy = findPreference(getString(R.string.sort_by));
            sortBy.setOnPreferenceChangeListener(this);
//        Preference filterBy = findPreference(getString(R.string.filter_by));
        }


        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            Toast.makeText(getContext(), "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX", Toast.LENGTH_SHORT).show();
            String stringValue = value.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex > 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                } else {
                    preference.setSummary(stringValue);
                }
            }
            return true;
        }
    }
}