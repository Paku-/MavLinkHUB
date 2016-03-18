package com.paku.mavlinkhub.fragments.item;

import android.support.v4.app.Fragment;

public class ItemFragment {

	public Fragment fragment;
	public String title;
	boolean visible;

	/**
	 * @param fragment
	 *            Fragment object represented by this Item
	 * @param title
	 *            It's title
	 * @param visible
	 *            boolean visibility switch
	 */
	public ItemFragment(Fragment fragment, String title, boolean visible) {
		this.fragment = fragment;
		this.title = title;
		this.visible = visible;
	}

}
