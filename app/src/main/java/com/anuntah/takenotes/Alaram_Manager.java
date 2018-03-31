package com.anuntah.takenotes;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.nio.charset.IllegalCharsetNameException;

/**
 * Created by Bhavit Yadav on 22-03-2018.
 */

public class Alaram_Manager extends Service {

   static Ringtone r;

   @Nullable
   @Override
   public IBinder onBind(Intent intent) {
      return null;
   }

   @Override
   public int onStartCommand(Intent intent,int flags,int startid){
      super.onStartCommand(intent,flags,startid);

      Uri notification=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
      r=RingtoneManager.getRingtone(getBaseContext(),notification);
      r.play();
      return START_NOT_STICKY;
   }

   @Override
   public  void onDestroy(){
      r.stop();
   }

   @Override
   public void onCreate(){
      super.onCreate();
       if (Build.VERSION.SDK_INT >= 26) {

           Notification notification = new NotificationCompat.Builder(this,"1")
                   .setContentTitle("")
                   .setContentText("").build();

           startForeground(1, notification);
       }
   }

}
