// $codepro.audit.disable unnecessaryOverride
package com.paku.mavlinkhub.fragments;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;

public class HUBFragment extends Fragment {

	protected HUBGlobals hub;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		hub = (HUBGlobals) getActivity().getApplication();

		setRetainInstance(true); // keep HUBFragments in the memory
		setHasOptionsMenu(true);

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		//menu.clear();
		inflater.inflate(R.menu.main, menu);

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
	}

}
