package com.paku.mavlinkhub.fragments;

import com.paku.mavlinkhub.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A dummy fragment representing a section of the app, but that simply
 * displays dummy text.
 */
public class RealTimeMavlinkFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */

	public RealTimeMavlinkFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main_dummy,
				container, false);

		return rootView;
	}
}