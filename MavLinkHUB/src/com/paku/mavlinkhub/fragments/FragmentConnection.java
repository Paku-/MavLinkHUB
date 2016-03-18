// $codepro.audit.disable unnecessaryOverride
package com.paku.mavlinkhub.fragments;

import com.paku.mavlinkhub.HUBActivityMain;
import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.enums.APP_STATE;
import com.paku.mavlinkhub.interfaces.IDroneConnected;
import com.paku.mavlinkhub.interfaces.IDroneConnectionFailed;
import com.paku.mavlinkhub.interfaces.IDroneConnectionLost;
import com.paku.mavlinkhub.interfaces.IDroneDisconnected;
import com.paku.mavlinkhub.interfaces.IServerStarted;
import com.paku.mavlinkhub.interfaces.IUiModeChanged;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FragmentConnection extends HUBFragment implements IUiModeChanged, IDroneConnectionFailed, IDroneConnected, IDroneDisconnected, IDroneConnectionLost, IServerStarted {

	@SuppressWarnings("unused")
	private static final String TAG = FragmentConnection.class.getSimpleName();

	// View progressBarConnectingBIG;

	LinearLayout droneView, gcsView;

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
		HUBGlobals.messenger.register(this, APP_STATE.MSG_DRONE_DISCONNECTED);
		HUBGlobals.messenger.register(this, APP_STATE.MSG_DRONE_CONNECTION_LOST);
		HUBGlobals.messenger.register(this, APP_STATE.MSG_SERVER_STARTED);
		//		onUiModeChanged();

	}

	@Override
	public void onPause() {
		super.onPause();

		HUBGlobals.messenger.unregister(this, APP_STATE.MSG_UI_MODE_CHANGED);
		HUBGlobals.messenger.unregister(this, APP_STATE.MSG_DRONE_CONNECTION_ATTEMPT_FAILED);
		HUBGlobals.messenger.unregister(this, APP_STATE.MSG_DRONE_CONNECTED);
		HUBGlobals.messenger.unregister(this, APP_STATE.MSG_DRONE_DISCONNECTED);
		HUBGlobals.messenger.unregister(this, APP_STATE.MSG_DRONE_CONNECTION_LOST);
		HUBGlobals.messenger.unregister(this, APP_STATE.MSG_SERVER_STARTED);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		final View connView = inflater.inflate(R.layout.fragment_connection, container, false);

		return connView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		//trick to access the included views
		droneView = (LinearLayout) view.findViewById(R.id.connection_state);
		gcsView = (LinearLayout) droneView.getChildAt(1);
		droneView = (LinearLayout) droneView.getChildAt(0);

		TextView txt = (TextView) droneView.findViewById(R.id.textView_endpoint_type);
		txt.setText("Drone");

		txt = (TextView) gcsView.findViewById(R.id.textView_endpoint_type);
		txt.setText("GCS Server");

		updateView();

	}

	// interfaces
	@Override
	public void onDroneConnectionFailed() {
		setViewDroneDisonnected();
	}

	@Override
	public void onDroneConnected() {
		setViewDroneConnected();
	}

	@Override
	public void onDroneDisconnected() {
		setViewDroneDisonnected();
	}

	@Override
	public void onDroneConnectionLost() {
		setViewDroneDisonnected();
	}

	@Override
	public void onUiModeChanged() {

		switch (hub.uiMode) {
		case UI_MODE_CREATED:
			break;
		case UI_MODE_TURNING_ON:
			break;
		case UI_MODE_STATE_ON:
			break;
		case UI_MODE_TURNING_OFF:
			break;
		case UI_MODE_STATE_OFF:
			break;
		case UI_MODE_CONNECTED:
			break;
		case UI_MODE_DISCONNECTED:
			break;
		default:
			break;
		}

	}

	private void updateView() {

		if (hub.droneClient.isConnected()) {
			setViewDroneConnected();
		}
		else {
			setViewDroneDisonnected();
		}

		setViewServerStart();

	}

	private final void setViewDroneConnected() {

		TextView txt = (TextView) droneView.findViewById(R.id.textView_endpoint_state);
		txt.setText(R.string.txt_connected);

		txt = (TextView) droneView.findViewById(R.id.textView_endpoint_interface);
		txt.setVisibility(View.VISIBLE);
		txt.setText(hub.droneClient.getMyPeerDevice().getDevInterface().toString());

		txt = (TextView) droneView.findViewById(R.id.textView_endpoint_address);
		txt.setVisibility(View.VISIBLE);
		txt.setText(hub.droneClient.getPeerAddress());

		txt = (TextView) droneView.findViewById(R.id.textView_endpoint_name);
		txt.setVisibility(View.VISIBLE);
		txt.setText(hub.droneClient.getPeerName());

		((HUBActivityMain) getActivity()).enableProgressBar(true);

	}

	private final void setViewDroneDisonnected() {

		TextView txt = (TextView) droneView.findViewById(R.id.textView_endpoint_state);
		txt.setText(R.string.txt_disconnected);

		txt = (TextView) droneView.findViewById(R.id.textView_endpoint_interface);
		txt.setVisibility(View.INVISIBLE);

		txt = (TextView) droneView.findViewById(R.id.textView_endpoint_address);
		txt.setVisibility(View.INVISIBLE);

		txt = (TextView) droneView.findViewById(R.id.textView_endpoint_name);
		txt.setVisibility(View.INVISIBLE);

		((HUBActivityMain) getActivity()).enableProgressBar(false);

	}

	private void setViewServerStart() {

		TextView txt = (TextView) gcsView.findViewById(R.id.textView_endpoint_state);

		if (hub.gcsServer.isRunning()) {
			txt.setText("Running");
		}
		else {
			txt.setText("Stopped");
		}

		txt = (TextView) gcsView.findViewById(R.id.textView_endpoint_name);
		txt.setText(hub.gcsServer.getServerMode().toString());

		txt = (TextView) gcsView.findViewById(R.id.textView_endpoint_address);
		txt.setText(hub.gcsServer.getAddress() + ":" + hub.gcsServer.getPort());

		txt = (TextView) gcsView.findViewById(R.id.textView_endpoint_interface);
		txt.setText("");

	}

	@Override
	public void onServerStarted(String adrress) {

		setViewServerStart();

	}

}