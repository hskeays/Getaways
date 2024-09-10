package com.example.getaways.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.getaways.R;
import com.example.getaways.UI.adapters.VacationAdapter;
import com.example.getaways.database.Repository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class VacationList extends AppCompatActivity {
    Repository repository;
    VacationAdapter vacationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vacation_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ***EVALUATION, TASK B1-a: Allow the user to add as many vacations as desired.
        // Create intent to go to Vacation Details activity for creating new vacation
        FloatingActionButton addFloatingActionButton = findViewById(R.id.fab_add_vacation_list);
        addFloatingActionButton.setOnClickListener(view -> {
            Intent vacationDetailsIntent = new Intent(VacationList.this, VacationDetails.class);
            startActivity(vacationDetailsIntent);
        });

        // Initialize repository and adapter
        repository = new Repository(getApplication());
        vacationAdapter = new VacationAdapter(this);

        // ***EVALUATION, TASK B1-a: Allow the user to add as many vacations as desired.
        // Create recycler view with all saved vacations queried from database

        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rv_vacation_list_items);
        recyclerView.setAdapter(vacationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Observe LiveData and update adapter
        repository.getAllVacations().observe(this, vacationList -> vacationAdapter.setVacations(vacationList));
    }

    @Override
    protected void onResume() {
        super.onResume();
        repository.getAllVacations().observe(this, vacationList -> vacationAdapter.setVacations(vacationList));
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