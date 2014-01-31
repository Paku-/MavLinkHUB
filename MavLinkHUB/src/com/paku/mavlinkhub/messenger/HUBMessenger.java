// $codepro.audit.disable
// com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.alwaysOverridetoString.alwaysOverrideToString
package com.paku.mavlinkhub.messenger;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.enums.APP_STATE;
import com.paku.mavlinkhub.enums.UI_MODE;
import com.paku.mavlinkhub.queue.items.ItemMavLinkMsg;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class HUBMessenger extends HUBInterfaceMenager {

	private static final String TAG = HUBMessenger.class.getSimpleName();
	//private static final String TAG = "SYSTEM";

	public Handler appMsgHandler;

	public HUBMessenger(HUBGlobals hubContext) {
		super(hubContext);

		appMsgHandler = new Handler(Looper.getMainLooper()) {
			public void handleMessage(Message msg) {

				APP_STATE[] appStates = APP_STATE.values();

				//				Log.d(TAG, "** msg : " + appStates[msg.what].name());

				// /Log.d(TAG, appStates[msg.what].toString());

				switch (appStates[msg.what]) {
				case MSG_SERVER_STARTED:
					HUBGlobals.logger.sysLog(TAG, "Server Start.. [" + msg.obj + "]");
					callFragments(APP_STATE.MSG_SERVER_STARTED, (String) msg.obj);
					// no action yet
					break;
				case MSG_SERVER_START_FAILED:
					HUBGlobals.logger.sysLog(TAG, "Server Start Failed !!! ");
					Toast.makeText(hub, R.string.error_server_start_failed, Toast.LENGTH_SHORT).show();
					// no action yet
					break;
				case MSG_SERVER_STOPPED:
					HUBGlobals.logger.sysLog(TAG, "Server ..Stop");
					callFragments(APP_STATE.MSG_SERVER_STOPPED);
					// no action yet
					break;
				case MSG_SERVER_GCS_CONNECTED:
					String tmpTxt2 = new String((byte[]) msg.obj);
					HUBGlobals.logger.sysLog(TAG, "GCS Connected: " + tmpTxt2);
					break;
				case MSG_SERVER_GCS_DISCONNECTED:
					HUBGlobals.logger.sysLog(TAG, "GCS Disconnected..");
					break;
				case MSG_QUEUE_MSGITEM_READY:
					callFragments(APP_STATE.MSG_QUEUE_MSGITEM_READY, (ItemMavLinkMsg) msg.obj);
					break;
				case MSG_DATA_UPDATE_SYSLOG:
					callFragments(APP_STATE.MSG_DATA_UPDATE_SYSLOG);
					break;
				case MSG_DATA_UPDATE_BYTELOG:
					callFragments(APP_STATE.MSG_DATA_UPDATE_BYTELOG);
					break;
				case MSG_DATA_UPDATE_STATS:
					callFragments(APP_STATE.MSG_DATA_UPDATE_STATS);
					break;
				case MSG_DRONE_CONNECTED:
					HUBGlobals.logger.sysLog(TAG, "Drone connected.");
					callFragments(APP_STATE.MSG_DRONE_CONNECTED);
					break;
				case MSG_DRONE_DISCONNECTED:
					HUBGlobals.logger.sysLog(TAG, "Drone disconnected.");
					callFragments(APP_STATE.MSG_DRONE_DISCONNECTED);
					break;
				case MSG_DRONE_CONNECTION_ATTEMPT_FAILED:

					String msgTxt = hub.getString(R.string.error_connection_failure) + new String((byte[]) msg.obj);

					HUBGlobals.logger.sysLog(TAG, msgTxt);
					Toast.makeText(hub, msgTxt, Toast.LENGTH_SHORT).show();

					hub.droneClient.stopClient();

					callFragments(APP_STATE.MSG_DRONE_CONNECTION_ATTEMPT_FAILED);

					break;
				case MSG_DRONE_CONNECTION_LOST:

					HUBGlobals.logger.sysLog(TAG, hub.getString(R.string.error_drone_connection_lost));
					Toast.makeText(hub, R.string.error_drone_connection_lost, Toast.LENGTH_SHORT).show();

					hub.droneClient.stopClient();

					callFragments(APP_STATE.MSG_DRONE_CONNECTION_LOST);

					break;
				default:
					super.handleMessage(msg);
				}

			}

		};

		startBroadcastReceiverBluetooth();
		startBroadcastReceiverUSB();

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
					final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, BluetoothAdapter.ERROR);

					switch (state) {
					case BluetoothAdapter.STATE_CONNECTING:
						HUBGlobals.logger.sysLog(TAG, "[BT_ADAPTER_CONNECTION_STATE_CHANGED]: STATE_CONNECTING");
						break;
					case BluetoothAdapter.STATE_CONNECTED:
						HUBGlobals.logger.sysLog(TAG, "[BT_ADAPTER_CONNECTION_STATE_CHANGED]: STATE_CONNECTED");
						break;
					case BluetoothAdapter.STATE_DISCONNECTING:
						HUBGlobals.logger.sysLog(TAG, "[BT_ADAPTER_CONNECTION_STATE_CHANGED]: STATE_DISCONNECTING");
						break;
					case BluetoothAdapter.STATE_DISCONNECTED:
						HUBGlobals.logger.sysLog(TAG, "[BT_ADAPTER_CONNECTION_STATE_CHANGED]: STATE_DISCONNECTED");
						break;
					default:
						break;
					}

				}

				if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
					final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
					switch (state) {
					case BluetoothAdapter.STATE_OFF:
						HUBGlobals.logger.sysLog(TAG, "[BT_ADAPTER_STATE_CHANGED]: STATE_OFF");
						hub.uiMode = UI_MODE.UI_MODE_STATE_OFF;
						break;
					case BluetoothAdapter.STATE_TURNING_OFF:
						HUBGlobals.logger.sysLog(TAG, "[BT_ADAPTER_STATE_CHANGED]: TURNING_OFF");
						hub.uiMode = UI_MODE.UI_MODE_TURNING_OFF;
						break;
					case BluetoothAdapter.STATE_ON:
						HUBGlobals.logger.sysLog(TAG, "[BT_ADAPTER_STATE_CHANGED]: STATE_ON"); // 2nd
						// after
						// turning_on
						hub.uiMode = UI_MODE.UI_MODE_STATE_ON;
						break;
					case BluetoothAdapter.STATE_TURNING_ON:
						HUBGlobals.logger.sysLog(TAG, "[BT_ADAPTER_STATE_CHANGED]: TURNING_ON"); // 1st
						// on
						// bt
						// enable
						hub.uiMode = UI_MODE.UI_MODE_TURNING_ON;
						break;
					default:
						HUBGlobals.logger.sysLog(TAG, "[BT_ADAPTER_STATE_CHANGED]: unknown");
						break;
					}

				}

				if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
					BluetoothDevice connDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					HUBGlobals.logger.sysLog(TAG, "[BT_DEVICE_CONNECTED] " + connDevice.getName() + " [" + connDevice.getAddress() + "]");
					HUBGlobals.sendAppMsg(APP_STATE.MSG_DRONE_CONNECTED);
					hub.uiMode = UI_MODE.UI_MODE_CONNECTED;
				}

				if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
					BluetoothDevice connDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					HUBGlobals.logger.sysLog(TAG, "[BT_DEVICE_DISCONNECTED] " + connDevice.getName() + " [" + connDevice.getAddress() + "]");
					HUBGlobals.sendAppMsg(APP_STATE.MSG_DRONE_DISCONNECTED);
					hub.uiMode = UI_MODE.UI_MODE_DISCONNECTED;
				}

				// no special UI_MODE msg anymore
				//callFragments(APP_STATE.MSG_UI_MODE_CHANGED);

			}
		};

		// finally register this receiver for intents on BT adapter changes
		hub.registerReceiver(broadcastReceiverBluetooth, intentFilterBluetooth);

	}

	private void startBroadcastReceiverUSB() {

		final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
		final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (ACTION_USB_PERMISSION.equals(action)) {
					synchronized (this) {
						UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

						if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
							if (null != device) {
								// call method to set up device communication
							}
						}
						else {
							Log.d(TAG, "permission denied for device " + device);
						}
					}
				}
			}
		};

		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		hub.registerReceiver(mUsbReceiver, filter);

	}

}
