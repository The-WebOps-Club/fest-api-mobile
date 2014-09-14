
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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PostView extends Activity {
	//String post_id;
	String actor_name, wall_name;
	public static String post_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.postview);
		android.app.ActionBar ab = getActionBar(); 
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#355088"));     
        ab.setBackgroundDrawable(colorDrawable);
        Bundle extras = getIntent().getExtras();
        String passed = null;
    	   passed = extras.getString("passed");
       
        if (passed.equals("passed")){
        	Log.d("if", "if");
        String subject = extras.getString("WallName");
        String username = extras.getString("username");
        String date = extras.getString("date");
        String discription = extras.getString("discription");
        String allComments = extras.getString("allComments");
        TextView subjectText = (TextView) findViewById(R.id.subject);
		subjectText.setText(subject);
		TextView setDdate = (TextView) findViewById(R.id.tvDate);
		setDdate.setText(date);
		TextView setContent = (TextView) findViewById(R.id.description);
		setContent.setText(discription);
		TextView setUser = (TextView) findViewById(R.id.username);
		setUser.setText(username);
		TextView comments = (TextView) findViewById(R.id.comment1);
		comments.setText(Html.fromHtml(allComments));
        }else if (passed.equals("notif")){
        	   boolean a = false;
       		ConnectivityManager connectivityManager 
                 = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
           NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
           a = activeNetworkInfo != null && activeNetworkInfo.isConnected();
       	if (a){
       		try{
       			new getComments().execute();
       		} catch (Exception e){
       		}
       	}else {
       		Toast.makeText(PostView.this, "No internet connection. Check your connection and " +
       			"try again later", Toast.LENGTH_SHORT).show();
       	}        	
        }
		getActionBar().setDisplayHomeAsUpEnabled(true);
		if (extras != null) {
			post_id = extras.getString("post_id");
			wall_name = extras.getString("WallName");
		}
		setTitle(wall_name);
		Button bPost = (Button) findViewById(R.id.but_comment);
		bPost.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stubduration
				
				try{
					new postComments().execute();	
				}catch(Exception e){
					Toast.makeText(PostView.this, "Not connected to internet", Toast.LENGTH_SHORT).show();
				}
				
				
			}
		});
		

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent back = new Intent(PostView.this, TestMenu.class);
		startActivity(back);
	    return super.onOptionsItemSelected(item);
	}

	class getComments extends AsyncTask<String, String, String> {
		JSONArray MainObject;
		JSONParser jsonParser = new JSONParser();
		JSONObject theData;
		int status;
		String username,subject, content, datePost;
		private ProgressDialog pDialog;
		//PrettyTime p = new PrettyTime();

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(PostView.this);
			pDialog.setMessage("Loading");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
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
			try {
				status = json.getInt("status");
				if (status == 1) {
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
					datePost = (String) DateUtils.getRelativeTimeSpanString(date.getTime()) ;
					} catch (ParseException e) {
					e.printStackTrace();
					}
					MainObject = theData.getJSONArray("comments");
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
			if (status == 1) {
				pDialog.dismiss();
				
				final String descriptionArray[] = new String[MainObject.length()];
				try {
					TextView subjectText = (TextView) findViewById(R.id.subject);
					subjectText.setText(subject);
					TextView setDdate = (TextView) findViewById(R.id.tvDate);
					setDdate.setText(datePost);
					TextView setContent = (TextView) findViewById(R.id.description);
					setContent.setText(content);
					TextView setUser = (TextView) findViewById(R.id.username);
					setUser.setText(username);
					String allComments = "";
					for (int i = 0; i < MainObject.length(); i++) {
						JSONObject jsonInside = MainObject.getJSONObject(i);
						JSONObject actor = jsonInside.getJSONObject("by");
						// String actorName = actor.getString("name");
						String user_id = actor.getString("first_name") + " " +
								actor.getString("last_name");
						String description = jsonInside
								.getString("description");
						String recievedDate = jsonInside.getString("time_created");
						Date date = null;
						String dateComment = " ";
						try {
						date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(recievedDate);
						dateComment =(String) DateUtils.getRelativeTimeSpanString(date.getTime()) ;
						} catch (ParseException e) {
						e.printStackTrace();
						}
						allComments = allComments +  "<br><b>" + user_id 
								+ " </b><br><font color='#899bc1'>"+ dateComment +"</font><br>" +" "+ description + "<br>";
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
			pDialog.setCancelable(true);
			pDialog.show();
		}
		
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String url = "api/mobile/comments/?post_id=" + post_id;
			getComment = (EditText) findViewById(R.id.comment);
			String theComment = getComment.getText().toString();
			List<NameValuePair> paramse = new ArrayList<NameValuePair>();
			paramse.add(new BasicNameValuePair("comment", theComment));
			SharedPreferences uid = getSharedPreferences("uid", MODE_PRIVATE);
			String token = uid.getString("uid", "Aaa");
			JSONObject json = jsonParser.makeHttpRequest(url, "POST", paramse, token);
			return null;
		}

		@Override
		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			getComment.setText(" ");
			Toast.makeText(PostView.this, "Done", Toast.LENGTH_SHORT).show();
			Intent i=new Intent(PostView.this,PostView.class);
            i.putExtra("post_id", post_id);
           i.putExtra("WallName", wall_name);
            i.putExtra("passed", "notif");
            startActivity(i);
		}

		/**
		 * @param args
		 */

	}

}