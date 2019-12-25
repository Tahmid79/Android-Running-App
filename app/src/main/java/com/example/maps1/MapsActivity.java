package com.example.maps1;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;


import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import java.util.ArrayList;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback    {

    public GoogleMap mMap;
    public FusedLocationProviderClient fusedLocationClient ;
    public int REQUEST_CHECK_SETTINGS = 1 ;

    Location mCurrentLocation ;
    boolean  requestingLocationUpdates ;


    LocationRequest locationRequest ;
    LocationCallback locationCallback ;

    ArrayList<LatLng> cord =  new ArrayList<>() ;


    LocationService mService ;
    public boolean mBound = false ;
    public BroadcastReceiver receiver ;

    private static  final String TAG = "MyService" ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // requestLocationPermission();
        //handleBroadcast();

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        receiver = new Receiver() ;

        IntentFilter filter = new IntentFilter() ;
        filter.addAction(LocationService.ACTION_BROADCAST);
        filter.addAction(LocationService.EXTRA_LOCATION);

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver ,
               filter);


    }

    public class Receiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {


            Location lrecent = intent.getParcelableExtra(LocationService.EXTRA_LOCATION) ;
            LatLng recent =  new LatLng(lrecent.getLatitude()  ,lrecent.getLongitude() ) ;
            cord.add(recent) ;

            PolylineOptions options = new PolylineOptions();
            options.addAll(cord);
            options.width(8);
            options.color(Color.GREEN);


            mMap.addMarker(new MarkerOptions().position(recent).title("Current Location") ) ;
            mMap.addPolyline(options);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(recent));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(20), 2000, null);



        }
    }

    public void handleBroadcast(){

        IntentFilter filter =  new IntentFilter();
        filter.addAction(LocationService.ACTION_BROADCAST);
        filter.addAction(LocationService.EXTRA_LOCATION) ;

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {



               Location lrecent = intent.getParcelableExtra(LocationService.EXTRA_LOCATION) ;
               LatLng recent =  new LatLng(lrecent.getLatitude()  ,lrecent.getLongitude() ) ;
               cord.add(recent) ;

                PolylineOptions options = new PolylineOptions();
                options.addAll(cord);
                options.width(8);
                options.color(Color.GREEN);


                mMap.addPolyline(options);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(recent));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(20), 2000, null);



            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent service =  new Intent(this, LocationService.class) ;
        bindService(service , mServiceConnection , Context.BIND_AUTO_CREATE ) ;
        //bind service
        //if service is in foreground mode it will stop foreground mode and will be a bound service
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mBound){

            unbindService(mServiceConnection);
            mBound  = false ;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        //this.registerReceiver(receiver , new IntentFilter( LocationService.ACTION_BROADCAST)) ;
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver ,
                new IntentFilter( LocationService.ACTION_BROADCAST));
    }

    @Override
    protected void onPause() {
        //this.unregisterReceiver(receiver) ;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onPause();
    }


    protected void onCreate1(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        createLocationRequest();

        fusedLocationClient  = LocationServices.getFusedLocationProviderClient(this) ;

        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                mCurrentLocation = location  ;

            }
        }) ;



        locationCallback  = new LocationCallback(){

            @Override
            public void onLocationResult(LocationResult locationResult) {

                if (locationResult == null) {
                    return;
                } else

                    for (Location location : locationResult.getLocations()) {

                        //Update UI
                        mCurrentLocation = location ;
                        LatLng crd = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(crd).title("Current Location"));

                        cord.add(crd);
                    }

                Location recent = locationResult.getLastLocation() ;
                LatLng last = new LatLng(recent.getLatitude(), recent.getLongitude());

                PolylineOptions options = new PolylineOptions();
                options.addAll(cord);
                options.width(8);
                options.color(Color.GREEN);


                mMap.addPolyline(options);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(last));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(20), 2000, null);


        }

        } ;



    }

    protected void createLocationRequest(){

        locationRequest = LocationRequest.create() ;
        locationRequest.setInterval(5000)  ;
        locationRequest.setFastestInterval(1000) ;
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY) ;

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest) ;

        SettingsClient client  = LocationServices.getSettingsClient(this) ;

        Task<LocationSettingsResponse> task  = client.checkLocationSettings(builder.build()) ;

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

            startLocationUpdates();

            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if ( e instanceof ResolvableApiException){

                    try{
                        ResolvableApiException resolvableApiException = (ResolvableApiException) e ;

                      resolvableApiException.startResolutionForResult(MapsActivity.this ,
                                REQUEST_CHECK_SETTINGS);



                    }catch (IntentSender.SendIntentException sendEx){

                    }

                }

            }
        }) ;


    }



    private void startLocationUpdates(){
        fusedLocationClient.requestLocationUpdates(locationRequest ,
                locationCallback , Looper.getMainLooper() ) ;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng hamburg = new LatLng(53.558, 9.927)  ;
        LatLng kiel = new LatLng(53.551, 9.993) ;


        Polyline polyline1 = mMap.addPolyline(new PolylineOptions().clickable(true).add(
                new LatLng(-35.016, 143.321),
                new LatLng(-34.747, 145.592),
                new LatLng(-34.364, 147.891),
                new LatLng(-33.501, 150.217),
                new LatLng(-32.306, 149.248),
                new LatLng(-32.491, 147.309)



        ));

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

        //mMap.addMarker(new MarkerOptions().position(hamburg).title("Hamburg") ) ;
        //mMap.addMarker(new MarkerOptions().position(kiel).title("Kiel")) ;

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney , 4));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(5)  , 2000  ,null);


    }


    public void requestLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this ,  new String[]{

                    Manifest.permission.ACCESS_FINE_LOCATION

            }, REQUEST_CHECK_SETTINGS);


        }

        else {
            //do if location already enabled

            bindService( new Intent(this ,  LocationService.class)
                    , mServiceConnection , Context.BIND_AUTO_CREATE ) ;

            mService.startLocationUpdates();

        }


    }

    public void requestLocation(){


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mService.locationRequest) ;

        SettingsClient client  = LocationServices.getSettingsClient(this) ;

        Task<LocationSettingsResponse> task  = client.checkLocationSettings(builder.build()) ;

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                mService.startLocationUpdates();

            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if ( e instanceof ResolvableApiException){

                    try{
                        ResolvableApiException resolvableApiException = (ResolvableApiException) e ;

                        resolvableApiException.startResolutionForResult(MapsActivity.this ,
                                REQUEST_CHECK_SETTINGS);



                    }catch (IntentSender.SendIntentException sendEx){

                    }

                }

            }
        }) ;



    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CHECK_SETTINGS ) {
            //do if permission granted after prompt

            mService.startLocationUpdates();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CHECK_SETTINGS && resultCode==RESULT_OK ) {
            //do if permission granted after prompt

            mService.startLocationUpdates();
        }

    }

    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationService.MyBinder binder = (LocationService.MyBinder)service ;
            mService = binder.getBoundService();
            mBound = true ;
            requestLocation();
            //mService.startLocationUpdates();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null ;
            mBound = false ;

        }
    };




}





