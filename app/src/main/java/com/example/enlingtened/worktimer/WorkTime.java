package com.example.enlingtened.worktimer;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import java.util.Date;
import java.lang.Object;

public class WorkTime extends LinearLayout{
    // Define hooks to the children of compound control and global variables
    TextView txtDateStart;
    int dayStart;
    int monthStart;
    int yearStart;

    TextView txtDateEnd;
    int dayEnd;
    int monthEnd;
    int yearEnd;

    Button btnAccept;
    Button btnCancel;
    public static TextView txtTimeStart;
    public TextView txtTimeEnd;

    public TextView txtSummary;

    Context con;

    View me = this;

    // For setting time; setTime
    Calendar localTime = Calendar.getInstance();
    Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("Poland"));

    int hourStart;
    int hourEnd;
    int minStart;
    int minEnd;

    public WorkTime(Context context) {
        super(context);
        init(context);

        con = context;

        //calendar.setTimeZone(TimeZone.getTimeZone("Warsaw/Poland"));

        /*String inflateService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater layoutInflater;

        layoutInflater = (LayoutInflater) getContext().getSystemService(inflateService);
        // Inflate the resource R.layout.work_time_layout in
        // the context of THIS view and
        // automatically attach it to THIS view
        layoutInflater.inflate(R.layout.work_time_layout, this, true);*/
    }

    private void init(Context context)
    {
        // Inflate layout
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.work_time_layout, this);

        // Get ids
        txtTimeStart = (TextView) findViewById(R.id.txtTimeStart);
        txtTimeEnd = (TextView) findViewById(R.id.txtTimeEnd);

        txtDateStart = (TextView) findViewById(R.id.txtDateStart);
        txtDateEnd = (TextView) findViewById(R.id.txtDateEnd);

        btnAccept = (Button) findViewById(R.id.btnAccept);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        txtSummary = (TextView) findViewById(R.id.txtSummary);

        hourStart = calendar.get(Calendar.HOUR_OF_DAY);
        minStart = calendar.get(Calendar.MINUTE);

        // Additional "0" if necessary
        StringBuilder timeStart = new StringBuilder();

        if(hourStart < 10)
        {
            timeStart.append("0");
        }
        timeStart.append(hourStart).append(":");

        if(minStart < 10)
        {
            timeStart.append("0");
        }
        timeStart.append(minStart);

        //setTime; setting default starting time - local actual time
        calendar.setTimeInMillis(localTime.getTimeInMillis());
        txtTimeStart.setText(timeStart);

        timeStart.delete(0, timeStart.length());

        //setDate; setting default starting date - local actual date
        StringBuilder sbDateStart = new StringBuilder();

        sbDateStart.append(calendar.get(Calendar.DAY_OF_MONTH)).append(" ");
        dayStart = calendar.get(Calendar.DAY_OF_MONTH);

        sbDateStart.append(getMonth(calendar.get(Calendar.MONTH) + 1));
        monthStart = calendar.get(Calendar.MONTH) + 1;

        sbDateStart.append(" ").append(calendar.get(Calendar.YEAR));
        yearStart = calendar.get(Calendar.YEAR);

        txtDateStart.setText(sbDateStart);

        hookupControls();
    }

    // hookup controls for layout compounds - onClickListeners
    private void hookupControls()
    {
        txtDateStart.setOnClickListener(new LinearLayout.OnClickListener(){
            public void onClick(View v)
            {

                showDatePicker(txtDateStart);
            }
        });

        txtTimeStart.setOnClickListener(new LinearLayout.OnClickListener(){
            public void onClick(View v)
            {

                showTimePicker(txtTimeStart);
            }
        });

        txtDateEnd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                showDatePicker(txtDateEnd);
            }
        });

        txtTimeEnd.setOnClickListener(new LinearLayout.OnClickListener(){
            public void onClick(View v)
            {

                showTimePicker(txtTimeEnd);
            }
        });

        btnAccept.setOnClickListener(new LinearLayout.OnClickListener()
        {
            public void onClick(View v)
            {

                if(txtDateEnd.getText() != "" && txtTimeEnd.getText() != "")
                {
                    // Set sum value - time from all work times
                    MainActivity.setSum(calculateTime());

                    // Get time from all work times
                    long temp = MainActivity.getSum();

                    // Calculate hour and min out of summary time (in milliseconds)
                    diffHour = temp / (60 * 60 * 1000);
                    diffMin = temp / (60 * 1000) % 60;

                    // Set string for displaying summary time
                    txtUpdate = diffHour + "h " + diffMin + "m ";

                    // Update string
                    MainActivity.updateSum(txtUpdate);

                    // Disable Accept button
                    btnAccept.setVisibility(GONE);
                    btnCancel.setText("@string/delete");

                    // Disable clickability
                    txtDateStart.setOnClickListener(null);
                    txtDateEnd.setOnClickListener(null);
                    txtTimeStart.setOnClickListener(null);
                    txtTimeEnd.setOnClickListener(null);
                }
                else
                {
                    Toast.makeText(con, "Upewnij się, że wypełniłeś wszystkie pola", Toast.LENGTH_LONG).show();
                }

            }
        });

        btnCancel.setOnClickListener(new LinearLayout.OnClickListener()
        {
            public void onClick(View v)
            {
                //Toast.makeText(con, "Clicked on btnCancel", Toast.LENGTH_SHORT).show();

                // Check if Accept button is disabled
                if(btnAccept.getVisibility() == btnAccept.GONE)
                {
                    // Get time from THIS work time
                    long x = calculateTime();

                    // Calculate future summary time value
                    long temp = MainActivity.getSum() - x;

                    // Calculate hour and min out of summary time (in milliseconds)
                    diffHour = temp / (60 * 60 * 1000);
                    diffMin = temp / (60 * 1000) % 60;

                    // Set string for displaying summary time
                    txtUpdate = diffHour + "h " + diffMin + "m ";

                    // Set sum value - time from all work times AND Update string
                    MainActivity.setSum(x * -1);
                    MainActivity.updateSum(txtUpdate);
                }

                // Delete THIS work time
                MainActivity.deleteWorkTime(me);
            }
        });

    }

    // Return month's name in String based on value from calendar
    private String getMonth(int m)
    {
        switch (m)
        {
            case 1:
                return getResources().getString(R.string.m1);

            case 2:
                return getResources().getString(R.string.m2);

            case 3:
                return getResources().getString(R.string.m3);

            case 4:
                return getResources().getString(R.string.m4);

            case 5:
                return getResources().getString(R.string.m5);

            case 6:
                return getResources().getString(R.string.m6);

            case 7:
                return getResources().getString(R.string.m7);

            case 8:
                return getResources().getString(R.string.m8);

            case 9:
                return getResources().getString(R.string.m9);

            case 10:
                return getResources().getString(R.string.m10);

            case 11:
                return getResources().getString(R.string.m11);

            case 12:
                return getResources().getString(R.string.m12);

            default:
                return getResources().getString(R.string.monthError);
        }
    }

    private void showTimePicker(final TextView tv)
    {
        // Get current calendar values
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);

        // Create TimePickerDialog, onTimeSetListener  AND onTimePick method
        TimePickerDialog timePickerDialog;
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener(){

            @Override
            public void onTimeSet(TimePicker timePicker, int h, int m) {
                String hour;
                String minute;

                // Additional "0" if necessary
                if(h <= 9)
                    hour = "0" + h;
                else
                    hour = "" + h;

                if(m <= 9)
                    minute = "0" + m;
                else
                    minute = "" + m;

                // Check what view was clicked AND save values to appropriate variables
                if(tv.getId() == R.id.txtTimeStart)
                {
                    hourStart = h;
                    minStart = m;
                }
                else
                {
                    hourEnd = h;
                    minEnd = m;
                }

                // Set chose values as text to TextView
                tv.setText(new StringBuilder().append(hour).append(":").append(minute));
            }
        };

        // Set TimePickerDialog info and starting date AND display it
        timePickerDialog = new TimePickerDialog(con, onTimeSetListener, hour, min, true);
        timePickerDialog.setTitle("Select time");
        timePickerDialog.show();
    }

    // Remove stored values from StringBuilder
    StringBuilder sbDate = new StringBuilder();

    private void showDatePicker(final TextView txtDate)
    {
        // Get current calendar values
        final Calendar c = Calendar.getInstance();
        int Day = calendar.get(Calendar.DAY_OF_MONTH);
        int Month = calendar.get(Calendar.MONTH);
        int Year = calendar.get(Calendar.YEAR);

        // Create DatePickerDialog, onDateSetListener AND onDateSate method
        DatePickerDialog datePickerDialog;
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener()
        {

            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                // Append values to StringBuilder
                sbDate.append(d).append(" ");
                sbDate.append(getMonth(m+1));
                sbDate.append(" ").append(y);

                // Set chose values stored in StringBuilder as text to TextView
                txtDate.setText(sbDate);

                // Check what view was clicked AND save values to appropriate variables
                if(txtDate.getId() == R.id.txtDateStart)
                {
                    dayStart = d;
                    monthStart = m + 1;
                    yearStart = y;
                }
                else
                {
                    dayEnd = d;
                    monthEnd = m + 1;
                    yearEnd = y;
                }
            }
        };

        // Set DatePickerDialog info and starting date AND display it
        datePickerDialog = new DatePickerDialog(con, onDateSetListener, Year, Month, Day);
        datePickerDialog.setTitle("Select date");
        datePickerDialog.show();

        // Remove stored values from StringBuilder
        sbDate.delete(0, sbDate.length());
    }

    // Global variables used in calculateTime() AND btnAccept's onClick AND btnCancel's onClick
    long diff = 0;
    long diffMin = 0;
    long diffHour = 0;
    String txtUpdate = null;

    // Method calculating time between dates set on single work time
    public long calculateTime()
    {
        // Set SimpleDateFormat to simplify date reading
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MM yyyy HH mm");

        // Set string representing start and end date AND times
        String timeStart = dayStart + " " + monthStart + " " + yearStart + " " + hourStart + " " + minStart;
        String timeEnd = dayEnd + " " + monthEnd + " " + yearEnd + " " + hourEnd + " " + minEnd;

        // Dates for initialized strings - timeStart AND timeEnd
        Date start;
        Date end;

        try
        {
            // Parse info from strings to Date type
            start = simpleDateFormat.parse(timeStart);
            end = simpleDateFormat.parse(timeEnd);

            // Calculate time between dates (in milliseconds)
            diff = end.getTime() - start.getTime();

        }
        // Nothin' to talk about here
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        // Return time between dates (in milliseconds)
        return diff;
    }
}
