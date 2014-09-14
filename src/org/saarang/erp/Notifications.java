package org.saarang.erp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class Notifications extends Fragment {
	public String not;

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
		boolean a = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		a = activeNetworkInfo != null && activeNetworkInfo.isConnected();
		if (a) {
			try {
				new getNotifications().execute();
			} catch (Exception e) {
			}
		} else {
			Toast.makeText(
					getActivity(),
					"No internet connection. Check your connection and "
							+ "try again later", Toast.LENGTH_SHORT).show();
		}

	}

	class getNotifications extends AsyncTask<String, String, String> {
		private ProgressDialog pDialog;
		JSONArray MainObject;
		JSONParser jsonParser = new JSONParser();
		int status;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Loading");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
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
			SharedPreferences uid = getActivity().getSharedPreferences("uid",
					Context.MODE_PRIVATE);
			String token = uid.getString("uid", "Aaa");
			JSONObject json = jsonParser.makeHttpRequest(url, "GET", paramse,
					token);
			try {
				status = json.getInt("status");
				if (status == 1) {
					MainObject = json.getJSONArray("data");
				} else {

				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			final String id[] = new String[MainObject.length()];
			if (status == 1) {
				final String header[] = new String[MainObject.length()];
				final String descriptionArray[] = new String[MainObject.length()];
				final String wallsArray[] = new String[MainObject.length()];
				final String dateArray[] = new String[MainObject.length()];
				try {
					for (int i = 0; i < MainObject.length(); i++) {
						JSONObject jsonInside = MainObject.getJSONObject(i);
						JSONObject wall = jsonInside.getJSONObject("wall");
						JSONObject target = jsonInside.getJSONObject("target");
						String post_id = target.getString("id");
						String wallName = wall.getString("name");
						JSONObject actor = jsonInside.getJSONObject("actor");
						String actorName = actor.getString("name");
						String verb = jsonInside.getString("verb");
						String timeStamp = jsonInside.getString("timestamp");
						String description = jsonInside
								.getString("description");
						header[i] = "<b>" + actorName + "</b>" + " " + verb
								+ " " + "<b>" + wallName + "</b> ";
						Date date = null;
						try {
							date = new SimpleDateFormat(
									"yyyy-MM-dd'T'HH:mm:ss'Z'")
									.parse(timeStamp);
							int year = date.getYear();
							dateArray[i] = (String) DateUtils.getRelativeTimeSpanString(date.getTime()+ (long)5.5*3600*1000);
						} catch (ParseException e) {
							e.printStackTrace();
						}

						descriptionArray[i] = description;
						id[i] = post_id;
						wallsArray[i] = wallName;
					}

					NotificationList adapter = new NotificationList(
							getActivity(), header, descriptionArray, dateArray);
					ListView list = (ListView) getView().findViewById(
							R.id.listView1);
					list.setAdapter(adapter);
					list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long useless_id) {

							Intent i = new Intent(getActivity(), PostView.class);
							i.putExtra("post_id", id[+position]);
							i.putExtra("WallName", wallsArray[+position]);
							i.putExtra("passed", "notif");
							startActivity(i);
							// Toast.makeText(getActivity(), "You Clicked at " +
							// header[+ position], Toast.LENGTH_SHORT).show();

						}
					});

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		}

	}

}
