package com.codepath.nytimessearch.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
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


public class SettingsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    EditText etBeginDate;
    Spinner spSortOrder;
    CheckBox cbArts;
    CheckBox cbFashionAndStyle;
    CheckBox cbSports;
    SearchFilter searchFilterToUse;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchFilterToUse = (SearchFilter) getIntent().getSerializableExtra("search_filter");
        setupViews();
    }

    public void setupViews(){
        etBeginDate = (EditText) findViewById(R.id.etBeginDate);
        cbArts = (CheckBox) findViewById(R.id.cbArts);
        cbFashionAndStyle = (CheckBox) findViewById(R.id.cbFashionAndStyle);
        cbSports = (CheckBox) findViewById(R.id.cbSports);

        spSortOrder = (Spinner) findViewById(R.id.spSortOrder);
        ArrayAdapter spAdapter = ArrayAdapter.createFromResource(this, R.array.sort_order, R.layout.support_simple_spinner_dropdown_item);
        spSortOrder.setAdapter(spAdapter);

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
    }

    public void showDatePickerDialog(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void setDate(final Calendar calendar) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        etBeginDate.setText(dateFormat.format(calendar.getTime()));

    }

    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar cal = new GregorianCalendar(year,month,day);
        setDate(cal);
    }

    public void onApplyFilter(View view) {
        // Prepare data intent
        Intent data = new Intent();

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
        data.putExtra("search_filter", searchFilterToUse);

        // Activity finished ok, return the data
        setResult(RESULT_OK, data); // set result code and bundle data for response
        finish(); // closes the activity, pass data to parent
    }

    public void onClearFilter(View view) {
        etBeginDate.setText("");
        cbArts.setChecked(false);
        cbFashionAndStyle.setChecked(false);
        cbSports.setChecked(false);
        spSortOrder.setSelection(0);
    }

    public static class DatePickerFragment extends DialogFragment
                {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(),
                    (DatePickerDialog.OnDateSetListener)
                            getActivity(), year, month, day);
        }


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
}
