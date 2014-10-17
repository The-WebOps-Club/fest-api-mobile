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
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Contacts extends Activity {
	public String not;
	 ExpandableListAdapter listAdapter;
	    ExpandableListView expListView;
	    List<String> listDataHeader;
	    HashMap<String, List<String>> listDataChild,listDataChild2,listDataChild3;
	    JSONObject deptJSON,coresJSON;
	    Context context;
	    List<String> Core,CoreMail,CoreTel,SuperCoords,CoordMail,CoordTel,Subdept;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		android.app.ActionBar ab = getActionBar();
		ColorDrawable colorDrawable = new ColorDrawable(
				Color.parseColor("#355088"));
		ab.setBackgroundDrawable(colorDrawable);
		setContentView(R.layout.contacts);
		context = getApplicationContext();
		String deptJSONasString=null,coresJSONasString=null;
		deptJSON=coresJSON=null;
		if (extras != null) {
			deptJSONasString = extras.getString("json");
			coresJSONasString = extras.getString("cores_json");
		}
		try {
			deptJSON= new JSONObject(deptJSONasString);
			coresJSON=new JSONObject(coresJSONasString);
			setTitle(deptJSON.getString("name"));
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
		// get the listview
        expListView = (ExpandableListView) findViewById(R.id.expandableListView1);
 
        // preparing list data
        try {
			prepareListData();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild,listDataChild2,listDataChild3);
 
        // setting list adapter
        expListView.setAdapter(listAdapter);
        for(int i=0; i < listAdapter.getGroupCount(); i++)
        	expListView.expandGroup(i);
        
		// TODO Auto-generated method stub

	}
	/*
     * Preparing the list data
     */
	void getUsers(JSONObject HasUserJSON,boolean type) throws JSONException{
		 
		JSONObject users = HasUserJSON.getJSONObject("users");
		JSONArray keys =users.names();
	        JSONObject name;
	        int i=0;
	        while(i<users.length())
	        {	String fullname,tel,mail;
	        	name= users.getJSONObject(keys.getString(i));
	        	tel=name.getString("mobile_number");
	        	mail=name.getString("email");
	        	fullname=name.getString("first_name")+" " +name.getString("last_name");
	        	//Log.d("name",fullname);
	        	
	        	if(type){
	        		if(searchUsers(coresJSON, fullname)&& !HasUserJSON.getString("name").equals("Cores")){
	        			Core.add(fullname);
	        			CoreMail.add(mail);
	        			CoreTel.add(tel);
	        		}
	        		else{
	        			SuperCoords.add(fullname);
	        			CoordMail.add(mail);
		        		CoordTel.add(tel);
	        		}
	        	}
	        	else{
	        		Subdept.add(fullname);
	        		CoordMail.add(mail);
	        		CoordTel.add(tel);}
	        	i++;
	        }
		
	}
    private void prepareListData() throws JSONException {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        listDataChild2 = new HashMap<String, List<String>>();
        listDataChild3 = new HashMap<String, List<String>>();
        // Adding child data
        
        
        //Lo
        // Adding child data
        Core= new ArrayList<String>();
        CoreMail=new ArrayList<String>();
        CoreTel=new ArrayList<String>();
        SuperCoords = new ArrayList<String>();
        CoordTel = new ArrayList<String>();
        CoordMail = new ArrayList<String>();
        
        getUsers(deptJSON,true);
        
        int i=0,j=0;
        
        //List<List<String>> group = new ArrayList<List<String>>(subdepts.length());
        if(deptJSON.getString("name").equals("Cores")){
        	listDataHeader.add("Members");
        	listDataChild.put(listDataHeader.get(j), SuperCoords);
        	listDataChild2.put(listDataHeader.get(j), CoordTel);
        	listDataChild3.put(listDataHeader.get(j), CoordMail);
        }
        else{
        	listDataHeader.add("Cores");
        	listDataChild.put(listDataHeader.get(j), Core);
        	listDataChild2.put(listDataHeader.get(j), CoreTel);
        	listDataChild3.put(listDataHeader.get(j), CoreMail);
        	if(!SuperCoords.isEmpty()){
        	j++;
        	listDataHeader.add("SuperCoords");
        	listDataChild.put(listDataHeader.get(j), SuperCoords);
        	listDataChild2.put(listDataHeader.get(j), CoordTel);
        	listDataChild3.put(listDataHeader.get(j), CoordMail);
        	}
        }
        j++;
        JSONObject subdepts = deptJSON.getJSONObject("subdepts");
        
        JSONArray keys =subdepts.names();
        while(i<subdepts.length()) {
        	Subdept = new ArrayList<String>();
        	CoordTel = new ArrayList<String>();
            CoordMail = new ArrayList<String>();
	        JSONObject subdept;
	        String fullname;
	        subdept= subdepts.getJSONObject(keys.getString(i));
        	getUsers(subdept,false);
        	String subdeptname=subdept.getString("name");
        	listDataHeader.add(subdeptname);
        	listDataChild.put(listDataHeader.get(i+j), Subdept);
        	listDataChild2.put(listDataHeader.get(i+j), CoordTel);
        	listDataChild3.put(listDataHeader.get(i+j), CoordMail);
        	i++;
        }
    }
    boolean searchUsers(JSONObject HasUserJSON,String searchq) throws JSONException{
		JSONObject users = HasUserJSON.getJSONObject("users");
		JSONArray keys =users.names();
	        JSONObject name;
	        int i=0;
	        boolean user_found=false;
	        while(i<users.length())
	        {	String fullname;
	        	name= users.getJSONObject(keys.getString(i));
	        	fullname=name.getString("first_name")+" " +name.getString("last_name");
	        	if(fullname.toLowerCase().contains(searchq.toLowerCase())){
	        	Log.d("corenamefound",fullname);
	        	user_found=true;}
	        	i++;
	        }
		return user_found;
	}
    
    
    
}
