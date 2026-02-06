package com.example.lab5_starter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public class CityDialogFragment extends DialogFragment {
    interface CityDialogListener {
        void updateCity(City city, String oldName, String newName, String newProvince);
        void addCity(City city);
        void deleteCity(City city);
    }
    private CityDialogListener listener;

    public static CityDialogFragment newInstance(City city){
        Bundle args = new Bundle();
        args.putSerializable("City", city);

        CityDialogFragment fragment = new CityDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CityDialogListener){
            listener = (CityDialogListener) context;
        }
        else {
            throw new RuntimeException("Implement listener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_city_details, null);
        EditText editMovieName = view.findViewById(R.id.edit_city_name);
        EditText editMovieYear = view.findViewById(R.id.edit_province);

        String tag = getTag();
        Bundle bundle = getArguments();
        City city;
        //If editing city, set text
        if (Objects.equals(tag, "City Details") && bundle != null) {
            city = (City) bundle.getSerializable("City");
            assert city != null;
            editMovieName.setText(city.getName());
            editMovieYear.setText(city.getProvince());
        } else {
            //otherwise add
            city = null;
        }
        City finalCity = city;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        if (Objects.equals(tag, "City Details")) {
            return builder
                    .setView(view)
                    .setTitle("City Details")
                    .setNegativeButton("Cancel", null)
                    //Delete removes city from FireStore
                    .setNeutralButton("Delete", (dialog, which) -> {
                        listener.deleteCity(finalCity);
                    })
                    //Update saves changed to FireStore
                    .setPositiveButton("Update", (dialog, which) -> {
                        String oldName = finalCity.getName();
                        String newName = editMovieName.getText().toString();
                        String newProvince = editMovieYear.getText().toString();
                        listener.updateCity(finalCity, oldName, newName, newProvince);
                    })
                    .create();
        } else {
            // Adding new city
            return builder
                    .setView(view)
                    .setTitle("Add City")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Add", (dialog, which) -> {
                        String title = editMovieName.getText().toString();
                        String year = editMovieYear.getText().toString();
                        listener.addCity(new City(title, year));
                    })
                    .create();
        }
    }
}
