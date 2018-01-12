package com.impactus.impactus_sensebox;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;


public class MainActivity extends AppCompatActivity implements android.location.LocationListener, com.google.android.gms.location.LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    EditText locaname;
    TextView status;
    Button enable;
    Button gpser;
    Button connect;
    Button nexter;
    private static final String TAG = "MainActivity";
    Drawable temp;
    Drawable temp2;
    ImageView logo;

    int flag1 = 0;
    int flag2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locaname=(EditText) findViewById(R.id.locationtext);
        logo=(ImageView)findViewById(R.id.logo);
        status = (TextView) findViewById(R.id.statustext);
        gpser = (Button) findViewById(R.id.gpsbtn);
        enable = (Button) findViewById(R.id.btenable);
        connect = (Button) findViewById(R.id.isconnect);
        nexter = (Button) findViewById(R.id.nextbtn);
        temp = connect.getBackground();
        temp2 = gpser.getBackground();

        logo.setAlpha((float) 0.1);

        BLUETOOTHCLASS.getInstance().btadptr = BluetoothAdapter.getDefaultAdapter();
        checkstatus();
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);

        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_FOUND); //Filter For Sensebox Connection
        registerReceiver(mReceiver1, filter1);

        gpser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0)
            {
                testgps();
            }

        });

        enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(enable.getText().toString().equals("ENABLE"))
                    enablebtn();
                else
                    disablebtn();

            }
        });

        connect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0){
                try
                {
                    connectbtn();
                }
                catch (IOException ex) { }

            }
        });

        nexter.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0){
                GPSCLASS.getInstance().location_name=locaname.getText().toString().trim();
                if(GPSCLASS.getInstance().location_name.length()>0) {
                    Intent I = new Intent(MainActivity.this, BTconnect.class);
                    startActivity(I);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Please Enter a Location Name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onStart()
    {
        super.onStart();
        if (GPSCLASS.getInstance().mgoogleapiclient == null)
        {
            GPSCLASS.getInstance().mgoogleapiclient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        GPSCLASS.getInstance().mgoogleapiclient.connect();
    }
    void testgps()
    {
        GPSCLASS.getInstance().imlocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        GPSCLASS.getInstance().imlocationListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                gpser.setBackgroundColor(Color.GREEN);
                Toast.makeText(getApplicationContext(), "Location Locked", Toast.LENGTH_SHORT).show();
                flag1=1;

                GPSCLASS.getInstance().latitude=location.getLatitude();
                GPSCLASS.getInstance().longitude=location.getLongitude();
                GPSCLASS.getInstance().imlocationManager.removeUpdates(this);
                checkstatus();

            }


            public void onStatusChanged(String provider, int status, Bundle extras) {

            }


            public void onProviderEnabled(String provider) {
                Toast.makeText(getApplicationContext(), "GPS Enabled", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(getApplicationContext(), "GPS Disabled", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Please Enable GPS", Toast.LENGTH_SHORT).show();
                gpser.setBackground(temp2);
                flag1=0;

                Intent i=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
                checkstatus();
                return;

            }
        };
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.INTERNET");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.INTERNET}, 23); //Any number
            }
        }
        configbutton();


    }
    void configbutton()
    {
        GPSCLASS.getInstance().imlocationManager.requestLocationUpdates("gps", 5000, 0, GPSCLASS.getInstance().imlocationListener);
    }
    @Override
    public void onConnected(Bundle connectionHint) {
        GPSCLASS.getInstance().locationRequest = LocationRequest.create();
        // PERMISSION check
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 46);
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(GPSCLASS.getInstance().mgoogleapiclient, GPSCLASS.getInstance().locationRequest, this);
        GPSCLASS.getInstance().location = LocationServices.FusedLocationApi.getLastLocation(GPSCLASS.getInstance().mgoogleapiclient);
        try
        {
            GPSCLASS.getInstance().latitude=GPSCLASS.getInstance().location.getLatitude();
            GPSCLASS.getInstance().longitude=GPSCLASS.getInstance().location.getLongitude();
        }
        catch(Exception error3)
        {
            System.out.println("QUAKE2 : " + error3.getMessage());
        }

    }

    public void onProviderEnabled(String s){};
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    public void onProviderDisabled(String provider) {}
    @Override
    public void onConnectionFailed(ConnectionResult arg0)
    {

    }



    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onLocationChanged(Location location)
    {
        GPSCLASS.getInstance().latitude=location.getLatitude();
        GPSCLASS.getInstance().longitude=location.getLongitude();

    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        unregisterReceiver(mReceiver1);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Toast.makeText(getApplicationContext(),"Bluetooth Off", Toast.LENGTH_SHORT).show();
                        flag2=0;
                        checkstatus();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        checkstatus();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Toast.makeText(getApplicationContext(),"Bluetooth On", Toast.LENGTH_SHORT).show();
                        checkstatus();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        checkstatus();
                        break;
                }
            }
        }
    };

    private final BroadcastReceiver mReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getAddress().equals("98:D3:34:90:B8:76")) {
                    BLUETOOTHCLASS.getInstance().btadptr.cancelDiscovery();
                    Toast.makeText(getApplicationContext(),"SENSEBOX Found", Toast.LENGTH_SHORT).show();
                    connect.setBackgroundColor(Color.GREEN);
                    flag2=1;
                    BLUETOOTHCLASS.getInstance().btdevice = device;
                    checkstatus();
                }

            }

        }
    };

    void enablebtn()
    {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent , 0);
    }

    void disablebtn()
    {
        BLUETOOTHCLASS.getInstance().btadptr.disable();
        checkstatus();

    }

    void checkstatus(){
        if(BLUETOOTHCLASS.getInstance().btadptr.isEnabled())
        {
            status.setText("BLUETOOTH ON");
            status.setTextColor(Color.BLUE);
            enable.setText("DISABLE");
            connect.setEnabled(true);
            nexter.setEnabled(false);

        }
        else
        {
            status.setText("BLUETOOTH OFF");
            status.setTextColor(Color.RED);
            enable.setText("ENABLE");
            connect.setBackground(temp);
            connect.setEnabled(false);
            nexter.setEnabled(false);
        }
        if(flag1==1 && flag2==1){
            nexter.setEnabled(true);
        }else{
            nexter.setEnabled(false);
        }
    }

    void connectbtn() throws IOException
    {
        BLUETOOTHCLASS.getInstance().btdevice = null;
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
        BLUETOOTHCLASS.getInstance().btadptr.startDiscovery();
        if(flag2==0){
            Toast.makeText(getApplicationContext(),"SENSEBOX Not Found", Toast.LENGTH_SHORT).show();

        }

    }


}
