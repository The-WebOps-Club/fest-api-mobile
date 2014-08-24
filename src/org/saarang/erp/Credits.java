package org.saarang.erp;

import android.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class Credits extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setHasOptionsMenu(true);

		return inflater.inflate(R.layout.credits, container, false);

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		android.app.ActionBar actionBar = getActivity().getActionBar();
		ColorDrawable colorDrawable = new ColorDrawable(
				Color.parseColor("#355088"));
		actionBar.setBackgroundDrawable(colorDrawable);
		Button bug, rate;
		bug = (Button) getView().findViewById(R.id.bug);
		rate = (Button) getView().findViewById(R.id.rate);
		bug.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				composeEmail("shahid@saarang.org", "Saarang erp app bug report");

			}
		});
		rate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final String appPackageName = getActivity().getPackageName(); // getPackageName()
																				// from
																				// Context
																				// or
																				// Activity
																				// object
				try {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri
							.parse("market://details?id=" + appPackageName)));
				} catch (android.content.ActivityNotFoundException anfe) {
					startActivity(new Intent(
							Intent.ACTION_VIEW,
							Uri.parse("http://play.google.com/store/apps/details?id="
									+ appPackageName)));
				}

			}
		});

	}

	public void composeEmail(String addresses, String subject) {
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		String[] email = { "webadmin@saarang.org" };
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, email);
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
				"ERP Android App Bug Report");
		emailIntent.setData(Uri.parse("mailto:"));
		emailIntent.setType("plain/text");

		if (emailIntent.resolveActivity(getActivity().getPackageManager()) != null) {
			startActivity(emailIntent);
		} else {
			Toast.makeText(getActivity(), "Email app not accessible",
					Toast.LENGTH_SHORT).show();
		}
	}

}
