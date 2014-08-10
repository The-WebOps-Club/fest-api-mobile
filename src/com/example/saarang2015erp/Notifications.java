package com.example.saarang2015erp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class Notifications extends Fragment {
	public String not;
	private ProgressDialog pDialog;

	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.notifications, container, false);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		new getNotifications().execute();

	}

	class getNotifications extends AsyncTask<String, String, String> {
		JSONArray theArray;
		JSONParser jsonParser = new JSONParser();
		int status;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
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
			paramse.add(new BasicNameValuePair("limit", "30"));
			paramse.add(new BasicNameValuePair("offset", "0"));
			SharedPreferences uid = getActivity().getSharedPreferences("uid", Context.MODE_PRIVATE);
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
			final String id[] = new String[theArray.length()];
			if (status == 1){
				final String header[] = new String[theArray.length()];
				final String descriptionArray[] = new String[theArray.length()];
				final String wallsArray[] = new String[theArray.length()];
				final String dateArray[] = new String[theArray.length()];
				try{
				for (int i = 0; i < theArray.length(); i++) {
					JSONObject jsonInside = theArray.getJSONObject(i);
					JSONObject wall = jsonInside.getJSONObject("wall");
					JSONObject target = jsonInside.getJSONObject("target");
					String post_id= target.getString("id");
					Log.d("id", post_id );
					String wallName = wall.getString("name");
					JSONObject actor = jsonInside.getJSONObject("actor");
					String actorName = actor.getString("name");
					String verb = jsonInside.getString("verb");
					String timeStamp = jsonInside.getString("timestamp");
					String description = jsonInside
							.getString("description");
					Log.d("heading", actorName + " " + verb + " "
							+ wallName);
					Log.d("Content", description);
					header[i] = "<b>" +actorName + "</b>"  + " " + verb + " "+"<b>" + wallName + "</b> ";
					Date date = null;
					try {
					date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(timeStamp);
					int year = date.getYear();
					dateArray[i] = new SimpleDateFormat("dd/MM/yyyy").format(date).toString();
					Log.d("Year", new SimpleDateFormat("dd/MM/yyyy").format(date).toString());
					} catch (ParseException e) {
					e.printStackTrace();
					}

					descriptionArray[i] = description;
					id[i] = post_id;
					wallsArray[i] = wallName;
				}
				
				
				
				
				NotificationList adapter = new NotificationList(getActivity(), header, descriptionArray, dateArray);
				ListView list = (ListView) getView().findViewById(R.id.listView1);
				list.setAdapter(adapter);
				list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

		            @Override
		            public void onItemClick(AdapterView<?> parent, View view,
		                                    int position, long useless_id) {
		              
		            	Intent i=new Intent(getActivity(),PostView.class);
			                i.putExtra("post_id", id[+ position]);
			               i.putExtra("WallName", wallsArray[+ position]);
			                
			                startActivity(i); 
		            //	Toast.makeText(getActivity(), "You Clicked at " + header[+ position], Toast.LENGTH_SHORT).show();

		            }
		        });

				}catch (Exception e){
					e.printStackTrace();
				}

			}
						
			
		}

	}

}
