package com.paku.androplayground.fragments;

import java.util.Locale;

import com.paku.androplayground.MainActivity;
import com.paku.androplayground.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class FragmentsPagerAdapter extends FragmentPagerAdapter {

	/**
	 * 
	 */
	private final MainActivity mainActivity;

	public FragmentsPagerAdapter(MainActivity mainActivity, FragmentManager fm) {
		super(fm);
		this.mainActivity = mainActivity;
	}

	@Override
	public Fragment getItem(int position) {
		// getItem is called to instantiate the fragment for the given page.
		// Return a DummySectionFragment (defined as a static inner class
		// below) with the page number as its lone argument.
		Fragment fragment = new DummySectionFragment();
		Bundle args = new Bundle();
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