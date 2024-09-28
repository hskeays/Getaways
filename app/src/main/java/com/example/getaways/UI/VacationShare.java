package com.example.getaways.UI;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.getaways.R;
import com.example.getaways.UI.adapters.ExcursionAdapter;
import com.example.getaways.database.Repository;
import com.example.getaways.entities.Excursion;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class VacationShare extends AppCompatActivity {
    ExcursionAdapter excursionAdapter;
    private EditText etmlUserMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vacation_share);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configure custom toolbar
        Toolbar customToolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(customToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Share Vacation");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Configure overflow icon
        Drawable overflowIcon = customToolbar.getOverflowIcon();
        if (overflowIcon != null) {
            overflowIcon.setTint(ContextCompat.getColor(this, android.R.color.black));
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
        TextView tvVacationTitle = findViewById(R.id.tv_vacation_title_share);
        tvVacationTitle.setText(vacationTitle);
        TextView tvHotelName = findViewById(R.id.tv_hotel_name_share);
        tvHotelName.setText(hotelName);
        TextView tvStartDate = findViewById(R.id.tv_start_date_share);
        tvStartDate.setText(startDate);
        TextView tvEndDate = findViewById(R.id.tv_end_date_share);
        tvEndDate.setText(endDate);

        etmlUserMessage = findViewById(R.id.etml_user_message);

        // Show associated excursions in recycler view
        RecyclerView recyclerView = findViewById(R.id.rv_excursion_list_items_share);
        excursionAdapter = new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Observe associated excursions
        repository.getAssociatedExcursions(vacationID).observe(this, excursions -> excursionAdapter.setExcursions(excursions));

        Button btnShareEmail = findViewById(R.id.btn_share_email);
        btnShareEmail.setOnClickListener(view -> handleShareEmailButtonClick());
    }

    private String constructEmailText(String userMessage, List<String> excursionTitles) {
        String vacationTitle = getIntent().getStringExtra("VACATION_TITLE");
        String hotelName = getIntent().getStringExtra("HOTEL_NAME");
        String startDate = getIntent().getStringExtra("VACATION_START_DATE");
        String endDate = getIntent().getStringExtra("VACATION_END_DATE");

        String vacationFormatted = "Vacation title: " + vacationTitle + "\n";
        String hotelFormatted = "Hotel name: " + hotelName + "\n";
        String datesFormatted = "Start date: " + startDate + "\nEnd date: " + endDate + "\n\n";

        // Format the list of excursions
        StringBuilder excursionsFormatted = new StringBuilder();
        excursionsFormatted.append("Excursions:\n");
        for (String excursionTitle : excursionTitles) {
            excursionsFormatted.append("- ").append(excursionTitle).append("\n");
        }

        return vacationFormatted + hotelFormatted + datesFormatted + excursionsFormatted + "\n" + userMessage;
    }

    // ***EVALUATION, TASK B3-f:  Include sharing features so the user can share all the vacation details via a sharing feature (either e-mail, clipboard or SMS) that automatically populates with the vacation details.
    // Method to handle share button click, creates implicit email intent with pre-populated information from current activity
    private void handleShareEmailButtonClick() {
        List<String> excursionTitlesAndDates = new ArrayList<>();
        if (excursionAdapter.getExcursions() != null) {
            for (Excursion excursion : excursionAdapter.getExcursions()) {
                excursionTitlesAndDates.add(excursion.getExcursionTitle() + " " + excursion.getExcursionDate());
            }
        }

        String userMessage = etmlUserMessage.getText().toString();
        String formattedMessageBody = constructEmailText(userMessage, excursionTitlesAndDates);

        String subject = "Details about upcoming vacation";
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, formattedMessageBody);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send email using..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(VacationShare.this, "No email clients installed.", Toast.LENGTH_SHORT).show();
        }
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
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();  // End the current activity
        }).setNegativeButton("No", (dialog, which) -> {
            // Dismiss the dialog if the user clicks "No"
            dialog.dismiss();
        }).create().show();
    }
}