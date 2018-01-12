package com.impactus.impactus_sensebox;

import android.content.Intent;
import android.database.Cursor;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.google.android.gms.maps.CameraUpdateFactory.newLatLngBounds;


public class ArchiveMap extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    Marker markerarray[]=new Marker[25];
    int i;
    private GoogleMap mMap;
    double latitude;
    double longitude;
    ImpactusDataBaseAdapter impactusadapter;
    Cursor impactuscursor;
    LatLngBounds.Builder builder = new LatLngBounds.Builder();
    CameraUpdate cu;
    int j;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive_map);
        i=0;
        impactusadapter=new ImpactusDataBaseAdapter(this);
        impactusadapter=impactusadapter.open();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        impactuscursor = impactusadapter.MapperReturn();
        if(impactuscursor!=null && impactuscursor.getCount()>0) {
            impactuscursor.moveToFirst();
            while (!impactuscursor.isLast()) {
                latitude = Double.parseDouble(impactuscursor.getString(0));
                longitude = Double.parseDouble(impactuscursor.getString(1));
                LatLng sydney = new LatLng(latitude, longitude);
                markerarray[i] = mMap.addMarker(new MarkerOptions().position(sydney).title(impactuscursor.getString(2)));
                builder.include(markerarray[i].getPosition());
                i++;
                impactuscursor.moveToNext();
            }
            latitude = Double.parseDouble(impactuscursor.getString(0));
            longitude = Double.parseDouble(impactuscursor.getString(1));
            LatLng sydney = new LatLng(latitude, longitude);
            markerarray[i] = mMap.addMarker(new MarkerOptions().position(sydney).title(impactuscursor.getString(2)));
            builder.include(markerarray[i].getPosition());
            LatLngBounds bounds = builder.build();
            if (i > 0) {
                cu = CameraUpdateFactory.newLatLngBounds(bounds, 80);
            } else {
                cu = CameraUpdateFactory.newLatLngZoom(sydney, 15F);
            }
            mMap.animateCamera(cu);

        }
        else{
            Toast.makeText(getApplicationContext(), "No IMPACTUS DATA Found",Toast.LENGTH_SHORT).show();
        }

    }

    public boolean onMarkerClick(final Marker mymarker) {
        GPSCLASS.getInstance().lat=mymarker.getPosition().latitude;
        GPSCLASS.getInstance().longi=mymarker.getPosition().longitude;
        Intent I= new Intent(ArchiveMap.this,DataLister.class);
        startActivity(I);
       // this.finish();
        return true;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close The Database
        impactuscursor.close();
        impactusadapter.close();
        ImpactusDataBaseAdapter.impactuscursor.close();
    }
    @Override
    protected void onStop(){
        super.onStop();
        impactuscursor.close();
        impactusadapter.close();
        ImpactusDataBaseAdapter.impactuscursor.close();

    }
}
