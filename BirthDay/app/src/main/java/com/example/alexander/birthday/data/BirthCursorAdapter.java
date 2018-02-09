package com.example.alexander.birthday.data;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.alexander.birthday.R;
import com.example.alexander.birthday.Utils;
import com.example.alexander.birthday.data.BirthContract.ManEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Alexander on 06.02.2018.
 */

public class BirthCursorAdapter extends CursorAdapter {

    public BirthCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView summaryTextView = (TextView) view.findViewById(R.id.summary);

        int nameColumnIndex = cursor.getColumnIndex(ManEntry.COLUMN_NAME);
        int dateColumnIndex = cursor.getColumnIndex(ManEntry.COLUMN_DATE);

        String name = cursor.getString(nameColumnIndex);
        String date = cursor.getString(dateColumnIndex);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

        try {
            cal.setTime(sdf.parse(date));// all done
        } catch (ParseException e) {
            e.printStackTrace();
        }

        nameTextView.setText(name);
        int diff = Utils.getDiffYears(Calendar.getInstance().getTime(), cal.getTime());
        summaryTextView.setText(String.format("%s возвраст: %d лет", sdf.format(cal.getTime()), diff));
    }
}
