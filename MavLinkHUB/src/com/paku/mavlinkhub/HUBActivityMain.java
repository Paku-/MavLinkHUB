package com.paku.mavlinkhub;

import com.paku.mavlinkhub.enums.MSG_SOURCE;
import com.paku.mavlinkhub.fragments.FragmentsAdapter;
import com.paku.mavlinkhub.fragments.dialogs.FragmentAlertDialog;
import com.paku.mavlinkhub.interfaces.IDataUpdateStats;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
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

//public class HUBActivityMain extends ActionBarActivity implements IDataUpdateStats {
public class HUBActivityMain extends FragmentActivity implements IDataUpdateStats {

	private static final String TAG = "HUBActivityMain";

	public HUBGlobals hub;

	private ProgressBar progressBarConnected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);

		requestWindowFeature(Window.FEATURE_ACTION_BAR);

		setContentView(R.layout.activity_main);

		hub = (HUBGlobals) this.getApplication();

		if (savedInstanceState == null) { // init only if we are just created
			hub.hubInit(this);
		}

		hub.mFragmentsPagerAdapter = new FragmentsAdapter(this, getSupportFragmentManager());
		hub.mViewPager = (ViewPager) findViewById(R.id.pager);
		hub.mViewPager.setAdapter(hub.mFragmentsPagerAdapter);

		progressBarConnected = (ProgressBar) findViewById(R.id.progressBarConnected);

	}

	@Override
	protected void onResume() {
		super.onResume();

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

		// register for call interface;
		hub.messenger.mainActivity = this;

		progressBarConnected.getIndeterminateDrawable().setColorFilter(0xFFFF0000, android.graphics.PorterDuff.Mode.MULTIPLY);
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
		hub.messenger.mainActivity = null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// getMenuInflater().inflate(R.menu.main, menu);

		// final ActionBar actionBar = getSupportActionBar();
		// final ActionBar actionBar = getActionBar();

		// actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// actionBar.setDisplayShowHomeEnabled(true);
		// actionBar.setDisplayShowTitleEnabled(true);

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
		case R.id.menu_empty:

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// Close hub respecting connection state
	public void CloseMeFragmentDialog() {

		FragmentTransaction fragManager = getSupportFragmentManager().beginTransaction();

		// this is a piece to check if we are not displayed - do not need it
		// here in my opinion as both exits form the fragment ends with
		// dismiss()

		/*
		 * Fragment prev =
		 * getSupportFragmentManager().findFragmentByTag("warn"); if (prev !=
		 * null) { fragManager.remove(prev); } // and back key action
		 * fragManager.addToBackStack(null);
		 */

		DialogFragment closeDialog = FragmentAlertDialog.newInstance(getApplicationContext(), 6, "Sample Title", "Message ...");
		closeDialog.setCancelable(false);
		closeDialog.show(fragManager, "closing");
	}

	// Close hub respecting connection state
	public void CloseMe() {

		OnClickListener positiveButtonClickListener = new AlertDialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				closeHUB();
			}
		};

		OnClickListener negativeButtonClickListener = new AlertDialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		};

		if (hub.droneClient.isConnected()) {

			final AlertDialog.Builder dlg = new AlertDialog.Builder(this);
			dlg.setTitle(getString(R.string.close_dlg_title_mavlink_closing) + "[" + hub.droneClient.getPeerName() + "]");
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
		hub.logger.sysLog(TAG, "MavLinkHUB closing ...");
		hub.droneClient.stopClient();
		hub.gsServer.stopServer();
		hub.queue.stopQueue();
		hub.logger.stopAllLogs();
		finish();
		killApp(true);
	}

	public void enableProgressBar(boolean on) {
		if (on) {
			progressBarConnected.setVisibility(View.VISIBLE);
		}
		else {
			progressBarConnected.setVisibility(View.INVISIBLE);
		}

	}

	@Override
	public void onDataUpdateStats() {

		final TextView mTextViewLogStats = (TextView) findViewById(R.id.textView_system_status_bar);
		mTextViewLogStats.setText(hub.logger.hubStats.toString_(MSG_SOURCE.FROM_ALL));

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
