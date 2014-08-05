package com.example.saarang2015erp;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.support.v7.app.ActionBarActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	TextView tv;
	int type = 0;
	String tx = null, error = null;
	String userName, password;
	EditText etuserName, etpassword;
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		SharedPreferences uid = getSharedPreferences("uid", MODE_PRIVATE);
		String token = uid.getString("uid", "text");
		if (token != "text") {
			Intent open = new Intent("com.example.saarang2015erp.Menu");
			startActivity(open);
		}
		// tv = (TextView) findViewById(R.id.textView2);
		etuserName = (EditText) findViewById(R.id.editText1);
		etpassword = (EditText) findViewById(R.id.editText2);
		Button login = (Button) findViewById(R.id.button1);

		login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new loadUser().execute();
				// new GetWalls().execute();

			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	class loadUser extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage("Loading UserData...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Check for success tag
			userName = etuserName.getText().toString();
			password = etpassword.getText().toString();
			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("username", userName));
				params.add(new BasicNameValuePair("password", password));
				
				// Posting user data to script
				Log.d("DF", "Reqeusting");
				// String LOGIN_URL = "http://10.42.0.1:8000/api-token-auth/";
				String LOGIN_URL = "api-token-auth/";
				JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "POST",
						params, null);

				Log.d("DF", "posted request");
				try {

					tx = json.getString("token");
					Log.d("token", tx);
				} catch (Exception e) {
					error = json.getString("non_field_errors");
					type = 1;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product deleted
			pDialog.dismiss();

			if (tx != null) {
				Log.d("response", "Success");
				SharedPreferences uid = getSharedPreferences("uid",
						MODE_PRIVATE);
				SharedPreferences.Editor editor = uid.edit();
				editor.putString("uid", tx);
				editor.commit();
				Intent openMenu = new Intent("com.example.saarang2015erp.Menu");
				startActivity(openMenu);

			} else {
				if (error != null) {
					Log.d("response", "Not it is");
					tv.setVisibility(View.VISIBLE);
				}
			}

		}

	}

	class GetWalls extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage("Loading UserData...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Check for success tag

			try {

				String LOGIN_URL = "http://10.42.0.1:8000/api/mobile/walls/";
				List<NameValuePair> params1 = new ArrayList<NameValuePair>();
				// params.add(new BasicNameValuePair("tag", "login"));
				params1.add(new BasicNameValuePair("usernam", "aqel"));
				JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "GET",
						params1, null);

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product deleted
			pDialog.dismiss();
			

		}

	}

}
