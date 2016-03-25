package com.shreyaslakhe.demo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity implements LocationListener {

    private GoogleMap googleMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        // Showing status
        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        } else { // Google Play Services are available

            // Getting reference to the SupportMapFragment of activity_map.xml
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            // Getting GoogleMap object from the fragment
            googleMap = fm.getMap();

            // Getting LocationManager object from System Service LOCATION_SERVICE
            final LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            Boolean gps_enabled = false;
            try {
                gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
            }           // Enabling MyLocation Layer of Google Map

            if (!gps_enabled) {
                final AlertDialog.Builder builder =
                        new AlertDialog.Builder(MapActivity.this);
                final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
                final String message = " Enable Location Services";
                final String title = "Locations Services Disabled";
                builder.setMessage(message)
                        .setPositiveButton("Enable",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface d, int id) {
                                        MapActivity.this.startActivity(new Intent(action));
                                        d.dismiss();
                                    }
                                })
                        .setTitle(title);
                AlertDialog alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setRotateGesturesEnabled(false);

            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            final String provider = locationManager.getBestProvider(criteria, true);

            // Getting Current Location
            final Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                googleMap.clear();
                onLocationChanged(location);
            }
            locationManager.requestLocationUpdates(provider, 20000, 0, this);


        }

    }

    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        MarkerOptions marker = new MarkerOptions().position(latLng);
        googleMap.clear();
        RelativeLayout distanceMarkerLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.marker, null);
        distanceMarkerLayout.setDrawingCacheEnabled(true);
        distanceMarkerLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        distanceMarkerLayout.layout(0, 0, distanceMarkerLayout.getMeasuredWidth(), distanceMarkerLayout.getMeasuredHeight());
        distanceMarkerLayout.buildDrawingCache(true);
        Bitmap flagBitmap = Bitmap.createBitmap(distanceMarkerLayout.getDrawingCache());
        distanceMarkerLayout.setDrawingCacheEnabled(false);
        BitmapDescriptor flagBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(flagBitmap);

        Marker m = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .snippet("")
                .icon(flagBitmapDescriptor));
        m.setDraggable(true);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(15)
                .bearing(90)
                .tilt(30)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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

}
