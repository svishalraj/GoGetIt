package com.gogetit.connector.firebase;

import com.gogetit.models.Item;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by vishalsanganaboina on 2/25/17.
 */

public interface FireBase {

    /**
     * Database reference.
     * @return db
     */
    DatabaseReference getRealTimeDatabase();

    /**
     * Save the object into db.
     * @param item model.
     */
    void save(Item item);

    /**
     * Remove the object from the db.
     * @param item
     */
    void remove(Item item);

}
