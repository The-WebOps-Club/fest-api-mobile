package org.saarang.erp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SearchResults extends Activity {
	public String not;
	 ExpandableListAdapter listAdapter;
	    ExpandableListView expListView;
	    List<String> listDataHeader;
	    HashMap<String, List<String>> listDataChild,listDataChild2,listDataChild3;
	    JSONObject contact_struct,dept;
	    Context context;
	    String searchq;
	    List<String> SuperCoords,CoordMail,CoordTel,Subdept;
	    int k=0;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		android.app.ActionBar ab = getActionBar();
		ColorDrawable colorDrawable = new ColorDrawable(
				Color.parseColor("#355088"));
		ab.setBackgroundDrawable(colorDrawable);
		setContentView(R.layout.contacts);
		context = getApplicationContext();
		String deptJSONasString=null; searchq=null;
		contact_struct=null;
		if (extras != null) {
			deptJSONasString = extras.getString("json");
			searchq=extras.getString("searchq");
		}
		try {
			contact_struct= new JSONObject(deptJSONasString);
			setTitle("Search Results for : \'" + searchq+"\'" );
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
    public void onBackPressed() {
       
		super.onBackPressed();
    }
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.contacts, menu);
	}
	@Override
	public void onStart() {
        
		super.onStart();
		boolean a = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		a = activeNetworkInfo != null && activeNetworkInfo.isConnected();
		if (a) {
			try {
				//new getContacts().execute();
			} catch (Exception e) {
			}
		} else {
			Toast.makeText(
					context,
					"No internet connection. Check your connection and "
							+ "try again later", Toast.LENGTH_SHORT).show();
		}
		// get the listview
        expListView = (ExpandableListView) findViewById(R.id.expandableListView1);
        JSONArray mainkeys =contact_struct.names();
        int i=0;
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        listDataChild2 = new HashMap<String, List<String>>();
        listDataChild3 = new HashMap<String, List<String>>();
        while(i<contact_struct.length()){
			try {
				Log.d("Milestone",mainkeys.getString(i));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
			//String repeated = new String(new char[i]).replace("\0", str);
	
	//dept=null;
	try {
		dept = contact_struct.getJSONObject(mainkeys.getString(i));
	} catch (JSONException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	try {
		prepareListData();
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	
	try {
		Log.d("Milestone- DeptName",dept.getString("name"));
	} catch (JSONException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	i++;
		}
 
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild,listDataChild2,listDataChild3);
        // setting list adapter
        expListView.setAdapter(listAdapter);
        for(int j=0; j < listAdapter.getGroupCount(); j++)
        	expListView.expandGroup(j);
		// TODO Auto-generated method stub

	}
	/*
     * Preparing the list data
     */
	boolean searchUsers(JSONObject HasUserJSON,boolean type) throws JSONException{
		 
		JSONObject users = HasUserJSON.getJSONObject("users");
		JSONArray keys =users.names();
	        JSONObject name;
	        int i=0;
	        type=false;
	        while(i<users.length())
	        {	String fullname,tel,mail;
	        	name= users.getJSONObject(keys.getString(i));
	        	tel=name.getString("mobile_number");
	        	mail=name.getString("email");
	        	fullname=name.getString("first_name")+" " +name.getString("last_name");
	        	Log.d("name",fullname);
	        	if(fullname.toLowerCase().contains(searchq.toLowerCase())){
        	Log.d("namefound",fullname);
	        	CoordMail.add(mail);
	        	CoordTel.add(tel);
	        	Subdept.add(fullname);
	        	type=true;}
	        	
	        	i++;
	        }
		return type;
	}
	
    private void prepareListData() throws JSONException {
        // Adding child data
       SuperCoords = new ArrayList<String>();
       CoordTel = new ArrayList<String>();
       CoordMail = new ArrayList<String>();
       Subdept = new ArrayList<String>();
        int i=0;
        Log.d("1212","done");
        JSONObject subdepts = dept.getJSONObject("subdepts");
        String deptname= dept.getString("name");
        
        if(searchUsers(dept,true)){
        	if(dept.getString("name").equals("Cores"))
            	listDataHeader.add(deptname);
            else
        	listDataHeader.add(deptname+" - SuperCoords");
        	
        	listDataChild.put(listDataHeader.get(k), Subdept);
        	
        	listDataChild2.put(listDataHeader.get(k), CoordTel);
        
        	listDataChild3.put(listDataHeader.get(k), CoordMail);
        	k++;
        }
        
        JSONArray keys =subdepts.names();
        while(i<subdepts.length()) {
        	Subdept = new ArrayList<String>();
        	CoordTel = new ArrayList<String>();
            CoordMail = new ArrayList<String>();
	        JSONObject subdept;
	        String fullname;
	        subdept= subdepts.getJSONObject(keys.getString(i));
        	if(searchUsers(subdept,false)){
        	Log.d("not empty?","y");
        	String subdeptname=subdept.getString("name");
        	listDataHeader.add(deptname+" - "+subdeptname);
        	listDataChild.put(listDataHeader.get(k), Subdept);
        	listDataChild2.put(listDataHeader.get(k), CoordTel);
        	listDataChild3.put(listDataHeader.get(k), CoordMail);
        	k++;
        	}
        	i++;
        }
    }
    
    public JSONObject getContactsObj() {
		String url = "static/json/user_structure.json";
		// "api/mobile/walls/";
		List<NameValuePair> paramse = new ArrayList<NameValuePair>();
		//paramse.add(new BasicNameValuePair("limit", "30"));
		//paramse.add(new BasicNameValuePair("offset", "0"));
		SharedPreferences uid = this.getSharedPreferences("uid",
				Context.MODE_PRIVATE);
		String token = uid.getString("uid", "Aaa");
		JSONParser jsonParser=new JSONParser();
		JSONObject MainObject = jsonParser.makeHttpRequest(url, "GET", paramse,
				token);
		Log.d("Milestone","Reached Here");
		return MainObject;
	}
    
	/* class getContacts extends AsyncTask<String, String, String> {
		private ProgressDialog pDialog;
		JSONObject MainObject;
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
			String url = "static/json/user_structure.json";
			// "api/mobile/walls/";
			List<NameValuePair> paramse = new ArrayList<NameValuePair>();
			//paramse.add(new BasicNameValuePair("limit", "30"));
			//paramse.add(new BasicNameValuePair("offset", "0"));
			SharedPreferences uid = getActivity().getSharedPreferences("uid",
					Context.MODE_PRIVATE);
			String token = uid.getString("uid", "Aaa");
			MainObject = jsonParser.makeHttpRequest(url, "GET", paramse,
					token);
			Log.d("Milestone","Reached Here");
			return null;
		}

		/*protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			final String id[] = new String[MainObject.length()];
			if (true) {
				final String header[] = new String[MainObject.length()];
				final String descriptionArray[] = new String[MainObject.length()];
				final String wallsArray[] = new String[MainObject.length()];
				final String dateArray[] = new String[MainObject.length()];
				final String DeptNameArray[] = new String[MainObject.length()+2];
				try {
					for (int i = 0; i < MainObject.length()+2; i++) {
						if(MainObject.has(Integer.toString(i))==false)
							break;							;
						JSONObject jsonInside = MainObject.getJSONObject(Integer.toString(i));
						JSONObject subDepts= jsonInside.getJSONObject("subdepts");
						JSONObject target = jsonInside.getJSONObject("target");
						//String post_id = target.getString("id");
						String DeptName = jsonInside.getString("name");
						//JSONObject actor = jsonInside.getJSONObject("actor");
						//String actorName = actor.getString("name");
						//String verb = jsonInside.getString("verb");
						//String timeStamp = jsonInside.getString("timestamp");
						//String description = jsonInside
						//		.getString("description");
						//header[i] = "<b>" + actorName + "</b>" + " " + verb
						//		+ " " + "<b>" + wallName + "</b> ";
						//Date date = null;
						DeptNameArray[i]=DeptName;
						//descriptionArray[i] = description;
						//id[i] = post_id;
						//wallsArray[i] = wallName;
					}

					NotificationList adapter = new NotificationList(
							getActivity(), header, descriptionArray, dateArray);
					ListView list = (ListView) getView().findViewById(
							R.id.listView1);
					list.setAdapter(adapter);
					/*list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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
					});*/
/*
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		}

	}*/

}
