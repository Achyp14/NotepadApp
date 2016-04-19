package com.example.achypur.notepadapp.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.util.Pair;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.achypur.notepadapp.CustomView.ProfilePicture;
import com.example.achypur.notepadapp.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class BaseActivity extends AppCompatActivity {


    private ListView mLeftListView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Bitmap mBitmap;
    ProfilePicture mProfilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(R.layout.activity_base);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mLeftListView = (ListView) findViewById(R.id.left_drawer);
        List<String> itemList = new ArrayList<>();
        ViewGroup headerGroup = (ViewGroup) View.inflate(this, R.layout.hamburger_menu_header, null);
        mProfilePicture = (ProfilePicture) headerGroup.findViewById(R.id.hamburger_header_profile_picture);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.people);
        mProfilePicture.setImageBitmap(mBitmap);
        headerGroup.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 700));
        mLeftListView.addHeaderView(headerGroup, null, true);
        List<Pair<String, View.OnClickListener>> navBarItemList = new ArrayList<>();

        navBarItemList.add(new Pair<String, View.OnClickListener>("Log out", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Achyp", "86|BaseActivity::onClick: ");
            }
        }));

        NavigationDrawerAdapter navigationDrawerAdapter = new NavigationDrawerAdapter(this);
        navigationDrawerAdapter.setList(navBarItemList);
        mLeftListView.setAdapter(navigationDrawerAdapter);

        mDrawerToggle = new ActionBarDrawerToggle((Activity) this, mDrawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(layoutResID, null, false);
        FrameLayout contentFrame = (FrameLayout) findViewById(R.id.content_frame);
        contentFrame.addView(contentView);

    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
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


        public void setList(List<Pair<String, View.OnClickListener>>  list) {
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

            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.hamburger_item_layout, parent, false);
                textView = (TextView) convertView.findViewById(R.id.hamburger_item_text);

                convertView.setTag(textView);
            } else {
                textView = (TextView) convertView.getTag();
            }
            Pair<String, View.OnClickListener> item = getItem(position);
            textView.setText(item.first);
            textView.setTag(item.second);
            return convertView;
        }
    }
}
