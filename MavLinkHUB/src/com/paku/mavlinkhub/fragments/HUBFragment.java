package com.paku.mavlinkhub.fragments;

import com.paku.mavlinkhub.hubapp.HUBGlobals;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class HUBFragment extends Fragment {

	protected final HUBGlobals globalVars = (HUBGlobals) getActivity().getApplication();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRetainInstance(true); // keep HUBFragments in the memory

	}

}
