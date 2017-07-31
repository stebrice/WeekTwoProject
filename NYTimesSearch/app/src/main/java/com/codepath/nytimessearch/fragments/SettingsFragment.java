package com.codepath.nytimessearch.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.models.SearchFilter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by STEPHAN987 on 7/30/2017.
 */

public class SettingsFragment extends DialogFragment {
    EditText etBeginDate;
    Spinner spSortOrder;
    CheckBox cbArts;
    CheckBox cbFashionAndStyle;
    CheckBox cbSports;
    SearchFilter searchFilterToUse;
    Button btnSubmit;
    Button btnClear;
    ImageView ivCalendar;

    public interface SettingsDialogListener {
        void onFinishSettingsDialog(SearchFilter searchFilterDefined);
    }

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance(SearchFilter searchFilter) {
        SettingsFragment settingsFragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putSerializable("search_filter", searchFilter);
        settingsFragment.setArguments(args);
        return settingsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        setupViews(view);

        getDialog().setTitle("FILTER QUERY");
    }

    public void setupViews(View view){
        etBeginDate = (EditText) view.findViewById(R.id.etBeginDate);
        cbArts = (CheckBox) view.findViewById(R.id.cbArts);
        cbFashionAndStyle = (CheckBox) view.findViewById(R.id.cbFashionAndStyle);
        cbSports = (CheckBox) view.findViewById(R.id.cbSports);

        spSortOrder = (Spinner) view.findViewById(R.id.spSortOrder);
        ArrayAdapter spAdapter = ArrayAdapter.createFromResource(this.getContext(), R.array.sort_order, R.layout.support_simple_spinner_dropdown_item);
        spSortOrder.setAdapter(spAdapter);

        searchFilterToUse = (SearchFilter) getArguments().getSerializable("search_filter");
        if (searchFilterToUse.getBeginDate().trim().length()>0){
            try {
                Date dateReceived = parseDate(searchFilterToUse.getBeginDate().trim(), "yyyyMMdd");
                etBeginDate.setText(parseDateToNewFormat(dateReceived, "dd/MM/yyyy"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (searchFilterToUse.getSortOrder().trim().length()>0){
            setSpinnerToValue(spSortOrder, searchFilterToUse.getSortOrder().trim());
        }

        if (searchFilterToUse.getNewsDeskValues().size() > 0)
        {
            if (searchFilterToUse.getNewsDeskValues().contains(getString(R.string.cb_arts_filter_text).toString()))
                cbArts.setChecked(true);
            if (searchFilterToUse.getNewsDeskValues().contains(getString(R.string.cb_fashion_filter_text)))
                cbFashionAndStyle.setChecked(true);
            if (searchFilterToUse.getNewsDeskValues().contains(getString(R.string.cb_sports_filter_text)))
                cbSports.setChecked(true);
        }

        btnSubmit = (Button) view.findViewById(R.id.btnApplyFilter);
        btnClear = (Button) view.findViewById(R.id.btnClearFilter);
        ivCalendar = (ImageView)  view.findViewById(R.id.ivBeginDate);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyFilter(view);
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFilter(view);
            }
        });

        ivCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
    }

    private Date parseDate(String date, String format) throws ParseException
    {
        DateFormat formatter = new SimpleDateFormat(format);
        return (Date) formatter.parse(date);
    }

    private String parseDateToNewFormat(Date date, String newFormat) throws ParseException
    {
        SimpleDateFormat formatter = new SimpleDateFormat(newFormat);
        return formatter.format(date);
    }

    public void setSpinnerToValue(Spinner spinner, String value) {
        int index = 0;
        SpinnerAdapter adapter = spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(value)) {
                index = i;
                break; // terminate loop
            }
        }
        spinner.setSelection(index);
    }

    private void showDatePickerDialog() {
        //FragmentManager fm = getFragmentManager();
        //DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(etBeginDate.getText().toString());

        //// SETS the target fragment for use later when sending results
        //datePickerFragment.setTargetFragment(SettingsFragment.this, 300);
        //datePickerFragment.show(fm, "datePicker");

        DatePickerFragment dpf = new DatePickerFragment().newInstance(etBeginDate.getText().toString());
        dpf.setCallBack(onDate);
        dpf.show(getFragmentManager().beginTransaction(), "DatePickerFragment");
        //DialogFragment picker = new DatePickerFragment();
        //picker.show(getFragmentManager(), "datePicker");
    }

    DatePickerDialog.OnDateSetListener onDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            Calendar cal = new GregorianCalendar(year,monthOfYear,dayOfMonth);
            final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            etBeginDate.setText(dateFormat.format(cal.getTime()));
        }
    };

    public void applyFilter(View view) {

        //Evaluate the selected values and prepare a string value for data
        // Pass relevant data back as a result
        Date date1=null;
        String beginDate = "";
        String sortOrder;
        ArrayList<String> newsDeskValues = new ArrayList<>();

        try {
            date1 = parseDate(etBeginDate.getText().toString(), "dd/MM/yyyy");
            beginDate = parseDateToNewFormat(date1,"yyyyMMdd");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        sortOrder = spSortOrder.getSelectedItem().toString();
        if (sortOrder.equals("-------"))
        {
            sortOrder = "";
        }

        if(cbArts.isChecked())
            newsDeskValues.add(cbArts.getText().toString());
        else
            newsDeskValues.remove(cbArts.getText().toString());

        if(cbFashionAndStyle.isChecked())
            newsDeskValues.add(cbFashionAndStyle.getText().toString());
        else
            newsDeskValues.remove(cbFashionAndStyle.getText().toString());

        if(cbSports.isChecked())
            newsDeskValues.add(cbSports.getText().toString());
        else
            newsDeskValues.remove(cbSports.getText().toString());

        searchFilterToUse = new SearchFilter(beginDate,sortOrder,newsDeskValues);

        SettingsDialogListener settingsDialogListener = (SettingsDialogListener) getActivity();
        settingsDialogListener.onFinishSettingsDialog(searchFilterToUse);

        // Close the dialog and return back to the parent activity
        dismiss();
    }

    public void clearFilter(View view) {
        etBeginDate.setText("");
        cbArts.setChecked(false);
        cbFashionAndStyle.setChecked(false);
        cbSports.setChecked(false);
        spSortOrder.setSelection(0);

        searchFilterToUse = new SearchFilter("","",new ArrayList<String>());

        SettingsDialogListener settingsDialogListener = (SettingsDialogListener) getActivity();
        settingsDialogListener.onFinishSettingsDialog(searchFilterToUse);

        // Close the dialog and return back to the parent activity
        dismiss();
    }

    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        super.onResume();
    }
}
