package com.example.earthquakeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.widgets.Snapshot;
import androidx.core.app.ActivityCompat;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.earthquakeapp.Model.Earthquake;
import com.example.earthquakeapp.util.Constants;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.List;

public class Activity2 extends AppCompatActivity {
    RequestQueue queue;
    TextView list;
    ListView listView;
    ArrayList<String> stringArrayList ;
    ArrayAdapter adapter;
    Earthquake earthquake = new Earthquake();
    List<Earthquake> earthquakeList;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        earthquakeList = new ArrayList<>();
        listView =findViewById(R.id.listview);
        stringArrayList  = new ArrayList<>();

        queue = Volley.newRequestQueue(Activity2.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.URL,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray features = response.getJSONArray("features");
                    for (int i = 0; i < Constants.LIMIT; i++) {
                        JSONObject properties = features.getJSONObject(i).getJSONObject("properties");
                        JSONObject geometry = features.getJSONObject(i).getJSONObject("geometry");
                        JSONArray coordinates = geometry.getJSONArray("coordinates");
                        final double lon = coordinates.getDouble(0);
                        final double lat = coordinates.getDouble(1);
                        earthquake.setPlace(properties.getString("place"));
                        earthquake.setTime(properties.getLong("time"));
                        earthquake.setLon(lon);
                        earthquake.setLat(lat);
                        java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
                        String formattedDate = dateFormat.format(new Date
                                (Long.valueOf(properties.getLong("time")))
                                .getTime());
                        stringArrayList.add(earthquake.getPlace() + "\n" + formattedDate);


                    }
                    adapter  = new ArrayAdapter(Activity2.this ,android.R.layout.simple_list_item_1,
                            stringArrayList);
                    listView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsonObjectRequest);



    }

}