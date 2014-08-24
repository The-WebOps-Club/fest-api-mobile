package org.saarang.erp;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class Walllist extends ArrayAdapter<String> {
	private final Activity context;
	private final String[] discription, allComments, username, subject, date,
			id;
	public static String comment, postId;

	public Walllist(Activity context, String[] discription,
			String[] allComments, String[] username, String[] subject,
			String[] date, String[] id) {
		super(context, R.layout.postview, discription);
		this.context = context;
		this.discription = discription;
		this.allComments = allComments;
		this.username = username;
		this.subject = subject;
		this.date = date;
		this.id = id;

	}

	public View getView(final int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		final View rowView = inflater.inflate(R.layout.postview_single, null,
				true);
		TextView txtTitle = (TextView) rowView.findViewById(R.id.comment1);
		// The Error Bo;y

		txtTitle.setText(Html.fromHtml(allComments[position]));
		TextView txtDescription = (TextView) rowView
				.findViewById(R.id.username);
		txtDescription.setText(username[position]);
		TextView txtTitle1 = (TextView) rowView.findViewById(R.id.subject);
		txtTitle1.setText(subject[position]);
		TextView txtDescription1 = (TextView) rowView
				.findViewById(R.id.description);
		txtDescription1.setText(discription[position]);
		TextView txtDate = (TextView) rowView.findViewById(R.id.tvDate);
		txtDate.setText(date[position]);
		Button bShow = (Button) rowView.findViewById(R.id.Button1);
		bShow.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context, PostView.class);
				intent.putExtra("passed", "passed");
				intent.putExtra("post_id", id[position]);
				intent.putExtra("WallName", subject[position]);
				intent.putExtra("username", username[position]);
				intent.putExtra("date", date[position]);
				intent.putExtra("discription", discription[position]);
				intent.putExtra("allComments", allComments[position]);
				context.startActivity(intent);
			}
		});

		// getComment.requestFocus();
		return rowView;
	}

	class postComments extends AsyncTask<String, String, String> {
		private ProgressDialog pDialog;
		JSONParser jsonParser = new JSONParser();

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			/*
			 * pDialog = new ProgressDialog(Walllist);
			 * pDialog.setMessage("Posting"); pDialog.setIndeterminate(false);
			 * pDialog.setCancelable(false); pDialog.show();
			 */
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String url = "api/mobile/comments/?post_id=" + Walllist.postId;
			List<NameValuePair> paramse = new ArrayList<NameValuePair>();
			paramse.add(new BasicNameValuePair("comment", Walllist.comment));
			JSONObject json = jsonParser.makeHttpRequest(url, "POST", paramse,
					"a26fa6831f477a5e5d88dd418714d20cd2f36cd1");
			return null;

		}

		protected void onPostExecute(String file_url) {
			// pDialog.dismiss();
			// getComment.setText(" ");

		}

		/**
		 * @param args
		 */

	}

}
