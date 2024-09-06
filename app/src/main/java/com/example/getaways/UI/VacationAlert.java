package com.example.getaways.UI;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.getaways.R;
import com.example.getaways.UI.adapters.ExcursionAdapter;
import com.example.getaways.UI.receivers.NotificationReceiver;
import com.example.getaways.database.Repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class VacationAlert extends AppCompatActivity {
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE);
        }

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

        Button btnAlert = findViewById(R.id.btn_set_alerts);
        btnAlert.setOnClickListener(view -> handleSetAlertButtonClick());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.ic_home) {
            startActivity(new Intent(this, MainActivity.class));
        } else if (id == R.id.ic_back) {
            getOnBackPressedDispatcher().onBackPressed();
        } else if (id == R.id.vacation_list) {
            startActivity(new Intent(this, VacationList.class));
        } else if (id == R.id.vacation_details) {
            startActivity(new Intent(this, VacationDetails.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleSetAlertButtonClick() {
        String startDate = getIntent().getStringExtra("VACATION_START_DATE");
        String endDate = getIntent().getStringExtra("VACATION_END_DATE");

        // Schedule notifications
        scheduleNotification(startDate, "Vacation Start Reminder");
        scheduleNotification(endDate, "Vacation End Reminder");

        Toast.makeText(this, "Notifications set for the start and end dates!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void scheduleNotification(String dateString, String message) {
        dateString = normalizeDate(dateString);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        try {
            Date date = sdf.parse(dateString);

            if (date != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                // Schedule the notification
                setNotificationAlarm(calendar.getTimeInMillis(), message);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setNotificationAlarm(long triggerTime, String message) {
        // Create an intent to trigger the notification
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("NOTIFICATION_MESSAGE", message);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Use AlarmManager to trigger the notification at the specified time
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }

    // Normalizes String dates for input validation
    private String normalizeDate(String date) {
        String[] parts = date.split("/");
        String month = parts[0].length() == 1 ? "0" + parts[0] : parts[0];
        String day = parts[1].length() == 1 ? "0" + parts[1] : parts[1];
        String year = parts[2];

        return month + "/" + day + "/" + year;
    }
}