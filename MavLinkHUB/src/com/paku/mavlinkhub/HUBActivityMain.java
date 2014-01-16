package com.paku.mavlinkhub;

import com.paku.mavlinkhub.enums.MSG_SOURCE;
import com.paku.mavlinkhub.fragments.FragmentsAdapter;
import com.paku.mavlinkhub.interfaces.IDataUpdateStats;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

public class HUBActivityMain extends FragmentActivity implements IDataUpdateStats {

	private static final String TAG = "HUBActivityMain";

	public HUBGlobals hub;

	private ProgressBar progressBarConnected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		hub = (HUBGlobals) this.getApplication();

		if (savedInstanceState == null) { // init only if we are just borned
			hub.Init(this);
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

		progressBarConnected.getIndeterminateDrawable().setColorFilter(0xFFFF0000,
				android.graphics.PorterDuff.Mode.MULTIPLY);
		refreshStats();

	}

	@Override
	public void onPause() {
		super.onPause();

		// unregister from call interface;
		hub.messenger.mainActivity = null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_settings:

			final Intent intent = new Intent();
			intent.setClass(HUBActivityMain.this, ActivitySettings.class);
			startActivityForResult(intent, 0);

			return true;
		case R.id.menu_select_bt:

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// Close hub respecting connection state
	public void CloseMe() {

		OnClickListener positiveButtonClickListener = new AlertDialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				closeHUB();
				finish();
			}
		};

		OnClickListener negativeButtonClickListener = new AlertDialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		};

		if (hub.droneClient.isConnected()) {

			final AlertDialog.Builder dlg = new AlertDialog.Builder(this);
			dlg.setTitle(getString(R.string.close_dlg_title_mavlink_closing) + "["
					+ hub.droneClient.getPeerName() + "]");
			dlg.setMessage(R.string.close_dlg_msg_current_connection_will_be_lost);
			dlg.setCancelable(false);
			dlg.setPositiveButton(R.string.close_dlg_positive, positiveButtonClickListener);
			dlg.setNegativeButton(R.string.close_dlg_negative, negativeButtonClickListener);
			dlg.create();
			dlg.show();

		}
		else {
			closeHUB();
			finish();
		}

	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();

		CloseMe();

	}

	/*
	 * public void onBackPressed() { if(backButtonCount >= 1) { Intent intent =
	 * new Intent(Intent.ACTION_MAIN); intent.addCategory(Intent.CATEGORY_HOME);
	 * intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); startActivity(intent); }
	 * else { Toast.makeText(this,
	 * "Press the back button once again to close the application.",
	 * Toast.LENGTH_SHORT).show(); backButtonCount++; } }
	 */
	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "** App onStop call ...**"); // 2nd

	}

	private void closeHUB() {
		hub.logger.sysLog(TAG, "MavLinkHUB closing ...");
		hub.droneClient.stopClient();
		hub.gsServer.stopServer();
		hub.mavlinkQueue.msgCollector.stopMAVLinkParserThread();
		hub.logger.stopAllLogs();
	}

	private void refreshStats() {
		final TextView mTextViewLogStats = (TextView) findViewById(R.id.textView_system_status_bar);
		mTextViewLogStats.setText(hub.logger.hubStats.toString_(MSG_SOURCE.FROM_ALL));
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
		refreshStats();
	}

}
