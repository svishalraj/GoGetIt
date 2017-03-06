package com.gogetit.component;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.gogetit.R;
import com.gogetit.connector.firebase.FireBase;
import com.gogetit.models.Item;
import com.google.firebase.database.ChildEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vishalsanganaboina on 3/4/17.
 */

public class ItemViewAdapter extends ArrayAdapter<Item> {

    private List<Item> itemList = new ArrayList();
    private FireBase fireBase = null;
    private ChildEventListener childEventListener;
    private Context context;
    private final Map<String, String> desc = new HashMap();
    private final Map<String, Integer> imgs = new HashMap();
    int count = 0;

    public ItemViewAdapter(Context context, int textViewResourceId, List<Item> list) {
        super(context, textViewResourceId, list);
        this.context = context;
        itemList = list;
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


    public void setFireBase(final FireBase fireBase) {
        this.fireBase = fireBase;
    }

    public void setChildEventListener(final ChildEventListener childEventListener) {
        this.childEventListener = childEventListener;
    }

    public void resetList(final List<Item> list) {
        itemList.clear();
        itemList.addAll(list);
    }

    public void cleanupListener() {
        fireBase.getRealTimeDatabase().removeEventListener(childEventListener);
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

    public void updateList(final String item) {
        final ImageView img = new ImageView(context);
        img.setBackgroundResource(imgs.get(item));
        new AlertDialog.Builder(context)
                .setTitle(desc.get(item))
                .setMessage(item)
                .setView(img)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final Item itemObj = new Item(String.valueOf(count++), item, false, desc.get(item));
                        fireBase.save(itemObj);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Log.v("ConvertView", String.valueOf(position));
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(
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
                    Item removeItem = new Item(item.get("id"), item.get("itemName"),
                            getItemChecked(item), item.get("description"));
                    //fireBase.save(tmp);
                    fireBase.remove(removeItem);
                }
            });
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        Map<String, String> item = (Map<String, String>) itemList.get(position);
        holder.code.setText("                " + item.get("description") + "");
        holder.img.setImageResource(imgs.get(item.get("itemName")));
        holder.name.setChecked(!getItemChecked(item));
        holder.name.setText("         " + item.get("itemName"));
        holder.name.setTag(item);

        return convertView;
    }
}