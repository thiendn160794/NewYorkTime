package com.thiendn.coderschool.newyorktime.dialog;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v7.widget.AppCompatSpinner;

import com.thiendn.coderschool.newyorktime.R;
import com.thiendn.coderschool.newyorktime.api.SearchRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by thiendn on 27/02/2017.
 */

public class SettingDialog extends DialogFragment {

    private EditText etBeginDate;
    private AppCompatSpinner spinner;
    private CheckBox cbArts;
    private CheckBox cbFashionAndStyle;
    private CheckBox cbSports;
    private Button btnSave;

    Context context;
    SearchRequest mSearchRequest;
    Calendar myCalendar = Calendar.getInstance();

    public static SettingDialog newInstance(SearchRequest searchRequest, Context context) {
        SettingDialog f = new SettingDialog();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        f.setArguments(args);
        f.setSearchRequest(searchRequest);
        f.setContext(context);
        return f;
    }

    private void setSearchRequest(SearchRequest searchRequest){
        mSearchRequest = searchRequest;
    }

    private void setContext(Context context){
        this.context = context;
    }

    public interface Listener{
        void onSaveSetting(SearchRequest searchRequest);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_fragment, container);

        etBeginDate = (EditText) view.findViewById(R.id.etBeginDate);
        spinner = (AppCompatSpinner) view.findViewById(R.id.spSort);
        cbArts = (CheckBox) view.findViewById(R.id.cbArts);
        cbFashionAndStyle = (CheckBox) view.findViewById(R.id.cbFashionAndStyle);
        cbSports = (CheckBox) view.findViewById(R.id.cbSports);
        btnSave = (Button) view.findViewById(R.id.btnSave);

        try {
            String trDate = mSearchRequest.getBeginDate();//yyyyMMdd
            Date tradeDate = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).parse(trDate);
            String krwtrDate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(tradeDate);
            etBeginDate.setText(krwtrDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<String> list = new ArrayList<String>();
        list.add("Newest");
        list.add("Oldest");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        if (mSearchRequest.getSort().equals("newest")) spinner.setSelection(0);
        if (mSearchRequest.getSort().equals("oldest")) spinner.setSelection(1);
        if (mSearchRequest.hasArts) cbArts.setChecked(true);
        if (mSearchRequest.hasFashionStyle) cbFashionAndStyle.setChecked(true);
        if (mSearchRequest.hasSports) cbSports.setChecked(true);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd/MM/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                etBeginDate.setText(sdf.format(myCalendar.getTime()));
            }
        };

        etBeginDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(context, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String beginDate = null;
                try {
                    Date tradeDate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(etBeginDate.getText().toString());
                    beginDate = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).format(tradeDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String sort = spinner.getSelectedItem().toString().toLowerCase();
                boolean hasArt = cbArts.isChecked();
                boolean hasFashionAndStyle = cbFashionAndStyle.isChecked();
                boolean hasSports = cbSports.isChecked();
                mSearchRequest.setBeginDate(beginDate);
                mSearchRequest.setSort(sort);
                mSearchRequest.hasArts = hasArt;
                mSearchRequest.hasFashionStyle = hasFashionAndStyle;
                mSearchRequest.hasSports = hasSports;
                SettingDialog.Listener activity = (Listener) getActivity();
                activity.onSaveSetting(mSearchRequest);
                getDialog().dismiss();
            }
        });

        return view;
    }
}
