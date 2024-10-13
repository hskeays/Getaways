package com.howardkeays.getaways.UI;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.howardkeays.getaways.R;
import com.howardkeays.getaways.UI.adapters.VacationAdapter;
import com.howardkeays.getaways.database.Repository;

public class VacationList extends BaseActivity {
    Repository repository;
    VacationAdapter vacationAdapter;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vacation_list);

        // InitializeSwipeRefreshLayout, Set up the swipe-to-refresh listener
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this::refreshVacationList);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configure custom toolbar
        Toolbar customToolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(customToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Vacations");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24);
            setStatusBarColorBasedOnTheme();
        }

        // Configure overflow icon
        Drawable overflowIcon = customToolbar.getOverflowIcon();
        if (overflowIcon != null) {
            overflowIcon.setTint(ContextCompat.getColor(this, android.R.color.black));
        }

        // Create intent to go to Vacation Details activity for creating new vacation
        ExtendedFloatingActionButton addFloatingActionButton = findViewById(R.id.fab_add_vacation_list);
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
        repository.getAllVacations().observe(this, vacationList -> {
            vacationAdapter.setVacations(vacationList);
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        setStatusBarColorBasedOnTheme();
        repository.getAllVacations().observe(this, vacationList -> vacationAdapter.setVacations(vacationList));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_menu, menu);
        menu.findItem(R.id.ic_home).setVisible(false);

        // Get the SearchView and set up the search configuration
        MenuItem searchItem = menu.findItem(R.id.ic_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        // Configure the search info and add any event listeners
        if (searchView != null) {
            searchView.setQueryHint("Search...");
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    vacationAdapter.filter(query); // Filter when the user submits the query
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    vacationAdapter.filter(newText); // Filter as the user types
                    return true;
                }
            });
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
        } else if (id == R.id.ic_logout) {
            showLogoutDialog();
        } else if (id == R.id.vacation_details) {
            startActivity(new Intent(this, VacationDetails.class));
        }
        return super.onOptionsItemSelected(item);
    }

    void refreshVacationList() {
        repository.getAllVacations().observe(this, vacationList -> {
            vacationAdapter.setVacations(vacationList);
            swipeRefreshLayout.setRefreshing(false);
        });
    }
}