package com.example.daimlerhackasia;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    DatabaseReference myRef;
    private MarkerOptions options = new MarkerOptions();
    String TAG = "BHAVYA";
    private ArrayList<LatLng> latlngs = new ArrayList<>();
    private ArrayList<String> ar1 = new ArrayList<>();
    private ArrayList<String> ar2 = new ArrayList<>();
    private ArrayList<String> ar3 = new ArrayList<>();
    LocationManager locationManager;
    int count = 0;

    Double latsos;
    Double longsos;



    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRefstatus = database.getReference("SOS_Status");
    DatabaseReference myRefLat = database.getReference("SOS_Location_lat");
    DatabaseReference myRefLong = database.getReference("SOS_Location_long");

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (!isGooglePlayServicesAvailable()) {
            finish();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment supportMapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        myRef = FirebaseDatabase.getInstance().getReference("Zones");
    }


    @Override
    public void onLocationChanged(Location location) {

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(latLng).title("My Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        Log.d("Latitude:", "latitude");
        Log.d("Longitude:", "longitude");
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            onLocationChanged(location);
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);




        /// Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    Zone zn = dataSnapshot1.getValue(Zone.class);
                    double lat = Double.parseDouble(zn.getZoneLat());
                    double longi = Double.parseDouble(zn.getZoneLong());
                    String dep = zn.getZoneTitle();
                    String prob = zn.getZoneData();

                    LatLng sydney = new LatLng(lat,longi);
                    latlngs.add(sydney);
                    ar1.add(dep);
                    ar2.add(prob);
                }

                for (LatLng point : latlngs) {
                    options.position(point);
                    options.title(ar1.get(count));
                    options.snippet(ar2.get(count));
                    mMap.addMarker(options);
                    mMap.addCircle(new CircleOptions()
                            .center(point)
                            .radius(100)
                            .strokeColor(-65536)
                            .fillColor(0x220000ff)
                            .strokeWidth(5.0f));
                    count = count+1;
                    Log.d("HELLO",point.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        // Add a marker in Sydney and move the camera
        //LatLng myLoc = new LatLng(latPK,longPK);
        //mMap.addMarker(new MarkerOptions().position(myLoc).title("My Location"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc,10));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng ln = marker.getPosition();
                double latti = ln.latitude;
                double longgi = ln.longitude;
                System.out.println("BUNDLEE"+String.valueOf(latti));

                Bundle bundle = new Bundle();

                /*ar3.add(String.valueOf(latti));
                ar3.add(String.valueOf(longgi));*/

                bundle.putString("LATITUDE",String.valueOf(latti));
                bundle.putString("LONGITUDE",String.valueOf(longgi));

                Intent intent = new Intent(MapsActivity.this,ZoneView.class);
                intent.putExtras(bundle);
                startActivity(intent);
                return false;
            }
        });


        // Read from the database
        myRefstatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                if(value=="on"){
                    // Read from the database
                    myRefLat.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.
                            String sos_lat = dataSnapshot.getValue(String.class);
                            latsos = Double.parseDouble(sos_lat);


                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Log.w(TAG, "Failed to read value.", error.toException());
                        }
                    });
                    // Read from the database
                    myRefLong.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.
                            String sos_long = dataSnapshot.getValue(String.class);
                            longsos = Double.parseDouble(sos_long);


                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Log.w(TAG, "Failed to read value.", error.toException());
                        }
                    });
                }



            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });



    }
}