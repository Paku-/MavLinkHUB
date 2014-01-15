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

public class ActivityMain extends FragmentActivity implements IDataUpdateStats {

	private static final String TAG = "ActivityMain";

	public HUBGlobals globalVars;

	private ProgressBar progressBarConnected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		globalVars = (HUBGlobals) this.getApplication();

		if (savedInstanceState == null) { // init only if we are just borned
			globalVars.Init(this);
		}

		globalVars.mFragmentsPagerAdapter = new FragmentsAdapter(this, getSupportFragmentManager());
		globalVars.mViewPager = (ViewPager) findViewById(R.id.pager);
		globalVars.mViewPager.setAdapter(globalVars.mFragmentsPagerAdapter);

		progressBarConnected = (ProgressBar) findViewById(R.id.progressBarConnected);

	}

	@Override
	protected void onResume() {
		super.onResume();
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

		// register for call interface;
		globalVars.messenger.mainActivity = this;

		progressBarConnected.getIndeterminateDrawable().setColorFilter(0xFFFF0000,
				android.graphics.PorterDuff.Mode.MULTIPLY);
		refreshStats();

	}

	@Override
	public void onPause() {
		super.onPause();

		// unregister from call interface;
		globalVars.messenger.mainActivity = null;
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
			intent.setClass(ActivityMain.this, ActivitySettings.class);
			startActivityForResult(intent, 0);

			return true;
		case R.id.menu_select_bt:

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// Close app respecting connection state
	public void CloseMe() {

		final OnClickListener positiveButtonClickListener = new AlertDialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				closeHUB();
				finish();
			}
		};

		final OnClickListener negativeButtonClickListener = new AlertDialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		};

		if (globalVars.droneClient.isConnected()) {

			final AlertDialog.Builder dlg = new AlertDialog.Builder(this);
			dlg.setTitle(getString(R.string.close_dlg_title_mavlink_closing) + "["
					+ globalVars.droneClient.getPeerName() + "]");
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

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "** App onStop call ...**"); // 2nd

	}

	private void closeHUB() {
		globalVars.logger.sysLog(TAG, "MavLinkHUB closing ...");
		globalVars.droneClient.stopConnection();
		globalVars.msgCenter.mavlinkCollector.stopMavLinkParserThread();
		globalVars.logger.stopAllLogs();
	}

	private void refreshStats() {
		final TextView mTextViewLogStats = (TextView) findViewById(R.id.textView_system_status_bar);
		mTextViewLogStats.setText(globalVars.msgCenter.mavlinkCollector.getLastParserStats(MSG_SOURCE.FROM_DRONE));
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
