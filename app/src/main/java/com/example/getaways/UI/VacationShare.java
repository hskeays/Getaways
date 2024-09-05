package com.example.getaways.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

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

public class VacationShare extends AppCompatActivity {
    ExcursionAdapter excursionAdapter;
    private TextView tvVacationTitle;
    private TextView tvHotelName;
    private TextView tvStartDate;
    private TextView tvEndDate;
    private Repository repository;

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

        // Initialize repository
        repository = new Repository(getApplication());

        // Get extras from previous activity
        int vacationID = getIntent().getIntExtra("VACATION_ID", 0);
        String vacationTitle = getIntent().getStringExtra("VACATION_TITLE");
        String hotelName = getIntent().getStringExtra("HOTEL_NAME");
        String startDate = getIntent().getStringExtra("VACATION_START_DATE");
        String endDate = getIntent().getStringExtra("VACATION_END_DATE");

        // Initialize text views, set text from previous VacationDetails activity
        tvVacationTitle = findViewById(R.id.tv_vacation_title_share);
        tvVacationTitle.setText(vacationTitle);
        tvHotelName = findViewById(R.id.tv_hotel_name_share);
        tvHotelName.setText(hotelName);
        tvStartDate = findViewById(R.id.tv_start_date_share);
        tvStartDate.setText(startDate);
        tvEndDate = findViewById(R.id.tv_end_date_share);
        tvEndDate.setText(endDate);

        // Show associated excursions in recycler view
        RecyclerView recyclerView = findViewById(R.id.rv_excursion_list_items_share);
        excursionAdapter = new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Observe associated excursions
        repository.getAssociatedExcursions(vacationID).observe(this, excursions -> excursionAdapter.setExcursions(excursions));

        //TODO: implement share button click handler, send all information to implicit email intent
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
}