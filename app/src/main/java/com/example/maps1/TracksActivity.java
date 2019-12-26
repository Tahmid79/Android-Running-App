package com.example.maps1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class TracksActivity extends AppCompatActivity {

    ListView listView ;
    static Cursor cursor ;

    static TracksView adapter ;
    ContentResolver contentResolver ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracks);

        contentResolver = getContentResolver() ;

        cursor = getContentResolver().query(TrackProvider.CONTENT_URI , null , null , null , null );

        listView = (ListView)findViewById(R.id.listview) ;

        adapter = new TracksView(this ,  cursor) ;
        adapter.notifyDataSetChanged();

        listView.setAdapter(adapter);           //populate the list view with the items
                                                //in database

    }

    public void sortDistance(View view){

        cursor = getContentResolver().query(TrackProvider.CONTENT_URI, null , null , null , "distance DESC");
        adapter.notifyDataSetChanged();         //sort according to longest distance travelled
        adapter.swapCursor(cursor) ;


    }

    public void sortTime(View view){
        cursor = getContentResolver().query(TrackProvider.CONTENT_URI, null , null , null , "duration ASC");
        adapter.notifyDataSetChanged();
        adapter.swapCursor(cursor) ;            //sort according to shortest time taken


    }

    public void goBack(View view){
        Intent intent = new Intent(this ,  MapsActivity.class) ;
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP) ;
        startActivity(intent);

    }




}
