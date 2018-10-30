MapsActivity.java


package com.example.jatinshad.track;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String addr;
    LocationListener locationListener;
    LocationManager locationManager;
    String singleParsed = " ";
    String dataParsed = " ";



    private class DownloadTask extends AsyncTask<String, Void ,String> {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try{
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection)url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data!=-1)
                {
                    char current = (char)data;
                    result +=current;
                    data=reader.read();
                }


                return result;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return "failed";
            }


        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==1)
        {
            if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {


                LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> listAddress = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                    if(listAddress!=null && listAddress.size()>0)
                    {
                        String address="";

                        if(listAddress.get(0).getSubThoroughfare()!=null)
                        {
                            address+=listAddress.get(0).getSubThoroughfare()+",";
                        }
                        if(listAddress.get(0).getThoroughfare()!=null)
                        {
                            address+=listAddress.get(0).getThoroughfare()+",";
                        }
                        if(listAddress.get(0).getSubLocality()!=null)
                        {
                            address+=listAddress.get(0).getSubLocality()+",";
                        }
                        if(listAddress.get(0).getLocality()!=null)
                        {
                            address+=listAddress.get(0).getLocality()+",";
                        }
                        if(listAddress.get(0).getPostalCode()!=null)
                        {
                            address+=listAddress.get(0).getPostalCode()+",";
                        }
                        if(listAddress.get(0).getCountryName()!=null)
                        {
                            address+=listAddress.get(0).getCountryName();
                        }

                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(userLocation).title(address));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));



                       if(!(MainActivity.array.get(MainActivity.array.size()-1).equals(address))) {
                           MainActivity.array.add(address);
                           MainActivity.location.add(userLocation);
                           MainActivity.arrayAdapter.notifyDataSetChanged();
                           DownloadTask task = new DownloadTask();
                           String result = " ";
                           String send=address;
                           send=send.replace(' ','@');

                           try
                           {
                               result=task.execute("http://192.168.1.7:8080/Web_service/path/track/"+send).get();

                           }
                           catch (Exception e)
                           {
                               e.printStackTrace();
                           }
                       }


                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }


            }



            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if(Build.VERSION.SDK_INT<23)
        {
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,0,0,locationListener);
        }
        else
        {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1 );
            }
            else
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                LatLng userLocation = new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());


                List<Address> listAddress = null;
                try {
                    listAddress = geocoder.getFromLocation(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude(),1);
                    if(listAddress!=null && listAddress.size()>0)
                    {
                        String address="";

                        if(listAddress.get(0).getSubThoroughfare()!=null)
                        {
                            address+=listAddress.get(0).getSubThoroughfare()+",";
                        }
                        if(listAddress.get(0).getThoroughfare()!=null)
                        {
                            address+=listAddress.get(0).getThoroughfare()+",";
                        }
                        if(listAddress.get(0).getSubLocality()!=null)
                        {
                            address+=listAddress.get(0).getSubLocality()+",";
                        }
                        if(listAddress.get(0).getLocality()!=null)
                        {
                            address+=listAddress.get(0).getLocality()+",";
                        }
                        if(listAddress.get(0).getPostalCode()!=null)
                        {
                            address+=listAddress.get(0).getPostalCode()+",";
                        }
                        if(listAddress.get(0).getCountryName()!=null)
                        {
                            address+=listAddress.get(0).getCountryName();
                        }

                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(userLocation).title(address));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));



                        if(!(MainActivity.array.get(MainActivity.array.size()-1).equals(address))) {
                            MainActivity.array.add(address);
                            MainActivity.location.add(userLocation);
                            MainActivity.arrayAdapter.notifyDataSetChanged();
                            DownloadTask task = new DownloadTask();
                            String result = " ";
                            String send=address;
                            send=send.replace(' ','@');

                            try
                            {
                                result=task.execute("http://192.168.1.7:8080/Web_service/path/track/"+send).get();

                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }


                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }


    }
}
