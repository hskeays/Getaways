package com.example.getaways.UI;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.getaways.R;
import com.google.firebase.auth.FirebaseAuth;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColorBasedOnTheme();
    }

    public void setStatusBarColorBasedOnTheme() {
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Light mode
                setStatusBarColor(R.color.white); // Set status bar color for light mode
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                // Dark mode
                setStatusBarColor(R.color.dark_grey); // Set status bar color for dark mode
                break;
        }
    }

    private void setStatusBarColor(int colorResId) {
        getWindow().setStatusBarColor(ContextCompat.getColor(this, colorResId));
    }

    public void showLogoutDialog() {
        new android.app.AlertDialog.Builder(this).setTitle("Log out").setMessage("Are you sure you want to log out?").setPositiveButton("Yes", (dialog, which) -> {
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
}
