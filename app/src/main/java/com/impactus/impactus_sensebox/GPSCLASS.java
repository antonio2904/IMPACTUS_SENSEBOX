package com.impactus.impactus_sensebox;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;


public class GPSCLASS {
    GoogleApiClient mgoogleapiclient;
    Double latitude, longitude;
    Location location;
    double lat;
    int p;
    double longi;
    LocationManager imlocationManager;
    String location_name;
    LocationListener imlocationListener;
    LocationRequest locationRequest;
    private static GPSCLASS instance=null;
    protected GPSCLASS(){}
    public static GPSCLASS getInstance()
    {
        if(null==instance){
            instance=new GPSCLASS();
        }
        return instance;
    }
}
