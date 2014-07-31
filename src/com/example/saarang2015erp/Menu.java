package com.example.saarang2015erp;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class Menu extends ActionBarActivity implements View.OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
		Button bPages = (Button) findViewById(R.id.button1);
		Button bNoti = (Button) findViewById(R.id.button2);
		bPages.setOnClickListener(this);
		bNoti.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button1:
			Intent i = new Intent("android.intent.action.PAGES");
			startActivity(i);
			break;
		case R.id.button2:
			SharedPreferences pages = getSharedPreferences("pages",
					MODE_PRIVATE);
			SharedPreferences.Editor editor = pages.edit();
			editor.putInt("pageNo", 0);
			editor.commit();
			break;
		}
	}

	
	
	
	
	
}
