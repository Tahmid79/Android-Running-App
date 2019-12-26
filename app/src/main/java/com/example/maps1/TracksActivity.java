package com.example.maps1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
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

        listView.setAdapter(adapter);


    }




}
