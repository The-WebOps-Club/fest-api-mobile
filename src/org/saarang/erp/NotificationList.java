package org.saarang.erp;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NotificationList extends ArrayAdapter<String> {
	private final Activity context;
	private final String[] header, description, date;

	public NotificationList(Activity context, String[] header,
			String[] description, String[] date) {
		super(context, R.layout.single_row_item, header);
		this.context = context;
		this.header = header;
		this.description = description;
		this.date = date;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.single_row_item, null, true);
		TextView txtTitle = (TextView) rowView.findViewById(R.id.tvDate);
		txtTitle.setText(Html.fromHtml(header[position]));
		TextView txtDate = (TextView) rowView.findViewById(R.id.textView3);
		txtDate.setText(date[position]);
		TextView txtDescription = (TextView) rowView
				.findViewById(R.id.textView2);
		txtDescription.setText(description[position]);
		return rowView;
	}

}
