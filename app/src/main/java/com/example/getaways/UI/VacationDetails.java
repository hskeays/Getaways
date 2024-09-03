package com.example.getaways.UI;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.getaways.R;
import com.example.getaways.UI.adapters.ExcursionAdapter;
import com.example.getaways.database.Repository;
import com.example.getaways.entities.Excursion;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.List;

public class VacationDetails extends AppCompatActivity {
    private EditText etvVacationTitle;
    private EditText etvHotelName;
    private Button btnPickStartDate;
    private Button btnPickEndDate;
    private Button btnSave;
    private Button btnDelete;
    private Button btnAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vacation_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views, set on click listeners for date picker buttons
        etvVacationTitle = findViewById(R.id.etv_vacation_title);
        etvHotelName = findViewById(R.id.etv_hotel_name);

        btnPickStartDate = findViewById(R.id.btn_start_date_picker);
        btnPickStartDate.setOnClickListener(view -> showDatePickerDialog(btnPickStartDate));

        btnPickEndDate = findViewById(R.id.btn_end_date_picker);
        btnPickEndDate.setOnClickListener(view -> showDatePickerDialog(btnPickEndDate));

        btnSave = findViewById(R.id.btn_save_vacation);
//        btnSave.setOnClickListener(view -> handleSaveButtonClick());

        // Initialize FAB, set on click listener to start ExcursionDetails activity
        FloatingActionButton addFloatingActionButton = findViewById(R.id.fab_add_vacation_details);
        addFloatingActionButton.setOnClickListener(view -> {
            Intent excursionDetailsIntent = new Intent(VacationDetails.this, ExcursionDetails.class);
            startActivity(excursionDetailsIntent);
        });

        // Get extras from previous intent
        String vacationTitle = getIntent().getStringExtra("VACATION_TITLE");
        String hotelName = getIntent().getStringExtra("HOTEL_NAME");
        String startDate = getIntent().getStringExtra("START_DATE");
        String endDate = getIntent().getStringExtra("END_DATE");
        int vacationID = getIntent().getIntExtra("ID", 0);

        // If vacation clicked and exists, prefill details in views
        if (vacationID != 0) {
            etvVacationTitle.setText(vacationTitle);
            etvHotelName.setText(hotelName);
            btnPickStartDate.setText(startDate);
            btnPickEndDate.setText(endDate);
        }

        // Show associated excursions in recycler view
        Repository repository = new Repository(getApplication());
        RecyclerView recyclerView = findViewById(R.id.rv_excursion_list_items);
        ExcursionAdapter excursionAdapter = new ExcursionAdapter(this);
        List<Excursion> excursionList = repository.getAssociatedExcursions(vacationID);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        excursionAdapter.setExcursions(excursionList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_menu, menu); // Inflate the app bar menu
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

    private void showDatePickerDialog(Button button) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String selectedDate = (selectedMonth + 1) + "/" + selectedDay + "/" + selectedYear;
            button.setText(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }
}