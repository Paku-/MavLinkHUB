package com.paku.mavlinkhub.fragments;

import java.util.Locale;

import com.paku.mavlinkhub.HUBActivityMain;
import com.paku.mavlinkhub.R;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/** DO NOT change to the FragmentStatePagerAdapter or take care for GUI!!! **/
public class FragmentsAdapter extends FragmentPagerAdapter {

	private final HUBActivityMain mainActivity;

	public FragmentsAdapter(HUBActivityMain mainActivity, FragmentManager fm) {
		super(fm);
		this.mainActivity = mainActivity;
	}

	@Override
	public Fragment getItem(int position) {

		Fragment fragment;

		switch (position) {
		case 0:
			fragment = new FragmentConnectionState();
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
		final Locale locale = Locale.getDefault();
		switch (position) {
		case 0:
			return this.mainActivity.getString(R.string.title_connectivity).toUpperCase(locale);
		case 1:
			return this.mainActivity.getString(R.string.title_realtime_mavlink).toUpperCase(locale);
		case 2:
			return this.mainActivity.getString(R.string.title_syslog).toUpperCase(locale);
		case 3:
			return this.mainActivity.getString(R.string.title_calibration).toUpperCase(locale);
		default:
			break;

		}
		return null;
	}
}