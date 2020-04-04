package com.shivamtaneja.covid_19updates;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import java.util.ArrayList;
import java.util.List;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class Piechart extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piechart);

        PieChartView pieChartView;
        pieChartView = findViewById(R.id.chart);

        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();
        int[] globalFigures = extras.getIntArray(getString(R.string.figures));
        String C = intent.getStringExtra(getString(R.string.key)); //if it's a string you stored.
//        Log.e("recd", +globalFigures[1] + "");
//        Log.e("frecd", "ffd" + "");
//        //int x=globalFigures[0];

        List<SliceValue> pieData = new ArrayList<>();

        assert globalFigures != null;
        pieData.add(new SliceValue(globalFigures[0], Color.parseColor("#CAB607")).setLabel(globalFigures[0] + ""));
        pieData.add(new SliceValue(globalFigures[1], Color.parseColor("#B71C1C")).setLabel(globalFigures[1] + ""));
        pieData.add(new SliceValue(globalFigures[2], Color.parseColor("#1A6742")).setLabel(globalFigures[2] + ""));
        //pieData.add(new SliceValue(60, Color.MAGENTA).setLabel("Q4: $28"));

        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true).setValueLabelTextSize(12);
        pieChartView.setPieChartData(pieChartData);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.graphical_analysis);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}