package com.example.achypur.notepadapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.achypur.notepadapp.dbhelper.DataBaseHelper;
import com.example.achypur.notepadapp.entities.ForecastEntity;
import com.example.achypur.notepadapp.jsonobjects.Forecast;
import com.example.achypur.notepadapp.jsonobjects.Main;
import com.example.achypur.notepadapp.jsonobjects.OtherInform;
import com.example.achypur.notepadapp.jsonobjects.Rain;
import com.example.achypur.notepadapp.jsonobjects.Weather;
import com.example.achypur.notepadapp.jsonobjects.Wind;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by achypur on 19.05.2016.
 */
public class ForecastDao {

    private SQLiteDatabase mSqLiteDatabase;
    private DataBaseHelper mDataBaseHelper;
    private String[] mColumns = {DataBaseHelper.KEY_ID, DataBaseHelper.KEY_NOTE_ID,
            DataBaseHelper.KEY_ICON, DataBaseHelper.KEY_DESCRIPTION, DataBaseHelper.KEY_COUNTRY,
            DataBaseHelper.KEY_CITY, DataBaseHelper.KEY_TEMPERATURE, DataBaseHelper.KEY_RAIN, DataBaseHelper.KEY_WIND};

    public ForecastDao(Context context) {
        mDataBaseHelper = new DataBaseHelper(context);
    }

    public void open() throws SQLException {
        mSqLiteDatabase = mDataBaseHelper.getWritableDatabase();
    }

    public void close() {
        mDataBaseHelper.close();
    }

    private ContentValues getForecastContentValues(ForecastEntity entity) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.KEY_NOTE_ID, entity.getmNoteId());
        contentValues.put(DataBaseHelper.KEY_ICON, entity.getmIcon());
        contentValues.put(DataBaseHelper.KEY_DESCRIPTION, entity.getmDescription());
        contentValues.put(DataBaseHelper.KEY_COUNTRY, entity.getmCountry());
        contentValues.put(DataBaseHelper.KEY_CITY, entity.getmCity());
        contentValues.put(DataBaseHelper.KEY_TEMPERATURE, entity.getmTemp());
        contentValues.put(DataBaseHelper.KEY_RAIN, entity.getmRain());
        contentValues.put(DataBaseHelper.KEY_WIND, entity.getmWind());
        return contentValues;
    }

    public ForecastEntity createForecast(Forecast forecast, Long noteId) {
        if(forecast.getmRain() == null) {
            Rain rain = new Rain();
            rain.setmCount(0.0);
            forecast.setmRain(rain);
        }
        ForecastEntity forecastEntity = new ForecastEntity(noteId, forecast.getIcon(),
                forecast.getmWeather().get(0).getmDescription(), forecast.getmOtherInform().getmCountry(),
                forecast.getmCity(), forecast.getmMain().getmTemperature(), forecast.getmRain().getmCount(),
                forecast.getmWind().getSpeed());

        Long id = mSqLiteDatabase.insert(DataBaseHelper.TABLE_FORECAST, null, getForecastContentValues(forecastEntity));

        forecastEntity.setmId(id);

        return forecastEntity;
    }

    private ForecastEntity cursorToForecast(Cursor cursor) {
        return new ForecastEntity(
                cursor.getLong(0),
                cursor.getLong(1),
                cursor.getBlob(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getDouble(6),
                cursor.getDouble(7),
                cursor.getDouble(8)
        );
    }

    public boolean ifExistForecastForNote(Long noteId) {
        Cursor cursor = mSqLiteDatabase.rawQuery("select * from " + DataBaseHelper.TABLE_FORECAST + " where note_id = ? ", new String[]{noteId.toString()});
        if (cursor.getCount() <= 0) {
            return false;
        } else {
            return true;
        }
    }

    private ForecastEntity findForecastByNoteId(Long noteId) {
        Cursor cursor = mSqLiteDatabase.rawQuery("select * from " + DataBaseHelper.TABLE_FORECAST + " where note_id = ? ", new String[]{noteId.toString()});
        cursor.moveToFirst();

        return cursorToForecast(cursor);
    }

    public Forecast forecastEntityToForecast(Long noteId) {
        ForecastEntity entity = findForecastByNoteId(noteId);
        Main main = new Main();
        main.setmTemperature(entity.getmTemp());
        OtherInform inform = new OtherInform();
        inform.setmCountry(entity.getmCountry());
        Rain rain = new Rain();
        rain.setmCount(entity.getmRain());
        Wind wind = new Wind();
        wind.setSpeed(entity.getmWind());
        List<Weather> weatherList = new ArrayList<>();
        Weather wether = new Weather();
        wether.setmDescription(entity.getmDescription());
        weatherList.add(wether);
        Forecast forecast = new Forecast();
        forecast.setIcon(entity.getmIcon());
        forecast.setmCity(entity.getmCity());
        forecast.setmMain(main);
        forecast.setmOtherInform(inform);
        forecast.setmRain(rain);
        forecast.setmWind(wind);
        forecast.setmWeather(weatherList);
        return forecast;
    }

    public Forecast updateWeather(Forecast forecast, Long noteId) {
      if(forecast.getmRain() == null) {
          Rain rain = new Rain();
          rain.setmCount(0.0);
          forecast.setmRain(rain);
      }
        ForecastEntity forecastEntity = new ForecastEntity(noteId, forecast.getIcon(),
                forecast.getmWeather().get(0).getmDescription(), forecast.getmOtherInform().getmCountry(),
                forecast.getmCity(), forecast.getmMain().getmTemperature(), forecast.getmRain().getmCount(), forecast.getmWind().getSpeed());

        mSqLiteDatabase.update(DataBaseHelper.TABLE_FORECAST, getForecastContentValues(forecastEntity),
                " id= " + findForecastByNoteId(noteId).getmId(), null);

        return forecastEntityToForecast(noteId);
    }

    public void deleteForecast(Long noteId) {
        mSqLiteDatabase.delete(DataBaseHelper.TABLE_FORECAST, DataBaseHelper.KEY_NOTE_ID + " = " + noteId, null);
    }
}
