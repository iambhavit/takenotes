
package com.anuntah.takenotes;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.icu.text.UnicodeSetSpanner;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import static android.content.Context.NOTIFICATION_SERVICE;


/**
 * Created by Bhavit Yadav on 16-03-2018.
 */


public class NotesReceiver extends BroadcastReceiver {

    public static final String ACTION_SNOOZE="actionsnooze";
    public static final String ACTION_STOP="actionstop";
    public static final String CHANNEL_ID="takenotes";

    NotesOpenHelper openHelper;
    String title1,description1;
    int id;

    @Override
    public void onReceive(Context context, Intent intent) {


        openHelper=NotesOpenHelper.getInstance(context);

        Bundle b=intent.getExtras();
        id=b.getInt(Constants.notes.COL_ID,-1);

        if(id!=-1){

            Intent intent2=new Intent(context,Alaram_Manager.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent2);
            }
            else
            context.startService(intent2);

            SQLiteDatabase db = openHelper.getReadableDatabase();
            String[] s2={id+""};
            Cursor cursor = db.query(Constants.notes.NAME, null, "id=?",s2, null, null, null);
            while (cursor.moveToNext()) {
                title1 = cursor.getString(cursor.getColumnIndex(Constants.notes.COL_TITLE));
                description1 = cursor.getString(cursor.getColumnIndex(Constants.notes.COL_DESCRIPTION));
            }


            Intent snooze_intent = new Intent(context,NotificationReceiver.class);
            snooze_intent.setAction(NotesReceiver.ACTION_SNOOZE);
            snooze_intent.putExtra("notification",id);
            PendingIntent pendingIntent1=PendingIntent.getBroadcast(context,0,snooze_intent,PendingIntent.FLAG_CANCEL_CURRENT);

            Intent stop_intent=new Intent(context,NotificationReceiver.class);
            stop_intent.setAction(NotesReceiver.ACTION_STOP);
            stop_intent.putExtra("notification",id);
            PendingIntent pendingIntent3=PendingIntent.getBroadcast(context,1,stop_intent,PendingIntent.FLAG_CANCEL_CURRENT);

            NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

            NotificationChannel mChannel;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Create the NotificationChannel
                CharSequence name = "TakeNotes";
                String description = "reminderChannel";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                mChannel.setDescription(description);
                // Register the channel with the system; you can't change the importance

                if (manager != null) {
                    manager.createNotificationChannel(mChannel);
                }
                // or other notification behaviors after this

            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,CHANNEL_ID);
            if(title1!=null&&!title1.equals(""))
            builder.setContentTitle(title1);
            if(description1!=null&&!description1.equals(""))
                builder.setContentText(description1);
            builder.setSmallIcon(R.mipmap.ic_launcher).setColor(Color.GREEN);
            builder.addAction(R.drawable.ic_snooze_black_24dp,"SNOOZE",pendingIntent1);
            builder.addAction(R.drawable.ic_cancel_black_24dp,"CANCEL",pendingIntent3);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setPriority(NotificationManager.IMPORTANCE_HIGH);
            }
            builder.setDefaults(Notification.DEFAULT_ALL);
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(description1));
            builder.setAutoCancel(true);


            builder.setDeleteIntent(pendingIntent3);
            Intent intent1 = new Intent(context,EditNote.class);
            b.putString(Constants.notes.COL_TITLE,title1);
            b.putString(Constants.notes.COL_DESCRIPTION,description1);
            b.putInt(MainActivity.code,10);
            intent1.putExtras(b);

            PendingIntent pendingIntent = PendingIntent.getActivity(context,2,intent1,PendingIntent.FLAG_CANCEL_CURRENT);

            builder.setContentIntent(pendingIntent);
            Notification notification = builder.build();
            if (manager != null) {
                manager.notify(id,notification);
            }
        }

    }
}

