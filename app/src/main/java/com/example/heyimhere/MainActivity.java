package com.example.heyimhere;

import androidx.annotation.RequiresApi;
import android.annotation.SuppressLint;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    ViewPagerFragmentAdapter myAdapter;

    // Permissions stuff
    private static final int CONTACTS_PERMISSION = 1;
    private static final int SMS_PERMISSION = 2;
    private static final int WRITE_PERMISSION = 3;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Define SlidingTabLayout (shown at top)
        // and ViewPager (shown at bottom) in the layout.
        tabLayout = findViewById(R.id.tab);
        viewPager = findViewById(R.id.viewpager);

        // Initialize ViewPagerAdapter w/stuff
        myAdapter = new ViewPagerFragmentAdapter(getSupportFragmentManager(), getLifecycle());
        myAdapter.addFragment(new Fragment_Sent());
        myAdapter.addFragment(new Fragment_Pending());
        myAdapter.addFragment(new Fragment_Saved());

        // Set Orientation
        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        // Add the adapter to the viewPager
        viewPager.setAdapter(myAdapter);

        // Initialize the tabs / tab layout
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, true, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(TabLayout.Tab tab, int position) {
                // position of the current tab and that tab
                if (position == 0) tab.setText("SENT");
                if (position == 1) tab.setText("PENDING");
                if (position == 2) tab.setText("SAVED");
            }
        });
        tabLayoutMediator.attach();

        // Go ahead and grab permissions on start up if we can
        GetPermissions();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuitem_settings:
                // Finish the settings activity later
                Toast.makeText(this, getString(R.string.menu_settings),
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menuitem_feedback:
                // Finish the settings activity later
                Toast.makeText(this, getString(R.string.menu_feedback),
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menuitem_about:
                // Finish the settings activity later
                Toast.makeText(this, getString(R.string.menu_about),
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menuitem_quit:
                // close the activity
                finish();
                return true;
        }
        return false;
    }

    // Permission warnings if we didnt get any
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case SMS_PERMISSION: {
                if (CheckSmsPermission()) {
                    Toast.makeText(this, "Cannot read messages, please grant the app SMS permissions in settings.", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case CONTACTS_PERMISSION: {
                if (!CheckContactsPermission()) {
                    Toast.makeText(this, "Cannot read contacts, please grant the app contacts permissions in settings.", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case WRITE_PERMISSION: {
                if (!CheckContactsPermission()) {
                    Toast.makeText(this, "Cannot write to external memory, please grant the app storage permissions in settings.", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    // Collection of permission checking helper functions
    private boolean CheckContactsPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean CheckSmsPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED;
    }

    private boolean CheckWritePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
    }

    // Checks for any missing permissions and asks for them.
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void GetPermissions() {
        ArrayList<String> permissions = new ArrayList<String>();

        if (!CheckSmsPermission()) {
            permissions.add(Manifest.permission.READ_SMS);
        }

        if (!CheckContactsPermission()) {
            permissions.add(Manifest.permission.READ_CONTACTS);
        }

        if (!CheckWritePermission()) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (permissions.size() > 0) {
            requestPermissions(permissions.toArray(new String[permissions.size()]), SMS_PERMISSION);
        }
    }

}