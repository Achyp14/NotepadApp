package com.example.achypur.notepadapp.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.achypur.notepadapp.DAO.CoordinateDao;
import com.example.achypur.notepadapp.DAO.NoteDao;
import com.example.achypur.notepadapp.DAO.UserDao;
import com.example.achypur.notepadapp.Entities.Coordinate;
import com.example.achypur.notepadapp.Entities.Note;
import com.example.achypur.notepadapp.R;
import com.example.achypur.notepadapp.Session.SessionManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class NoteActivity extends AppCompatActivity {
    private final static String NOTE_ID_KEY = "id";

    NoteDao mNoteDao;
    UserDao mUserDao;
    CoordinateDao mCoordinateDao;
    Note mNote = null;
    HashMap<String, String> mCurrentUser;
    SessionManager mSession;
    LocationManager mLocationManager;
    SupportMapFragment mMapFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.note_toolbar);
        setSupportActionBar(toolbar);


        mNoteDao = new NoteDao(this);
        mUserDao = new UserDao(this);
        mCoordinateDao = new CoordinateDao(this);
        mSession = new SessionManager(this);
        mCurrentUser = mSession.getUserDetails();

        try {
            mNoteDao.open();
            mUserDao.open();
            mCoordinateDao.open();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }


        final EditText title = (EditText) findViewById(R.id.note_edit_title);
        final EditText content = (EditText) findViewById(R.id.note_edit_content);
        final TextView time = (TextView) findViewById(R.id.note_edit_time);

        final Button save = (Button) findViewById(R.id.note_button_submit);
        final Button cancel = (Button) findViewById(R.id.note_button_cancel);

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT + 2:00"));
        final Date currentLocalTime = calendar.getTime();
        final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT + 2:00"));

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        time.setText(dateFormat.format(currentLocalTime));
        save.setEnabled(false);
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (title.getText().toString().trim().equals("") ||
                        content.getText().toString().trim().equals("")) {
                    save.setEnabled(false);
                } else {
                    save.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        title.addTextChangedListener(watcher);
        content.addTextChangedListener(watcher);
        mMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.note_map);
        mMapFragment.getView().setVisibility(View.INVISIBLE);
        initParamsFromIntent(getIntent());
        if (isEditMode()) {
            title.setText(mNote.getmTitle());
            content.setText(mNote.getmContent());
            if (mNote.getmUserId() != mUserDao.findUserById(mUserDao.findUserByLogin
                    (mCurrentUser.get(SessionManager.KEY_LOGIN))).getId()) {
                title.setEnabled(false);
                content.setEnabled(false);
                save.setText("OK");
                title.setTextColor(Color.BLACK);
                content.setTextColor(Color.BLACK);
            }
            if (mNote.getmLocation() != 0) {
                mMapFragment.getView().setVisibility(View.VISIBLE);
                mMapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        Coordinate coordinate = mCoordinateDao.getCoordinateById(mNote.getmLocation());
                        final LatLng currentPosition = new LatLng(coordinate.getLatitude(), coordinate.getLongtitude());
                        googleMap.addMarker(new MarkerOptions().position(currentPosition));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(8), 2000, null);
                    }
                });
            }
        }

        final Intent intent = new Intent();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditMode()) {
                    mNote.setmTitle(title.getText().toString().trim());
                    mNote.setmContent(content.getText().toString().trim());
                    mNote.setmModifiedDate(dateFormat.format(currentLocalTime));
                    mNoteDao.updateNote(mNote);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {

                    mNoteDao.createNote(title.getText().toString().trim(),
                            content.getText().toString().trim(), mUserDao.findUserByLogin
                                    (mCurrentUser.get(SessionManager.KEY_LOGIN)),
                            dateFormat.format(currentLocalTime),
                            dateFormat.format(currentLocalTime), false, null);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        final Intent mainActivity = new Intent(this, MainActivity.class);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(mainActivity);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_menu, menu);
        if (isEditMode()) {
            menu.findItem(R.id.note_menu_location).setVisible(true);
            menu.findItem(R.id.note_menu_check_shared).setVisible(true).
                    setChecked(mNote.getmPolicyStatus());
            if (mNote.getmUserId() != mUserDao.findUserById(mUserDao.findUserByLogin
                    (mCurrentUser.get(SessionManager.KEY_LOGIN))).getId()) {
                menu.findItem(R.id.note_menu_location).setVisible(false);
                menu.findItem(R.id.note_menu_check_shared).setVisible(false).
                        setChecked(mNote.getmPolicyStatus());
            }
            if (mNote.getmLocation() != 0) {
                menu.findItem(R.id.note_menu_location).setTitle("Edit location");
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final AlertDialog.Builder aBuilder = new AlertDialog.Builder(this);
        final AlertDialog alertDialog;
        switch (item.getItemId()) {
            case R.id.note_menu_check_shared:
                if (!item.isChecked()) {
                    aBuilder.setMessage("Make this note public?").setCancelable(true)
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mNote.setmPolicyId(true);
                                    item.setChecked(true);
                                }
                            })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    alertDialog = aBuilder.create();
                    alertDialog.show();
                    return true;
                } else {
                    mNote.setmPolicyId(false);
                    item.setChecked(false);
                    return true;
                }
            case R.id.note_menu_location:
                if (Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Location permissions required", Toast.LENGTH_SHORT).show();
                }
                mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();

                String provider = mLocationManager.getBestProvider(criteria, true);
                Location location = mLocationManager.getLastKnownLocation(provider);
                if (location != null) {
                    mNote.setmLocation(mCoordinateDao.createCoordinate(location.getLatitude(),
                            location.getLongitude()));
                }
                mLocationManager.requestLocationUpdates(provider, 0, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        mNote.setmLocation(mCoordinateDao.createCoordinate(location.getLatitude(),
                                location.getLongitude()));
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                    }
                });
                if (!item.isChecked()) {
                    Coordinate coordinate = mCoordinateDao.getCoordinateById(mNote.getmLocation());
                    final LatLng currentPosition = new LatLng(coordinate.getLatitude(), coordinate.getLongtitude());
                    Geocoder geocoder = new Geocoder(NoteActivity.this, Locale.getDefault());
                    List<Address> address = null;
                    try {
                        address = geocoder.getFromLocation(coordinate.getLatitude(), coordinate.getLongtitude(), 100);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String city;
                    if (address.get(0).getLocality() != null) {
                        city = "Current location: " + address.get(0).getLocality();
                    } else {
                        city = "Can't find your current location";
                    }
                    aBuilder.setMessage(city).setCancelable(true)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mMapFragment.getView().setVisibility(View.VISIBLE);
                                    mMapFragment.getMapAsync(new OnMapReadyCallback() {
                                        @Override
                                        public void onMapReady(GoogleMap googleMap) {
                                            googleMap.addMarker(new MarkerOptions().position(currentPosition));
                                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
                                            googleMap.animateCamera(CameraUpdateFactory.zoomTo(8), 2000, null);
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }

                            });
                    alertDialog = aBuilder.create();
                    alertDialog.show();
                    return true;
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isEditMode() {
        return mNote != null;
    }

    private void initParamsFromIntent(Intent intent) {
        mNote = null;
        if (intent != null && intent.getExtras() != null && intent.getExtras().containsKey(NOTE_ID_KEY)) {
            mNote = mNoteDao.getNoteById(intent.getLongExtra(NOTE_ID_KEY, -1));

        }
    }

    public static Intent createIntentForAddNote(Context context) {
        Intent intent = new Intent(context, NoteActivity.class);
        return intent;
    }

    public static Intent createIntentForEditNote(Context context, Long id) {
        Intent intent = new Intent(context, NoteActivity.class);
        intent.putExtra(NOTE_ID_KEY, id);
        return intent;
    }

    private void refresh(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog alertDialog;


    }

}

