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
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

/** DO NOT change to the FragmentStatePagerAdapter or take care for GUI!!! **/

public class HUBFragmentsAdapter extends FragmentStatePagerAdapter {

	ArrayList<ItemFragment> fragments;

	private final HUBActivityMain parentActivity;

	public HUBFragmentsAdapter(HUBActivityMain parent, FragmentManager fm) {
		super(fm);

		parentActivity = parent;

		fragments = new ArrayList<ItemFragment>();

		fragments.add(new ItemFragment(new FragmentConnection(), parentActivity.getString(R.string.title_connectivity).toUpperCase(Locale.getDefault()), true));

		if (parent.hub.prefs.getBoolean("pref_analyzer_view", true)) {
			fragments.add(new ItemFragment(new FragmentAnalyzer(), parentActivity.getString(R.string.title_analyzer).toUpperCase(Locale.getDefault()), true));
		}

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

	@Override
	public int getItemPosition(Object object) {

		//	      if(true) //this includes deleting or adding pages
		return PagerAdapter.POSITION_NONE;
		//	    	     }
		//	    	 else
		//	    	 return PagerAdapter.POSITION_UNCHANGED; //this ensures high performance in other operations such as editing list items.

	}

}