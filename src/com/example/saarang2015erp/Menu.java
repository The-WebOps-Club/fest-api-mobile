package com.example.saarang2015erp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class Menu extends Activity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		android.app.ActionBar ab = getActionBar(); 
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#355088"));     
        ab.setBackgroundDrawable(colorDrawable);
		mTitle = mDrawerTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		SharedPreferences uid = getSharedPreferences("uid", MODE_PRIVATE);
		int noWalls = uid.getInt("noWalls", 3);
		Log.d("No; of pages", Integer.toString(noWalls));
		String[] DrawerItems = new String[noWalls + 2];
		DrawerItems[0] = "Notifications";

		Log.d("Reached Where?", "Loading Array");
		for (int i = 0; i < noWalls; i++) {
			String pageName = uid.getString("page_" + Integer.toString(i),
					"none");
			Log.d("Preferences", pageName);
			DrawerItems[i + 1] = pageName;
			pageName = uid.getString("page_" + Integer.toString(i), "none");
		}
		Log.d("Reached Where?", "Loaded Array");

		DrawerItems[noWalls + 1] = "SignOut";
		// for(int s = 0; s<5; s++){
		// Log.d("Item", DrawerItems[s]);
		// }
		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// set up the drawer's list view with items and click listener
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, DrawerItems));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(false);
		getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			selectItem(0);
		}
	}

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {

		// update the main content by replacing fragments

		SharedPreferences uid = getSharedPreferences("uid", MODE_PRIVATE);
		int noWalls = uid.getInt("noWalls", 3);

		if (position == noWalls + 1) {
			uid.edit().clear().commit();
			Intent mainPage = new Intent(Menu.this, MainActivity.class);
			startActivity(mainPage);
		} else if (position == 0) {
			Fragment fragment = new Notifications();
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, fragment).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);

			setTitle("Notifications");
			mDrawerLayout.closeDrawer(mDrawerList);

		} else {
			Fragment fragment = new WallViewFragment();
			mDrawerLayout.closeDrawer(mDrawerList);
			Bundle args = new Bundle();
			int prefNum = position - 1;
			String WallName = uid.getString(
					"page_" + Integer.toString(prefNum), "0");
			int page_id = uid.getInt("page_id_" + Integer.toString(prefNum),
					0);
			args.putString("page_id", Integer.toString(page_id));
			args.putString("wallName", WallName);
			fragment.setArguments(args);
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, fragment).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);

			setTitle(WallName);
			


		}

		switch (position) {
		case 0:

			break;

		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}

	/**
	 * Fragment that appears in the "content_frame", shows a planet
	 */

}