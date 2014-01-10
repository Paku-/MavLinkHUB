package com.paku.mavlinkhub;

import com.paku.mavlinkhub.fragments.FragmentSettings;

import android.app.Activity;
import android.os.Bundle;

public class ActivitySettings extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Display the fragment as the main content.
		getFragmentManager().beginTransaction().replace(android.R.id.content, new FragmentSettings()).commit();
	}
}