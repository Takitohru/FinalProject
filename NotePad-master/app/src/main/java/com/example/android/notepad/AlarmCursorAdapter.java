package com.example.android.notepad;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.SimpleCursorAdapter;

public class AlarmCursorAdapter extends SimpleCursorAdapter {
    public AlarmCursorAdapter(Context context, int layout, Cursor c,
                           String[] from, int[] to) {
        super(context, layout, c, from, to);
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor){
        super.bindView(view, context, cursor);
        int colAlarmIndex = cursor.getColumnIndex(NotePad.Notes.COLUMN_NAME_ALARM);
        String dateTime = cursor.getString(colAlarmIndex);
        if (dateTime == null) {
            view.findViewById(R.id.alarm).setVisibility(View.INVISIBLE);
        }else{
            view.findViewById(R.id.alarm).setVisibility(View.VISIBLE);
        }
    }
}
