package com.example.lab_5_task_9_shibitov_nikolay;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {

    public interface OnCityClickListener {
        void onClick(String city);
        boolean onLongClick(String city, int position);
    }

    private final List<String> cities;
    private final List<String> temperatures; // e.g. "+5°C" or "..."
    private final OnCityClickListener listener;

    public CityAdapter(List<String> cities, List<String> temperatures, OnCityClickListener listener) {
        this.cities = cities;
        this.temperatures = temperatures;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_city, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String city = cities.get(position);
        String temp = position < temperatures.size() ? temperatures.get(position) : "...";
        holder.tvCity.setText(city);
        holder.tvTemp.setText(temp);
        holder.itemView.setOnClickListener(v -> listener.onClick(city));
        holder.itemView.setOnLongClickListener(v -> listener.onLongClick(city, holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCity, tvTemp;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCity = itemView.findViewById(R.id.tvCity);
            tvTemp = itemView.findViewById(R.id.tvTemp);
        }
    }
}
