package com.gogetit;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.gogetit.component.ItemViewAdapter;
import com.gogetit.connector.ServerFactory;
import com.gogetit.connector.firebase.FireBase;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    //NEC API parameters
    private static final String PACKAGE_NAME = "blue.arche.lite.client";
    private static final String CLASS_NAME = "blue.arche.lite.client.AuthClientActivity";
    private final int REQ_CODE = 1000;
    private final String SEARCH_RESULT="searchResult";
    //List view adapter
    private ItemViewAdapter dataAdapter = null;
    //firebase interface.
    private FireBase fireBase = null;



    @Override
    public void onDestroy() {
        super.onDestroy();
        dataAdapter.cleanupListener();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClassName(PACKAGE_NAME, CLASS_NAME);
                startActivityForResult(intent, REQ_CODE);
            }
        });
        fireBase = ServerFactory.getInstance();
        registerFireBaseListeners();
    }

    private void registerFireBaseListeners() {
        //create an ArrayAdaptar from the String Array
        dataAdapter = new ItemViewAdapter(this, R.layout.items_info, new ArrayList());

        final ListView listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                update((ArrayList) dataSnapshot.getValue());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                update((ArrayList) dataSnapshot.getValue());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            private void update(final ArrayList items) {
                dataAdapter.resetList(items);
                dataAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        fireBase.getRealTimeDatabase().addChildEventListener(childEventListener);
        dataAdapter.setChildEventListener(childEventListener);
        dataAdapter.setFireBase(fireBase);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int responseCode, Intent intent){
        super.onActivityResult(requestCode, responseCode, intent);

        if(requestCode == REQ_CODE && responseCode == RESULT_OK && intent != null){

            if(intent.hasExtra(SEARCH_RESULT)){
                String receiveValue = intent.getStringExtra(SEARCH_RESULT);
                try {
                    JSONArray jsonArray = new JSONArray(receiveValue);
                    JSONObject jsonObj = (JSONObject) jsonArray.get(0);
                    JSONArray prodDetails = (JSONArray) jsonObj.get("appendInfo");
                    dataAdapter.updateList((String) prodDetails.get(0));
                }catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Invalid data",
                            Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(getApplicationContext(), "No data found",
                        Toast.LENGTH_LONG).show();
            }

        }else{
            if(requestCode != 1000){
                Toast.makeText(getApplicationContext(), "Failed to get results: Request code not set correctly",
                        Toast.LENGTH_LONG).show();
            }
            if(responseCode == RESULT_CANCELED){
                Toast.makeText(getApplicationContext(),"Failed to get results：Called application terminated",
                        Toast.LENGTH_LONG).show();
            }else if(responseCode != RESULT_OK){
                Toast.makeText(getApplicationContext(),"Failed to get results：Response code is invalid",
                        Toast.LENGTH_LONG).show();
            }
            if(intent == null){
                Toast.makeText(getApplicationContext(),"Failed to get results：Intent is null",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
