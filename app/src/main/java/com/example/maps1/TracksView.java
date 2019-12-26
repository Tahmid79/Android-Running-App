package com.example.maps1;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class TracksView extends CursorAdapter {

    String date ;
    Context cont ;

    public TracksView(Context context, Cursor cursor) {
        super(context, cursor,0) ;
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.custom_row, parent, false);
    }


    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        String duration = cursor.getString( cursor.getColumnIndex("duration")  );
        String distance = cursor.getString( cursor.getColumnIndex("distance") ) ;
        date = cursor.getString( cursor.getColumnIndex("date")  ) ;

        String reading = "Distance = " + distance+" metres" + "\nDuration = "+ duration +" seconds";

        ImageView img = (ImageView)view.findViewById(R.id.imageView) ;
        img.setImageResource(R.drawable.rcp);

        TextView title_view = (TextView)view.findViewById(R.id.title_txt) ;
        title_view.setText(reading);

        TextView datetext = (TextView)view.findViewById(R.id.date_txt) ;
        datetext.setText(date);


        Button delete_btn = (Button)view.findViewById(R.id.delete_btn) ;

         cont = context ;

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Delete track

                String [] ags = { date} ;
                String sel_clause = "date = ?"  ;

                int deleted = cont.getContentResolver().delete(TrackProvider.CONTENT_URI , sel_clause , ags ) ;


                TracksActivity.cursor = cont.getContentResolver().query(TrackProvider.CONTENT_URI , null , null , null , null );
                TracksActivity.adapter.notifyDataSetChanged();
                TracksActivity.adapter.swapCursor(TracksActivity.cursor) ;


            }
        });




    }



}
