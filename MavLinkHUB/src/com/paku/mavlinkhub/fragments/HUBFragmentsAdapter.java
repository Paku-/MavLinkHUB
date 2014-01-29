// $codepro.audit.disable
// com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.alwaysOverridetoString.alwaysOverrideToString
package com.paku.mavlinkhub.fragments;

import java.util.ArrayList;
import java.util.Locale;

import com.paku.mavlinkhub.HUBActivityMain;
import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.fragments.item.ItemFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/** DO NOT change to the FragmentStatePagerAdapter or take care for GUI!!! **/
public class HUBFragmentsAdapter extends FragmentPagerAdapter {

	ArrayList<ItemFragment> fragments;

	private final HUBActivityMain parentActivity;

	public HUBFragmentsAdapter(HUBActivityMain parent, FragmentManager fm) {
		super(fm);

		parentActivity = parent;

		fragments = new ArrayList<ItemFragment>();

		fragments.add(new ItemFragment(new FragmentConnectionState(), parentActivity.getString(R.string.title_connectivity).toUpperCase(Locale.getDefault()), true));
		fragments.add(new ItemFragment(new FragmentRealTimeMavlink(), parentActivity.getString(R.string.title_analyser).toUpperCase(Locale.getDefault()), true));
		fragments.add(new ItemFragment(new FragmentSysLog(), parentActivity.getString(R.string.title_syslog).toUpperCase(Locale.getDefault()), true));

	}

	@Override
	public Fragment getItem(int position) {

		return fragments.get(position).fragment;

	}

	@Override
	public int getCount() {

		return fragments.size();

	}

	@Override
	public CharSequence getPageTitle(int position) {

		return fragments.get(position).title;

	}
}