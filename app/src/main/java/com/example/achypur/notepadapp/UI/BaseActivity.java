package com.example.achypur.notepadapp.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.util.Pair;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.achypur.notepadapp.Application.NoteApplication;
import com.example.achypur.notepadapp.CustomView.PictureConvertor;
import com.example.achypur.notepadapp.CustomView.ProfilePicture;
import com.example.achypur.notepadapp.Entities.User;
import com.example.achypur.notepadapp.Managers.AccountManager;
import com.example.achypur.notepadapp.R;
import java.util.ArrayList;
import java.util.List;


public class BaseActivity extends AppCompatActivity {


    private ListView mLeftListView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ProfilePicture mProfilePicture;
    private User mLoggedUser;
    TextView mLogin;
    TextView mEmail;
    AccountManager mAccountManager;
    PictureConvertor mPictureConvertor;
    NoteApplication noteApplication = new NoteApplication();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(R.layout.activity_base);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        NoteActivity n = new NoteActivity();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        mAccountManager = noteApplication.getsAccountManager();
        mAccountManager.createUserRepository();
        mAccountManager.initLoginSession();
        mPictureConvertor = PictureConvertor.getInstance();

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("userId")) {
            Long id = (Long) extras.get("userId");
            mLoggedUser = mAccountManager.findUserById(id);
        } else {
            mLoggedUser = mAccountManager.findUserById(mAccountManager.findUserId(mAccountManager.retrieveLogin()));
        }

        mLeftListView = (ListView) findViewById(R.id.left_drawer);
        ViewGroup headerGroup = (ViewGroup) View.inflate(this, R.layout.hamburger_menu_header, null);
        headerGroup.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.WRAP_CONTENT));
        mProfilePicture = (ProfilePicture) headerGroup.findViewById(R.id.hamburger_header_profile_picture);
        mLogin = (TextView) headerGroup.findViewById(R.id.hamburger_header_login);
        mEmail = (TextView) headerGroup.findViewById(R.id.hamburger_header_email);


        mLogin.setText(mLoggedUser.getLogin());
        if (!mLoggedUser.getEmail().equals("")) {
            mEmail.setVisibility(View.VISIBLE);
            mEmail.setText(mLoggedUser.getEmail());
        } else {
            mEmail.setVisibility(View.GONE);
        }

        if (mLoggedUser.getImage() != null) {
            mProfilePicture.setImageBitmap(mPictureConvertor.byteToBitMap(mLoggedUser.getImage()));
        } else {
            mProfilePicture.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.people));
        }


        mLeftListView.addHeaderView(headerGroup, null, true);
        List<Pair<String, View.OnClickListener>> navBarItemList = new ArrayList<>();
        final Intent intent = new Intent(this, ProfileActivity.class);
        navBarItemList.add(new Pair<String, View.OnClickListener>("Profile configuration", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("userId", mLoggedUser.getId());
                mDrawerLayout.closeDrawer(mLeftListView);
                startActivity(intent);
            }
        }));

        navBarItemList.add(new Pair<String, View.OnClickListener>("Log out", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAccountManager.logoutUser();
                finish();
            }
        }));


        NavigationDrawerAdapter navigationDrawerAdapter = new NavigationDrawerAdapter(this);
        navigationDrawerAdapter.setList(navBarItemList);
        mLeftListView.setAdapter(navigationDrawerAdapter);

        mDrawerToggle = new ActionBarDrawerToggle((Activity) this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(layoutResID, null, false);
        FrameLayout contentFrame = (FrameLayout) findViewById(R.id.content_frame);
        contentFrame.addView(contentView);

    }

    @Override
    protected void onResume() {
        super.onResume();

        mLoggedUser = mAccountManager.findUserById(mLoggedUser.getId());
        if(mLoggedUser.getEmail() != null){
            mEmail.setText(mLoggedUser.getEmail());
        }
        mLogin.setText(mLoggedUser.getLogin());
        if(mLoggedUser.getImage() != null){
            mProfilePicture.setImageBitmap(mPictureConvertor.byteToBitMap(mLoggedUser.getImage()));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    class NavigationDrawerAdapter extends BaseAdapter {

        List<Pair<String, View.OnClickListener>> mItemList;
        LayoutInflater mLayoutInflater;


        NavigationDrawerAdapter(Context context) {
            mLayoutInflater = LayoutInflater.from(context);
        }


        public void setList(List<Pair<String, View.OnClickListener>> list) {
            mItemList = list;
        }

        @Override

        public int getCount() {
            return mItemList.size();
        }

        @Override
        public Pair<String, View.OnClickListener> getItem(int position) {
            return mItemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            Pair<String, View.OnClickListener> item = getItem(position);
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.hamburger_item_layout, parent, false);
                textView = (TextView) convertView.findViewById(R.id.hamburger_item_text);
                textView.setTag(item.second);
                convertView.setTag(textView);
            } else {
                textView = (TextView) convertView.getTag();
            }

            textView.setText(item.first);
            textView.setOnClickListener(item.second);

            return convertView;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}
