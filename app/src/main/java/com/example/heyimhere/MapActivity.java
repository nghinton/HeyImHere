package com.example.heyimhere;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    Place selectedLocation;
    Circle radiusCircle;
    float radius = 0;
    DecimalFormat radiusFormat;

    EditText editText;
    Button selectButton;
    SeekBar radiusSlider;
    TextView radiusLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Grab client location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize map fragment and start async task
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Initialize Places API
        Places.initialize(getApplicationContext(), "AIzaSyABxvqOAG2tirVurngGweJ0VwluqkRoE5w");

        // Initialize select button and set values to return
        selectButton = findViewById(R.id.selectButton);
        selectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Create reply intent and set data
                Intent data = new Intent();
                data.putExtra("latitude", selectedLocation.getLatLng().latitude);
                data.putExtra("longitude", selectedLocation.getLatLng().longitude);
                data.putExtra("radius", toMiles(radius));
                data.putExtra("name", selectedLocation.getName());
                setResult(RESULT_OK,data);
                // Finish activity
                finish();
            }
        });

        // Disable location select button while location is null
        disableSelectButton();

        // Initialize edit text field
        editText = findViewById(R.id.places_autocomplete_edit_text);
        editText.setFocusable(false);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initialize place field list
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS
                        ,Place.Field.LAT_LNG, Place.Field.NAME);
                //Create intent
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY
                        ,fieldList).build(MapActivity.this);
                //Start activity result
                startActivityForResult(intent, 100);
            }
        });

        // radiusSlider setup
        radiusSlider = findViewById(R.id.radiusSlider);
        radiusSlider.setOnSeekBarChangeListener(seekBarChangeListener);
        radiusSlider.setVisibility(View.GONE);

        // radiusLabel setup
        radiusLabel = findViewById(R.id.textView);
        radiusLabel.setVisibility(View.GONE);
        radiusFormat = new DecimalFormat("#.##");

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(currentLocation).title("Marker"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 5.0f));
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {

            //Get place object
            Place place = Autocomplete.getPlaceFromIntent(data);
            selectedLocation = place;

            //Set address on EditText
            editText.setText(place.getAddress());

            //Set marker and move to location
            mMap.clear();
            LatLng selectedLocation = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
            mMap.addMarker(new MarkerOptions().position(selectedLocation));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(place.getLatLng().latitude, place.getLatLng().longitude), 8.0f));

            //create radius circle
            radiusCircle = mMap.addCircle(
                    new CircleOptions()
                            .center(place.getLatLng())
                            .radius(toMeters(radiusSlider.getProgress()))
                            .strokeColor(Color.RED)
                            .strokeWidth(3f)
                            .fillColor(Color.argb(70, 150, 50, 50))
            );

            enableSelectButton();

            radiusLabel.setVisibility(View.VISIBLE);
            radiusSlider.setVisibility(View.VISIBLE);
            radiusLabel.setText("Radius: " + radiusFormat.format(toMiles(radiusSlider.getProgress())) + " miles");

            radius = radiusSlider.getProgress();

        } else if (resultCode == AutocompleteActivity.RESULT_ERROR){
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getApplicationContext(), status.getStatusMessage()
                    ,Toast.LENGTH_SHORT).show();
        }
    }

    // Misc functions

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            radius = progress;
            radiusCircle.setRadius(toMeters(radius));
            radiusLabel.setText("Radius: " + radiusFormat.format(toMiles(radius)) + " miles");
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    void disableSelectButton() {
        selectButton.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        selectButton.setAlpha(.5f);
        selectButton.setClickable(false);
    }
    void enableSelectButton() {
        selectButton.getBackground().setColorFilter(null);
        selectButton.setAlpha(1.0f);
        selectButton.setClickable(true);
    }

    float toMeters(float x) {
        return toMiles(x) * 1609.34f;
    }
    float toMiles(float x) {
        return x / 100f;
    }

}
