package org.saarang.erp;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.saarang.erp.PostView.postComments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewPost extends Activity {

	public static String wall_id = null;
	public static int wallNo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newpost);
		android.app.ActionBar ab = getActionBar();
		ColorDrawable colorDrawable = new ColorDrawable(
				Color.parseColor("#355088"));
		ab.setBackgroundDrawable(colorDrawable);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		Button bPost = (Button) findViewById(R.id.butPost);
		Bundle bascket = getIntent().getExtras();
		wall_id = bascket.getString("wall_id");
		wallNo = bascket.getInt("wallNo");
		bPost.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stubduration

				new postPost().execute();
			}
		});

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent back = new Intent(NewPost.this, TestMenu.class);
		startActivity(back);
		return super.onOptionsItemSelected(item);
	}

	class postPost extends AsyncTask<String, String, String> {
		private ProgressDialog pDialog;
		JSONParser jsonParser = new JSONParser();
		EditText getpost;
		EditText getsubjuct;
		String thepost;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(NewPost.this);
			pDialog.setMessage("Posting");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			String post_id = null;
			EditText getpost = (EditText) findViewById(R.id.editText2);

			EditText getsubjuct = (EditText) findViewById(R.id.editText1);
			String url = "api/mobile/posts/?wall_id=" + NewPost.wall_id;
			String thesubjuct = getsubjuct.getText().toString();
			String thepost = getpost.getText().toString();

			List<NameValuePair> paramse = new ArrayList<NameValuePair>();
			paramse.add(new BasicNameValuePair("new_post_subject", thesubjuct));
			paramse.add(new BasicNameValuePair("new_post", thepost));
			SharedPreferences uid = getSharedPreferences("uid", MODE_PRIVATE);
			String token = uid.getString("uid", "Aaa");
			JSONObject json = jsonParser.makeHttpRequest(url, "POST", paramse,
					token);
			return null;
		}

		protected void onPostExecute(String file_url) {
			pDialog.dismiss();

			Toast.makeText(NewPost.this, "Done", Toast.LENGTH_SHORT).show();
			Intent open = new Intent(NewPost.this, TestMenu.class);
			open.putExtra("frage", "fragment");
			open.putExtra("wallNo", wallNo);
			startActivity(open);

		}

	}

}
