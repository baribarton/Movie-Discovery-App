package com.example.moviediscovery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import me.relex.circleindicator.CircleIndicator3;

public class ViewPagerFragment extends Fragment {
    private SampleRecyclerAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sample_viewpager2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mAdapter = new SampleRecyclerAdapter(5);

        ViewPager2 viewpager = view.findViewById(R.id.view_pager);
        viewpager.setAdapter(mAdapter);

        // CircleIndicator3 for RecyclerView
        CircleIndicator3 indicator = view.findViewById(R.id.indicator);
        indicator.setViewPager(viewpager);

        // Observe Data Change
        mAdapter.registerAdapterDataObserver(indicator.getAdapterDataObserver());
    }
}
