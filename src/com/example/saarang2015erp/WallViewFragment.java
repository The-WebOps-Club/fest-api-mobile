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

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class WallViewFragment extends Fragment {

	TextView tv;
	int type = 0;
	String tx = null, error = null;
	String userName, password;
	EditText etuserName, etpassword;
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	int status;
	JSONArray theArray;
	JSONArray comment;
	public static String wallId;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.wall, container, false);
	}


	@Override
	public void onStart() {
		super.onStart();		
		wallId = getArguments().getString("page_id");
		new loadWall().execute();
		
		
		
	}



		class loadWall extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
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
				String wallNo = WallViewFragment.wallId;				
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("wall_id", wallNo));
				// Posting user data to script
				Log.d("DF", "Reqeusting");
				String URL = "api/mobile/posts/";
				SharedPreferences uid = getActivity().getSharedPreferences("uid", Context.MODE_PRIVATE);
				String token = uid.getString("uid", "Aaa");
				JSONObject json = jsonParser.makeHttpRequest(URL, "GET",
						params, token);
				theArray = json.getJSONArray("data");
				status = json.getInt("status");


				Log.d("DF", "posted request");


			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product deleted
			pDialog.dismiss();
			final String username[] = new String[theArray.length()];
			final String subject[] = new String[theArray.length()];
			final String discription[] = new String[theArray.length()];
			final String allComments[] = new String[theArray.length()];
			final String dateArray[] = new String[theArray.length()];
			final String id[] = new String[theArray.length()];


			if (status==1){
				for (int i = 0; i < theArray.length(); i++) {
					String completeComment = " ";
					try {
						JSONObject jsonInside = theArray.getJSONObject(i);
						String oneId = Integer.toString(jsonInside.getInt("id"));
						id[i] = oneId;
						JSONObject by=jsonInside.getJSONObject("by");
						 username[i]=by.getString("first_name") +" " + by.getString("last_name");
						 subject[i]=jsonInside.getString("subject");
						 discription[i]=jsonInside.getString("description");
						 comment = jsonInside.getJSONArray("comments");
						 String recievedDateb = jsonInside.getString("time_created");
							Date dateb = null;
							try {
							dateb = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(recievedDateb);
							recievedDateb = new SimpleDateFormat("dd/MM/yyyy").format(dateb).toString();
							} catch (ParseException e) {
							e.printStackTrace();
							}
						dateArray[i] = recievedDateb;
						 for (int j = 0; j < comment.length(); j++) {
							JSONObject commentObject = comment.getJSONObject(j);
							JSONObject commentBy = commentObject.getJSONObject("by");
							String recievedDate = commentObject.getString("time_created");
							Date date = null;
							String dateComment = " ";
							try {
							date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(recievedDate);
							dateComment = new SimpleDateFormat("dd/MM/yyyy").format(date).toString();
							} catch (ParseException e) {
							e.printStackTrace();
							}

							completeComment = completeComment + "<br><b>" +
									commentBy.getString("first_name") + " " + 
									commentBy.getString("last_name") + "</b><br><small>"+dateComment + "</small><br> " + " " +
									commentObject.getString("description") + "<br>";
						}

						 allComments[i] = completeComment;
						
						

						
						
								
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				
				Walllist Adapter = new Walllist(getActivity(), discription, allComments, username, subject, dateArray, id);
				ListView list = (ListView) getView().findViewById(R.id.listView1);
				list.setAdapter(Adapter);
				
				
				
			}

		}

	}




	
}
