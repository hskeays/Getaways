package com.howardkeays.getaways.UI.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.howardkeays.getaways.R;
import com.howardkeays.getaways.UI.BottomSheetFragment;
import com.howardkeays.getaways.UI.VacationDetails;
import com.howardkeays.getaways.entities.Vacation;

import java.util.ArrayList;
import java.util.List;

public class VacationAdapter extends RecyclerView.Adapter<VacationAdapter.VacationViewHolder> {

    private final Context context;
    private final LayoutInflater inflater;
    private List<Vacation> vacations = new ArrayList<>();
    private List<Vacation> filteredVacations = new ArrayList<>();

    public VacationAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public VacationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.vacation_list_item, parent, false);
        return new VacationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VacationViewHolder holder, int position) {
        final Vacation current = filteredVacations.get(position);
        holder.vacationItemView.setText(current.getVacationTitle());
        String date = current.getStartDate() + " -\n" + current.getEndDate();
        holder.vacationItemViewDate.setText(date);
    }

    @Override
    public int getItemCount() {
        return filteredVacations.size();
    }

    public void setVacations(List<Vacation> vacations) {
        this.vacations = vacations;
        this.filteredVacations = new ArrayList<>(vacations);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        if (query.isEmpty()) {
            // Restore the original full list
            filteredVacations = new ArrayList<>(vacations);
        } else {
            List<Vacation> filteredList = new ArrayList<>();
            for (Vacation vacation : vacations) {
                // Check if vacation title matches the query
                if (vacation.getVacationTitle().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(vacation);
                }
            }
            filteredVacations = filteredList; // Update the filtered list
        }

        // Notify the adapter that the data set has changed
        notifyDataSetChanged();
    }

    public class VacationViewHolder extends RecyclerView.ViewHolder {
        private final TextView vacationItemView;
        private final TextView vacationItemViewDate;
        private final ImageButton btnMenu;

        public VacationViewHolder(@NonNull View itemView) {
            super(itemView);
            vacationItemView = itemView.findViewById(R.id.tv_vacation_list_item);
            vacationItemViewDate = itemView.findViewById(R.id.tv_vacation_list_item_date);
            btnMenu = itemView.findViewById(R.id.btn_menu);

            View vacationItemViewCard = itemView.findViewById(R.id.tv_vacation_list_item_card);
            vacationItemViewCard.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    final Vacation current = filteredVacations.get(position);
                    Intent intent = new Intent(context, VacationDetails.class);
                    intent.putExtra("ID", current.getId());
                    intent.putExtra("VACATION_TITLE", current.getVacationTitle());
                    intent.putExtra("HOTEL_NAME", current.getHotelName());
                    intent.putExtra("START_DATE", current.getStartDate());
                    intent.putExtra("END_DATE", current.getEndDate());
                    context.startActivity(intent);
                }
            });

            // Click listener for the menu icon
            btnMenu.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    final Vacation current = filteredVacations.get(position);

                    // Create a new instance of the BottomSheetFragment with vacation details
                    BottomSheetFragment bottomSheet = BottomSheetFragment.newInstance(current.getId(), current.getVacationTitle(), current.getHotelName(), current.getStartDate(), current.getEndDate());
                    bottomSheet.show(((AppCompatActivity) context).getSupportFragmentManager(), bottomSheet.getTag());
                }
            });
        }
    }
}
