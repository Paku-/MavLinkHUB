package com.paku.mavlinkhub;

import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.fragments.FragmentsAdapter;

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

public class ActivityMain extends FragmentActivity {

	private static final String TAG = "ActivityMain";

	private HUBGlobals globalVars; // global vars and constants object.

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		globalVars = (HUBGlobals) this.getApplication();

		if (savedInstanceState == null) {
			// init only if we are just borned ...
			globalVars.Init(this);

		}

		globalVars.mFragmentsPagerAdapter = new FragmentsAdapter(this, getSupportFragmentManager());
		globalVars.mViewPager = (ViewPager) findViewById(R.id.pager);
		globalVars.mViewPager.setAdapter(globalVars.mFragmentsPagerAdapter);

	}

	@Override
	protected void onResume() {
		super.onResume();
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
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

			Intent intent = new Intent();
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

		if (globalVars.incommingConnector.isConnected()) {

			final AlertDialog.Builder dlg = new AlertDialog.Builder(this);
			dlg.setTitle(getString(R.string.close_dlg_title_mavlink_closing) + "["
					+ globalVars.incommingConnector.getPeerName() + "]");
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
		// TODO Auto-generated method stub
		super.onStop();
		Log.d(TAG, "** App onStop call ...**"); // 2nd

	}

	private void closeHUB() {
		globalVars.logger.sysLog(TAG, "MavLinkHUB closing ...");
		globalVars.incommingConnector.closeConnection();
		globalVars.mMavLinkCollector.stopMavLinkParserThread();
		globalVars.logger.stopAllLogs();
	}

}
