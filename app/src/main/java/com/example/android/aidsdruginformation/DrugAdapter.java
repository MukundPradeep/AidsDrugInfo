package com.example.android.aidsdruginformation;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Admin on 21-05-2016.
 */
public class DrugAdapter extends CursorAdapter {

    public DrugAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
         View view = LayoutInflater.from(context).inflate(R.layout.drug_item, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView drugName = (TextView) view.findViewById(R.id.drugName);
        String body = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        drugName.setText(body);
        drugName.setContentDescription("Click to get info for " + body);
    }
}
