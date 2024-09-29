package com.example.getaways.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.getaways.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetFragment extends BottomSheetDialogFragment {
    private static final String VACATION_TITLE = "Placeholder";
    private TextView vacationTitle;

    public static BottomSheetFragment newInstance(String vacationTitle) {
        BottomSheetFragment fragment = new BottomSheetFragment();
        Bundle args = new Bundle();
        args.putString(VACATION_TITLE, vacationTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_fragment, container, false);

        // Retrieve the vacation title from the arguments
        if (getArguments() != null) {
            String vacationTitle = getArguments().getString(VACATION_TITLE);
            TextView titleTextView = view.findViewById(R.id.tv_bottom_sheet_vacation_title);
            titleTextView.setText(vacationTitle);
        }

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