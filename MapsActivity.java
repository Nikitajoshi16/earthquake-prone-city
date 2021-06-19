package com.example.earthquakeapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.earthquakeapp.Data.CustomInfoWindow;
import com.example.earthquakeapp.Model.Earthquake;
import com.example.earthquakeapp.util.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback ,
        GoogleMap.OnInfoWindowClickListener , GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    RequestQueue queue;
    Earthquake earthquake = new Earthquake();
    AlertDialog.Builder builder ;
    AlertDialog dialog;
    Button dismissbutton;
    TextView poplist ;
    ImageButton cross;
    StringBuilder stringBuilder;
    private  BitmapDescriptor[] colors;
    Button showlist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.URL,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray features = response.getJSONArray("features");
                    for (int i =0 ; i<Constants.LIMIT ; i++){
                        JSONObject properties = features.getJSONObject(i).getJSONObject("properties");
                        JSONObject geometry = features.getJSONObject(i).getJSONObject("geometry");
                        JSONArray coordinates = geometry.getJSONArray("coordinates");
                        double lon = coordinates.getDouble(0);
                        double lat = coordinates.getDouble(1);
                        // Log.d("json", "onResponse: " + lat + " " + lon);
                        earthquake.setPlace(properties.getString("place"));
                        earthquake.setType(properties.getString("type"));
                        earthquake.setLat(lat);
                        earthquake.setLon(lon);
                        earthquake.setTime(properties.getLong("time"));
                        earthquake.setMagnitude(properties.getDouble("mag"));
                        earthquake.setDetaillinl(properties.getString("detail"));
                        java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
                        String formattedDate =  dateFormat.format(new Date
                                (Long.valueOf(properties.getLong("time")))
                                .getTime());

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.icon(colors[Constants.randomInt(colors.length , 0)]);
                        markerOptions.title(earthquake.getPlace());
                        markerOptions.position(new LatLng(lat,lon));
                        markerOptions.snippet("Magnitude: " + earthquake.getMagnitude() +
                                "\n" + "Date: " + earthquake.getTime());
                        //add circle to map that has magnitude > x
                        if(earthquake.getMagnitude() > 2.0){
                            //adding a circle to marker
                            CircleOptions circleOptions = new CircleOptions();
                            circleOptions.center(new LatLng(earthquake.getLat() , earthquake.getLon()));
                            circleOptions.radius(30000);
                            circleOptions.strokeWidth(3.6f);
                            circleOptions.fillColor(Color.RED);
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker
                                    (BitmapDescriptorFactory.HUE_RED));
                            mMap.addCircle(circleOptions);
                        }
                        Marker marker = mMap.addMarker(markerOptions);
                       marker.setTag(earthquake.getDetaillinl());


                    }
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

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        showlist = findViewById(R.id.showlist);
        showlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MapsActivity.this , Activity2.class));
            }
        });
        colors = new BitmapDescriptor[]{
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)
        };
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
        mMap.setInfoWindowAdapter(new CustomInfoWindow(getApplicationContext()));
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerClickListener(this);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
               // Log.d("location", "onLocationChanged: " + location.toString());
            }
        };
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                    0, locationListener);


        // Add a marker in Sydney and move the camera
       // LatLng betul = new LatLng( 21.910803, 77.901184);
        //Map.addMarker(new MarkerOptions().position(betul).title("Marker in Betul"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(betul));}

        new ActivityCompat.OnRequestPermissionsResultCallback() {
            @Override
            public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MapsActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                                0, locationListener);
                        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


                    }
                }
            }
        };



    }
}

    @Override
    public void onInfoWindowClick(Marker marker) {
        getQuakeDetails(marker.getTag().toString());
       // Toast.makeText(getApplicationContext() , marker.getTag().toString(), Toast.LENGTH_SHORT).show();

    }

    private void getQuakeDetails(String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url,null , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                String detailsUrl = "";
                 stringBuilder = new StringBuilder();

                try {
                    JSONObject properties = response.getJSONObject("properties");
                    JSONObject products = properties.getJSONObject("products");


                    if(products.has("nearby-cities")){
                        JSONArray nearby = products.getJSONArray("nearby-cities");
                        if(nearby != null){
                        for(int j = 0 ; j<nearby.length();j++) {
                            JSONObject nearbyobj = nearby.getJSONObject(j);
                            JSONObject contents = nearbyobj.getJSONObject("contents");
                            JSONObject nearbyjson = contents.getJSONObject("nearby-cities.json");

                            detailsUrl = nearbyjson.getString("url");

                        }
                    }getMoreDetails(detailsUrl);
                    }
                    else{

                        builder = new AlertDialog.Builder(MapsActivity.this);
                       View view = getLayoutInflater().inflate(R.layout.popup , null);
                        dismissbutton = view.findViewById(R.id.button4);
                        poplist = view.findViewById(R.id.textview);
                        cross = view.findViewById(R.id.imageButton3);

                         if(products.has("origin")){
                        JSONArray origin = products.getJSONArray("origin");
                        for(int i=0 ; i<origin.length() ; i++) {
                            JSONObject geoserveObj = origin.getJSONObject(i);
                            if(geoserveObj.has("properties")) {
                                JSONObject contentObj = geoserveObj.getJSONObject("properties");

                                stringBuilder.append("City: " + contentObj.getString("title") + "\n" +
                                        "Depth: " + contentObj.getString("depth") + "\n"
                                        + "Magnitude: " + contentObj.getString("magnitude") + "\n"
                                        + "Time: " + contentObj.getString("eventtime"));
                            }}


                                poplist.setText(stringBuilder);
                             dismissbutton.setOnClickListener(new View.OnClickListener() {
                                 @Override
                                 public void onClick(View view) {
                                     dialog.dismiss();
                                 }
                             });

                         }
                        builder.setView(view);
                        dialog = builder.create();
                        dialog.show();}



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", "onErrorResponse: " + error);

            }
        });
        queue.add(jsonObjectRequest);


    }

    private void getMoreDetails(String Url) {
        RequestQueue queue1 = Volley.newRequestQueue(MapsActivity.this);

        builder = new AlertDialog.Builder(MapsActivity.this);
       View view2 = getLayoutInflater().inflate(R.layout.popup , null);
         dismissbutton = view2.findViewById(R.id.button4);
        poplist = view2.findViewById(R.id.textview);
        cross = view2.findViewById(R.id.imageButton3);
          stringBuilder = new StringBuilder();


        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Url,
                null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i = 0 ; i<response.length() ; i++) {
                    try {
                        JSONObject citiesobj = response.getJSONObject(i);
                        stringBuilder.append("Nearby cities :" +"\n" +"\n" +
                                "City : " + citiesobj.getString("name") +"\n" +
                                "Distance: " + citiesobj.has("distance") + "\n" +
                                "Direction: " + citiesobj.getString("direction")  );

                        //  Log.d("kk", "onResponse: " + stringBuilder);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                poplist.setText(stringBuilder);
                dismissbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag", "onErrorResponse: " + error);

            }
        });
        queue1.add(jsonArrayRequest);


        builder.setView(view2);
        dialog = builder.create();
        dialog.show();

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}


