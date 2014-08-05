package com.example.saarang2015erp;

import java.util.ArrayList;
import java.util.List;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class Notifications extends Activity {
	public String not;
	private ProgressDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notifications);
		new getNotifications().execute();

	}

	class getNotifications extends AsyncTask<String, String, String> {
		JSONArray theArray;
		JSONParser jsonParser = new JSONParser();
		int status;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Notifications.this);
			pDialog.setMessage("Loading");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			// String url = "http://10.0.2.2:80/JSON/wall.php";
			String url = "api/mobile/notifications/";
			// "api/mobile/walls/";
			List<NameValuePair> paramse = new ArrayList<NameValuePair>();
			paramse.add(new BasicNameValuePair("username", "userName"));
			SharedPreferences uid = getSharedPreferences("uid", MODE_PRIVATE);
			String token = uid.getString("uid", "Aaa");
			JSONObject json = jsonParser.makeHttpRequest(url, "GET", paramse,
					token);
			try {
				Log.d("json", json.toString());
				status = json.getInt("status");
				Log.d("Success or not?", Integer.toString(status));
				if (status == 1) {
					Log.d("message", "its success dude");
					theArray = json.getJSONArray("data");
				} else {

				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			if (status == 1){
				final String header[] = new String[theArray.length()];
				final String descriptionArray[] = new String[theArray.length()];
				try{
				for (int i = 0; i < theArray.length(); i++) {
					JSONObject jsonInside = theArray.getJSONObject(i);
					JSONObject wall = jsonInside.getJSONObject("wall");
					String wallName = wall.getString("name");
					JSONObject actor = jsonInside.getJSONObject("actor");
					String actorName = actor.getString("name");
					String verb = jsonInside.getString("verb");
					String description = jsonInside
							.getString("description");
					Log.d("heading", actorName + " " + verb + " "
							+ wallName);
					Log.d("Content", description);
					header[i] = "<b>" +actorName + "</b>" + " " + verb + " "+"<b>" + wallName + "</b>";
					descriptionArray[i] = description;
				}
				
				NotificationList adapter = new NotificationList(Notifications.this, header, descriptionArray);
				ListView list = (ListView) findViewById(R.id.listView1);
				list.setAdapter(adapter);
				list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

		            @Override
		            public void onItemClick(AdapterView<?> parent, View view,
		                                    int position, long id) {
		                Toast.makeText(Notifications.this, "You Clicked at " + header[+ position], Toast.LENGTH_SHORT).show();

		            }
		        });

				}catch (Exception e){
					e.printStackTrace();
				}

			}
						
			
		}

	}

}
