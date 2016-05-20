package com.example.achypur.notepadapp.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.achypur.notepadapp.DAO.CoordinateDao;
import com.example.achypur.notepadapp.DAO.ForecastDao;
import com.example.achypur.notepadapp.DAO.NoteDao;
import com.example.achypur.notepadapp.DAO.PictureDao;
import com.example.achypur.notepadapp.DAO.TagDao;
import com.example.achypur.notepadapp.DAO.TagOfNotesDao;
import com.example.achypur.notepadapp.DAO.UserDao;
import com.example.achypur.notepadapp.JsonObjects.Forecast;
import com.example.achypur.notepadapp.JsonObjects.ForecastFetcher;
import com.example.achypur.notepadapp.JsonObjects.Rain;
import com.example.achypur.notepadapp.Spannable.EmailClickableSpan;
import com.example.achypur.notepadapp.Entities.Coordinate;
import com.example.achypur.notepadapp.Entities.Note;
import com.example.achypur.notepadapp.Entities.Tag;
import com.example.achypur.notepadapp.Entities.User;
import com.example.achypur.notepadapp.R;
import com.example.achypur.notepadapp.Session.SessionManager;
import com.example.achypur.notepadapp.Spannable.PhoneCLickableSpan;
import com.example.achypur.notepadapp.Spannable.UrlClickableSpan;
import com.example.achypur.notepadapp.Util.DataBaseUtil;
import com.example.achypur.tagview.TagView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
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
import java.util.concurrent.ExecutionException;

public class NoteActivity extends AppCompatActivity {

    private final static String NOTE_ID_KEY = "id";
    private final static String REVISE_MODE = "MODE";
    private final static int UPLOAD_KEY = 1;

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
    List<Tag> mCurrentAddTagsList = new ArrayList<>();
    List<Tag> mTagsList = new ArrayList<>();
    List<Tag> mCurrentRemoveTagsList = new ArrayList<>();
    DataBaseUtil mDataBaseUtil;
    List<byte[]> mUriList = new ArrayList<>();
    List<byte[]> mCurrentAddPictures = new ArrayList<>();
    List<byte[]> mCurrentRemovePictures = new ArrayList<>();
    GridViewAdapter mGridViewAdapter;
    PictureDao mPictureDao;
    GridView mGridView;
    User mLoggedUser;
    boolean mReviseMode = false;
    Long mLocation;
    Forecast mForecast;
    LinearLayout mForecastLayout;
    ForecastDao mForecastDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        mNoteDao = new NoteDao(this);
        mUserDao = new UserDao(this);
        mCoordinateDao = new CoordinateDao(this);
        mTagDao = new TagDao(this);
        mTagOfNotesDao = new TagOfNotesDao(this);
        mSession = new SessionManager(this);
        mPictureDao = new PictureDao(this);
        mForecastDao = new ForecastDao(this);

        try {
            mNoteDao.open();
            mUserDao.open();
            mCoordinateDao.open();
            mTagDao.open();
            mTagOfNotesDao.open();
            mPictureDao.open();
            mForecastDao.open();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        mCurrentUser = mSession.getUserDetails();
        mLoggedUser = mUserDao.findUserById(mUserDao.findUserByLogin(mCurrentUser.get(SessionManager.KEY_LOGIN)));

        mTagView = (TagView) findViewById(R.id.tag_grid);
        mGridViewAdapter = new GridViewAdapter(this);
        final EditText title = (EditText) findViewById(R.id.note_edit_title);
        final EditText content = (EditText) findViewById(R.id.note_edit_content);
        final TextView time = (TextView) findViewById(R.id.note_edit_time);
        final Button save = (Button) findViewById(R.id.note_button_submit);
        final Button cancel = (Button) findViewById(R.id.note_button_cancel);
        mForecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        TextView tags = (TextView) findViewById(R.id.note_edit_tag);

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT + 2:00"));
        final Date currentLocalTime = calendar.getTime();
        View line = findViewById(R.id.line);
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT + 2:00"));
        time.setText("Created at " + dateFormat.format(currentLocalTime));
        save.setEnabled(false);
        LinearLayout buttonLayout = (LinearLayout) findViewById(R.id.buttons);
        mGridView = (GridView) findViewById(R.id.note_edit_pictures);
        mDataBaseUtil = new DataBaseUtil(mTagOfNotesDao, mTagView, mTagDao, mNote);
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

        if (title != null && content != null) {
            title.addTextChangedListener(watcher);
            content.addTextChangedListener(watcher);
        }
        mMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.note_map);
        mMapFragment.getView().setVisibility(View.GONE);
        title.requestFocus();

        initParamsFromIntent(getIntent());
        title.requestFocus();
        title.setSelection(title.getText().length());
        if (isEditMode()) {
            time.setText("Last modified at " + mNote.getmModifiedDate());
            tags.setVisibility(View.VISIBLE);

            Reader reader = new StringReader(mNote.getmContent());
            StreamTokenizer streamTokenizer = new StreamTokenizer(reader);
            streamTokenizer.wordChars('@', '@');
            streamTokenizer.wordChars('/', '/');
            streamTokenizer.wordChars(':', ':');
            streamTokenizer.ordinaryChar(' ');


            SpannableStringBuilder builder = new SpannableStringBuilder("");

            try {
                while (streamTokenizer.nextToken() != StreamTokenizer.TT_EOF) {
                    if (content == null)
                        continue;
                    Spannable sp;
                    switch (streamTokenizer.ttype) {
                        case StreamTokenizer.TT_WORD:
                            String word = streamTokenizer.sval;
                            if (Patterns.EMAIL_ADDRESS.matcher(word).matches()) {
                                sp = new SpannableString(word);
                                sp.setSpan(new EmailClickableSpan(this, word), 0, word.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                builder.append(sp);
                            } else if (Patterns.WEB_URL.matcher(word).matches()) {
                                sp = new SpannableString(word);
                                sp.setSpan(new UrlClickableSpan(this, word), 0, word.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                builder.append(sp);
                            } else {
                                sp = new SpannableString(word);
                                sp.setSpan(null, 0, 0, 0);
                                builder.append(sp);
                            }
                            break;
                        case StreamTokenizer.TT_NUMBER:
                            Double number = streamTokenizer.nval;
                            Integer integer = number.intValue();
                            if (Patterns.PHONE.matcher(integer.toString()).matches()) {
                                sp = new SpannableString(integer.toString());
                                sp.setSpan(new PhoneCLickableSpan(this, integer.toString()), 0, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                builder.append(sp);
                            } else {
                                sp = new SpannableString(String.valueOf(integer));
                                sp.setSpan(null, 0, 0, 0);
                                builder.append(sp);
                            }
                            break;
                        default:
                            char[] chars = Character.toChars(streamTokenizer.ttype);
                            sp = new SpannableString(String.valueOf(chars[0]));
                            sp.setSpan(null, 0, 0, 0);
                            builder.append(sp);
                            break;
                    }
                    content.setText(builder);
                    content.setMovementMethod(LinkMovementMethod.getInstance());
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            if (mForecastDao.ifExistForecastForNote(mNote.getmId())) {
                mForecast = mForecastDao.forecastEntityToForecast(mNote.getmId());
            } else {
                mForecast = null;
            }

            if (mForecast != null) {
                showForecastLayout(mForecastLayout);
            }

            title.setText(mNote.getmTitle());

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
            if (isReviseMode() || mNote.getmUserId() != mLoggedUser.getId()) {
                title.setEnabled(false);
                content.setFocusable(false);
                save.setText("OK");
                title.setTextColor(Color.BLACK);
                content.setTextColor(Color.BLACK);
                mTagView.setEnabled(false);
                mGridView.setLongClickable(false);
                buttonLayout.setVisibility(View.GONE);
                line.setVisibility(View.GONE);
                tags.setVisibility(View.GONE);
            }

            mUriList = mPictureDao.getAllPicture(mNote.getmId());
            mGridViewAdapter.setList(mUriList);
            mTagsList = mDataBaseUtil.showAllTags(mNote.getmId(), mTagsList);
            List<String> tagContentList = new ArrayList<>();
            List<Tag> tagList = mTagDao.findAllTag();
            for (Tag tag : tagList) {
                tagContentList.add(tag.getmTag());
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tagContentList);
            mTagView.setAdapter(arrayAdapter);
            mGridView.setAdapter(mGridViewAdapter);
        }

        if (mGridView.isLongClickable()) {
            mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    final DecorAdapter decorAdapter = new DecorAdapter(NoteActivity.this);
                    decorAdapter.setAdapter(mGridViewAdapter);
                    mGridView.requestFocus();
                    mGridView.setFocusable(true);
                    decorAdapter.setListener(new DecorAdapter.Listener() {
                        @Override
                        public void onRemoveClicked(int position) {
                            mCurrentRemovePictures.add(mUriList.get(position));
                            mUriList.remove(position);
                            mGridViewAdapter.setList(mUriList);
                        }
                    });
                    mGridView.setAdapter(decorAdapter);
                    return true;
                }
            });
        }

        mGridView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mGridView.setAdapter(mGridViewAdapter);
                    mGridViewAdapter.notifyDataSetChanged();
                }
            }
        });

        final Intent intent = new Intent(this, MainActivity.class);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditMode()) {
                    mNote.setmTitle(title.getText().toString().trim());
                    mNote.setmContent(content.getText().toString().trim());
                    mNote.setmModifiedDate(dateFormat.format(currentLocalTime));
                    mNoteDao.updateNote(mNote);
                    if (!mForecastDao.ifExistForecastForNote(mNote.getmId()) && mForecast != null) {
                        mForecastDao.createForecast(mForecast, mNote.getmId());
                    }
                    startActivity(createIntentForReviseNote(NoteActivity.this, mNote.getmId(), true));
                    finish();
                } else {
                    mNote = mNoteDao.createNote(title.getText().toString().trim(),
                            content.getText().toString().trim(), mUserDao.findUserByLogin
                                    (mCurrentUser.get(SessionManager.KEY_LOGIN)),
                            dateFormat.format(currentLocalTime),
                            dateFormat.format(currentLocalTime), false, mLocation);
                    startActivity(intent);
                    finish();
                }

                mDataBaseUtil.setmNote(mNote);
                if (!mCurrentAddTagsList.isEmpty()) {
                    mDataBaseUtil.createTagInDb(mCurrentAddTagsList);
                }

                if (!mCurrentRemoveTagsList.isEmpty()) {
                    mDataBaseUtil.deleteTagFromDb(mCurrentRemoveTagsList);
                }

                if (!mCurrentAddPictures.isEmpty()) {
                    for (int i = 0; i < mCurrentAddPictures.size(); i++) {
                        mPictureDao.createPicture(mCurrentAddPictures.get(i), mNote.getmId());
                    }
                }

                if (!mCurrentRemovePictures.isEmpty()) {
                    for (int i = 0; i < mCurrentRemovePictures.size(); i++) {
                        Long id = mPictureDao.findPictureByNoteId(mNote.getmId());
                        mPictureDao.deletePicture(id, mNote.getmId());
                    }
                }


                mNoteDao.close();
                mPictureDao.close();
                mTagDao.close();
                mTagOfNotesDao.close();
                mCoordinateDao.close();
                mUserDao.close();
            }
        });

        if (cancel != null) {
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mNoteDao.close();
                    mPictureDao.close();
                    mTagDao.close();
                    mTagOfNotesDao.close();
                    mCoordinateDao.close();
                    mUserDao.close();
                    if (!isEditMode()) {
                        startActivity(new Intent(NoteActivity.this, MainActivity.class));
                        finish();
                    } else {
                        startActivity(createIntentForReviseNote(NoteActivity.this, mNote.getmId(), true));
                        finish();
                    }

                }
            });


        }

        mTagView.setListener(new TagView.Listener() {
            @Override
            public void onAddingTag(String tag) {
                Tag item = new Tag(tag);
                if (ifExistTag(item, mCurrentRemoveTagsList)) {
                    mCurrentRemoveTagsList.remove(item);
                    mTagView.addTag(tag);
                    return;
                }

                if (ifExistTag(item, mCurrentAddTagsList)) {
                    alertForTag(item);
                    return;
                }

                if (!ifExistTag(item, mTagsList)) {
                    mCurrentAddTagsList.add(item);
                    mTagView.addTag(tag);
                } else {
                    alertForTag(item);
                }
            }

            @Override
            public void onRemovingTag(String tag) {
                mTagView.removeTag(tag);
                mCurrentRemoveTagsList.add(new Tag(tag));
            }
        });

    }

    private void alertForTag(Tag tag) {
        AlertDialog.Builder builder = new AlertDialog.Builder(NoteActivity.this);
        AlertDialog alertDialog = builder.setMessage("Tag " + tag.getmTag() + " already exists").
                setPositiveButton("OK", null).create();
        alertDialog.show();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isEditMode()) {
            menu.findItem(R.id.note_menu_edit).setVisible(false);
            if (mNote.getmLocation() != 0) {
                menu.findItem(R.id.note_menu_location).setTitle("Edit location");
            } else {
                menu.findItem(R.id.note_menu_location).setTitle("Add location");
            }
            if (mNote.getmPolicyStatus()) {
                menu.findItem(R.id.note_menu_check_shared).setChecked(true);
            }

            if (mForecastLayout.getVisibility() == View.VISIBLE) {
                menu.findItem(R.id.note_menu_weather).setTitle("Update weather");
            }
        }

        if (isReviseMode()) {
            menu.findItem(R.id.note_menu_location).setVisible(false);
            menu.findItem(R.id.note_menu_check_shared).setVisible(false).
                    setChecked(mNote.getmPolicyStatus());
            menu.findItem(R.id.note_menu_picture).setVisible(false);
            menu.findItem(R.id.note_menu_weather).setVisible(false);
            menu.findItem(R.id.note_menu_edit).setVisible(true);

            if (mNote.getmUserId() != mLoggedUser.getId()) {
                menu.findItem(R.id.note_menu_location).setVisible(false);
                menu.findItem(R.id.note_menu_check_shared).setVisible(false).
                        setChecked(mNote.getmPolicyStatus());
                menu.findItem(R.id.note_menu_edit).setVisible(false);
                menu.findItem(R.id.note_menu_weather).setVisible(false);
                menu.findItem(R.id.note_menu_picture).setVisible(false);
            }
        }

        if (!isReviseMode() && !isEditMode()) {
            menu.findItem(R.id.note_menu_location).setVisible(true);
            menu.findItem(R.id.note_menu_edit).setVisible(false);
            menu.findItem(R.id.note_menu_check_shared).setVisible(false);
            menu.findItem(R.id.note_menu_picture).setVisible(false);
            menu.findItem(R.id.note_menu_weather).setVisible(false);
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
        Location location;
        switch (item.getItemId()) {
            case R.id.note_menu_check_shared:
                if (!item.isChecked()) {
                    aBuilder.setMessage("Make this note public?").setCancelable(true)
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mNote.setmPolicyStatus(true);
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
                    mNote.setmPolicyStatus(false);
                    item.setChecked(false);
                    return true;
                }
            case R.id.note_menu_location:
                location = findingLocation(this);
                if (location != null) {
                    LatLng latLng = findingCoordinate(location);
                    String city = findingCityName();
                    locationDialog(this, latLng, city);
                    return true;
                } else {
                    Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
                    return true;
                }

            case R.id.note_menu_picture:
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
                startActivityForResult(chooserIntent, UPLOAD_KEY);
                return true;
            case R.id.note_menu_edit:
                startActivity(createIntentForEditNote(this, mNote.getmId()));
                item.setVisible(false);
                finish();
                return true;

            case R.id.note_menu_weather:
                location = findingLocation(this);
                if (location != null) {
                    try {
                        ForecastFetcher forecastFetcher = new ForecastFetcher();
                        if(!forecastFetcher.isNetworkAvailable(this)) {
                            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                            return  true;
                        } else {
                            Coordinate coordinate = new Coordinate(location.getLatitude(), location.getLongitude());
                            mForecast = forecastFetcher.execute(coordinate).get();
                            weatherDialog(this, mForecast.getmCity());
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            case android.R.id.home:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isEditMode() {
        return mNote != null;
    }

    private boolean isReviseMode() {
        return mReviseMode;
    }

    private void initParamsFromIntent(Intent intent) {
        mNote = null;

        if (intent != null && intent.getExtras() != null && intent.getExtras().containsKey(NOTE_ID_KEY) &&
                intent.getExtras().containsKey(REVISE_MODE)) {
            mNote = mNoteDao.getNoteById(intent.getLongExtra(NOTE_ID_KEY, -1));
            mReviseMode = true;
            return;
        }

        if (intent != null && intent.getExtras() != null && intent.getExtras().containsKey(NOTE_ID_KEY)) {
            mNote = mNoteDao.getNoteById(intent.getLongExtra(NOTE_ID_KEY, -1));
        }

    }

    public static Intent createIntentForAddNote(Context context) {
        Intent intent = new Intent(context, NoteActivity.class);
        return intent;
    }

    public static Intent createIntentForReviseNote(Context context, Long id, boolean reviseMode) {
        Intent intent = new Intent(context, NoteActivity.class);
        intent.putExtra(NOTE_ID_KEY, id);
        intent.putExtra(REVISE_MODE, reviseMode);
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
        Location location = mLocationManager.getLastKnownLocation(provider);
        return location;
    }

    private LatLng findingCoordinate(Location location) {
        if (location != null) {
            if (mNote != null) {
                mLocation = mCoordinateDao.createCoordinate(location.getLatitude(),
                        location.getLongitude());

                mNote.setmLocation(mLocation);
            } else {
                mLocation = mCoordinateDao.createCoordinate(location.getLatitude(),
                        location.getLongitude());
            }
        }
        try {
            Coordinate coordinate;
            if (mNote != null) {
                coordinate = mCoordinateDao.getCoordinateById(mNote.getmLocation());
            } else {
                coordinate = mCoordinateDao.getCoordinateById(mLocation);
            }
            return new LatLng(coordinate.getLatitude(), coordinate.getLongtitude());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }

    private String findingCityName() {
        Coordinate coordinate;
        if (mNote != null) {
            coordinate = mCoordinateDao.getCoordinateById(mNote.getmLocation());
        } else {
            coordinate = mCoordinateDao.getCoordinateById(mLocation);
        }
        Geocoder geocoder = new Geocoder(NoteActivity.this, Locale.getDefault());
        List<Address> address = null;
        try {
            address = geocoder.getFromLocation(coordinate.getLatitude(), coordinate.getLongtitude(), 100);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (address.get(0).getLocality() != null) {
            return address.get(0).getLocality();
        } else {
            return "Can't find your current location";
        }
    }

    private void locationDialog(Context context, final LatLng latLng, String city) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.layout_dialog);

        final DialogHolder dialogHolder = new DialogHolder();

        dialogHolder.title = (TextView) dialog.findViewById(R.id.dialog_text);
        dialogHolder.ok = (Button) dialog.findViewById(R.id.dialog_ok);
        dialogHolder.cancel = (Button) dialog.findViewById(R.id.dialog_cancel);
        dialogHolder.refresh = (ImageView) dialog.findViewById(R.id.dialog_refresh);
        dialogHolder.remove = (Button) dialog.findViewById(R.id.dialog_remove);

        dialogHolder.title.setText(city);
        dialogHolder.ok.setText("OK");
        dialogHolder.cancel.setText("CANCEL");
        dialogHolder.remove.setText("Remove");
        dialogHolder.refresh.setVisibility(View.VISIBLE);

        dialog.show();

        String dialogTitle = "Current location";
        dialog.setTitle(dialogTitle);

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
                locationDialog(NoteActivity.this, latLng, city);
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

    private void weatherDialog(Context context, String city) {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.layout_dialog);

        final DialogHolder dialogHolder = new DialogHolder();

        dialogHolder.title = (TextView) dialog.findViewById(R.id.dialog_text);
        dialogHolder.ok = (Button) dialog.findViewById(R.id.dialog_ok);
        dialogHolder.cancel = (Button) dialog.findViewById(R.id.dialog_cancel);
        dialogHolder.refresh = (ImageView) dialog.findViewById(R.id.dialog_refresh);
        dialogHolder.remove = (Button) dialog.findViewById(R.id.dialog_remove);

        if(mForecastLayout.getVisibility()==View.VISIBLE) {
            dialogHolder.refresh.setVisibility(View.VISIBLE);
        }

        dialogHolder.title.setText(city);
        dialogHolder.ok.setText("OK");
        dialogHolder.cancel.setText("CANCEL");
        dialogHolder.remove.setText("Remove");

        dialog.show();

        String dialogTitle = "Current location";
        dialog.setTitle(dialogTitle);



        dialogHolder.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location location = findingLocation(NoteActivity.this);
                if (location != null) {
                    Coordinate coordinate = new Coordinate(location.getLatitude(), location.getLongitude());
                    ForecastFetcher forecastFetcher = new ForecastFetcher();
                    try {
                        mForecast = forecastFetcher.execute(coordinate).get();
                        if (mForecast != null) {
                            showForecastLayout(mForecastLayout);
                        }
                    } catch (ExecutionException ex) {
                        ex.printStackTrace();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                dialog.dismiss();
            }
        });

        dialogHolder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialogHolder.refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location location = findingLocation(NoteActivity.this);
                Coordinate coordinate = new Coordinate(location.getLatitude(), location.getLongitude());
                ForecastFetcher forecastFetcher = new ForecastFetcher();
                try {
                    mForecast = forecastFetcher.execute(coordinate).get();
                    if (mForecast != null && mForecastDao.ifExistForecastForNote(mNote.getmId())) {
                        mForecast = mForecastDao.updateWeather(mForecast, mNote.getmId());
                        showForecastLayout(mForecastLayout);
                        weatherDialog(NoteActivity.this, mForecast.getmCity());
                    } else {
                        Toast.makeText(NoteActivity.this, "Please, save changes", Toast.LENGTH_SHORT).show();
                    }
                } catch (ExecutionException ex) {
                    ex.printStackTrace();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                dialog.dismiss();
            }
        });

        dialogHolder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mForecastLayout.getVisibility() != View.VISIBLE) {
                    Toast.makeText(NoteActivity.this, "Nothing to remove", Toast.LENGTH_SHORT).show();
                } else {
                    mForecastLayout.setVisibility(View.GONE);
                    mForecastDao.deleteForecast(mNote.getmId());
                    mForecast = null;
                    mMenu.findItem(R.id.note_menu_weather).setTitle("Add weather");
                    dialog.dismiss();
                }
            }
        });
    }


    static class DialogHolder {
        TextView title;
        Button ok;
        Button cancel;
        ImageView refresh;
        Button remove;

        public DialogHolder() {
        }
    }

    public boolean ifExistTag(Tag tag, List<Tag> list) {
        for (Tag itemTag : list) {
            if (tag.equals(itemTag)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPLOAD_KEY && data != null && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();

            try {
                InputStream iStream = getContentResolver().openInputStream(selectedImage);
                byte[] inputData = getBytes(iStream);
                mUriList.add(inputData);
                mCurrentAddPictures.add(inputData);
                mGridViewAdapter.setList(mUriList);
                mGridViewAdapter.notifyDataSetChanged();
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }

    class GridViewAdapter extends BaseAdapter {
        LayoutInflater mLayoutInflater;
        List<byte[]> uriList;

        public GridViewAdapter(Context context) {
            mLayoutInflater = LayoutInflater.from(context);
        }

        public void setList(List<byte[]> list) {
            uriList = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return uriList.size();
        }

        @Override
        public byte[] getItem(int position) {
            return uriList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return uriList.indexOf(uriList.get(position));
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.note_picture, parent, false);
                imageView = (ImageView) convertView.findViewById(R.id.note_picture);
                convertView.setTag(imageView);
            } else {
                imageView = (ImageView) convertView.getTag();

            }
            byte[] uri = getItem(position);
            imageView.setImageBitmap(getImage(uri));
            imageView.setAdjustViewBounds(true);
            return convertView;
        }

    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    @Override
    public void onBackPressed() {
        if (isEditMode()) {
            startActivity(createIntentForReviseNote(NoteActivity.this, mNote.getmId(), true));
        } else {
            super.onBackPressed();
        }
    }

    private void showForecastLayout(View view) {
        view.setVisibility(View.VISIBLE);
        TextView city = (TextView) view.findViewById(R.id.forecast_city);
        TextView temp = (TextView) view.findViewById(R.id.forecast_temp);
        TextView description = (TextView) view.findViewById(R.id.forecast_description);
        TextView wind = (TextView) view.findViewById(R.id.forecast_wind);
        TextView rain = (TextView) view.findViewById(R.id.forecast_rain);
        ImageView iconWeather = (ImageView) view.findViewById(R.id.forecast_icon);

        city.setText(mForecast.getmCity() + ", " + mForecast.getmOtherInform().getmCountry());
        temp.setText(String.valueOf(mForecast.getmMain().getmTemperature()) + " C°");
        iconWeather.setImageBitmap(getImage(mForecast.getIcon()));
        description.setText(mForecast.getmWeather().get(0).getmDescription());
        wind.setText(String.valueOf("w: " + mForecast.getmWind().getSpeed()) + " m/s");
        if (mForecast.getmRain() != null) {
            rain.setText(String.valueOf("r: " + mForecast.getmRain().getmCount()) + " mm");
        } else {
            Rain rainWeather = new Rain();
            rainWeather.setmCount(0.0);
            mForecast.setmRain(rainWeather);
            rain.setText("r: " + String.valueOf(mForecast.getmRain().getmCount()));
        }
    }


}