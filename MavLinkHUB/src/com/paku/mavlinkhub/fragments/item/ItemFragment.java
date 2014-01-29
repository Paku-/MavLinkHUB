package com.paku.mavlinkhub.fragments.item;

import android.support.v4.app.Fragment;

public class ItemFragment {

	public Fragment fragment;
	public String title;
	boolean visible;

	public ItemFragment(Fragment fragment, String title, boolean visible) {
		this.fragment = fragment;
		this.title = title;
		this.visible = visible;
	}

}
