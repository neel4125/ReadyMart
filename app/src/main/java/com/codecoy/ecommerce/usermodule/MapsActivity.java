package com.codecoy.ecommerce.usermodule;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.codecoy.ecommerce.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String Lat, Lang;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //------------------------------------get lat lang from database---------------------//

        Lat = Utils.getPreferences("lat", getApplicationContext());
        Lang = Utils.getPreferences("lang", getApplicationContext());
        if (Lat != null && Lang != null) {
            Toast.makeText(this, "Lat:" + Lat + " " + "Lang:" + Lang, Toast.LENGTH_SHORT).show();
           // onMapReady(mMap);
        } else {
            Toast.makeText(this, "Lat Lang Not Found", Toast.LENGTH_SHORT).show();
        }

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

        // Add a marker in Location and move the camera
        LatLng location = new LatLng(Double.valueOf(Lat), Double.valueOf(Lang));
        mMap.addMarker(new MarkerOptions().position(location).title("Marker in Your Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10.0f));
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(MapsActivity.this, Main_Cat_Activity.class));
        super.onBackPressed();
    }
}
