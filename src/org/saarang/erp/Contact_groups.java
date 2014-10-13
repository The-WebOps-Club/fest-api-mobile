package org.saarang.erp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ActionBar.LayoutParams;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Contact_groups extends Fragment {
	public String not;
	 ExpandableListAdapter listAdapter;
	    ExpandableListView expListView;
	    List<String> listDataHeader;
	    HashMap<String, List<String>> listDataChild;
	    private ProgressDialog pDialog;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setHasOptionsMenu(true);
		return inflater.inflate(R.layout.contact_groups, container, false);
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.contacts, menu);
	}
	public void onViewCreated (View view, Bundle savedInstanceState){
		super.onViewCreated(view, savedInstanceState);

		SharedPreferences uid = getActivity().getSharedPreferences("uid",
				Context.MODE_PRIVATE);
		String loadedJSON = uid.getString("contactJSON", "none");
		boolean a = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		a = activeNetworkInfo != null && activeNetworkInfo.isConnected();

		if (loadedJSON != "none") {
			new loadContactsFromPref().execute();
		} else {
			if (a) {
				try {

					new loadContacts().execute();
				} catch (Exception e) {
				}
			} else {
				Toast.makeText(
						getActivity(),
						"No internet connection. Check your connection and "
								+ "try again later", Toast.LENGTH_SHORT).show();
			}}
	}
	public boolean onOptionsItemSelected(MenuItem item) {
		// handle item selection
		switch (item.getItemId()) {
		case R.id.refresh:
			boolean a = false;
			ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetworkInfo = connectivityManager
					.getActiveNetworkInfo();
			a = activeNetworkInfo != null && activeNetworkInfo.isConnected();
			if (a) {
				try {

					new loadContacts().execute();
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
	@Override
	public void onStart() {
        
		super.onStart();
		

		}
	JSONParser jsonParser = new JSONParser();
	
	public void DisplayContacts(final JSONObject contact_struct){
		int i=0;
		JSONArray keys =contact_struct.names();
		Button myButton[] = new Button[contact_struct.length()];
		while(i<contact_struct.length()){
			//String repeated = new String(new char[i]).replace("\0", str);
	
	JSONObject dept = null;
	myButton[i]=new Button(getActivity());
	myButton[i].setId(i);
	try {
		dept = contact_struct.getJSONObject(keys.getString(i));
		Log.d("Milestone",keys.getString(i)+dept.getString("name"));
			myButton[i].setText(dept.getString("name"));
			//myButton[i].setTextColor(Color.parseColor("white"));
			myButton[i].setBackgroundResource(R.drawable.button1);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
			        LayoutParams.WRAP_CONTENT,      
			        LayoutParams.WRAP_CONTENT
			);
			params.setMargins(5,20,5,20);
			myButton[i].setLayoutParams(params);
			//MarginLayoutParams params = (MarginLayoutParams) myButton[i].getLayoutParams();
			//params.width = 200; params.leftMargin = 100;
		//	params.topMargin = 200;
			//myButton[i].setLayoutParams(params);
			//myButton[i].setPadding(5, 5, 5, 5);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	

	LinearLayout ll = (LinearLayout)getView().findViewById(R.id.LinLayout);
	LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	ll.addView(myButton[i], lp);
	final String deptString=dept.toString();
	myButton[i].setOnClickListener(new View.OnClickListener() {
	    public void onClick(View v) {
	    	Intent contactview = new Intent(getActivity(), Contacts.class);
			contactview.putExtra("json",deptString);
			startActivity(contactview);
	    
	    }
	});
		i++;
		}
		Button search=(Button) getView().findViewById(R.id.search);
		final EditText searchq= (EditText) getView().findViewById(R.id.editText1);
		search.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	Intent searchr = new Intent(getActivity(), SearchResults.class);
		    	searchr.putExtra("searchq",searchq.getText().toString());
		    	searchr.putExtra("json",contact_struct.toString());
				startActivity(searchr);
		    
		    }
		});
		

	}
	
	
	
	
	class loadContacts extends AsyncTask<String, String, String> {
		SharedPreferences uid;
		int status;
		JSONObject contact_struct;
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
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				//params.add(new BasicNameValuePair("Contacts_id", ContactsNo));
				// Posting user data to script
				String URL = "static/json/user_structure.json";
				uid = getActivity().getSharedPreferences("uid",
						Context.MODE_PRIVATE);
				String token = uid.getString("uid", "Aaa");
				JSONObject json = jsonParser.makeHttpRequest(URL, "GET",
						params, token);
				Log.d("json",json.toString());
				SharedPreferences.Editor editor = uid.edit();
				editor.putString("contactJSON", json.toString());
				editor.commit();
				contact_struct=json;
				status = 1;

			} catch (Exception e) {
				status=0;
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product deleted
			pDialog.dismiss();
			if (status == 1) {
				DisplayContacts(contact_struct);
		}

	}
	}
	class loadContactsFromPref extends AsyncTask<String, String, String> {
		SharedPreferences uid;
		int status;
		JSONObject contact_struct;
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
				SharedPreferences uid = getActivity().getSharedPreferences(
						"uid", Context.MODE_PRIVATE);
				String jsonString = uid.getString("contactJSON",
						"none");
				JSONObject json = new JSONObject(jsonString);
				//MainObject = json.getJSONArray("data");
				status = 1;
				contact_struct=json;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product deleted
			pDialog.dismiss();
			if (status == 1) {
				DisplayContacts(contact_struct);
			}
			}
		}
	
}