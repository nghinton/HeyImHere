package com.example.heyimhere;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.heyimhere.database.LocationRule;

public class AddLocationRuleActivity extends AppCompatActivity {

    public static final int CHOOSE_LOCATION_ACTIVITY_REQUEST_CODE = 1;

    MessagesViewModel mMessagesViewModel;
    LocationRuleViewModel mLocationRuleViewModel;
    int messageID;

    TextView txtName;
    TextView txtLatitude;
    TextView txtLongitude;
    TextView txtRadius;
    private String locationName;
    private double longitude = 0;
    private double latitude = 0;
    private float radius = 0;

    private boolean hasLocationRule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addlocationrule);

        txtName = findViewById(R.id.txtLocationName);
        txtLatitude = findViewById(R.id.txtLocationLat);
        txtLongitude = findViewById(R.id.txtLocationLong);
        txtRadius = findViewById(R.id.txtLocationRadius);

        // Get message id that was passed in
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            messageID = (Integer) bundle.get("messageID");
        }

        // Get a new or existing ViewModel from the ViewModelProvider.
        mMessagesViewModel = new ViewModelProvider(this).get(MessagesViewModel.class);
        mLocationRuleViewModel = new ViewModelProvider(this).get(LocationRuleViewModel.class);

        Button selectLocation = findViewById(R.id.btnChooseLocation);
        selectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseLocation = new Intent(AddLocationRuleActivity.this, MapActivity.class);
                startActivityForResult(chooseLocation, CHOOSE_LOCATION_ACTIVITY_REQUEST_CODE);
            }
        });

        // Set Save Contact button and its listener
        Button SaveButton = findViewById(R.id.btnSave);
        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRule();
            }
        });

        // Set Cancel button and its listener
        Button CancelButton = findViewById(R.id.btnCancel);
        CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    public void setRule() {
        if (hasLocationRule) {
            LocationRule locationRule = new LocationRule(messageID, false, false, locationName, latitude, longitude, radius);
            mLocationRuleViewModel.insert(locationRule, getApplicationContext());
        }
        onBackPressed();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case(CHOOSE_LOCATION_ACTIVITY_REQUEST_CODE) : {
                if (resultCode == RESULT_OK) {
                    // Get data from intent
                    latitude = data.getDoubleExtra("latitude", 0.00);
                    longitude = data.getDoubleExtra("longitude", 0.00);
                    radius = data.getFloatExtra("radius", (float) 0.00);
                    locationName = data.getStringExtra("name");

                    // Set data in relevant text fields
                    txtName.setText(locationName);
                    txtLongitude.setText(Double.toString(longitude));
                    txtLatitude.setText(Double.toString(latitude));
                    txtRadius.setText(Float.toString(radius));
                    hasLocationRule = true;
                }
            }
        }
    }

}
