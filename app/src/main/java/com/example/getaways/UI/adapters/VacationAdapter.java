package com.example.getaways.UI.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.getaways.R;
import com.example.getaways.UI.VacationDetails;
import com.example.getaways.entities.Vacation;

import java.util.ArrayList;
import java.util.List;

public class VacationAdapter extends RecyclerView.Adapter<VacationAdapter.VacationViewHolder> {

    private final Context context;
    private final LayoutInflater inflater;
    private List<Vacation> vacations = new ArrayList<>();

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
        final Vacation current = vacations.get(position);
        holder.vacationItemView.setText(current.getVacationTitle());
    }

    @Override
    public int getItemCount() {
        return vacations.size();
    }

    public void setVacations(List<Vacation> vacations) {
        this.vacations = vacations;
        notifyDataSetChanged();
    }

    public class VacationViewHolder extends RecyclerView.ViewHolder {
        private final TextView vacationItemView;

        public VacationViewHolder(@NonNull View itemView) {
            super(itemView);
            vacationItemView = itemView.findViewById(R.id.tv_vacation_list_item);
            vacationItemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    final Vacation current = vacations.get(position);
                    Intent intent = new Intent(context, VacationDetails.class);
                    intent.putExtra("ID", current.getId());
                    intent.putExtra("VACATION_TITLE", current.getVacationTitle());
                    intent.putExtra("HOTEL_NAME", current.getHotelName());
                    intent.putExtra("START_DATE", current.getStartDate());
                    intent.putExtra("END_DATE", current.getEndDate());
                    context.startActivity(intent);
                }
            });
        }
    }
}
