package com.paku.mavlinkhub.messenger;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.enums.APP_STATE;
import com.paku.mavlinkhub.enums.UI_MODE;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class HUBMessenger extends HUBInterfaceMenager {

	private static final String TAG = "HUBMessenger";

	public Handler appMsgHandler;

	public HUBMessenger(HUBGlobals hubContext) {
		super(hubContext);

		appMsgHandler = new Handler(Looper.getMainLooper()) {
			public void handleMessage(Message msg) {

				APP_STATE[] appStates = APP_STATE.values();
				switch (appStates[msg.what]) {
				case MSG_SERVER_STARTED:
					app.logger.sysLog(TAG, "Server Started on port: " + msg.obj.toString());
					// no action yet
					break;
				case MSG_SERVER_STOPPED:
					app.logger.sysLog(TAG, "Server Stopped");
					// no action yet
					break;
				case MSG_SERVER_CLIENT_CONNECTED:
					String tmpTxt = new String((byte[]) msg.obj);
					app.logger.sysLog(TAG, "Client Connected: " + tmpTxt);
					// no action yet
					// Received MLmsg
					break;
				case MSG_SERVER_CLIENT_DISCONNECTED:
					app.logger.sysLog(TAG, "Client Disconnected...");
					// no action yet
					// Received MLmsg
					break;
				case MSG_QUEUE_MSGITEM_READY:
					call(APP_STATE.MSG_QUEUE_MSGITEM_READY);
					// processOnQueueMsgItemReady();
					break;
				case MSG_DATA_UPDATE_SYSLOG:
					call(APP_STATE.MSG_DATA_UPDATE_SYSLOG);
					break;
				case MSG_DATA_UPDATE_BYTELOG:
					call(APP_STATE.MSG_DATA_UPDATE_BYTELOG);
					break;
				case MSG_DATA_UPDATE_STATS:
					call(APP_STATE.MSG_DATA_UPDATE_STATS);
					break;
				case MSG_DRONE_CONNECTED:
					call(APP_STATE.MSG_DRONE_CONNECTED);
					break;
				case MSG_DRONE_CONNECTION_FAILED:
					String msgTxt = new String((byte[]) msg.obj);
					call(APP_STATE.MSG_DRONE_CONNECTION_FAILED, app.getString(R.string.connection_failure) + msgTxt);
					break;
				default:
					super.handleMessage(msg);
				}

			}

		};

		startBroadcastReceiverBluetooth();

	}

	// Bluetooth specific messages handling
	// *****************************************

	private void startBroadcastReceiverBluetooth() {

		final IntentFilter intentFilterBluetooth;
		final BroadcastReceiver broadcastReceiverBluetooth;

		intentFilterBluetooth = new IntentFilter();
		intentFilterBluetooth.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
		intentFilterBluetooth.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		intentFilterBluetooth.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
		intentFilterBluetooth.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

		// create and register BT BroadcastReceiver
		broadcastReceiverBluetooth = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

				final String action = intent.getAction();

				if (action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)) {
					final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE,
							BluetoothAdapter.ERROR);

					switch (state) {
					case BluetoothAdapter.STATE_CONNECTING:
						app.logger.sysLog(TAG, "[BT_ADAPTER_CONNECTION_STATE_CHANGED]: STATE_CONNECTING");
						break;
					case BluetoothAdapter.STATE_CONNECTED:
						app.logger.sysLog(TAG, "[BT_ADAPTER_CONNECTION_STATE_CHANGED]: STATE_CONNECTED");
						break;
					case BluetoothAdapter.STATE_DISCONNECTING:
						app.logger.sysLog(TAG, "[BT_ADAPTER_CONNECTION_STATE_CHANGED]: STATE_DISCONNECTING");
						break;
					case BluetoothAdapter.STATE_DISCONNECTED:
						app.logger.sysLog(TAG, "[BT_ADAPTER_CONNECTION_STATE_CHANGED]: STATE_DISCONNECTED");
						break;
					default:
						break;
					}

				}

				if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
					final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
					switch (state) {
					case BluetoothAdapter.STATE_OFF:
						app.logger.sysLog(TAG, "[BT_ADAPTER_STATE_CHANGED]: STATE_OFF");
						app.uiMode = UI_MODE.UI_MODE_STATE_OFF;
						break;
					case BluetoothAdapter.STATE_TURNING_OFF:
						app.logger.sysLog(TAG, "[BT_ADAPTER_STATE_CHANGED]: TURNING_OFF");
						app.uiMode = UI_MODE.UI_MODE_TURNING_OFF;
						break;
					case BluetoothAdapter.STATE_ON:
						app.logger.sysLog(TAG, "[BT_ADAPTER_STATE_CHANGED]: STATE_ON"); // 2nd
						// after
						// turning_on
						app.uiMode = UI_MODE.UI_MODE_STATE_ON;
						break;
					case BluetoothAdapter.STATE_TURNING_ON:
						app.logger.sysLog(TAG, "[BT_ADAPTER_STATE_CHANGED]: TURNING_ON"); // 1st
						// on
						// bt
						// enable
						app.uiMode = UI_MODE.UI_MODE_TURNING_ON;
						break;
					default:
						app.logger.sysLog(TAG, "[BT_ADAPTER_STATE_CHANGED]: unknown");
						break;
					}

				}

				if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
					BluetoothDevice connDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					app.logger.sysLog(TAG,
							"[BT_DEVICE_CONNECTED] " + connDevice.getName() + " [" + connDevice.getAddress() + "]");
					app.uiMode = UI_MODE.UI_MODE_CONNECTED;
				}

				if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
					BluetoothDevice connDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					app.logger.sysLog(TAG,
							"[BT_DEVICE_DISCONNECTED] " + connDevice.getName() + " [" + connDevice.getAddress() + "]");

					app.uiMode = UI_MODE.UI_MODE_DISCONNECTED;
				}

				call(APP_STATE.MSG_UI_MODE_CHANGED);

			}
		};

		// finally register this receiver for intents on BT adapter changes
		app.registerReceiver(broadcastReceiverBluetooth, intentFilterBluetooth);

	}

}
