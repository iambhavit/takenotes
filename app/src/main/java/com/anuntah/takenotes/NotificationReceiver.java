package com.anuntah.takenotes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Bhavit Yadav on 23-03-2018.
 */

public class NotificationReceiver extends BroadcastReceiver {

    int id;
    @Override
    public void onReceive(Context context, Intent intent) {


        AlarmManager alarmManager=null;
        String s=intent.getAction();
        id=intent.getIntExtra("notification",-1);
        if(s.equals(NotesReceiver.ACTION_SNOOZE)){
            Intent stopIntent=new Intent(context,Alaram_Manager.class);
            context.stopService(stopIntent);
            alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            Intent intent1 = new Intent(context, NotesReceiver.class);
            Bundle b = new Bundle();
            b.putInt(Constants.notes.COL_ID, id);
            intent1.putExtras(b);
            PendingIntent pendingIntent= PendingIntent.getBroadcast(context,id,intent1,0);
            alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+300000,pendingIntent);
        }

        if(s.equals(NotesReceiver.ACTION_STOP)){
            Intent stopIntent=new Intent(context,Alaram_Manager.class);
            context.stopService(stopIntent);
            alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

            Intent intent1 = new Intent(context, NotesReceiver.class);
            Bundle b = new Bundle();
            b.putInt(Constants.notes.COL_ID, id);
            intent1.putExtras(b);
            PendingIntent pendingIntent= PendingIntent.getBroadcast(context,id,intent1,PendingIntent.FLAG_UPDATE_CURRENT);
            pendingIntent.cancel();
            alarmManager.cancel(pendingIntent);

        }

    }
}
