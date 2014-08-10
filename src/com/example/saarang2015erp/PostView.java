package com.example.saarang2015erp;

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

import com.example.saarang2015erp.Notifications.getNotifications;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Global;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PostView extends Activity {
	//String post_id;
	String actor_name, wall_name;
	public static String post_id;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.postview);
		android.app.ActionBar ab = getActionBar(); 
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#355088"));     
        ab.setBackgroundDrawable(colorDrawable);
		new getComments().execute();
		getActionBar().setDisplayHomeAsUpEnabled(true);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			post_id = extras.getString("post_id");
			Log.d("its ", "here");
			Log.d("postid", post_id);
			wall_name = extras.getString("WallName");
			Log.d("wall name", wall_name);
		}
		setTitle(wall_name);
		Button bPost = (Button) findViewById(R.id.but_comment);
		bPost.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stubduration
				
				Toast.makeText(PostView.this, "I clicked", Toast.LENGTH_SHORT).show();
				Log.d("Click", "I clicked");
				new postComments().execute();
				
			}
		});
		

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent back = new Intent(PostView.this, Menu.class);
		startActivity(back);
	    return super.onOptionsItemSelected(item);
	}

	class getComments extends AsyncTask<String, String, String> {
		JSONArray theArray;
		JSONParser jsonParser = new JSONParser();
		JSONObject theData;
		int status;
		String username,subject, content, datePost;
		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(PostView.this);
			pDialog.setMessage("Loading");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		
		
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
					theData = json.getJSONObject("data");
					JSONObject by = theData.getJSONObject("by");
					username = by.getString("first_name") + " " + by.getString("last_name"); 
					subject = theData.getString("subject");
					content = theData.getString("description");
					String recievedDate = theData.getString("time_created");
					Date date = null;
					try {
					date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(recievedDate);
					int year = date.getYear();
					datePost = new SimpleDateFormat("dd/MM/yyyy").format(date).toString();
					Log.d("Year", new SimpleDateFormat("dd/MM/yyyy").format(date).toString());
					} catch (ParseException e) {
					e.printStackTrace();
					}
					theArray = theData.getJSONArray("comments");
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
				pDialog.dismiss();
				
				final String descriptionArray[] = new String[theArray.length()];
				try {
					TextView subjectText = (TextView) findViewById(R.id.subject);
					subjectText.setText(subject);
					TextView setDdate = (TextView) findViewById(R.id.tvDate);
					setDdate.setText(datePost);
					TextView setContent = (TextView) findViewById(R.id.description);
					setContent.setText(content);
					TextView setUser = (TextView) findViewById(R.id.username);
					setUser.setText(username);
					Log.d("Thesub", subject);
					String allComments = "";
					for (int i = 0; i < theArray.length(); i++) {
						JSONObject jsonInside = theArray.getJSONObject(i);
						JSONObject actor = jsonInside.getJSONObject("by");
						// String actorName = actor.getString("name");
						String user_id = actor.getString("first_name") + " " +
								actor.getString("last_name");
						String description = jsonInside
								.getString("description");
						String recievedDate = jsonInside.getString("time_created");
						Log.d("Date", recievedDate);
						Date date = null;
						String dateComment = " ";
						try {
						date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(recievedDate);
						dateComment = new SimpleDateFormat("dd/MM/yyyy").format(date).toString();
						Log.d("Year", new SimpleDateFormat("dd/MM/yyyy").format(date).toString());
						} catch (ParseException e) {
						e.printStackTrace();
						}
						allComments = allComments +  "<br><b>" + user_id 
								+ " </b><br>"+ dateComment +"<br>" +" "+ description + "<br>";
					}
					
					
/*
					ListView list = (ListView) findViewById(R.id.listView1);
					// CommentList adapter= new
					// CommentList(PostView.this,descriptionArray );
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(
							PostView.this, R.layout.commentlist,
							descriptionArray) {
						@Override
						public View getView(int position, View convertView,
								ViewGroup parent) {
							TextView tv = (TextView) super.getView(position,
									convertView, parent);
							String existingText;
							existingText = tv.getText().toString();
							tv.setText(Html.fromHtml(existingText));
							
							return tv;
						} 
					};
					list.setAdapter(adapter);*/
					TextView comments = (TextView) findViewById(R.id.comment1);
					comments.setText(Html.fromHtml(allComments));
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

	class postComments extends AsyncTask<String, String, String> {
		private ProgressDialog pDialog;
		JSONParser jsonParser = new JSONParser();
		EditText getComment;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(PostView.this);
			pDialog.setMessage("Posting");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			Log.d("postid", post_id);
			String url = "api/mobile/comments/?post_id=" + post_id;
			getComment = (EditText) findViewById(R.id.comment);
			String theComment = getComment.getText().toString();
			List<NameValuePair> paramse = new ArrayList<NameValuePair>();
			paramse.add(new BasicNameValuePair("comment", theComment));
			SharedPreferences uid = getSharedPreferences("uid", MODE_PRIVATE);
			String token = uid.getString("uid", "Aaa");
			Log.d("toke", token);
			JSONObject json = jsonParser.makeHttpRequest(url, "POST", paramse, token);
			Log.d("JSON ", json.toString());		
			return null;
		}

		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			getComment.setText(" ");
			PostView.this.recreate();
		}

		/**
		 * @param args
		 */

	}

}
