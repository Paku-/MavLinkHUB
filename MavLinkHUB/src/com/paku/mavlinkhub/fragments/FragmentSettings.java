package com.paku.mavlinkhub.fragments;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.R;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;

public class FragmentSettings extends PreferenceFragment {

	protected HUBGlobals hub;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		hub = (HUBGlobals) getActivity().getApplication();

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);

		EditTextPreference logFolderPref = (EditTextPreference) findPreference("pref_log_mavlink_folder");
		logFolderPref.setSummary(hub.getExternalFilesDir(null).getAbsolutePath());

		//ListPreference pref = (ListPreference) findPreference("thePreferencesKey");		

	}

}
