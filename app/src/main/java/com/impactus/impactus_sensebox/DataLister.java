package com.impactus.impactus_sensebox;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class DataLister extends AppCompatActivity{

    Cursor impactuscursor2;
    ImpactusDataBaseAdapter impactusadapter2;
    String temp;
    String humi;
    String soil;
    String date;
    String line;
    TextView lati;
    TextView longi;
    TextView loca;
    ListView lister;

    ArrayAdapter<String> adapter;
    ArrayList<String> listItems=new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_lister);

        lati=(TextView)findViewById(R.id.latitude);
        longi=(TextView)findViewById(R.id.longitude);
        loca=(TextView)findViewById(R.id.location);
        lister=(ListView)findViewById(R.id.lister);


        impactusadapter2=new ImpactusDataBaseAdapter(this);
        impactusadapter2=impactusadapter2.open();
        impactuscursor2=impactusadapter2.ListReturn(GPSCLASS.getInstance().lat,GPSCLASS.getInstance().longi);
        if(impactuscursor2!=null)
        {
            lati.append(impactuscursor2.getString(4));
            longi.append(impactuscursor2.getString(5));
            loca.append(impactuscursor2.getString(6));



            adapter=new ArrayAdapter<String>(this,
                    R.layout.customlistview,
                    listItems);
            lister.setAdapter(adapter);
            while(!impactuscursor2.isAfterLast())
            {
                temp=impactuscursor2.getString(0);
                humi=impactuscursor2.getString(1);
                soil=impactuscursor2.getString(2);
                date=impactuscursor2.getString(3);
                line=temp+"C | "+humi+"% | "+soil+"% | "+date;
                listItems.add(line);
                impactuscursor2.moveToNext();

            }
            adapter.notifyDataSetChanged();
        }

    }
    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close The Database
        impactuscursor2.close();
        impactusadapter2.close();
        ImpactusDataBaseAdapter.impactuscursor2.close();
    }
    @Override
    protected void onStop(){
        super.onStop();
        impactuscursor2.close();
        impactusadapter2.close();
        ImpactusDataBaseAdapter.impactuscursor2.close();

    }


}
