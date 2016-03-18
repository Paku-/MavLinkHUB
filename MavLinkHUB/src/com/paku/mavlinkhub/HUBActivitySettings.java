// $codepro.audit.disable com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.alwaysOverridetoString.alwaysOverrideToString
package com.paku.mavlinkhub;

import com.paku.mavlinkhub.fragments.FragmentSettings;

import android.app.Activity;
import android.os.Bundle;

public class HUBActivitySettings extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Display the fragment as the main content.
		getFragmentManager().beginTransaction().replace(android.R.id.content, new FragmentSettings()).commit();
	}
}