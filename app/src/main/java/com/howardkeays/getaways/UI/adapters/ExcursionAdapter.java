package com.howardkeays.getaways.UI.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.howardkeays.getaways.R;
import com.howardkeays.getaways.UI.ExcursionDetails;
import com.howardkeays.getaways.entities.Excursion;

import java.util.ArrayList;
import java.util.List;

public class ExcursionAdapter extends RecyclerView.Adapter<ExcursionAdapter.ExcursionViewHolder> {
    private final Context context;
    private final LayoutInflater inflater;
    private List<Excursion> excursions = new ArrayList<>();

    public ExcursionAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ExcursionAdapter.ExcursionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.excursion_list_item, parent, false);
        return new ExcursionAdapter.ExcursionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExcursionAdapter.ExcursionViewHolder holder, int position) {
        Excursion current = excursions.get(position);
        String title = current.getExcursionTitle();
        String excursionDate = current.getExcursionDate();
        int vacationID = current.getVacationID();
        holder.excursionTextView.setText(title);
        holder.excursionTextViewDate.setText(excursionDate);
    }

    @Override
    public int getItemCount() {
        return excursions.size();
    }

    public List<Excursion> getExcursions() {
        return excursions;
    }

    public void setExcursions(List<Excursion> excursions) {
        this.excursions = excursions;
        notifyDataSetChanged();
    }

    public class ExcursionViewHolder extends RecyclerView.ViewHolder {
        private final TextView excursionTextView;
        private final TextView excursionTextViewDate;

        public ExcursionViewHolder(@NonNull View itemView) {
            super(itemView);
            excursionTextView = itemView.findViewById(R.id.tv_excursion_list_item);
            excursionTextViewDate = itemView.findViewById(R.id.tv_excursion_list_item_date);

            View excursionTextViewCard = itemView.findViewById(R.id.tv_excursion_list_item_card);
            excursionTextViewCard.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    final Excursion current = excursions.get(position);
                    Intent intent = new Intent(context, ExcursionDetails.class);
                    intent.putExtra("ID", current.getId());
                    intent.putExtra("EXCURSION_DATE", current.getExcursionDate());
                    intent.putExtra("EXCURSION_TITLE", current.getExcursionTitle());
                    intent.putExtra("VACATION_ID", current.getVacationID());
                    context.startActivity(intent);
                }
            });
        }
    }
}
