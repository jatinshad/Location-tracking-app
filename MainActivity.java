MainActivity.java

package com.example.jatinshad.track;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static ArrayList<String> array = new ArrayList<String>();
    static  ArrayList<LatLng> location = new ArrayList<LatLng>();
    static ArrayAdapter arrayAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView)findViewById(R.id.listView);


        array.add("Start tracking !");

        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,array);
        listView.setAdapter(arrayAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(MainActivity.this, Integer.toString(i), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("index",i);
                startActivity(intent);

            }
        });
    }
}
