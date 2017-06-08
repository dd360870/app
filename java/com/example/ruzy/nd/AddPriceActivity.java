package com.example.ruzy.nd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ruzy.nd.databaseModel.Price;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Ruzy on 2017/3/15.
 */

public class AddPriceActivity extends AppCompatActivity
        implements OnMapReadyCallback{
    private static final int REQUEST_PLACE_PICKER = 1;

    private EditText price;
    private TextView mViewName;
    private TextView mViewAddress;
    private TextView mViewAttributions;
    private Button submit;
    private Button map_api;

    private Place place;
    private String barCode;

    @Override
    public void onMapReady(GoogleMap map) {
        /*map.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_price);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        barCode = getIntent().getStringExtra("barCode");

        price = (EditText) findViewById(R.id.add_price_price);
        mViewName = (TextView) findViewById(R.id.mName);
        mViewAddress = (TextView) findViewById(R.id.mAddress);
        mViewAttributions = (TextView) findViewById(R.id.mAttr);
        submit = (Button) findViewById(R.id.add_price_submit);
        map_api = (Button) findViewById(R.id.map_api);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(price.getText().length() == 0)
                    price.setError("必填項目");
                else if(place == null || !place.isDataValid()) {
                    Snackbar.make(findViewById(android.R.id.content), "請選擇一個地點", Snackbar.LENGTH_LONG).show();
                }
                else {
                    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("prices/"+barCode);
                    databaseRef = databaseRef.push();
                    Price p = new Price(price.getText().toString(),
                            FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                            FirebaseAuth.getInstance().getCurrentUser().getUid());
                    p.setMap(place.getName().toString(),
                            place.getLatLng().latitude,
                            place.getLatLng().longitude,
                            place.getId(),
                            place.getAddress().toString());
                    databaseRef.setValue(p);
                    new manipulateUserInformation().plusEnergy(20);
                    finish();
                }
            }
        });

        map_api.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PlacePicker.IntentBuilder intentBuilder =
                            new PlacePicker.IntentBuilder();
                    Intent intent = intentBuilder.build(AddPriceActivity.this);
                    // Start the intent by requesting a result,
                    // identified by a request code.
                    startActivityForResult(intent, REQUEST_PLACE_PICKER);

                } catch (GooglePlayServicesRepairableException e) {
                    // ...
                } catch (GooglePlayServicesNotAvailableException e) {
                    // ...
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        if (requestCode == REQUEST_PLACE_PICKER
                && resultCode == Activity.RESULT_OK) {

            // The user has selected a place. Extract the name and address.
            //final Place place = PlacePicker.getPlace(data, this);
            place = PlacePicker.getPlace(AddPriceActivity.this, data);

            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            String attr = place.getId();

            mViewName.setText(name);
            mViewAddress.setText(address);
            mViewAttributions.setText(attr);

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
