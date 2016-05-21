package com.example.korinel.course01;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class Main2Activity extends AppCompatActivity{
    public String first, second, types;
    public float [][] coords = new float[2][2];
    public float point1, point2;
    public String apikey = "AIzaSyCu0-Dg09RvSOOgjGkaa1bP8-BodYL52E8";
    public int [][] color = new int[6][1];
    public String[] adds = {"cafe&restaurant&","store&","library&","night_club&","museum&","park&"};
    public String places;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar)
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            first = extras.getString("First");
            second = extras.getString("Second");
        }
        places = "";
        JSONObject data1 = null;
        JSONObject data2 = null;
        try {
            data1 = new JSONObject(first);
            JSONArray results = data1.getJSONArray("results");
            JSONObject info = results.getJSONObject(0);
            JSONObject geometry = info.getJSONObject("geometry");
            JSONObject location = geometry.getJSONObject("location");
            coords[0][0] = Float.parseFloat(location.getString("lat"));
            coords[0][1] = Float.parseFloat(location.getString("lng"));
            data2 = new JSONObject(second);
            JSONArray results2 = data2.getJSONArray("results");
            JSONObject info2 = results2.getJSONObject(0);
            JSONObject geometry2 = info2.getJSONObject("geometry");
            JSONObject location2 = geometry2.getJSONObject("location");
            coords[1][0] = Float.parseFloat(location2.getString("lat"));
            coords[1][1] = Float.parseFloat(location2.getString("lng"));
        } catch (Exception e) {
        e.printStackTrace();
         }
        point1 = (coords[0][0] + coords[1][0] )/2;
        point2 = (coords[0][1] + coords[1][1] )/2;
        System.out.println(point1);
        System.out.println(point2);

        for(int i =  0; i < 6 ; i++ )
            color[i][0] = 0;
        ListView cates = (ListView) findViewById(R.id.categories);
        String[] cats = {"Meal","Store","Library","Night Club","Museum","Park"};
        ArrayAdapter<String>
                adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cats);
        cates.setAdapter(adapter);
        AdapterView.OnItemClickListener
                choose =
                new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView parent,
                                            View v,
                                            int position,
                                            long id) {
                        if(color[position][0] == 0) {
                            ((TextView) v).setBackgroundColor(Color.LTGRAY);
                            ((TextView) v).invalidate();
                            color[position][0] = 1;
                        } else {
                            ((TextView) v).setBackgroundColor(Color.TRANSPARENT);
                            ((TextView) v).invalidate();
                            color[position][0] = 0;
                        }
                    }
                };
        cates.setOnItemClickListener(choose);
    }
    public void tomap (View v){

        int cnt = 0;
        types = "";
        for(int j = 0; j<6; j++)
        {
            if(color[j][0] == 1) {
                types = String.format("%s%s", types, adds[j]);
                cnt ++;
            }
        }
        if (cnt == 0)
        {
            Toast.makeText(this, "You need to choose at least 1 category", Toast.LENGTH_SHORT).show();
        } else {
            String search;
            search = String.format("https://maps.googleapis.com/maps/api/place/search/json?location=%f,%f&radius=%d&types=%s&sensor=false&key=%s", point1, point2, 1000, types, apikey);
            search = search.replaceAll(" ", "%20");
            ParseTaskPlaces go = new ParseTaskPlaces();
            go.execute(search);
        }
    }


    private class ParseTaskPlaces extends AsyncTask<String, Void, String> {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(String... addr) {
            // получаем данные с внешнего ресурса
            try {
                for (String req : addr) {
                    URL url = new URL(req);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();

                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    resultJson = buffer.toString();
                    places = resultJson;
                    System.out.println(places);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Intent third = new Intent(Main2Activity.this, MapsActivity.class);
            third.putExtra("point1", point1);
            third.putExtra("point2", point2);
            third.putExtra("y1", coords[0][0]);
            third.putExtra("y2", coords[0][1]);
            third.putExtra("f1", coords[1][0]);
            third.putExtra("f2", coords[1][1]);
            third.putExtra("allplaces", places);
            startActivity(third);
            // moving to next activity
            return resultJson;
        }
    }

}
