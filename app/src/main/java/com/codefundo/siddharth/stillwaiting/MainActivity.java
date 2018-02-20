package com.codefundo.siddharth.stillwaiting;

import android.content.Context;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.codefundo.siddharth.stillwaiting.data.PhoneNumber;
import com.codefundo.siddharth.stillwaiting.data.PhoneNumberHelper;

import com.codefundo.siddharth.stillwaiting.data.WaitlistContract;
import com.codefundo.siddharth.stillwaiting.data.WaitlistDbHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


public class MainActivity extends AppCompatActivity {

    private GuestListAdapter mAdapter;
    private SQLiteDatabase mDb;
    private SQLiteDatabase mDb2;
    private EditText mNewGuestNameEditText;
    private EditText mNewPartySizeEditText;
    private final static String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //we are using recycler viewer to view the data.
        RecyclerView waitlistRecyclerView;

        waitlistRecyclerView = (RecyclerView) this.findViewById(R.id.all_guests_list_view);

        //here we get refrence to the elements of xml layout
        mNewGuestNameEditText = (EditText) this.findViewById(R.id.person_name_edit_text);
        mNewPartySizeEditText = (EditText) this.findViewById(R.id.party_count_edit_text);

        // Setting layout for the RecyclerView
        waitlistRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        // Create a DB helper (this will create the DB if run for the first time)
        WaitlistDbHelper dbHelper = new WaitlistDbHelper(this);
        PhoneNumberHelper sidHelp= new PhoneNumberHelper(this);

        mDb = dbHelper.getWritableDatabase();
        mDb2= sidHelp.getWritableDatabase();

        // Getting info from the database and saving in a cursor
        Cursor cursor = getAllGuests();

        // Creating an adapter for that cursor to display the data
        mAdapter = new GuestListAdapter(this, cursor);

        // Link the adapter to the RecyclerView
        waitlistRecyclerView.setAdapter(mAdapter);


        //handles both LEFT and RIGHT swipe directions

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //do nothing
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                long id = (long) viewHolder.itemView.getTag();
                //remove from DB
                removeGuest(id);
                //update the list
                mAdapter.swapCursor(getAllGuests());
            }

        }).attachToRecyclerView(waitlistRecyclerView);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.Help) {
            Intent intent=new Intent(this,Help.class);
            startActivity(intent);
            return true;
        }else if(itemThatWasClickedId == R.id.AboutUS) {
            Intent intent=new Intent(this,AboutUs.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addToWaitlist(View view) {

        EditText mob=(EditText)findViewById(R.id.siddharth);
        String num=mob.getText().toString();
        mob.setText("");

        if (mNewGuestNameEditText.getText().length() == 0 ||
                mNewPartySizeEditText.getText().length() == 0) {
            return;
        }
        //default party size is 1
        int partySize = 1;
        try {
            partySize = Integer.parseInt(mNewPartySizeEditText.getText().toString());
        } catch (NumberFormatException ex) {
            Log.e(LOG_TAG, "Failed to parse party size text to number: " + ex.getMessage());
        }

        // Add guest info to mDb
        addNewGuestSiddharth(mNewGuestNameEditText.getText().toString(),num);
        addNewGuest(mNewGuestNameEditText.getText().toString(), partySize);

        // Update the cursor in the adapter to trigger UI to display the new list
        mAdapter.swapCursor(getAllGuests());

        //clear UI text fields
        mNewPartySizeEditText.clearFocus();
        mNewGuestNameEditText.getText().clear();
        mNewPartySizeEditText.getText().clear();

    }



    private Cursor getAllGuests() {
        return mDb.query(
                WaitlistContract.WaitlistEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                WaitlistContract.WaitlistEntry.COLUMN_TIMESTAMP
        );
    }

    private long addNewGuest(String name, int partySize) {


        ContentValues cv = new ContentValues();
        cv.put(WaitlistContract.WaitlistEntry.COLUMN_GUEST_NAME, name);
        cv.put(WaitlistContract.WaitlistEntry.COLUMN_PARTY_SIZE, partySize);
        return mDb.insert(WaitlistContract.WaitlistEntry.TABLE_NAME, null, cv);
    }

    private long addNewGuestSiddharth(String name, String num) {
        ContentValues cv = new ContentValues();
        cv.put(PhoneNumber.siddharth.COLUMN_GUEST_NAME, name);
        cv.put(PhoneNumber.siddharth.COLUMN_NUMBER, num);
        return mDb2.insert(PhoneNumber.siddharth.TABLE_NAME, null, cv);
    }

    private boolean removeGuest(long id) {


        int x=(int) id;

        String[] sd= new String[2];


        Cursor cursor=mDb2.query(PhoneNumber.siddharth.TABLE_NAME,new String[] {PhoneNumber.siddharth._ID, PhoneNumber.siddharth.COLUMN_GUEST_NAME, PhoneNumber.siddharth.COLUMN_NUMBER},PhoneNumber.siddharth._ID+"=?",
                new String[] {String.valueOf(x)},null,null,null,null);
        if(cursor!=null){

            cursor.moveToFirst();
        }

        sd[0]=cursor.getString(1);
        sd[1]=cursor.getString(2);

        Log.d("RESPONSE", ""+sd[0]);




        String messageBody="Dear "+sd[0]+"\n"+"Your turn has come! Please arrive within 5 minutes. Thanks for your cooperation!";
        Log.d("RESPONSE", ""+messageBody);

        foo(sd[1],messageBody);
        Log.d("RESPONSE", "foo comleted");

        Context context=MainActivity.this;
        String textToShow = "Sending Teext Message to " + sd[0];
        Toast.makeText(context, textToShow, Toast.LENGTH_SHORT).show();


        return mDb.delete(WaitlistContract.WaitlistEntry.TABLE_NAME, WaitlistContract.WaitlistEntry._ID + "=" + id, null) > 0;
    }









    void foo(String messageAdress,String messageBody){

        //Android SMS API integration code

        //Your authentication key
        String authkey = "180063ALIDm3S2EkF859e8f457";
        //Multiple mobiles numbers separated by comma
        String mobiles = messageAdress;
        //Sender ID,While using route4 sender id should be 6 characters long.
        String senderId = "MSIITG";
        //Your message to send, Add URL encoding here.
        String message = messageBody;
        //define route
        String route="4";
        String country="91";

        URLConnection myURLConnection=null;
        URL myURL=null;
        BufferedReader reader=null;

        //encoding message
        //String encoded_message= URLEncoder.encode(message);


        String encoded_message= null;
        try {
            encoded_message = URLEncoder.encode(message, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            Log.d(LOG_TAG, "problem with encoding");
            e.printStackTrace();
        }
        //Send SMS API
        String mainUrl="https://control.msg91.com/api/sendhttp.php?";

        //Building URL string
        StringBuilder sbPostData= new StringBuilder(mainUrl);
        sbPostData.append("authkey="+authkey);
        sbPostData.append("&mobiles="+mobiles);
        sbPostData.append("&message="+encoded_message);
        sbPostData.append("&sender="+senderId);
        sbPostData.append("&route="+route);
        sbPostData.append("&country="+country);


        Log.d("RESPONSE", ""+sbPostData);


        //final string
        mainUrl = sbPostData.toString();

        //convert string to URL
        try {
            myURL = new URL(mainUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        new myAsyncTask().execute(myURL);
    }


    public class myAsyncTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... params) {
            URL myURL=params[0];

            try {

                HttpURLConnection conn= (HttpURLConnection) myURL.openConnection();

                conn.setRequestMethod("GET");
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setDoOutput(true);
                conn.connect();
                BufferedReader reader=null;
                reader= new BufferedReader(new InputStreamReader(conn.getInputStream()));
                Log.d("RESPONSE", "reader started");
                conn.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "sid";
            //returning variable as the return typr is string but there is actually no need of the variable
        }

        @Override
        protected void onPostExecute(String githubSearchResults) {
//            do nothing
        }
    }




}