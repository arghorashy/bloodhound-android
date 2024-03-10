package com.mumbo.bloodhound;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ButtonFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ButtonFragment extends Fragment {
    AlertDialog.Builder alertBuilder;
    MaterialButton button;
    private static final String ARG_NAME = "arg_name";
    private static final String ARG_SCALE_MAX = "arg_scale_max";
    private static final String ARG_HAS_TEXT_FIELD = "arg_has_text_field";

    private String paramName;
    private int paramScaleMax;

    private boolean paramHasTextField;

    public ButtonFragment() {
        // Required empty public constructor
    }

    public static ButtonFragment newInstance(BloodhoundConfigRow row) {
        ButtonFragment fragment = new ButtonFragment();
        Bundle args = new Bundle();
        // Set up button name
        args.putString(ARG_NAME, row.buttonName);
        // Set up Slider
        int scaleMax = row.scaleMax.isEmpty() ? 0 : Integer.parseInt(row.scaleMax);
        args.putInt(ARG_SCALE_MAX, scaleMax);
        // Set up Text Field
        args.putBoolean(ARG_HAS_TEXT_FIELD, row.text.equals("true"));
        // Send off Arguments
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            paramName = getArguments().getString(ARG_NAME);
            paramScaleMax = getArguments().getInt(ARG_SCALE_MAX);
            paramHasTextField = getArguments().getBoolean(ARG_HAS_TEXT_FIELD);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_button, container, false);

        alertBuilder = new AlertDialog.Builder(view.getContext());

        button = view.findViewById(R.id.button);
        button.setText(this.paramName);
        button.setOnClickListener((View v) -> {
            // TODO(team) disable additional button presses until the first one is resolved

            Log.d("ButtonFragment", "Click registered for button with name: " + this.paramName);

            // Create custom dialog layout
            final View dialogLayout = getLayoutInflater().inflate(R.layout.dialog, null);

            // Set up Slider
            RangeSlider slider = dialogLayout.findViewById(R.id.range_slider);
            if (this.dialogHasSlider()) {
                slider.setValueTo(this.paramScaleMax);
            } else {
                slider.setVisibility(View.GONE);
            }

            // Set up Text Field
            TextInputLayout textField = dialogLayout.findViewById(R.id.text_field);
            Log.d("this.paramHasTextField", String.valueOf(this.paramHasTextField));
            if (!this.paramHasTextField) {
                textField.setVisibility(View.GONE);
            }

            alertBuilder
                .setTitle("Log \"" + this.paramName + "\"?")
                .setView(dialogLayout)
                .setCancelable(false)
                .setPositiveButton("Log it!", (DialogInterface dialog, int id) -> {
                    dialog.cancel();
                    String sliderValueString = getStringSliderValue(slider);
                    String textFieldString = textField.getEditText() == null ? "" : textField.getEditText().getText().toString();
                    sendLogToSpreadsheet(sliderValueString, textFieldString);
                    String toastMessage = createSuccessfulLogToast(sliderValueString);
                    showToast(view.getContext(), toastMessage);
                })
                .setNegativeButton("No", (DialogInterface dialog, int id) -> {
                    dialog.cancel();
                    showToast(view.getContext(), "Log cancelled.");
                });

            AlertDialog alert = alertBuilder.create();
            alert.show();
        });

        return view;
    }

    private String createSuccessfulLogToast(String sliderValueString) {
        String toastMessage = "Writing \"" + this.paramName;
        if (dialogHasSlider()) {
            toastMessage += " " + sliderValueString + "/" + paramScaleMax;
        }
        toastMessage += "\"";
        return toastMessage;
    }

    private void sendLogToSpreadsheet(String sliderValue, String textFieldString) {
        ArrayList<String> logArguments = new ArrayList<>();
        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:m a");
        logArguments.add(dateFormat.format(today));
        logArguments.add(timeFormat.format(today));
        logArguments.add(this.paramName);
        logArguments.add(this.dialogHasSlider() ? sliderValue : "");
        logArguments.add(textFieldString);
        GoogleSheetsAPI.writeRowToLogSheet(this.getContext(), logArguments);
    }

    private String getStringSliderValue(RangeSlider slider) {
        int rawSliderValue = Math.round(slider.getValues().get(0));
       return String.valueOf(rawSliderValue);
    }

    private void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    private boolean dialogHasSlider() {
        return this.paramScaleMax > 0;
    }
}