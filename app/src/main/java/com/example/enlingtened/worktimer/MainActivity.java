package com.example.enlingtened.worktimer;

import android.app.*;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;


public class MainActivity extends AppCompatActivity{

    // activity_main.xml views
    static LinearLayout wrapper;
    Button btnAdd = null;
    static ArrayList<Object> workTimes = new ArrayList<>();

    static TextView txtSummary;

    // Variable storing time from all work times
    static int sum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get ids
        wrapper = (LinearLayout) findViewById(R.id.mainWrapper);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        txtSummary = (TextView) findViewById(R.id.txtSummary);

        sum = 0;

        // Set btnAdd OnClickListener AND onClick
        btnAdd.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {

                // Add single WorkTime to LinearLayout wrapper AND ArrayList<Object> workTimes
                addWorkTime();
            }
        });
    }

    // Just chillin' for now
    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }


    private void addWorkTime()
    {
        // Temporary variable
        View temp = new WorkTime(this);

        // Add created WorkTime to ArrayList<Object> workTimes AND LinearLayout wrapper
        workTimes.add(temp);
        wrapper.addView(temp);
    }

    public static void deleteWorkTime(View workTime)
    {
        // Remove single WorkTime from ArrayList<Object> workTimes AND LinearLayout wrapper
        workTimes.remove(workTime);
        wrapper.removeView(workTime);
    }

    public static void setSum(long amount)
    {
        // Add time from single WorkTime to sum (amount may be < 0)
        sum += amount;
    }

    public static long getSum()
    {
        // Return sum value
        return sum;
    }

    public static void updateSum(String sum)
    {
        // Display sum represented as String in "HH mm" format
        txtSummary.setText(sum);
    }
}
