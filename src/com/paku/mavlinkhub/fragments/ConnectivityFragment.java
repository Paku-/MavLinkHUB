package com.paku.mavlinkhub.fragments;

import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.communication.CommunicationHUB;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ConnectivityFragment extends Fragment {

	public ConnectivityFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View connView = inflater.inflate(R.layout.fragment_connectivity,
				container, false);

		
		final Button button = (Button) connView
				.findViewById(R.id.button_connect);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				CommunicationHUB comHUB = (CommunicationHUB) getActivity()
						.getApplication();
				comHUB.ConnectBT();
			}
		});

		return connView;
	}

}