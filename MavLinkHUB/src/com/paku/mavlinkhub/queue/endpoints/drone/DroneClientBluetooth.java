// $codepro.audit.disable
// com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.alwaysOverridetoString.alwaysOverrideToString
package com.paku.mavlinkhub.queue.endpoints.drone;

import java.io.IOException;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.enums.DEVICE_INTERFACE;
import com.paku.mavlinkhub.queue.endpoints.DroneClient;
import com.paku.mavlinkhub.utils.ThreadReaderSocketBased;
import com.paku.mavlinkhub.viewadapters.devicelist.ItemPeerDevice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class DroneClientBluetooth extends DroneClient {

	private static final String TAG = DroneClientBluetooth.class.getSimpleName();
	private static final int SIZEBUFF = 1024;

	private final BluetoothAdapter mBluetoothAdapter;
	private BluetoothDevice mBluetoothDevice;
	private BluetoothSocket mBluetoothSocket;

	private DroneClientBluetoothConnThread droneConnectingBluetoothThread;
	private ThreadReaderSocketBased readerThreadBT;

	public DroneClientBluetooth(HUBGlobals hub) {
		super(hub, SIZEBUFF);
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	}

	@Override
	public void startClient(ItemPeerDevice drone) {

		if (drone.getDevInterface() == DEVICE_INTERFACE.Bluetooth) {

			// start connection threat
			if (null == mBluetoothAdapter) {
				return;
			}

			mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(drone.getAddress());
			// create and start BT specific connection thread
			droneConnectingBluetoothThread = new DroneClientBluetoothConnThread(this, mBluetoothAdapter, mBluetoothDevice);
			droneConnectingBluetoothThread.start();
		}

		return;

	}

	public void startClientReaderThread(BluetoothSocket socket) {

		mBluetoothSocket = socket;

		// start receiver thread
		readerThreadBT = new ThreadReaderSocketBased(mBluetoothSocket, ConnMsgHandler);
		readerThreadBT.start();
	}

	@Override
	public void stopClient() {
		Log.d(TAG, "Closing connection..");

		stopMsgHandler();

		// stop socket thread
		if (isConnected()) {
			readerThreadBT.stopMe();
		}
	}

	@Override
	public boolean isConnected() {
		if (null == readerThreadBT) {
			return false;
		}
		else {
			return readerThreadBT.isRunning();
		}

	}

	@Override
	public String getPeerName() {
		return (isConnected()) ? mBluetoothSocket.getRemoteDevice().getName() : "";

	}

	@Override
	public String getPeerAddress() {
		return (isConnected()) ? mBluetoothSocket.getRemoteDevice().getAddress() : "";

	}

	public BluetoothAdapter getBluetoothAdapter() {
		return mBluetoothAdapter;
	}

	@Override
	public String getMyName() {
		return mBluetoothAdapter.getName();
	}

	@Override
	public String getMyAddress() {
		return mBluetoothAdapter.getAddress();
	}

	@Override
	public boolean writeBytes(byte[] bytes) throws IOException {

		if (isConnected()) {
			readerThreadBT.writeBytes(bytes);
			return true;
		}
		return false;
	}

}
