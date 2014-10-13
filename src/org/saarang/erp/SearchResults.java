package org.saarang.erp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ExpandableListView;

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
		Log.d("Startingsearch","yup");
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
	@Override
    public void onResume() {
		Log.d("in onresume?","yup");
		super.onResume();
		//finish();
    }
	@Override
    public void onPause() {
       
		super.onPause();
		//finish();
    }
	@Override
	public void onStop() {
		Log.d("stopped searchresults activity?","yes");
		finish(); 
		super.onStop();
		//finish();
    }
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.contacts, menu);
	}
	@Override
	public void onStart() {
		Log.d("in onstart?","yup");
		super.onStart();
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
}
