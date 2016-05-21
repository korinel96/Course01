package com.example.korinel.course01;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public float point1, point2, y1, y2, f1, f2;
    public String places;
    public information [] all = new information[50];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            point1 = extras.getFloat("point1");
            point2 = extras.getFloat("point2");
            y1 = extras.getFloat("y1");
            y2 = extras.getFloat("y2");
            f1 = extras.getFloat("f1");
            f2 = extras.getFloat("f2");
            places = extras.getString("allplaces");
        }
        try {
            System.out.println("HERE WE GO!!!!!!");
            JSONObject data1 = null;
            data1 = new JSONObject(places);
            JSONArray results = data1.getJSONArray("results");
            for (int i = 0; i < all.length; i++)
                all[i] = new information();
            for (int i = 0; i < results.length(); i ++)
            {
                JSONObject info = results.getJSONObject(i);
                JSONObject geometry = info.getJSONObject("geometry");
                JSONObject location = geometry.getJSONObject("location");
                all[i].lat = Float.parseFloat(location.getString("lat"));
                all[i].lng = Float.parseFloat(location.getString("lng"));
                all[i].name = info.getString("name");
                System.out.println(all[i].name);
                all[i].vicinity = info.getString("vicinity");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    public class information{
        float lat, lng;
        String name, vicinity;
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

        // Add a marker in Sydney and move the camera
        LatLng you = new LatLng(y1, y2);
        mMap.addMarker(new MarkerOptions().position(you).title("you are HERE"));
        LatLng friend = new LatLng(f1, f2);
        mMap.addMarker(new MarkerOptions().position(friend).title("your friend is HERE"));
        LatLng place1 = new LatLng(all[0].lat, all[0].lng);
        mMap.addMarker(new MarkerOptions().position(place1).title(all[0].name).snippet(all[0].vicinity));
        LatLng place2 = new LatLng(all[1].lat, all[1].lng);
        mMap.addMarker(new MarkerOptions().position(place2).title(all[1].name).snippet(all[1].vicinity));
        LatLng place3 = new LatLng(all[2].lat, all[2].lng);
        mMap.addMarker(new MarkerOptions().position(place3).title(all[2].name).snippet(all[2].vicinity));
        LatLng place4 = new LatLng(all[3].lat, all[3].lng);
        mMap.addMarker(new MarkerOptions().position(place4).title(all[3].name).snippet(all[3].vicinity));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place1, 12));
    }
}
