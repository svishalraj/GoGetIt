package shop.com.gogetit;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import shop.com.gogetit.models.Item;

/**
 * Created by vishalsanganaboina on 2/25/17.
 */

public class FireBase {
    DatabaseReference dbRef = null;

    FireBase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("gogetit");
    }

    DatabaseReference getDbRef() {
        return dbRef;
    }

    public void save(Item item) {
          dbRef.child("list/"+item.getId()).setValue(item);//.push().getKey();
//        Map<String, Object> postValues = item.toMap();
//        Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put("/list/" + key, postValues);
//        dbRef.updateChildren(childUpdates);
//        Toast.makeText(context, "Saved the data",
//                Toast.LENGTH_LONG).show();
    }




}
