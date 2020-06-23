package com.example.android.notepad;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        Toast.makeText(context, bundle.getString("Title")+"闹钟已响起", Toast.LENGTH_LONG).show();
        Intent intent2 = new Intent(context, BlankFragment.class);
        intent2.putExtra("alarmOff",true);
        context.startActivity(intent2);
    }
}
