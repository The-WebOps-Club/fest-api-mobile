package org.fest.erp.erp;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.app.PendingIntent;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.internal.t;
import com.google.android.gms.plus.Plus;

import java.util.Arrays;
import java.util.List;

public class Main extends Activity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
// Facebook variables
    private LoginButton loginBtn; // facebook
    private Button HomeButton;
    private TextView userName;

    private UiLifecycleHelper uiHelper;

    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");

    //FB variables end, Google + variables start
    /* Track whether the sign-in button has been clicked so that we know to resolve
     * all issues preventing sign-in without waiting.
     */
    private boolean mSignInClicked;

    /* Store the connection result from onConnectionFailed callbacks so that we can
     * resolve them when the user clicks sign-in.
     */
    private ConnectionResult mConnectionResult;


    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 0;
    private boolean mIntentInProgress;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uiHelper = new UiLifecycleHelper(this, statusCallback);
        uiHelper.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                              .addApi(Plus.API, null)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();



        setContentView(R.layout.activity_main);

        userName = (TextView) findViewById(R.id.user_name);
        loginBtn = (LoginButton) findViewById(R.id.fb_login_button);
        HomeButton = (Button) findViewById(R.id.home);

        loginBtn.setUserInfoChangedCallback(new UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser user) {
                if (user != null) {
                    userName.setText("Hello, " + user.getName());
                } else {
                    userName.setText("You are not logged in");
                }
            }
        });

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.home).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        updateUI(false);
    }


    public void onClick(View view) {
        if (view.getId() == R.id.sign_in_button)
            signInWithGplus();
        if(view.getId()== R.id.home) {
            startActivity(new Intent(Main.this, Home.class));
        }
        if(view.getId()== R.id.sign_out_button)
            signOutFromGplus();
}
    private Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
            if (state.isOpened()) {

                startActivity(new Intent(Main.this, Home.class));
                updateUI(true);
                Log.d("FacebookSampleActivity", "Facebook session opened");
            } else if (state.isClosed()) {
                updateUI(false);
                Log.d("FacebookSampleActivity", "Facebook session closed");
            }
        }
    };

    // for adding a home button, etc once logged in.
    public void updateUI(boolean LoggedIn)
    { if(LoggedIn)
        { HomeButton.setVisibility(View.VISIBLE); }
      else
        { HomeButton.setVisibility(View.GONE); }

    }

    /**
     * Sign-in into google
     * */
    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    /**
     * Method to resolve any signin errors
     * */
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    /**
     * Sign-out from google
     * */
    private void signOutFromGplus() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
            Button SignOut = (Button) findViewById(R.id.sign_out_button);
            SignOut.setVisibility(View.GONE);
        }

    }

    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }

    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        Session session = Session.getActiveSession();
        if (!session.isClosed())
            updateUI(false);

    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    public void onConnected(Bundle connectionHint) {
        mSignInClicked = false;
        updateUI(true);
        Button SignOut = (Button) findViewById(R.id.sign_out_button);
        SignOut.setVisibility(View.VISIBLE);
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if ( resultCode!= RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        uiHelper.onSaveInstanceState(savedState);
    }


}