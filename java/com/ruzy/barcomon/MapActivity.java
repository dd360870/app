package com.ruzy.barcomon;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.ui.IconGenerator;
import com.ruzy.barcomon.databaseModel.Price;

/**
 * Created by Ruzy on 2017/3/21.
 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private String barCode;
    private String id;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        barCode = getIntent().getStringExtra("barCode");
        id = getIntent().getStringExtra("id");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(final GoogleMap map) {
        this.map = map;
        /*map.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));*/

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setTrafficEnabled(false);
        map.setIndoorEnabled(true);
        map.setBuildingsEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        LatLng Taiwan = new LatLng(23.5942134,119.8997087);
        map.moveCamera( CameraUpdateFactory.newLatLngZoom( Taiwan, 8.5f) );
        /*LatLng MELBOURNE = new LatLng(-37.81319, 144.96298);
        Marker melbourne = map.addMarker(new MarkerOptions()
                .position(MELBOURNE)
                .title("Melbourne")
                .snippet("Population: 4,137,400"));*/
        FirebaseDatabase.getInstance().getReference("prices/"+barCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Marker center = null;
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                IconGenerator iconFactory = new IconGenerator(MapActivity.this);
                iconFactory.setRotation(0);
                iconFactory.setContentRotation(0);
                iconFactory.setStyle(IconGenerator.STYLE_GREEN);
                for(DataSnapshot price: dataSnapshot.getChildren()) {
                    Price p = price.getValue(Price.class);
                    builder.include(new LatLng(p.mapLat, p.mapLng));
                    Marker m = addIcon(iconFactory, "$ "+p.price, p.mapName,new LatLng(p.mapLat, p.mapLng));
                    if(id.matches(p.mapId))
                        center = m;
                }
                LatLngBounds bounds = builder.build();
                int width = getResources().getDisplayMetrics().widthPixels;
                int height = getResources().getDisplayMetrics().heightPixels;
                int padding = (int) (width * 0.20); // offset from edges of the map 12% of screen*/
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                map.animateCamera(cu);
                if(center != null)
                    center.showInfoWindow();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        /*map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return false;
            }
        });*/
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private Marker addIcon(IconGenerator iconFactory, CharSequence text, String title, LatLng position) {
        MarkerOptions markerOptions = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(text)))
                .position(position)
                .anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV())
                .title(title);
        Marker m = map.addMarker(markerOptions);
        return m;
    }
}
