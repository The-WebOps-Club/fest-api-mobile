package org.saarang.erp;

import java.text.ParseException;
import android.text.format.DateUtils;

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
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class WallViewFragment extends Fragment {

	TextView tv;
	long shift_timezone= (long) (5.5*3600*1000);
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
	public static int wallNo;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setHasOptionsMenu(true);

		return inflater.inflate(R.layout.wall, container, false);

	}

	@Override
	public void onStart() {
		super.onStart();

		wallId = getArguments().getString("page_id");
		wallNo = getArguments().getInt("wallNo");
		SharedPreferences uid = getActivity().getSharedPreferences("uid",
				Context.MODE_PRIVATE);
		String loadedJSON = uid.getString("theJSONwall_" + wallId, "none");
		boolean a = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		a = activeNetworkInfo != null && activeNetworkInfo.isConnected();

		if (loadedJSON != "none") {
			new loadWallFromPref().execute();
		} else {
			if (a) {
				try {

					new loadWall().execute();
				} catch (Exception e) {
				}
			} else {
				Toast.makeText(
						getActivity(),
						"No internet connection. Check your connection and "
								+ "try again later", Toast.LENGTH_SHORT).show();
			}

		}

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.main, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// handle item selection
		switch (item.getItemId()) {
		case R.id.new_post:
			Intent newPost = new Intent("com.example.saarang2015erp.NewPost");
			newPost.putExtra("wall_id", wallId);
			newPost.putExtra("wallNo", wallNo);
			startActivity(newPost);
			return true;
		case R.id.refresh:
			boolean a = false;
			ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetworkInfo = connectivityManager
					.getActiveNetworkInfo();
			a = activeNetworkInfo != null && activeNetworkInfo.isConnected();
			if (a) {
				try {

					new loadWall().execute();
				} catch (Exception e) {
				}
			} else {
				Toast.makeText(
						getActivity(),
						"No internet connection. Check your connection and "
								+ "try again later", Toast.LENGTH_SHORT).show();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	class loadWall extends AsyncTask<String, String, String> {
		SharedPreferences uid;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Loading...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
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
				String URL = "api/mobile/posts/";
				uid = getActivity().getSharedPreferences("uid",
						Context.MODE_PRIVATE);
				String token = uid.getString("uid", "Aaa");
				JSONObject json = jsonParser.makeHttpRequest(URL, "GET",
						params, token);
				SharedPreferences.Editor editor = uid.edit();
				editor.putString("theJSONwall_" + wallNo, json.toString());
				editor.commit();
				theArray = json.getJSONArray("data");

				status = 0;
				status = json.getInt("status");

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product deleted
			pDialog.dismiss();
			final String username[] = new String[theArray.length()];
			final String subject[] = new String[theArray.length()];
			final String discription[] = new String[theArray.length()];
			final String allComments[] = new String[theArray.length()];
			final String dateArray[] = new String[theArray.length()];
			final String id[] = new String[theArray.length()];

			if (status == 1) {
				for (int i = 0; i < theArray.length(); i++) {
					String completeComment = " ";
					try {
						JSONObject jsonInside = theArray.getJSONObject(i);
						String oneId = Integer
								.toString(jsonInside.getInt("id"));
						id[i] = oneId;
						JSONObject by = jsonInside.getJSONObject("by");
						username[i] = by.getString("first_name") + " "
								+ by.getString("last_name");
						subject[i] = jsonInside.getString("subject");
						discription[i] = jsonInside.getString("description");
						comment = jsonInside.getJSONArray("comments");
						String recievedDateb = jsonInside
								.getString("time_created");
						Date dateb = null;
						try {
							dateb = new SimpleDateFormat(
									"yyyy-MM-dd'T'HH:mm:ss'Z'")
									.parse(recievedDateb);

							recievedDateb = (String) DateUtils
									.getRelativeTimeSpanString(dateb.getTime()+shift_timezone);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						dateArray[i] = recievedDateb;
						for (int j = 0; j < comment.length(); j++) {
							JSONObject commentObject = comment.getJSONObject(j);
							JSONObject commentBy = commentObject
									.getJSONObject("by");
							String recievedDate = commentObject
									.getString("time_created");
							Date date = null;
							String dateComment = " ";
							try {
								date = new SimpleDateFormat(
										"yyyy-MM-dd'T'HH:mm:ss'Z'")
										.parse(recievedDate);

								dateComment = (String) DateUtils.getRelativeTimeSpanString(date.getTime()+shift_timezone) ;
							} catch (ParseException e) {
								e.printStackTrace();
							}

							completeComment = completeComment + "<br><b>"
									+ commentBy.getString("first_name") + " "
									+ commentBy.getString("last_name")
									+ "</b><br><small><font color='#899bc1'>"
									+ dateComment + "</font></small><br> "
									+ " "
									+ commentObject.getString("description")
									+ "<br>";
						}

						allComments[i] = completeComment;

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				Walllist Adapter = new Walllist(getActivity(), discription,
						allComments, username, subject, dateArray, id);
				ListView list = (ListView) getView().findViewById(
						R.id.listView1);
				list.setAdapter(Adapter);

			}

		}

	}

	class loadWallFromPref extends AsyncTask<String, String, String> {
		SharedPreferences uid;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Loading...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Check for success tag

			try {
				String wallNo = WallViewFragment.wallId;
				SharedPreferences uid = getActivity().getSharedPreferences(
						"uid", Context.MODE_PRIVATE);
				String jsonString = uid.getString("theJSONwall_" + wallNo,
						"none");
				JSONObject json = new JSONObject(jsonString);
				theArray = json.getJSONArray("data");
				status = 1;

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product deleted
			pDialog.dismiss();
			final String username[] = new String[theArray.length()];
			final String subject[] = new String[theArray.length()];
			final String discription[] = new String[theArray.length()];
			final String allComments[] = new String[theArray.length()];
			final String dateArray[] = new String[theArray.length()];
			final String id[] = new String[theArray.length()];

			for (int i = 0; i < theArray.length(); i++) {
				String completeComment = " ";
				try {
					JSONObject jsonInside = theArray.getJSONObject(i);
					String oneId = Integer.toString(jsonInside.getInt("id"));
					id[i] = oneId;
					JSONObject by = jsonInside.getJSONObject("by");
					username[i] = by.getString("first_name") + " "
							+ by.getString("last_name");
					subject[i] = jsonInside.getString("subject");
					discription[i] = jsonInside.getString("description");
					comment = jsonInside.getJSONArray("comments");
					String recievedDateb = jsonInside.getString("time_created");
					Date dateb = null;
					try {
						dateb = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
								.parse(recievedDateb);
						recievedDateb = (String) DateUtils.getRelativeTimeSpanString(dateb.getTime()+shift_timezone) ;

					} catch (ParseException e) {
						e.printStackTrace();
					}
					dateArray[i] = recievedDateb;
					for (int j = 0; j < comment.length(); j++) {
						JSONObject commentObject = comment.getJSONObject(j);
						JSONObject commentBy = commentObject
								.getJSONObject("by");
						String recievedDate = commentObject
								.getString("time_created");
						Date date = null;
						String dateComment = " ";
						try {
							date = new SimpleDateFormat(
									"yyyy-MM-dd'T'HH:mm:ss'Z'")
									.parse(recievedDate);
							dateComment = (String) DateUtils.getRelativeTimeSpanString(date.getTime()+shift_timezone) ;
						} catch (ParseException e) {
							e.printStackTrace();
						}

						completeComment = completeComment + "<br><b>"
								+ commentBy.getString("first_name") + " "
								+ commentBy.getString("last_name")
								+ "</b><br><small>" + dateComment
								+ "</small><br> " + " "
								+ commentObject.getString("description")
								+ "<br>";
					}

					allComments[i] = completeComment;

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			Walllist Adapter = new Walllist(getActivity(), discription,
					allComments, username, subject, dateArray, id);
			ListView list = (ListView) getView().findViewById(R.id.listView1);
			list.setAdapter(Adapter);

		}

	}

}
