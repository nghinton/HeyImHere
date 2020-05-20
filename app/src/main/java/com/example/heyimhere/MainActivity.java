package com.example.heyimhere;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.heyimhere.database.Contact;
import com.example.heyimhere.database.LocationRule;
import com.example.heyimhere.database.Message;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    ViewPagerFragmentAdapter myAdapter;

    //Permissions stuff
    private static final int CONTACTS_PERMISSION = 1;
    private static final int SMS_PERMISSION = 2;
    private static final int READ_PHONE_STATE_PERMISSION = 3;
    private static final int LOCATION_PERMISSION = 4;

    String phoneNumber; //The user's phone number.
    private ContactViewModel mContactViewModel;
    private MessagesViewModel mMessagesViewModel;
    private boolean messagesPopulated;
    private boolean contactsPopulated;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Define SlidingTabLayout (shown at top)
        // and ViewPager (shown at bottom) in the layout.
        tabLayout = findViewById(R.id.tab);
        viewPager = findViewById(R.id.viewpager);

        // Initialize adapter
        myAdapter = new ViewPagerFragmentAdapter(getSupportFragmentManager(), getLifecycle());
        myAdapter.addFragment(new ChatFragment());
        myAdapter.addFragment(new DraftsFragment());

        // Set Orientation
        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        // Add the adapter to the viewPager
        viewPager.setAdapter(myAdapter);

        mContactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);
        mMessagesViewModel = new ViewModelProvider(this).get(MessagesViewModel.class);

        //Get any necessary permissions.
        messagesPopulated = false;
        contactsPopulated = false;
        GetPermissions();

        //Populate the database if already possible.
        if (mMessagesViewModel.getCount() == 0 && mContactViewModel.getCount() == 0) {
            PopulateDB();
        }

        if (CheckLocationPermission()) {
            startService(new Intent(this, LocationRuleManager.class));
        }

        // Initialize the tabs / tab layout
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, true, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(TabLayout.Tab tab, int position) {
                // position of the current tab and that tab
                if (position == 0) tab.setText("CHAT");
                if (position == 1) tab.setText("DRAFTS");
            }
        });
        tabLayoutMediator.attach();

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
                // Direct to the website about page
                Intent feedbackIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://peytonshotts.github.io/HeyImHere/about.html"));
                startActivity(feedbackIntent);
                return true;
            case R.id.menuitem_about:
                // Direct to the website home page
                Intent aboutIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://peytonshotts.github.io/HeyImHere/index.html"));
                startActivity(aboutIntent);
                return true;
            case R.id.menuitem_quit:
                // close the activity
                finish();
                return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case SMS_PERMISSION: {
                if (!CheckSmsPermission()) {
                    Toast.makeText(this, "Cannot read messages, please grant the app SMS permissions in settings.", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case CONTACTS_PERMISSION: {
                if (!CheckContactsPermission()) {
                    Toast.makeText(this, "Cannot read messages, please grant the app contacts permissions in settings.", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case READ_PHONE_STATE_PERMISSION: {
                if (!CheckPhoneStatePermission()) {
                    Toast.makeText(this, "Cannot read your phone number, please grant the app permission to read phone state  in settings.", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
        PopulateDB(); //Populate any bits of the db that can be populated.
    }

    @SuppressLint("MissingPermission")
    private void PopulateDB() {
        if (CheckPhoneStatePermission()) {
            if (phoneNumber == null) {
                TelephonyManager tMgr = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);
                phoneNumber = PhoneNumberUtils.GetOwn(tMgr);
            }
            if (CheckContactsPermission() && !contactsPopulated) {
                ImportContacts();
                contactsPopulated = true;
            }
            if (CheckSmsPermission() && !messagesPopulated && contactsPopulated) {
                ImportMessages();
                messagesPopulated = true;
            }
        }
    }

    private boolean CheckContactsPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean CheckPhoneStatePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean CheckLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean CheckSmsPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    //Checks for any missing permissions and asks for them.
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void GetPermissions() {
        ArrayList<String> permissions = new ArrayList<String>();
        if (!CheckPhoneStatePermission()) {
            permissions.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (!CheckSmsPermission()) {
            permissions.add(Manifest.permission.READ_SMS);
        }

        if (!CheckContactsPermission()) {
            permissions.add(Manifest.permission.READ_CONTACTS);
        }

        if (!CheckLocationPermission()) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }

        if (permissions.size() > 0) {
            requestPermissions(permissions.toArray(new String[permissions.size()]), SMS_PERMISSION);
        }
    }

    public void ImportContacts() {
        Contact myself = new Contact("Myself", phoneNumber);
        mContactViewModel.insertMainThread(myself); //Had some weird issues with this being overwritten somehow. Forcing it in first to avoid these.

        ContentResolver contentResolver = this.getContentResolver();
        Cursor contactsCursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if (contactsCursor == null || !contactsCursor.moveToFirst()) return;

        do {
            String id = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts._ID));
            if (contactsCursor.getInt(contactsCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                String name = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                Cursor numberCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{id}, null
                );
                numberCursor.moveToFirst();
                String number = numberCursor.getString(numberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                // Create new contact and insert it contact into the database
                Contact contact = new Contact(name, number);
                mContactViewModel.insert(contact);
            }
        } while (contactsCursor.moveToNext());
        contactsCursor.close();
    }

    public void ImportMessages() {
        //Get received messages.
        ContentResolver contentResolver = this.getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        if (smsInboxCursor == null || !smsInboxCursor.moveToFirst()) return;
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexSender = smsInboxCursor.getColumnIndex("address");
        int indexTimeStamp = smsInboxCursor.getColumnIndex("date");

        do {
            String sender = smsInboxCursor.getString(indexSender);
            sender = PhoneNumberUtils.Clean(sender);
            String body = smsInboxCursor.getString(indexBody);
            String timeStamp = smsInboxCursor.getString(indexTimeStamp); //Remind me to look over how these are generated later.
            Message message = new Message(body, sender, phoneNumber, true, timeStamp);
            mMessagesViewModel.insert(message);
        } while (smsInboxCursor.moveToNext());
        smsInboxCursor.close();

        //Get sent messages.
        Cursor smsOutboxCursor = contentResolver.query(Uri.parse("content://sms/sent"), null, null, null, null);
        if (smsOutboxCursor == null || !smsOutboxCursor.moveToFirst()) return;
        indexBody = smsOutboxCursor.getColumnIndex("body");
        int indexReceiver = smsOutboxCursor.getColumnIndex("address");
        indexTimeStamp = smsOutboxCursor.getColumnIndex("date");

        do {
            String receiver = smsOutboxCursor.getString(indexReceiver);
            if (!receiver.equals(phoneNumber)) { //Avoid duplicates for self-sent messages.
                receiver = PhoneNumberUtils.Clean(receiver);
                String body = smsOutboxCursor.getString(indexBody);
                String timeStamp = smsOutboxCursor.getString(indexTimeStamp); //Remind me to look over how these are generated later.
                Message message = new Message(body, phoneNumber, receiver,true, timeStamp);
                mMessagesViewModel.insert(message);
            }
        } while (smsOutboxCursor.moveToNext());
        smsOutboxCursor.close();
    }
}