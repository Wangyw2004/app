package com.example.no1.features.counter.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.no1.R;
import com.example.no1.features.counter.viewmodels.CounterViewModel;

public class CounterFragment extends Fragment {

    private CounterViewModel viewModel;
    private TextView counterText;
    private Button incrementButton;
    private Button resetButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_counter, container, false);

        initViews(view);
        setupViewModel();
        setupObservers();
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        counterText = view.findViewById(R.id.counterText);
        incrementButton = view.findViewById(R.id.incrementButton);
        resetButton = view.findViewById(R.id.resetButton);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(CounterViewModel.class);
    }

    private void setupObservers() {
        viewModel.getCounter().observe(getViewLifecycleOwner(), counter -> {
            if (counter != null) {
                counterText.setText(String.valueOf(counter.getCount()));

                if (counter.getCount() == 10) {
                    Toast.makeText(getContext(), "达到10了！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupListeners() {
        incrementButton.setOnClickListener(v -> viewModel.increment());
        resetButton.setOnClickListener(v -> viewModel.reset());
    }
}