package com.example.maps1;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

//contains static variables so that data can be retrieved throughout
//the app's lifecycle

public class Data {

    public static ArrayList<LatLng> cord   ;
    public static ArrayList<Location> loc  ;

    public static float total_distance ;
    public static double duration ;
    public static String date ;


}
