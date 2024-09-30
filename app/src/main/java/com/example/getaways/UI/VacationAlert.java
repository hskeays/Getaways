package com.example.getaways.UI;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.getaways.R;
import com.example.getaways.UI.adapters.ExcursionAdapter;
import com.example.getaways.UI.receivers.NotificationReceiver;
import com.example.getaways.database.Repository;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class VacationAlert extends BaseActivity {
    private static final int REQUEST_CODE = 0;
    ExcursionAdapter excursionAdapter;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vacation_alert);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configure custom toolbar
        Toolbar customToolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(customToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Vacation Alert");
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

        // Initialize repository
        Repository repository = new Repository(getApplication());

        // Get extras from previous activity
        int vacationID = getIntent().getIntExtra("VACATION_ID", 0);
        String vacationTitle = getIntent().getStringExtra("VACATION_TITLE");
        String hotelName = getIntent().getStringExtra("HOTEL_NAME");
        String startDate = getIntent().getStringExtra("VACATION_START_DATE");
        String endDate = getIntent().getStringExtra("VACATION_END_DATE");

        // Initialize text views, set text from previous VacationDetails activity
        TextView tvVacationTitle = findViewById(R.id.tv_vacation_title_alert);
        tvVacationTitle.setText(vacationTitle);
        TextView tvHotelName = findViewById(R.id.tv_hotel_name_alert);
        tvHotelName.setText(hotelName);
        TextView tvStartDate = findViewById(R.id.tv_start_date_alert);
        tvStartDate.setText(startDate);
        TextView tvEndDate = findViewById(R.id.tv_end_date_alert);
        tvEndDate.setText(endDate);

        // Show associated excursions in recycler view
        RecyclerView recyclerView = findViewById(R.id.rv_excursion_list_items_alert);
        excursionAdapter = new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Observe associated excursions
        repository.getAssociatedExcursions(vacationID).observe(this, excursions -> excursionAdapter.setExcursions(excursions));

        // Initialize and set on click handler to alert button
        Button btnAlert = findViewById(R.id.btn_set_alerts);
        btnAlert.setOnClickListener(view -> handleSetAlertButtonClick());
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
            startActivity(new Intent(this, VacationList.class));
        } else if (id == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
        } else if (id == R.id.ic_logout) {
            showLogoutDialog();
        } else if (id == R.id.vacation_list) {
            startActivity(new Intent(this, VacationList.class));
        } else if (id == R.id.vacation_details) {
            startActivity(new Intent(this, VacationDetails.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this).setTitle("Log out").setMessage("Are you sure you want to log out?").setPositiveButton("Yes", (dialog, which) -> {
            // Log out the user and navigate to the login screen
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();  // End the current activity
        }).setNegativeButton("No", (dialog, which) -> {
            // Dismiss the dialog if the user clicks "No"
            dialog.dismiss();
        }).create().show();
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

    // ***EVALUATION, TASK B3-e:  Include an alert that the user can set which will trigger on the start and end date, displaying the vacation title and whether it is starting or ending.
    // Method to handle setting/scheduling start date/end date alerts with the vacation title in the notification
    private void handleSetAlertButtonClick() {
        String vacationTitle = getIntent().getStringExtra("VACATION_TITLE");
        String startDate = getIntent().getStringExtra("VACATION_START_DATE");
        String endDate = getIntent().getStringExtra("VACATION_END_DATE");

        // Schedule notifications
        scheduleNotification(startDate, "Vacation Start Reminder for: " + vacationTitle, 0);
        scheduleNotification(endDate, "Vacation End Reminder for: " + vacationTitle, 1);

        Toast.makeText(this, "Notifications set for the start and end dates.", Toast.LENGTH_SHORT).show();
        finish();
    }

    // ***EVALUATION, TASK B3-e:  Include an alert that the user can set which will trigger on the start and end date, displaying the vacation title and whether it is starting or ending.
    // Method to schedule notification
    private void scheduleNotification(String dateString, String message, int requestCode) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        try {
            Date date = sdf.parse(dateString);

            if (date != null) {
                Calendar calendar = Calendar.getInstance();
//                calendar.setTime(date);

//                // Set time of day to 8:00 AM
//                calendar.set(Calendar.HOUR_OF_DAY, 8);
//                calendar.set(Calendar.MINUTE, 0);
//                calendar.set(Calendar.SECOND, 0);

                // FOR TESTING, SET NOTIFICATION FOR 2 SECONDS FROM ALERT BEING SET
                calendar.add(Calendar.SECOND, 2);

                // Schedule the notification
                setNotificationAlarm(calendar.getTimeInMillis(), message, requestCode);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setNotificationAlarm(long triggerTime, String message, int requestCode) {
        // Create an intent to trigger the notification
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("NOTIFICATION_MESSAGE", message);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

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
        CharSequence name = "Vacation Reminders";
        String description = "Notifications for vacation start and end dates";
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