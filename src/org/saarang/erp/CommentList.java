package org.saarang.erp;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CommentList extends ArrayAdapter<String>{
	private final Activity context;
	private final String[] description;
	
	public CommentList(Activity context, String[] description) {
		super(context, R.layout.commentlist);
		this.context = context;
		
		this.description = description;
		// TODO Auto-generated constructor stub
	}
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView= inflater.inflate(R.layout.commentlist, null, true);
		TextView txtTitle = (TextView) rowView.findViewById(R.id.comment);
		txtTitle.setText(Html.fromHtml(description[position]));
		//TextView txtDescription = (TextView) rowView.findViewById(R.id.textView2);
		//txtDescription.setText(description[position]);
		return rowView;
	}
}

