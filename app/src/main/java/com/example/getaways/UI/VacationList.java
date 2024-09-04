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
import com.example.getaways.entities.Excursion;
import com.example.getaways.entities.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class VacationList extends AppCompatActivity {
    Repository repository;
    List<Vacation> vacationList;
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

        FloatingActionButton addFloatingActionButton = findViewById(R.id.fab_add_vacation_list);
        addFloatingActionButton.setOnClickListener(view -> {
            Intent vacationDetailsIntent = new Intent(VacationList.this, VacationDetails.class);
            startActivity(vacationDetailsIntent);
        });

        // Initialize repository and adapter
        repository = new Repository(getApplication());
        vacationAdapter = new VacationAdapter(this);

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
        } else if (id == R.id.add_sample_data) {
            Repository repository = new Repository(getApplication());

            Vacation vacation1 = new Vacation("Miami", "Hilton", "09/01/2024", "09/05/2024");
            repository.insert(vacation1);
            Vacation vacation2 = new Vacation("Vegas", "Desert Inn", "09/07/2024", "09/12/2024");
            repository.insert(vacation2);

            Excursion excursion1 = new Excursion("09/07/2024", "Gambling", 1);
            repository.insert(excursion1);
            Excursion excursion2 = new Excursion("09/01/2024", "Loitering", 1);
            repository.insert(excursion2);
        }
        return super.onOptionsItemSelected(item);
    }
}