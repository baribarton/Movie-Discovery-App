package com.example.moviediscovery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SampleRecyclerAdapter extends RecyclerView.Adapter<SampleRecyclerAdapter.TextViewHolder> {
    protected int pagesCount;

    public SampleRecyclerAdapter(int pagesCount) {
        this.pagesCount = pagesCount;
    }

    @NonNull
    @Override
    public TextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new TextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TextViewHolder holder, int position) {
        holder.textView.setText("TEST------------------------------");
    }

    @Override
    public int getItemCount() {
        return pagesCount;
    }

    static class TextViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;

        public TextViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.textView);
        }
    }
}
