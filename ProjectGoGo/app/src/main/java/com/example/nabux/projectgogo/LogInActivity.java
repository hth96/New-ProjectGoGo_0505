package com.example.nabux.projectgogo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.widget.Toast;

public class LogInActivity extends AppCompatActivity {


    private Session session;
    Handler myHandler;

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    // single user url
    private static final String url_user_detials = "http://45.55.213.89/nabu_connect/query_login.php";

    EditText edtid, edtpsd;
    Button btnLog, btnRes;
    String usernickname, userid, userpsd, userno;
    String global_id;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_USER = "user";
    private static final String TAG_ID = "id";
    private static final String TAG_PSD = "psd";
    private static final String TAG_NICKNAME = "nickname";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        session = new Session(getApplicationContext());

        userid = null;
        userpsd = null;
        usernickname = null;
        btnLog = (Button) findViewById(R.id.btnlogin);
        btnRes = (Button) findViewById(R.id.btnregist);
        edtid = (EditText) findViewById(R.id.ed1);
        edtpsd = (EditText) findViewById(R.id.ed2);

        Intent intent = getIntent();
        if(intent.hasExtra("userID")) {
            final String uID = intent.getStringExtra("userID");
            final String uPSD = intent.getStringExtra("userPSD");
            global_id = uID;
            Log.d("0410", uID+"..");
            myHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case 0:
                            // calling to this function from onPostExecute
                            if (uID.equals(userid) && uPSD.equals(userpsd)) {
                                Intent homein = new Intent(getApplicationContext(), HomeActivity.class);
                                homein.putExtra(TAG_NICKNAME, usernickname);
                                homein.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                homein.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                homein.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(homein);
                                finish();
                            }else{
                                Toast.makeText(getApplication(), "Log in failed.", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        default:
                            break;
                    }
                }
            };
            // Getting user details in background thread
            new CheckUserDetails().execute();
        }


        // login button click event
        btnLog.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                final String id = edtid.getText().toString();
                final String psd = edtpsd.getText().toString();
                global_id = edtid.getText().toString();

                myHandler = new Handler() {

                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case 0:
                                // calling to this function from onPostExecute
                                if (id.equals(userid) && psd.equals(userpsd)) {

                                    session.setUserNo(userno);
                                    session.setUserID(userid);
                                    session.setUserPSD(userpsd);


                                    Intent homein = new Intent(getApplicationContext(), HomeActivity.class);
                                    homein.putExtra(TAG_NICKNAME, usernickname);
                                    homein.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    homein.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    homein.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(homein);
                                    finish();
                                }else{
                                    Toast.makeText(getApplication(), "Log in failed.", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                break;
                        }
                    }
                };




                // Getting user details in background thread
                new CheckUserDetails().execute();



            }
        });



        // resgist button click event
        btnRes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent in = new Intent(getApplicationContext(), RegistActivity.class);
                startActivity(in);
                finish();
            }
        });



    }


    /**
     * Background Async Task to Get User details
     * */
    class CheckUserDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LogInActivity.this);
            pDialog.setMessage("Loading ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Getting User details in background thread
         * */
        protected String doInBackground(String... args) {

            // updating UI from Background Thread
            //runOnUiThread(new Runnable() {
                //public void run() {
                    String id = global_id;

                    Log.d("Test", "!!!!!!!!!!!!");
                    try {
                        // Building Parameters
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("id", id));
                        Log.d("ObOv", "!!!!!!!!!!!!");
                        // getting user details by making HTTP request
                        // Note that user details url will use GET request
                        JSONObject json = jsonParser.makeHttpRequest(
                                url_user_detials, "GET", params);

                        // check your log for json response
                        Log.d("User Details", json.toString());

                        // json success tag
                        int success;
                        success = json.getInt(TAG_SUCCESS);
                        if (success == 1) {
                            // successfully received user details
                            JSONArray userObj = json
                                    .getJSONArray(TAG_USER); // JSON Array

                            // get first user object from JSON Array
                            JSONObject user = userObj.getJSONObject(0);


                            // user detail
                            userid = user.getString(TAG_ID);
                            userpsd = user.getString(TAG_PSD);
                            usernickname = user.getString(TAG_NICKNAME);





                        }else{
                            // user with id not found
                            Log.d("Account Not found", "user id not in database");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                //}
            //});

            return null;
        }


        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once got all details
            pDialog.dismiss();
            myHandler.sendEmptyMessage(0);
        }
    }



}
