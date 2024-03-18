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
import com.google.android.material.switchmaterial.SwitchMaterial;
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
    private static final String ARG_OPTION1 = "arg_option1";
    private static final String ARG_OPTION2 = "arg_option2";
    private static final String ARG_OPTION3 = "arg_option3";

    private String paramName;
    private int paramScaleMax;

    private boolean paramHasTextField;
    private String paramOption1;
    private String paramOption2;
    private String paramOption3;

    public ButtonFragment() {
        // Required empty public constructor
    }

    public static ButtonFragment newInstance(BloodhoundConfigRow row) {
        ButtonFragment fragment = new ButtonFragment();
        Bundle args = new Bundle();
        // Set up button name
        args.putString(ARG_NAME, row.buttonName);
        // Set up Slider
        args.putInt(ARG_SCALE_MAX, row.scaleMax);
        // Set up Text Field
        args.putBoolean(ARG_HAS_TEXT_FIELD, row.text.equals("true"));
        // Set up Options
        args.putString(ARG_OPTION1, row.option1);
        args.putString(ARG_OPTION2, row.option2);
        args.putString(ARG_OPTION3, row.option3);
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
            paramOption1 = getArguments().getString(ARG_OPTION1);
            paramOption2 = getArguments().getString(ARG_OPTION2);
            paramOption3 = getArguments().getString(ARG_OPTION3);
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
            Log.d("ButtonFragment", "Click registered for button with name: " + this.paramName);

            // Disable button until alert is resolved.
            button.setClickable(false);

            // Create custom dialog layout
            final View dialogLayout = getLayoutInflater().inflate(R.layout.log_dialog, null);

            // Set up Slider
            RangeSlider slider = dialogLayout.findViewById(R.id.range_slider);
            if (this.dialogHasSlider()) {
                slider.setValueTo(this.paramScaleMax);
            } else {
                slider.setVisibility(View.GONE);
                dialogLayout.findViewById(R.id.range_slider_name_text).setVisibility(View.GONE);
            }

            // Set up Text Field
            TextInputLayout textField = dialogLayout.findViewById(R.id.text_field);
            if (!this.paramHasTextField) {
                textField.setVisibility(View.GONE);
            }

            // Set up options
            SwitchMaterial option1 = dialogLayout.findViewById(R.id.option1);
            if(!this.dialogHasOption1()) {
                option1.setVisibility(View.GONE);
            } else {
                option1.setText(this.paramOption1);
            }
            SwitchMaterial option2 = dialogLayout.findViewById(R.id.option2);
            if(!this.dialogHasOption2()) {
                option2.setVisibility(View.GONE);
            } else {
                option2.setText(this.paramOption2);
            }
            SwitchMaterial option3 = dialogLayout.findViewById(R.id.option3);
            if(!this.dialogHasOption3()) {
                option3.setVisibility(View.GONE);
            } else {
                option3.setText(this.paramOption3);
            }

            alertBuilder
                .setTitle(this.paramName)
                .setView(dialogLayout)
                .setCancelable(false)
                .setPositiveButton("Log it!", (DialogInterface dialog, int id) -> {
                    dialog.cancel();
                    String sliderValueString = getStringSliderValue(slider);
                    String textFieldString = getTextFieldValue(textField);
                    String option1Value = getOptionValue(option1, this.paramOption1);
                    String option2Value = getOptionValue(option2, this.paramOption2);
                    String option3Value = getOptionValue(option3, this.paramOption3);
                    RangeSlider timeAgoSlider = dialogLayout.findViewById(R.id.time_picker);
                    int timeAgoMn = Math.round(timeAgoSlider.getValues().get(0));
                    sendLogToSpreadsheet(timeAgoMn, sliderValueString, textFieldString, option1Value, option2Value, option3Value);
                    String toastMessage = createSuccessfulLogToast(sliderValueString);
                    showToast(view.getContext(), toastMessage);
                    button.setClickable(true);
                })
                .setNegativeButton("No", (DialogInterface dialog, int id) -> {
                    dialog.cancel();
                    button.setClickable(true);
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

    private void sendLogToSpreadsheet(int timeAgoMn, String sliderValue, String textFieldString,
                                      String option1Value, String option2Value, String option3Value) {
        ArrayList<String> logArguments = new ArrayList<>();
        Date logTime = new Date(System.currentTimeMillis() - timeAgoMn * 60000);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        logArguments.add(dateFormat.format(logTime));
        logArguments.add(timeFormat.format(logTime));
        logArguments.add(this.paramName);
        logArguments.add(this.dialogHasSlider() ? sliderValue : "");
        logArguments.add(textFieldString);
        logArguments.add(option1Value);
        logArguments.add(option2Value);
        logArguments.add(option3Value);
        Context context = this.getContext();
        GoogleSheetsAPI.writeRowToLogSheet(context, AppState.profileMgr.getActiveProfile(), logArguments);
    }


    private String getOptionValue(SwitchMaterial optionToggle, String optionName) {
        return optionToggle.isChecked() ? optionName : "";
    }
    private String getStringSliderValue(RangeSlider slider) {
       int rawSliderValue = Math.round(slider.getValues().get(0));
       return String.valueOf(rawSliderValue);
    }
    private String getTextFieldValue(TextInputLayout textField) {
        return textField.getEditText() == null ? "" : textField.getEditText().getText().toString();
    }

    private void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    private boolean dialogHasSlider() {
        return this.paramScaleMax > 0;
    }

    private boolean dialogHasOption1() {
        return !this.paramOption1.isEmpty();
    }
    private boolean dialogHasOption2() {
        return !this.paramOption2.isEmpty();
    }
    private boolean dialogHasOption3() {
        return !this.paramOption3.isEmpty();
    }

}