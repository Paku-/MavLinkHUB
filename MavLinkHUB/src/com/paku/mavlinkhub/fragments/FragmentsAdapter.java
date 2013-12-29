package com.paku.mavlinkhub.fragments;

import java.util.Locale;

import com.paku.mavlinkhub.MainActivity;
import com.paku.mavlinkhub.R;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/** DO NOT change to the FragmentStatePagerAdapter or take care for GUI!!! **/
public class FragmentsAdapter extends FragmentPagerAdapter {

	/**
	 * 
	 */
	private final MainActivity mainActivity;

	public FragmentsAdapter(MainActivity mainActivity, FragmentManager fm) {
		super(fm);
		this.mainActivity = mainActivity;
	}

	@Override
	public Fragment getItem(int position) {

		Fragment fragment;

		switch (position) {
		case 0:
			fragment = new ConnectivityFragment();
			break;
		case 1:
			fragment = new RealTimeMavlinkFragment();
			break;
		case 2:
			fragment = new CalibrationFragment();
			break;
		// case 3:
		// fragment = new SettingsFragment().getFragmentManager().;
		// break;

		default:
			fragment = new DummySectionFragment();
			break;
		}
		return fragment;
	}

	@Override
	public int getCount() {
		// Show 3 total pages.
		return 3;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		Locale l = Locale.getDefault();
		switch (position) {
		case 0:
			return this.mainActivity.getString(R.string.title_connectivity)
					.toUpperCase(l);
		case 1:
			return this.mainActivity.getString(R.string.title_realtime_mavlink)
					.toUpperCase(l);
		case 2:
			return this.mainActivity.getString(R.string.title_calibration)
					.toUpperCase(l);
		}
		return null;
	}
}