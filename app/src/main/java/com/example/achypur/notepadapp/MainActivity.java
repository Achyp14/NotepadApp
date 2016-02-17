package com.example.achypur.notepadapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    final String ATTRIBUTE_NAME_TEXT = "title";
    final String ATTRIBUTE_NAME_DESCRIPTION = "description";
    final String ATTRIBUTE_NAME_CHECKBOX = "checkbox";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Map<String,Object>> noteList = new ArrayList<Map<String,Object>>();

        String []titles = {"title1","title2","title3"};
        String []descriptions = {"description1","description2","description3"};
        Boolean []cheked = {true,false,true};

        Map<String,Object> map;
        for(int i = 0; i < titles.length; i++) {
            map = new HashMap<String, Object>();
            map.put(ATTRIBUTE_NAME_TEXT,titles[i]);
            map.put(ATTRIBUTE_NAME_DESCRIPTION,descriptions[i]);
            map.put(ATTRIBUTE_NAME_CHECKBOX,cheked[i]);
            noteList.add(map);
        }

        String []from = {ATTRIBUTE_NAME_TEXT,ATTRIBUTE_NAME_DESCRIPTION,ATTRIBUTE_NAME_CHECKBOX};
        int []to = {R.id.item_title,R.id.item_description,R.id.item_check};

        SimpleAdapter simpleAdapter = new SimpleAdapter(this,noteList,R.layout.item,from,to);
        ListView listView = (ListView) findViewById(R.id.note_list);
        listView.setAdapter(simpleAdapter);

    }

}
