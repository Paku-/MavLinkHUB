package com.paku.mavlinkhub.fragments;

import java.util.Locale;

import com.paku.mavlinkhub.MainActivity;
import com.paku.mavlinkhub.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class FragmentsStatePagerAdapter extends FragmentStatePagerAdapter {

	/**
	 * 
	 */
	private final MainActivity mainActivity;

	public FragmentsStatePagerAdapter(MainActivity mainActivity, FragmentManager fm) {
		super(fm);
		this.mainActivity = mainActivity;
	}

	@Override
	public Fragment getItem(int position) {
		// getItem is called to instantiate the fragment for the given page.
		// Return a DummySectionFragment (defined as a static inner class
		// below) with the page number as its lone argument.
		Fragment fragment;
		Bundle args = new Bundle();		
		
		switch (position) {
		case 0:
			fragment = new DummySectionFragment();
			break;
		case 1:
			fragment = new DummySectionFragment();
			break;
		case 2:
			fragment = new CalibrationFragment();
			break;
//		case 3:
//			fragment = new SettingsFragment().getFragmentManager().;
//			break;
			
		
			
		default:
				fragment = new DummySectionFragment();
			break;
		}		
		args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
		fragment.setArguments(args);
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
			return this.mainActivity.getString(R.string.title_connectivity).toUpperCase(l);
		case 1:
			return this.mainActivity.getString(R.string.title_realtime_mavlink).toUpperCase(l);
		case 2:
			return this.mainActivity.getString(R.string.title_calibration).toUpperCase(l);
		}
		return null;
	}
}