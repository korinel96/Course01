package com.example.korinel.course01;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONException;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private TextView textout;
    public String [] results = new String[2];
    public int cnt = 0;
    public String toastMsg = "Addresses should not be empty";
    public int PLACE_PICKER_REQUEST = 1;
    public String message;


    public void next_click(View v) throws IOException, JSONException {
        //collecting inputed addresses
        String url1, url2;
        EditText edit1 = (EditText) findViewById(R.id.addr1);
        EditText edit2 =  (EditText) findViewById(R.id.addr2);
        String address1 = (String) edit1.getText().toString();
        String address2 = (String) edit2.getText().toString();
        if(address1.isEmpty() || address2.isEmpty()) {
            Toast.makeText(this, toastMsg, Toast.LENGTH_SHORT).show();
        }else {

            address1 = String.format("%s %s", address1, "Москва"); // можно потом переделать под любой город*
            address2 = String.format("%s %s", address2, "Москва");
            url1 = String.format("http://maps.googleapis.com/maps/api/geocode" +
                    "/json?address=%s&sensor=true_or_false", address1);
            url2 = String.format("http://maps.googleapis.com/maps/api/geocode" +
                    "/json?address=%s&sensor=true_or_false", address2);
            url1 = url1.replaceAll(" ", "%20");
            url2 = url2.replaceAll(" ", "%20");
            ParseTask yours = new ParseTask();
            results[0] = "";
            results[1] = "";
            yours.execute(url1, url2);
        }
    }

    public void toplaces (View v){
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try{
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                //Текст СмсКи
                message = String.format("Let's meet at %s \n Address:%s", place.getName(),place.getAddress());
                //Формирование СМС сообщния
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.putExtra("sms_body", message);
                sendIntent.setType("vnd.android-dir/mms-sms");
                startActivity(sendIntent);
            }
        }
    }

    private class ParseTask extends AsyncTask<String, Void, String> {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";
        Intent second = new Intent(MainActivity.this, Main2Activity.class);

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
                    results[cnt++] = resultJson;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            second.putExtra("First", results[0]);
            second.putExtra("Second", results[1]);
            cnt = 0;
            startActivity(second); // moving to next activity
            return resultJson;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
