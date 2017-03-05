package com.gogetit.connector;

import com.gogetit.connector.firebase.FireBase;
import com.gogetit.connector.firebase.impl.FireBaseImpl;

/**
 * Created by vishalsanganaboina on 3/4/17.
 */

public class ServerFactory {
    // Firebase reference.
    static FireBase firebase = null;

    /**
     * Get the fire base reference.
     * @return
     */
    public static FireBase getInstance() {
        if(firebase == null) {
            firebase = new FireBaseImpl();
        }
        return firebase;
    }
}
