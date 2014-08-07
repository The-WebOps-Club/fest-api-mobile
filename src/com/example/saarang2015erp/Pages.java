package com.example.saarang2015erp;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Pages extends Activity {
	
	JSONParser jsonParser = new JSONParser();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pages);
				
		
		SharedPreferences uid = getSharedPreferences("uid", MODE_PRIVATE);
		int i = 0;
		String pageName = uid.getString("page_" + Integer.toString(i), "none");
		while ( pageName != "none") {
		Log.d("Preferences", pageName);
		i++;
		pageName = uid.getString("page_" + Integer.toString(i), "none");
		}
	}

	class loadWalls extends AsyncTask<String, String, String> {
		JSONArray user;

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			//String url = "http://10.0.2.2:80/JSON/wall.php";
			String url = "api/mobile/notifications/";
					//"api/mobile/walls/";
			List<NameValuePair> paramse = new ArrayList<NameValuePair>();
			paramse.add(new BasicNameValuePair("username", "userName"));
			SharedPreferences uid = getSharedPreferences("uid", MODE_PRIVATE);
		    String token =  uid.getString("uid", "Aaa");
		    Log.d("token",token);
			JSONObject json = jsonParser.makeHttpRequest(url, "GET", paramse, token);
			try {
				Log.d("json", json.toString());
				String message = json.getString("message");
				Log.d("Success or not?", message);
				user = json.getJSONArray("data");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String file_url) {
			JSONObject c;
			try {
				SharedPreferences pages = getSharedPreferences("pages",
						MODE_PRIVATE);
				SharedPreferences.Editor editor = pages.edit();
				editor.putInt("pageNo", user.length());

				for (int i = 0; i < user.length(); i++) {
					c = user.getJSONObject(i);
					Log.d("name", c.getString("name"));
					editor.putString("page_" + i, c.getString("name"));
				}
				editor.commit();
				Intent i = new Intent("android.intent.action.PAGES");
				startActivity(i);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
