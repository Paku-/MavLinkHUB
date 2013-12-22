package com.paku.mavlinkhub.fragments;

import com.paku.mavlinkhub.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A dummy fragment representing a section of the app, but that simply
 * displays dummy text.
 */
public class ConnectivityFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */

	public ConnectivityFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_connectivity,
				container, false);
		return rootView;
	}
}