package com.paku.mavlinkhub.fragments;

import com.paku.mavlinkhub.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentCalibration extends HUBFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_calibration, container, false);
		return rootView;
	}

}