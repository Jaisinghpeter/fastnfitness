package com.easyfitness;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragmentCompat {

    Toolbar top_toolbar = null;
    MainActivity mActivity = null;

    public final static String WEIGHT_UNIT_PARAM =  "defaultUnit";
    public final static String DISTANCE_UNIT_PARAM =  "defaultDistanceUnit";

    /**
     * Create a new instance of DetailsFragment, initialized to
     * show the text at 'index'.
     */
    public static SettingsFragment newInstance(String name, int id) {
        SettingsFragment f = new SettingsFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String param) {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_about, container, false);
        return view;
    }

    private void updateSummary(ListPreference pref, String val, String prefix) {
        int prefIndex = pref.findIndexOfValue(val);
        if (prefIndex >= 0) {
            //finally set's it value changed
            pref.setSummary(prefix + pref.getEntries()[prefIndex]);
        }
    }
}
