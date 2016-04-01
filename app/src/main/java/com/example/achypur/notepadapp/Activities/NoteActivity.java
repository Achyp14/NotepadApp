package com.example.achypur.notepadapp.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.achypur.notepadapp.DAO.CoordinateDao;
import com.example.achypur.notepadapp.DAO.NoteDao;
import com.example.achypur.notepadapp.DAO.TagDao;
import com.example.achypur.notepadapp.DAO.TagOfNotesDao;
import com.example.achypur.notepadapp.DAO.UserDao;
import com.example.achypur.notepadapp.Entities.Coordinate;
import com.example.achypur.notepadapp.Entities.Note;
import com.example.achypur.notepadapp.Entities.Tag;
import com.example.achypur.notepadapp.R;
import com.example.achypur.notepadapp.Session.SessionManager;
import com.example.achypur.tagview.TagView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    TagDao mTagDao;
    CoordinateDao mCoordinateDao;
    TagOfNotesDao mTagOfNotesDao;
    Note mNote = null;
    HashMap<String, String> mCurrentUser;
    SessionManager mSession;
    LocationManager mLocationManager;
    SupportMapFragment mMapFragment;
    Menu mMenu;
    TagView mTagView;
    List<Tag> mAddTagsList = new ArrayList<>();
    List<Tag> mDeleteTagsList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.note_toolbar);
        setSupportActionBar(toolbar);
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 10);
        mNoteDao = new NoteDao(this);
        mUserDao = new UserDao(this);
        mCoordinateDao = new CoordinateDao(this);
        mTagDao = new TagDao(this);
        mTagOfNotesDao = new TagOfNotesDao(this);
        mSession = new SessionManager(this);
        mCurrentUser = mSession.getUserDetails();

        try {
            mNoteDao.open();
            mUserDao.open();
            mCoordinateDao.open();
            mTagDao.open();
            mTagOfNotesDao.open();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        final EditText title = (EditText) findViewById(R.id.note_edit_title);
        final EditText content = (EditText) findViewById(R.id.note_edit_content);
        final TextView time = (TextView) findViewById(R.id.note_edit_time);

        final Button save = (Button) findViewById(R.id.note_button_submit);
        final Button cancel = (Button) findViewById(R.id.note_button_cancel);

        EditText tag = (EditText) findViewById(R.id.tag_text);

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT + 2:00"));
        final Date currentLocalTime = calendar.getTime();
        final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT + 2:00"));
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

        mTagView = (TagView) findViewById(R.id.tag_grid);

        mTagView.setListener(new TagView.Listener() {
            @Override
            public void onAddingTag(String tag) {
                mTagView.addTag(tag);
                mAddTagsList.add(new Tag(tag));
            }

            @Override
            public void onRemovingTag(String tag) {
                mTagView.removeTag(tag);
                mDeleteTagsList.remove(new Tag(tag));
            }
        });

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
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
                menu.findItem(R.id.note_menu_location).setTitle("Edit Location");
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_menu, menu);

        this.mMenu = menu;
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
                Location location = findingLocation(this);
                LatLng latLng = findingCoordinate(location);
                String city = findingCityName();
                dialogWindow(this, latLng, city);
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

    private Location findingLocation(final Context context) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Location permissions required", Toast.LENGTH_SHORT).show();
        }

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        final String provider = mLocationManager.getBestProvider(criteria, true);
        final Location location = mLocationManager.getLastKnownLocation(provider);

        mLocationManager.requestLocationUpdates(provider, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
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

        return location;
    }

    private LatLng findingCoordinate(Location location) {
        Log.e("Achyp", "317|NoteActivity::findingCoordinate: " + mNote.getmId());
        if (location != null) {
            mNote.setmLocation(mCoordinateDao.createCoordinate(location.getLatitude(),
                    location.getLongitude()));
        }
        Coordinate coordinate = mCoordinateDao.getCoordinateById(mNote.getmLocation());
        return new LatLng(coordinate.getLatitude(), coordinate.getLongtitude());
    }

    private String findingCityName() {
        Coordinate coordinate = mCoordinateDao.getCoordinateById(mNote.getmLocation());
        Geocoder geocoder = new Geocoder(NoteActivity.this, Locale.getDefault());
        List<Address> address = null;
        try {
            address = geocoder.getFromLocation(coordinate.getLatitude(), coordinate.getLongtitude(), 100);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (address.get(0).getLocality() != null) {
            return "Current location: " + address.get(0).getLocality();
        } else {
            return "Can't find your current location";
        }
    }

    private void dialogWindow(Context context, final LatLng latLng, String city) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.layout_dialog);

        final DialogHolder dialogHolder = new DialogHolder();

        dialogHolder.title = (TextView) dialog.findViewById(R.id.dialog_text);
        dialogHolder.ok = (Button) dialog.findViewById(R.id.dialog_ok);
        dialogHolder.cancel = (Button) dialog.findViewById(R.id.dialog_cancel);
        dialogHolder.refresh = (Button) dialog.findViewById(R.id.dialog_refresh);
        dialogHolder.remove = (Button) dialog.findViewById(R.id.dialog_remove);

        dialogHolder.title.setText(city);
        dialogHolder.ok.setText("OK");
        dialogHolder.cancel.setText("CANCEL");
        dialogHolder.remove.setText("Remove");

        dialog.show();

        dialogHolder.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapFragment.getView().setVisibility(View.VISIBLE);
                mMapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        googleMap.addMarker(new MarkerOptions().position(latLng));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
                    }
                });
                dialog.dismiss();
                mMenu.findItem(R.id.note_menu_location).setTitle("Edit Location");
            }
        });

        dialogHolder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNote.setmLocation(Long.valueOf(0));
                dialog.dismiss();
            }
        });

        dialogHolder.refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Location location = findingLocation(NoteActivity.this);
                LatLng latLng = findingCoordinate(location);
                String city = findingCityName();
                dialogWindow(NoteActivity.this, latLng, city);
            }
        });

        dialogHolder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMapFragment.getView().getVisibility() != View.VISIBLE) {
                    Toast.makeText(NoteActivity.this, "Nothing to remove", Toast.LENGTH_SHORT).show();
                } else {
                    mMapFragment.getView().setVisibility(View.INVISIBLE);
                    mNote.setmLocation(Long.valueOf(0));
                    mMenu.findItem(R.id.note_menu_location).setTitle("Add Location");
                    dialog.dismiss();
                }
            }
        });
    }

    static class DialogHolder {
        TextView title;
        Button ok;
        Button cancel;
        Button refresh;
        Button remove;

        public DialogHolder() {
        }
    }

}