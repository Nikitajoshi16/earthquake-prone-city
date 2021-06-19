package com.example.earthquakeapp.Data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.earthquakeapp.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {

    View view;
    LayoutInflater layoutInflater;
    Context context;

    public CustomInfoWindow(Context context) {
        this.context = context;

        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.custom_window_info , null);

    }


    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        TextView place = view.findViewById(R.id.place);
        TextView snippet = view.findViewById(R.id.snippet);
        place.setText(marker.getTitle());
        snippet.setText(marker.getSnippet());
        return view;
    }
}
