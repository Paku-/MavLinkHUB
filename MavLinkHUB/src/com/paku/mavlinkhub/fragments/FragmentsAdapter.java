package com.paku.mavlinkhub.fragments;

import java.util.Locale;

import com.paku.mavlinkhub.ActivityMain;
import com.paku.mavlinkhub.R;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/** DO NOT change to the FragmentStatePagerAdapter or take care for GUI!!! **/
public class FragmentsAdapter extends FragmentPagerAdapter {

	private final ActivityMain mainActivity;

	public FragmentsAdapter(ActivityMain mainActivity, FragmentManager fm) {
		super(fm);
		this.mainActivity = mainActivity;
	}

	@Override
	public Fragment getItem(int position) {

		Fragment fragment;

		switch (position) {
		case 0:
			fragment = new FragmentConnectivity();
			break;
		case 1:
			fragment = new FragmentRealTimeMavlink();
			break;
		case 2:
			fragment = new FragmentSysLog();
			break;
		case 3:
			fragment = new FragmentCalibration();
			break;
		default:
			fragment = new FragmentDummySection();
			break;
		}
		return fragment;
	}

	@Override
	public int getCount() {
		// Show 3 total pages.
		return 4;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		Locale l = Locale.getDefault();
		switch (position) {
		case 0:
			return this.mainActivity.getString(R.string.title_connectivity).toUpperCase(l);
		case 1:
			return this.mainActivity.getString(R.string.title_realtime_mavlink).toUpperCase(l);
		case 2:
			return this.mainActivity.getString(R.string.title_syslog).toUpperCase(l);
		case 3:
			return this.mainActivity.getString(R.string.title_calibration).toUpperCase(l);

		}
		return null;
	}
}