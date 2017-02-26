package shop.com.gogetit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shop.com.gogetit.models.Item;

public class MainActivity extends AppCompatActivity {
    private static final String PACKAGE_NAME = "blue.arche.lite.client";
    private static final String CLASS_NAME = ".AuthClientActivity";
    private final int REQ_CODE = 1000;
    final List<Item> listItems = new ArrayList();
    private Context context = this;
    int count = 0;
    MyCustomAdapter dataAdapter = null;
    final FireBase fireBase = new FireBase();
    private ChildEventListener mChildEventListener;
    private final Map<String, String> desc = new HashMap();
    private final Map<String, Integer> imgs = new HashMap();


    @Override
    public void onDestroy() {
        super.onDestroy();
        dataAdapter.cleanupListener();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClassName(PACKAGE_NAME, PACKAGE_NAME + CLASS_NAME);
                startActivityForResult(intent, REQ_CODE);
            }
        });
        displayList();
        loadData();
    }

    private void loadData() {
        desc.put("Cup_Noodle_Tomato", "Chilli Tomato");
        desc.put("Coca Cola", "1 litre 5 bottles");
        desc.put("Rice", "20 lb, 1 bag");
        desc.put("Dairy Milk", "1 box");
        desc.put("Thomas", "Thomas and Perci 1 each");

        imgs.put("Cup_Noodle_Tomato", R.drawable.chilli);
        imgs.put("Coca Cola", R.drawable.cocacola);
        imgs.put("Rice", R.drawable.rice);
        imgs.put("Dairy Milk", R.drawable.dairymilk);
        imgs.put("Thomas", R.drawable.thomas);
    }


    private void displayList() {
        //create an ArrayAdaptar from the String Array
        dataAdapter = new MyCustomAdapter(this, R.layout.items_info, listItems);
        ListView listView = (ListView) findViewById(R.id.listView1);
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
                listItems.clear();
                listItems.addAll(items);
                dataAdapter.itemList.clear();
                dataAdapter.itemList.addAll(listItems);
                dataAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        fireBase.getDbRef().addChildEventListener(childEventListener);
        mChildEventListener = childEventListener;
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

    private void updateList(final String item) {
        final ImageView img = new ImageView(context);
        img.setBackgroundResource(imgs.get(item));
        new AlertDialog.Builder(this)
                .setTitle(desc.get(item))
                .setMessage(item)
                .setView(img)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final Item itemObj = new Item(String.valueOf(count++), item, false, desc.get(item));
                        listItems.add(itemObj);
                        fireBase.save(itemObj);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }


    protected void onActivityResult(int requestCode, int responseCode, Intent intent){
        super.onActivityResult(requestCode, responseCode, intent);

        if(requestCode == REQ_CODE && responseCode == RESULT_OK && intent != null){

            if(intent.hasExtra("searchResult")){
                String receiveValue = intent.getStringExtra("searchResult");
                try {
                    JSONArray jsonArray = new JSONArray(receiveValue);
                    JSONObject jsonObj = (JSONObject) jsonArray.get(0);
                    JSONArray prodDetails = (JSONArray) jsonObj.get("appendInfo");
                    updateList((String) prodDetails.get(0));
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

    private class MyCustomAdapter extends ArrayAdapter<Item> {

        private final List<Item> itemList = new ArrayList();

        public MyCustomAdapter(Context context, int textViewResourceId, List<Item> items) {
            super(context, textViewResourceId, items);
            this.itemList.addAll(items);
//            ChildEventListener childEventListener = new ChildEventListener() {
//                @Override
//                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                      update((ArrayList) dataSnapshot.getValue());
//                }
//
//                @Override
//                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                    update((ArrayList) dataSnapshot.getValue());
//                }
//
//                @Override
//                public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                }
//
//                private void update(final ArrayList items) {
//                    itemList.clear();
//                    itemList.addAll(items);
//                    Toast.makeText(getApplicationContext(), "child count "+itemList.size(),
//                            Toast.LENGTH_LONG).show();
//                    notifyDataSetChanged();
//                }
//
//                @Override
//                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            };
//
//            fireBase.getDbRef().addChildEventListener(childEventListener);
//            mChildEventListener = childEventListener;
        }

        public void cleanupListener() {
            fireBase.getDbRef().removeEventListener(mChildEventListener);
        }

        private class ViewHolder {
            TextView code;
            CheckBox name;
            ImageView img;
        }

        boolean getItemChecked(final Map<String, String> item) {
            boolean flag = false;
            String val = String.valueOf(item.get("selected"));
            if (val.equals("false")) {
                item.put("selected", "true");
                flag = true;
            } else {
                item.put("selected", "false");
            }
            return flag;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.items_info, null);

                holder = new ViewHolder();
                holder.code = (TextView) convertView.findViewById(R.id.code);
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                holder.img  = (ImageView)  convertView.findViewById(R.id.img);
                holder.img.getLayoutParams().height = 200;
                convertView.setTag(holder);

                holder.name.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        Map<String, String> item = (Map<String, String>) cb.getTag();
                        Toast.makeText(getApplicationContext(), "Purchased the item "+item.get("itemName"),
                                Toast.LENGTH_LONG).show();
//                        boolean flag = false;
//                        String val = String.valueOf(item.get("selected"));
//                        if (val.equals("false")) {
//                            item.put("selected", "true");
//                            flag = true;
//                        } else {
//                            item.put("selected", "false");
//                        }
                        Item tmp = new Item(item.get("id"), item.get("itemName"), getItemChecked(item), item.get("description"));
                        fireBase.save(tmp);
                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (position < itemList.size()) {
                Map<String, String> item = (Map<String, String>) itemList.get(position);
//                if(item != null) {
                    holder.code.setText("                " + item.get("description") + "");
                    holder.img.setImageResource(imgs.get(item.get("itemName")));
                    holder.name.setChecked(!getItemChecked(item));
                    holder.name.setText("         " + item.get("itemName"));
                    holder.name.setTag(item);
//                }
            }
            return convertView;
        }
    }
}
