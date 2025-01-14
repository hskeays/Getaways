package com.howardkeays.getaways.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.howardkeays.getaways.R;
import com.howardkeays.getaways.database.Repository;

public class BottomSheetFragment extends BottomSheetDialogFragment {
    private static final String VACATION_ID = "VACATION_ID";
    private static final String VACATION_TITLE = "VACATION_TITLE";
    private static final String HOTEL_NAME = "HOTEL_NAME";
    private static final String VACATION_START_DATE = "VACATION_START_DATE";
    private static final String VACATION_END_DATE = "VACATION_END_DATE";

    private Repository repository;
    private int vacationID;
    private String vacationTitle;
    private String hotelName;
    private String startDate;
    private String endDate;

    public static BottomSheetFragment newInstance(int vacationID, String vacationTitle, String hotelName, String startDate, String endDate) {
        BottomSheetFragment fragment = new BottomSheetFragment();
        Bundle args = new Bundle();
        args.putInt(VACATION_ID, vacationID);
        args.putString(VACATION_TITLE, vacationTitle);
        args.putString(HOTEL_NAME, hotelName);
        args.putString(VACATION_START_DATE, startDate);
        args.putString(VACATION_END_DATE, endDate);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_fragment, container, false);

        // Initialize repository
        repository = new Repository(requireActivity().getApplication());

        // Retrieve data from arguments
        if (getArguments() != null) {
            vacationID = getArguments().getInt(VACATION_ID);
            vacationTitle = getArguments().getString(VACATION_TITLE);
            hotelName = getArguments().getString(HOTEL_NAME);
            startDate = getArguments().getString(VACATION_START_DATE);
            endDate = getArguments().getString(VACATION_END_DATE);

            // Populate vacation details in the bottom sheet
            TextView rowTitleText = view.findViewById(R.id.tv_bottom_sheet_vacation_title);
            rowTitleText.setText(vacationTitle);
        }

        // Set up click listeners for the menu options (edit, alert, share, delete)
        view.findViewById(R.id.tv_edit_row).setOnClickListener(v -> {
            Intent editIntent = new Intent(getContext(), com.howardkeays.getaways.UI.VacationDetails.class);
            editIntent.putExtra("ID", vacationID);
            editIntent.putExtra("VACATION_TITLE", vacationTitle);
            editIntent.putExtra("HOTEL_NAME", hotelName);
            editIntent.putExtra("START_DATE", startDate);
            editIntent.putExtra("END_DATE", endDate);
            startActivity(editIntent);
        });

        view.findViewById(R.id.tv_alert_row).setOnClickListener(v -> {
            Intent alertIntent = new Intent(getContext(), com.howardkeays.getaways.UI.VacationAlert.class);
            alertIntent.putExtra("VACATION_ID", vacationID);
            alertIntent.putExtra("VACATION_TITLE", vacationTitle);
            alertIntent.putExtra("HOTEL_NAME", hotelName);
            alertIntent.putExtra("VACATION_START_DATE", startDate);
            alertIntent.putExtra("VACATION_END_DATE", endDate);
            startActivity(alertIntent);
        });

        view.findViewById(R.id.tv_share_row).setOnClickListener(v -> {
            Intent shareIntent = new Intent(getContext(), com.howardkeays.getaways.UI.VacationShare.class);
            shareIntent.putExtra("VACATION_ID", vacationID);
            shareIntent.putExtra("VACATION_TITLE", vacationTitle);
            shareIntent.putExtra("HOTEL_NAME", hotelName);
            shareIntent.putExtra("VACATION_START_DATE", startDate);
            shareIntent.putExtra("VACATION_END_DATE", endDate);
            startActivity(shareIntent);
        });

        view.findViewById(R.id.tv_delete_row).setOnClickListener(v -> repository.vacationExists(vacationID).observe(this, exists -> {
            if (exists) {
                repository.getVacationByID(vacationID).observe(this, vacation -> {
                    if (vacation != null) {
                        repository.getAssociatedExcursions(vacationID).observe(this, excursions -> {
                            // Prevent deletion of vacations if the associated excursions list is not empty
                            if (excursions == null || excursions.isEmpty()) {
                                new AlertDialog.Builder(requireContext()).setTitle("Delete Vacation").setMessage("Are you sure you want to delete this vacation?").setPositiveButton("Yes", (dialog, which) -> {
                                    repository.delete(vacation);
                                    Toast.makeText(getContext(), "Successfully deleted vacation.", Toast.LENGTH_SHORT).show();
                                    dismiss();
                                }).setNegativeButton("No", (dialog, which) -> dialog.dismiss()).show();
                            } else {
                                new AlertDialog.Builder(requireContext()).setTitle("Delete All Excursions").setMessage("Cannot delete vacation with associated excursions. Do you want to delete all excursions?").setPositiveButton("Yes", (dialog, which) -> {
                                    new AlertDialog.Builder(requireContext()).setTitle("Delete All Excursions").setMessage("Are you sure you want to delete this vacation and all excursions?").setPositiveButton("Yes", (innerDialog, innerWhich) -> {
                                        repository.deleteAllAssociatedExcursions(vacationID); // Call to delete excursions
                                        repository.delete(vacation); // Call to delete vacation
                                        Toast.makeText(getContext(), "Successfully deleted vacation and excursions.", Toast.LENGTH_SHORT).show();
                                        dismiss(); // Close the bottom sheet
                                    }).setNegativeButton("No", (innerDialog, innerWhich) -> innerDialog.dismiss()).show();
                                }).setNegativeButton("No", (dialog, which) -> dialog.dismiss()).show();
                            }
                        });
                    }
                });
            }
        }));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        if (dialog != null) {
            FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior<View> behavior = null;
            if (bottomSheet != null) {
                behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED); // Expand to full height
            }
        }
    }
}
