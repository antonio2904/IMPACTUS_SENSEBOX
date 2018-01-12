package com.impactus.impactus_sensebox;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SecondScreen extends AppCompatActivity {

    Button impsensebutton;
    Button impdatabutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_screen);
        impsensebutton = (Button) findViewById(R.id.impsensebtn);
        impdatabutton = (Button) findViewById(R.id.impdatabtn);
        GPSCLASS.getInstance().p=1;

        impsensebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0)
            {
                Intent i= new Intent(SecondScreen.this,MainActivity.class);
                startActivity(i);
            }

        });
        impdatabutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0)
            {
                Intent i= new Intent(SecondScreen.this,ArchiveMap.class);
                startActivity(i);
            }

        });
    }
}
