package com.example.getaways.UI.receivers;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("NOTIFICATION_MESSAGE");

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "CHANNEL_ID").setSmallIcon(android.R.drawable.ic_dialog_alert).setContentTitle("Vacation Reminder").setContentText(message).setPriority(NotificationCompat.PRIORITY_HIGH).setAutoCancel(true);

        // Check if the app has the required notification permission (Android 13+)
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED || android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.TIRAMISU) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(1, builder.build());
        } else {
            Toast.makeText(context, "Notifications set for the start and end dates!", Toast.LENGTH_SHORT).show();
        }
    }
}
