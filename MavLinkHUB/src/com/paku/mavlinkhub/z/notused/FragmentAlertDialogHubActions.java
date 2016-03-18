package com.paku.mavlinkhub.z.notused;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.R;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class FragmentAlertDialogHubActions extends DialogFragment {

	int viewMode;
	int currentAction;
	String titleTxt;
	String msgTxt;
	Parcelable params;

	HUBGlobals hub;

	public static FragmentAlertDialogHubActions newInstance(Context context, int viewMode, String title, String msg) {

		final FragmentAlertDialogHubActions me = new FragmentAlertDialogHubActions();

		final Bundle args = new Bundle();
		args.putInt("viewMode", viewMode);
		args.putString("title", title);
		args.putString("msg", msg);
		me.setArguments(args);

		return me;

	}

	public static FragmentAlertDialogHubActions newInstance(Context context, int viewMode, String title, String msg, int action, Parcelable params) {

		final FragmentAlertDialogHubActions me = new FragmentAlertDialogHubActions();

		final Bundle args = new Bundle();
		args.putInt("viewMode", viewMode);
		args.putString("title", title);
		args.putString("msg", msg);
		args.putInt("action", action);
		args.putParcelable("params", params);
		me.setArguments(args);

		return me;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		viewMode = getArguments().getInt("viewMode");
		titleTxt = getArguments().getString("title");
		msgTxt = getArguments().getString("msg");
		currentAction = getArguments().getInt("action");
		params = getArguments().getParcelable("params");

		hub = ((HUBGlobals) getActivity().getApplication());

		int style = DialogFragment.STYLE_NORMAL;
		int theme = 0;

		switch ((viewMode - 1) % 6) {
		case 1:
			style = DialogFragment.STYLE_NO_TITLE;
			break;
		case 2:
			style = DialogFragment.STYLE_NO_FRAME;
			break;
		case 3:
			style = DialogFragment.STYLE_NO_INPUT;
			break;
		case 4:
			style = DialogFragment.STYLE_NORMAL;
			break;
		case 5:
			style = DialogFragment.STYLE_NORMAL;
			break;
		case 6:
			style = DialogFragment.STYLE_NO_TITLE;
			break;
		case 7:
			style = DialogFragment.STYLE_NO_FRAME;
			break;
		case 8:
			style = DialogFragment.STYLE_NORMAL;
			break;
		}
		switch ((viewMode - 1) % 6) {
		case 4:
			theme = android.R.style.Theme_Holo;
			break;
		case 5:
			theme = android.R.style.Theme_Holo_Light_Dialog;
			break;
		case 6:
			theme = android.R.style.Theme_Holo_Light;
			break;
		case 7:
			theme = android.R.style.Theme_Holo_Light_Panel;
			break;
		case 8:
			theme = android.R.style.Theme_Holo_Light;
			break;
		}
		setStyle(style, theme);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		getDialog().setTitle(titleTxt);

		final View viewDlg = inflater.inflate(R.layout.fragment_alert_dialog, container, false);
		final View viewMsgTxt = viewDlg.findViewById(R.id.textView_alert_message);

		((TextView) viewMsgTxt).setText(msgTxt);

		final Button buttPositive = (Button) viewDlg.findViewById(R.id.button_positive);
		buttPositive.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				switch (currentAction) {
				case 0:
					// hub.do_something;
					break;
				case 1:
					break;
				default:
					break;
				}
				dismiss();
			}

		});

		final Button buttNegative = (Button) viewDlg.findViewById(R.id.button_negative);
		buttNegative.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dismiss();
			}

		});

		return viewDlg;
	}

}