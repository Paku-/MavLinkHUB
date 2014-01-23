package com.paku.mavlinkhub.fragments;

import com.paku.mavlinkhub.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentDummySection extends HUBFragment {

	public static final String TAG = FragmentDummySection.class.getSimpleName();

	public FragmentDummySection() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_main_dummy, container, false);
		return rootView;
	}
}