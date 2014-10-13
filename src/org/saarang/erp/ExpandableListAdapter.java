
package org.saarang.erp;
 
import java.util.HashMap;
import java.util.List;
 
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
 
public class ExpandableListAdapter extends BaseExpandableListAdapter {
 
    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild,_listDataChild2,_listDataChild3;
 
    public ExpandableListAdapter(Context context, List<String> listDataHeader,
            HashMap<String, List<String>> listChildData,HashMap<String, List<String>> listChildData2,HashMap<String, List<String>> listChildData3) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this._listDataChild2 = listChildData2;
        this._listDataChild3 = listChildData3;
    }
 
    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }
    public Object getChild2(int groupPosition, int childPosititon) {
        return this._listDataChild2.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }
    public Object getChild3(int groupPosition, int childPosititon) {
        return this._listDataChild3.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
    boolean flag=false;
    @Override
    public View getChildView(int groupPosition, int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
 
        String childText = (String) getChild(groupPosition, childPosition);
        final String coordMail = (String) getChild3(groupPosition, childPosition);
       final String childTel = (String) getChild2(groupPosition, childPosition);
       //Log.d(coordMail+childTel,Integer.toString(groupPosition)+Integer.toString(childPosition));
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }
 
        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);
        childText=capitalizeSentence(childText);
        txtListChild.setText(childText);
        TextView coordtel = (TextView) convertView
                .findViewById(R.id.ECoordTel);
        ImageButton b1 = (ImageButton) convertView.findViewById(R.id.email);
        b1.setOnClickListener(new OnClickListener() {
		public void onClick(View arg0) {
			Intent mailIntent = new Intent();
			  mailIntent.setAction(Intent.ACTION_SEND);
			  mailIntent.setType("message/rfc822");
			  String[] myStrings = { coordMail};
			  mailIntent.putExtra(Intent.EXTRA_EMAIL, myStrings);
			  mailIntent.putExtra(Intent.EXTRA_SUBJECT, "Saarang:");
			  mailIntent.putExtra(Intent.EXTRA_TEXT, "\n\n-sent from the ERP android app");
			  _context.startActivity(mailIntent);
		}});
        ImageButton b2 = (ImageButton) convertView.findViewById(R.id.call);
        coordtel.setText("Email:"+coordMail);
        if(!childTel.isEmpty()){
        	coordtel.setText("Mob: "+childTel+"\nEmail: "+coordMail);
        	b2.setVisibility(View.VISIBLE);
        }
        else
        	b2.setVisibility(View.INVISIBLE);
        b2.setOnClickListener(new OnClickListener() {
		public void onClick(View arg0) {
			String uri = "tel:" + childTel.trim() ;
			 Intent intent = new Intent(Intent.ACTION_DIAL);
			 intent.setData(Uri.parse(uri));
			 _context.startActivity(intent);
			
		}});
        return convertView;
    }
 
    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }
 
    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }
 
    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }
 
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
 
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }
 
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
 
        return convertView;
    }
    static public String firstLetterCaps ( String data )
    {
        String firstLetter = data.substring(0,1).toUpperCase();
        String restLetters = data.substring(1).toLowerCase();
        return firstLetter + restLetters;
    }
    private static String capitalizeSentence(String sentence) {
    	char[] characters = sentence.toCharArray();
    	boolean capitalizeWord = true;
    	
    	for (int i = 0; i < characters.length; i++) {
    		char c = characters[i];
    		
    		if (Character.isWhitespace(c)) {
    			capitalizeWord = true;
    		}
    		else if (capitalizeWord) {
    			capitalizeWord = false;
    			characters[i] = Character.toUpperCase(c);
    		}
    		else {
    			characters[i] = Character.toLowerCase(c);
    		}
    	}
    	
    	return new String(characters);
    }


    @Override
    public boolean hasStableIds() {
        return false;
    }
 
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}