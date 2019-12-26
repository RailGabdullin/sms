package com.gabdullin.rail.smstest;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

public class smsReceiver extends BroadcastReceiver {

    int messageID = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("SMS: ", "got it!");
//        if(intent != null && intent.getAction() != null){
            Bundle bundle = intent.getExtras();
            Object[] pdus = (Object[]) bundle.get("pdus");
            SmsMessage[] messages = new SmsMessage[pdus.length];
            for (int i = 0; i < pdus.length; i++) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], bundle.getString("format"));
                }
                else
                {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
            }
            String smsFromPhone = messages[0].getDisplayOriginatingAddress();
            StringBuilder body = new StringBuilder();
            for (int i = 0; i < messages.length; i++) {
                body.append(messages[i].getMessageBody());
            }
            String bodyText = body.toString();
            makeNote(context, smsFromPhone, bodyText);
            abortBroadcast();
        }
//    }

    private void makeNote(Context context, String addressFrom, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "2")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(String.format("Sms [%s]", addressFrom))
                .setContentText(message);
        Intent resultIntent = new Intent(context, smsReceiver.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(messageID++, builder.build());
    }
}
