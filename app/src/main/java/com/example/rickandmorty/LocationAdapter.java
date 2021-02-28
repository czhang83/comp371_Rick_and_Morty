package com.example.rickandmorty;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {
    private List<Location> locations;

    //pass this list into the constructor of the adapter
    public LocationAdapter(List<Location> locations) {
        this.locations = locations;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // inflate the customer layout
        View locationView = inflater.inflate(R.layout.item_location, parent, false);
        return new ViewHolder(locationView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // populate data into the item through holder
        Location location = locations.get(position);

        holder.textView_item_location_name.setText(location.getName());
        holder.textView_item_location_type.setText("Type: " + location.getType());
        holder.textView_item_location_dimension.setText("Dimension: " + location.getDimension());
    }

    @Override
    public int getItemCount() {
        // return the total number of items in the list
        return locations.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView_item_location_name;
        TextView textView_item_location_type;
        TextView textView_item_location_dimension;

        public ViewHolder(View itemView) {
            super(itemView);
            textView_item_location_name = itemView.findViewById(R.id.textView_item_location_name);
            textView_item_location_type = itemView.findViewById(R.id.textView_item_location_type);
            textView_item_location_dimension = itemView.findViewById(R.id.textView_item_location_dimension);
        }

    }
}    
