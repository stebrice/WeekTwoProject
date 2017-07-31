package com.codepath.nytimessearch.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.nytimessearch.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by STEPHAN987 on 7/30/2017.
 */

public class DatePickerFragment extends DialogFragment  {
    String dateReceived;
    DatePickerDialog.OnDateSetListener onDateSet;
    private boolean isModal = false;

    public static DatePickerFragment newInstance(String dateChosen)
    {
        DatePickerFragment frag = new DatePickerFragment();
        frag.isModal = true; // WHEN FRAGMENT IS CALLED AS A DIALOG SET FLAG

        Bundle args = new Bundle();
        args.putString("chosen_date", dateChosen);
        frag.setArguments(args);
        return frag;
    }

    public DatePickerFragment(){}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        dateReceived =  getArguments().getString("chosen_date");
        Date dateValue = null;

        if (dateReceived.trim().length() > 0)
        {
            try {
                dateValue = (Date) parseDate(dateReceived,"dd/MM/yyyy");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        int year;
        int month;
        int day;
        if (dateValue == null) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }
        else{
            final Calendar c = Calendar.getInstance();
            c.setTime(dateValue);
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }
        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), onDateSet, year, month, day);
    }

    private Date parseDate(String date, String format) throws ParseException
    {
        DateFormat formatter = new SimpleDateFormat(format);
        return (Date) formatter.parse(date);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(isModal) // AVOID REQUEST FEATURE CRASH
        {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
        else {
            View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
            return rootView;
        }
    }

    public void setCallBack(DatePickerDialog.OnDateSetListener onDate) {
        onDateSet = onDate;
    }
}
