package com.gogetit.connector.firebase.impl;

import com.gogetit.connector.firebase.FireBase;
import com.gogetit.models.Item;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by vishalsanganaboina on 3/4/17.
 */

public class FireBaseImpl implements FireBase {

    private final static String DB_REFERENCE = "gogetit";
    private DatabaseReference dbReference = null;

    @Override
    public DatabaseReference getRealTimeDatabase() {
        if(dbReference == null) {
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            dbReference = database.getReference(DB_REFERENCE);
        }
        return dbReference;
    }

    @Override
    public void save(Item item) {
        getRealTimeDatabase().child("list/"+item.getId()).setValue(item);
    }

    @Override
    public void remove(Item item) {
        getRealTimeDatabase().child("list/"+item.getId()).removeValue();
    }
}
