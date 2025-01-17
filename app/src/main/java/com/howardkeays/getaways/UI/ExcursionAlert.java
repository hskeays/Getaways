package com.howardkeays.getaways.UI;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.howardkeays.getaways.R;
import com.howardkeays.getaways.UI.receivers.NotificationReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ExcursionAlert extends BaseActivity {
    private static final int REQUEST_CODE = 0;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_excursion_alert);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configure custom toolbar
        Toolbar customToolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(customToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Excursion Alert");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24);
        }

        // Configure overflow icon
        Drawable overflowIcon = customToolbar.getOverflowIcon();
        if (overflowIcon != null) {
            overflowIcon.setTint(ContextCompat.getColor(this, android.R.color.black));
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE);
        }

        // Create notification channel
        createNotificationChannel();

        // Get extras from previous activity
        int excursionID = getIntent().getIntExtra("EXCURSION_ID", 0);
        String excursionTitle = getIntent().getStringExtra("EXCURSION_TITLE");
        String excursionDate = getIntent().getStringExtra("EXCURSION_DATE");

        TextView tvExcursionTitle = findViewById(R.id.tv_excursion_title_alert);
        tvExcursionTitle.setText(excursionTitle);
        TextView tvExcursionDate = findViewById(R.id.tv_excursion_date_alert);
        tvExcursionDate.setText(excursionDate);

        Button btnSetAlert = findViewById(R.id.btn_set_alerts_excursion);
        btnSetAlert.setOnClickListener(view -> handleSetAlertsButtonClick());
    }

    @Override
    protected void onResume() {
        super.onResume();
        setStatusBarColorBasedOnTheme();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_menu, menu); // Inflate the app bar menu
        menu.findItem(R.id.ic_search).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.ic_home) {
            startActivity(new Intent(this, com.howardkeays.getaways.UI.LoginActivity.class));
        } else if (id == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
        } else if (id == R.id.ic_logout) {
            showLogoutDialog();
        } else if (id == R.id.vacation_list) {
            startActivity(new Intent(this, com.howardkeays.getaways.UI.VacationList.class));
        } else if (id == R.id.vacation_details) {
            startActivity(new Intent(this, com.howardkeays.getaways.UI.VacationDetails.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission is required for alerts.", Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // ***EVALUATION, TASK B5-d:  Include an alert that the user can set that will trigger on the excursion date, stating the excursion title.
    // Method for handling share button click to schedule alert notification for excursion
    private void handleSetAlertsButtonClick() {
        String date = getIntent().getStringExtra("EXCURSION_DATE");

        // Schedule notifications
        scheduleNotification(date);

        Toast.makeText(this, "Notification set for the start date.", Toast.LENGTH_SHORT).show();
        finish();
    }

    // ***EVALUATION, TASK B5-d:  Include an alert that the user can set that will trigger on the excursion date, stating the excursion title.
    // Method for scheduling notification
    private void scheduleNotification(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        try {
            Date date = sdf.parse(dateString);

            if (date != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                // Set time of day to 8:00 AM
                calendar.set(Calendar.HOUR_OF_DAY, 8);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);

                // FOR TESTING, SET NOTIFICATION FOR 2 SECONDS FROM ALERT BEING SET
                //calendar.add(Calendar.SECOND, 2);

                // Schedule the notification
                setNotificationAlarm(calendar.getTimeInMillis());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // ***EVALUATION, TASK B5-d:  Include an alert that the user can set that will trigger on the excursion date, stating the excursion title.
    // Method for setting notification for start date with excursion title
    private void setNotificationAlarm(long triggerTime) {
        String excursionTitle = getIntent().getStringExtra("EXCURSION_TITLE");
        // Create an intent to trigger the notification
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("NOTIFICATION_MESSAGE", "Excursion start reminder for: " + excursionTitle);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 3, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Use AlarmManager to trigger the notification at the specified time
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Check if the app can schedule exact alarms
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(this, "Please enable exact alarms in app settings.", Toast.LENGTH_LONG).show();
                // Open the settings where the user can grant permission
                Intent settingsIntent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(settingsIntent);
            }
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }

    private void createNotificationChannel() {
        String channelId = "CHANNEL_ID";
        CharSequence name = "Excursion Reminders";
        String description = "Notifications for excursion start date";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        NotificationChannel channel = new NotificationChannel(channelId, name, importance);
        channel.setDescription(description);

        Context context = getApplicationContext();
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }
}