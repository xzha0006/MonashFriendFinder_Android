package com.johnsyard.monashfriendfinder.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;
import java.util.Calendar;

import com.johnsyard.monashfriendfinder.R;



/**
 * This class is used to show the date picker as a fragment
 * Created by xuanzhang on 30/04/2017.
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{
    private DatePickerDialog datePickerDialog;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //create a calendar
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(getActivity(), this, yy, mm, dd);
        datePickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTime().getTime());
        //set the start date to 57 years ago
        datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTime().getTime() - 31536000 * 57 * 1000L);
        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int yy, int mm, int dd) {
        populateSetDate(yy, mm+1, dd);
    }
    public void populateSetDate(int year, int month, int day) {
        //set the date time into text view
        ((TextView) getActivity().findViewById(R.id.tv_datepicker_dob)).setText(year + "-" + month + "-" + day);
    }
}
