package com.example.getaways.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.getaways.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_fragment, container, false);

        // TODO: Implement bottom sheet functionality
        view.findViewById(R.id.text_option_1).setOnClickListener(v -> {
            // Handle Option 1 click
            dismiss();
        });

        view.findViewById(R.id.text_option_2).setOnClickListener(v -> {
            // Handle Option 2 click
            dismiss();
        });

        view.findViewById(R.id.text_option_3).setOnClickListener(v -> {
            // Handle Option 3 click
            dismiss();
        });

        return view;
    }
}