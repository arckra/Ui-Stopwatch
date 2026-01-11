package com.example.snamer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LapAdapter extends RecyclerView.Adapter<LapAdapter.VH> {

    private final List<String> laps;

    public LapAdapter(List<String> laps) {
        this.laps = laps;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lap, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.txtLapTime.setText(laps.get(position));
    }

    @Override
    public int getItemCount() {
        return laps.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView txtLapTime;
        VH(@NonNull View itemView) {
            super(itemView);
            txtLapTime = itemView.findViewById(R.id.txtLapTime);
        }
    }
}
