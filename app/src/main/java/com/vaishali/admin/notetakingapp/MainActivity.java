package com.vaishali.admin.notetakingapp;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.*;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks,View.OnClickListener,EditFragment.OnFragmentInteractionListener,List1Fragment.OnFragmentInteractionListener,Communicator ,UpdateFragment.OnFragmentInteractionListener{

    GoogleApiClient google_api_client;
    Button addbutton,viewbutton;
    GoogleApiAvailability google_api_availability;
    SignInButton signIn_btn;
    private static final int SIGN_IN_CODE = 0;
    private static final int PROFILE_PIC_SIZE = 120;
    private ConnectionResult connection_result;
    private boolean is_intent_inprogress;
    private boolean is_signInBtn_clicked;
    private int request_code;
    ProgressDialog progress_dialog;

    EditFragment e;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buidNewGoogleApiClient();
        setContentView(R.layout.activity_main);
        custimizeSignBtn();
        setBtnClickListeners();
        progress_dialog = new ProgressDialog(this);
        progress_dialog.setMessage("Signing in....");

        addbutton= (Button) findViewById(R.id.addbutton);
        viewbutton= (Button) findViewById(R.id.viewbutton);

    }

    public void Add(View v)
    {


        e=new EditFragment();
        getSupportFragmentManager().beginTransaction()
        .replace(R.id.notemainlayout,e,"Edit")
                .commit();
        findViewById(R.id.notemainlayout).setVisibility(View.VISIBLE);
    }
    public void View(View v)
    {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.notemainlayout,new List1Fragment(),"View")
                .commit();
        findViewById(R.id.notemainlayout).setVisibility(View.VISIBLE);
        AlertDialog dialog;
        AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("UPDATE/DELETE");


        builder.setMessage("Click on the delete icon to delete a note.\n"+"Long click an item to update/change the note.");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog=builder.create();
        dialog.show();

    }
    private void buidNewGoogleApiClient(){

        google_api_client =  new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API,Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }
    private void custimizeSignBtn(){

        signIn_btn = (SignInButton) findViewById(R.id.sign_in_button);
        signIn_btn.setSize(SignInButton.SIZE_STANDARD);
        signIn_btn.setScopes(new Scope[]{Plus.SCOPE_PLUS_LOGIN});

    }
    private void setBtnClickListeners(){

        signIn_btn.setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);

    }
    protected void onStart() {
        super.onStart();
        google_api_client.connect();
    }
    protected void onStop() {
        super.onStop();
        if (google_api_client.isConnected()) {
            google_api_client.disconnect();
        }
    }
    protected void onResume(){
        super.onResume();
        if (google_api_client.isConnected()) {
            google_api_client.connect();
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()) {
            google_api_availability.getErrorDialog(this, connectionResult.getErrorCode(),request_code).show();
            return;
        }

        if (!is_intent_inprogress) {

            connection_result = connectionResult;

            if (is_signInBtn_clicked) {

                resolveSignInError();
            }
        }


    }
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {

        if (requestCode == SIGN_IN_CODE) {
            request_code = requestCode;
            if (responseCode != RESULT_OK) {
                is_signInBtn_clicked = false;
                progress_dialog.dismiss();

            }

            is_intent_inprogress = false;

            if (!google_api_client.isConnecting()) {
                google_api_client.connect();
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        is_signInBtn_clicked = false;

        getProfileInfo();

        changeUI(true);

    }

    @Override
    public void onConnectionSuspended(int i) {
        google_api_client.connect();
        changeUI(false);

    }
    private void gPlusSignIn() {
        if (!google_api_client.isConnecting()) {
            Log.d("user connected","connected");
            is_signInBtn_clicked = true;
            progress_dialog.show();
            resolveSignInError();

        }
    }
    private void gPlusSignOut() {
        if (google_api_client.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(google_api_client);
            google_api_client.disconnect();
            google_api_client.connect();
            changeUI(false);
        }
    }

    private void resolveSignInError() {
        if (connection_result.hasResolution()) {
            try {
                is_intent_inprogress = true;
                connection_result.startResolutionForResult(this, SIGN_IN_CODE);
                Log.d("resolve error", "sign in error resolved");
            } catch (IntentSender.SendIntentException e) {
                is_intent_inprogress = false;
                google_api_client.connect();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                Toast.makeText(this, "Signing in!", Toast.LENGTH_SHORT).show();
                gPlusSignIn();
                break;
            case R.id.sign_out_button:
                Toast.makeText(this, "Signing out!", Toast.LENGTH_LONG).show();
                findViewById(R.id.notemainlayout).setVisibility(View.GONE);
                gPlusSignOut();
                break;

        }

    }
    private void changeUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
            findViewById(R.id.notemainlayout).setVisibility(View.GONE);



        } else {

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
            findViewById(R.id.notemainlayout).setVisibility(View.GONE);
        }
    }
    private void getProfileInfo() {

        try {

            if (Plus.PeopleApi.getCurrentPerson(google_api_client) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(google_api_client);
                setPersonalInfo(currentPerson);

            } else {
                Toast.makeText(getApplicationContext(),
                        "No Personal info mention", Toast.LENGTH_LONG).show();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void setPersonalInfo(Person currentPerson){

        String personName = currentPerson.getDisplayName();
        String personPhotoUrl = currentPerson.getImage().getUrl();
        String email = Plus.AccountApi.getAccountName(google_api_client);
        SharedPreferences sp=getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("email",email);
        editor.commit();
        TextView   user_name = (TextView) findViewById(R.id.userName);
        user_name.setText("Name: "+personName);
        TextView gemail_id = (TextView)findViewById(R.id.emailId);
        gemail_id.setText("Email Id: " +email);

        setProfilePic(personPhotoUrl);
        progress_dialog.dismiss();

    }
    private void setProfilePic(String profile_pic){
        profile_pic = profile_pic.substring(0,
                profile_pic.length() - 2)
                + PROFILE_PIC_SIZE;
        ImageView    user_picture = (ImageView)findViewById(R.id.profile_pic);
        new LoadProfilePic(user_picture).execute(profile_pic);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void respond(String data1,String data2) {
        SharedPreferences sp1=getSharedPreferences("MyData1", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp1.edit();
        editor.putString("title",data1);
        editor.putString("content",data2);
        editor.commit();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.notemainlayout,new UpdateFragment(),"View")
                .addToBackStack(null)
                .commit();


    }

    private class LoadProfilePic extends AsyncTask<String, Void, Bitmap> {
        ImageView bitmap_img;

        public LoadProfilePic(ImageView bitmap_img) {
            this.bitmap_img = bitmap_img;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap new_icon = null;
            try {
                InputStream in_stream = new java.net.URL(url).openStream();
                new_icon = BitmapFactory.decodeStream(in_stream);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return new_icon;
        }

        protected void onPostExecute(Bitmap result_img) {

            bitmap_img.setImageBitmap(result_img);
        }
    }
}
