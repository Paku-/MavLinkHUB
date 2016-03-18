// $codepro.audit.disable
// com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.alwaysOverridetoString.alwaysOverrideToString
package com.paku.mavlinkhub;

import com.paku.mavlinkhub.enums.MSG_SOURCE;
import com.paku.mavlinkhub.enums.SCREEN_SIZE;
import com.paku.mavlinkhub.utils.Utils;
import com.paku.mavlinkhub.fragments.HUBFragmentsAdapter;
import com.paku.mavlinkhub.fragments.dialogs.FragmentDialogSelectDevice;
import com.paku.mavlinkhub.interfaces.IDataUpdateStats;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

// public class HUBActivityMain extends ActionBarActivity implements IDataUpdateStats {
public class HUBActivityMain extends FragmentActivity implements IDataUpdateStats {

	private static final String TAG = HUBActivityMain.class.getSimpleName();

	// we are a Fragment activity
	public HUBFragmentsAdapter fragmentsAdapter;
	public ViewPager fragmentsViewPager;

	public HUBGlobals hub;

	private ProgressBar progressBarDroneConnected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);

		requestWindowFeature(Window.FEATURE_ACTION_BAR);

		setContentView(R.layout.activity_main);

		hub = (HUBGlobals) this.getApplication();

		if (null == savedInstanceState) { // init only if we are just created
			hub.hubInit(this);
		}

		progressBarDroneConnected = (ProgressBar) findViewById(R.id.progressBarConnected);

	}

	public void resetFragmentsAdapter() {
		fragmentsAdapter = new HUBFragmentsAdapter(this, getSupportFragmentManager());
		fragmentsViewPager = (ViewPager) findViewById(R.id.viewPager);
		fragmentsViewPager.setAdapter(fragmentsAdapter);

	}

	@Override
	protected void onResume() {
		super.onResume();

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

		//as it's based on the prefs as well
		resetFragmentsAdapter();

		invalidateOptionsMenu();

		// register for call interface;
		HUBGlobals.messenger.mainActivity = this;

		progressBarDroneConnected.getIndeterminateDrawable().setColorFilter(0xFFFF0000, android.graphics.PorterDuff.Mode.MULTIPLY);

		onDataUpdateStats();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, " === Main Activity on Destroy ===");
	}

	@Override
	public void onPause() {
		super.onPause();

		// unregister from call interface;
		HUBGlobals.messenger.mainActivity = null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// getMenuInflater().inflate(R.menu.main, menu);

		//final ActionBar actionBar = getSupportActionBar();
		final ActionBar actionBar = getActionBar();

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowTitleEnabled(Utils.getScreenSize(this).compareTo(SCREEN_SIZE.NORMAL) > 0);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection

		switch (item.getItemId()) {
		case R.id.menu_settings:
			final Intent intent = new Intent();
			intent.setClass(HUBActivityMain.this, HUBActivitySettings.class);
			startActivityForResult(intent, 0);
			return true;
		case R.id.menu_select_device_bluetooth:

			final FragmentTransaction fragManager = getSupportFragmentManager().beginTransaction();

			final Fragment prev = getSupportFragmentManager().findFragmentByTag("bluetooth");
			if (null != prev) {
				fragManager.remove(prev);
			}
			fragManager.addToBackStack(null);

			final DialogFragment deviceSelectDialogBT = FragmentDialogSelectDevice.newInstance();
			deviceSelectDialogBT.setCancelable(false);
			deviceSelectDialogBT.show(fragManager, "bluetooth");

			return true;

		case R.id.menu_select_device_usb:

			final FragmentTransaction fragManager_usb = getSupportFragmentManager().beginTransaction();

			final Fragment prev_usb = getSupportFragmentManager().findFragmentByTag("usb");
			if (null != prev_usb) {
				fragManager_usb.remove(prev_usb);
			}
			fragManager_usb.addToBackStack(null);

			final DialogFragment deviceSelectDialogUSB = FragmentDialogSelectDevice.newInstance();
			deviceSelectDialogUSB.setCancelable(false);
			deviceSelectDialogUSB.show(fragManager_usb, "usb");

			return true;

		case R.id.menu_switch_server_mode:

			hub.switchServer();

			Toast.makeText(this, getResources().getString(R.string.txt_server_mode_set_to) + hub.gcsServer.getServerMode().toString(), Toast.LENGTH_SHORT).show();

			return true;

		case R.id.menu_switch_analyzer_view:

			//			Intent myIntent = getIntent();
			//			finish();
			//			startActivity(myIntent);

			//switch prefs for Analyzer
			hub.prefs.edit().putBoolean("pref_analyzer_view", !hub.prefs.getBoolean("pref_analyzer_view", true)).commit();
			// then reload pages.			
			resetFragmentsAdapter();

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// Close hub respecting connection state
	public void CloseMe() {

		final OnClickListener positiveButtonClickListener = new AlertDialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				closeHUB();
			}
		};

		final OnClickListener negativeButtonClickListener = new AlertDialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		};

		if (hub.droneClient.isConnected()) {

			final AlertDialog.Builder dlg = new AlertDialog.Builder(this);
			dlg.setTitle(getString(R.string.close_dlg_title_mavlink_closing) + " [" + hub.droneClient.getPeerName() + "]");
			dlg.setMessage(R.string.close_dlg_msg_current_connection_will_be_lost);
			dlg.setCancelable(false);
			dlg.setPositiveButton(R.string.close_dlg_positive, positiveButtonClickListener);
			dlg.setNegativeButton(R.string.close_dlg_negative, negativeButtonClickListener);
			dlg.create();
			dlg.show();

		}
		else {
			closeHUB();
		}

	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();

		CloseMe();

	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "** Main Activity Stopped ...**"); // 2nd

	}

	private void closeHUB() {
		HUBGlobals.logger.sysLog(TAG, "MavLinkHUB closing ...");
		hub.droneClient.stopClient();
		hub.gcsServer.stopServer();
		hub.queue.stopQueue();
		HUBGlobals.logger.stopAllLogs();
		finish();
		killApp(true);
	}

	public void enableProgressBar(boolean on) {
		if (on) {
			progressBarDroneConnected.setVisibility(View.VISIBLE);
		}
		else {
			progressBarDroneConnected.setVisibility(View.INVISIBLE);
		}

	}

	@Override
	public void onDataUpdateStats() {

		final TextView mTextViewLogStats = (TextView) findViewById(R.id.textView_system_status_bar);
		mTextViewLogStats.setText(HUBGlobals.logger.hubStats.toString(MSG_SOURCE.FROM_ALL));

	}

	@SuppressWarnings("deprecation")
	public static void killApp(boolean killSafely) {
		if (killSafely) {
			System.runFinalizersOnExit(true);
			System.exit(0);
		}
		else {
			android.os.Process.killProcess(android.os.Process.myPid());
		}

	}

}
