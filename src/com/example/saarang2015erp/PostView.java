package com.example.saarang2015erp;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.saarang2015erp.Notifications.getNotifications;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Global;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PostView extends Activity {
	String post_id;
	String actor_name;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.postview);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			post_id = extras.getString("post_id");
		}
		setTitle("Post");

		new getComments().execute();

	}

	class getComments extends AsyncTask<String, String, String> {
		JSONArray theArray;
		JSONParser jsonParser = new JSONParser();
		JSONObject theDaya;
		int status;
		String subject, content;

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			String url = "api/mobile/comments/";
			List<NameValuePair> paramse = new ArrayList<NameValuePair>();
			paramse.add(new BasicNameValuePair("post_id", post_id));
			SharedPreferences uid = getSharedPreferences("uid", MODE_PRIVATE);
			String token = uid.getString("uid", "Aaa");
			JSONObject json = jsonParser.makeHttpRequest(url, "GET", paramse,
					token);
			Log.d("post_id", post_id);
			try {
				Log.d("json", json.toString());
				status = json.getInt("status");
				Log.d("Success or not?", Integer.toString(status));
				if (status == 1) {
					Log.d("message", "its success dude");
					theDaya = json.getJSONObject("data");
					subject = theDaya.getString("subject");
					content = theDaya.getString("description");
					theArray = theDaya.getJSONArray("comments");
				} else {

				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String file_url) {
			if (status == 1) {
				
				final String descriptionArray[] = new String[theArray.length()];
				try {
					TextView subjectText = (TextView) findViewById(R.id.subject);
					subjectText.setText(subject);
					TextView setContent = (TextView) findViewById(R.id.description);
					setContent.setText(content);
					Log.d("Thesub", subject);
					for (int i = 0; i < theArray.length(); i++) {
						JSONObject jsonInside = theArray.getJSONObject(i);
						JSONObject actor = jsonInside.getJSONObject("by");
						// String actorName = actor.getString("name");
						String user_id = actor.getString("first_name") + " " +
								actor.getString("last_name");
						String description = jsonInside
								.getString("description");
						// Log.d("heading", actorName + " " + verb + " " );

						// header[i] = "<b>" +actorName + "</b>" + " " + verb +
						// " "+"<b>" + "</b>";
						descriptionArray[i] = "<b><small>" + user_id
								+ " </small></b> <br>" + description;
					}
					
					

					ListView list = (ListView) findViewById(R.id.listView1);
					// CommentList adapter= new
					// CommentList(PostView.this,descriptionArray );
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(
							PostView.this, android.R.layout.simple_list_item_1,
							descriptionArray) {
						@Override
						public View getView(int position, View convertView,
								ViewGroup parent) {
							TextView tv = (TextView) super.getView(position,
									convertView, parent);
							String existingText;
							existingText = tv.getText().toString();
							tv.setText(Html.fromHtml(existingText));
							/*
							 * if (position < 3) { tv.setTextSize(24.0f); } else
							 * { tv.setTextSize(14.0f); }
							 */
							return tv;
						}
					};
					list.setAdapter(adapter);

				}

				catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

		/**
		 * @param args
		 */

	}
}
