package com.example.getaways.UI;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.getaways.R;

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
}
