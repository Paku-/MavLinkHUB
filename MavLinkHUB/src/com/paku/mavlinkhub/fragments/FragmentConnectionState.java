// $codepro.audit.disable unnecessaryOverride
package com.paku.mavlinkhub.fragments;

import com.paku.mavlinkhub.HUBActivityMain;
import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.enums.APP_STATE;
import com.paku.mavlinkhub.interfaces.IDroneConnected;
import com.paku.mavlinkhub.interfaces.IDroneConnectionFailed;
import com.paku.mavlinkhub.interfaces.IDroneConnectionLost;
import com.paku.mavlinkhub.interfaces.IUiModeChanged;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentConnectionState extends HUBFragment implements IUiModeChanged, IDroneConnectionFailed, IDroneConnected, IDroneConnectionLost {

	@SuppressWarnings("unused")
	private static final String TAG = FragmentConnectionState.class.getSimpleName();

	// View progressBarConnectingBIG;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public void onResume() {
		super.onResume();
		HUBGlobals.messenger.register(this, APP_STATE.MSG_UI_MODE_CHANGED);
		HUBGlobals.messenger.register(this, APP_STATE.MSG_DRONE_CONNECTION_ATTEMPT_FAILED);
		HUBGlobals.messenger.register(this, APP_STATE.MSG_DRONE_CONNECTED);
		HUBGlobals.messenger.register(this, APP_STATE.MSG_DRONE_CONNECTION_LOST);
		onUiModeChanged();

	}

	@Override
	public void onPause() {
		super.onPause();

		HUBGlobals.messenger.unregister(this, APP_STATE.MSG_UI_MODE_CHANGED);
		HUBGlobals.messenger.unregister(this, APP_STATE.MSG_DRONE_CONNECTION_ATTEMPT_FAILED);
		HUBGlobals.messenger.unregister(this, APP_STATE.MSG_DRONE_CONNECTED);
		HUBGlobals.messenger.unregister(this, APP_STATE.MSG_DRONE_CONNECTION_LOST);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		final View connView = inflater.inflate(R.layout.fragment_connection_state, container, false);

		return connView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// progressBarConnectingBIG =
		// getView().findViewById(R.id.RelativeLayoutProgressBarBig);
		onUiModeChanged();

	}

	// interfaces
	@Override
	public void onDroneConnectionFailed() {
	}

	@Override
	public void onDroneConnected() {
	}

	@Override
	public void onDroneConnectionLost() {
	}

	@Override
	public void onUiModeChanged() {

		// mostly used states set as defaults
		((HUBActivityMain) getActivity()).enableProgressBar(false);
		// progressBarConnectingBIG.setVisibility(View.INVISIBLE);

		switch (hub.uiMode) {
		case UI_MODE_CREATED:
			break;
		case UI_MODE_TURNING_ON:
			// progressBarConnectingBIG.setVisibility(View.VISIBLE);
			break;
		case UI_MODE_STATE_ON:
			break;
		case UI_MODE_TURNING_OFF:
			// progressBarConnectingBIG.setVisibility(View.VISIBLE);
			break;
		case UI_MODE_STATE_OFF:
			break;
		case UI_MODE_CONNECTED:
			((HUBActivityMain) getActivity()).enableProgressBar(true);
			break;
		case UI_MODE_DISCONNECTED:
			break;
		default:
			break;
		}

	}

}