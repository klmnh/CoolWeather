package com.adt.lenovo.coolweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.adt.lenovo.coolweather.util.MyApplication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Lenovo on 2016/12/23 023.
 */

public class CoolWeatherOpenHelper extends SQLiteOpenHelper {

    private static CoolWeatherOpenHelper coolWeatherOpenHelper = null;

    public static final String CoolWeatherDBName = "CoolWeather";
    public static final Integer version = 1;

    private static SQLiteDatabase db = null;

    private String Create_Province = "CREATE TABLE Province(" +
            "id Integer Primary Key autoincrement," +
            "provinceName TEXT," +
            "provinceCode TEXT" +
            ")";
    private String Create_City = "CREATE TABLE City(" +
            "id Integer Primary Key AUTOINCREMENT," +
            "cityName TEXT," +
            "cityCode TEXT," +
            "provinceId Integer" +
            ")";
    private String Create_County = "CREATE TABLE County(" +
            "id Integer Primary Key AUTOINCREMENT," +
            "countyName TEXT," +
            "countyCode TEXT," +
            "cityId Integer" +
            ")";

    public synchronized static CoolWeatherOpenHelper GetInstance() {
        if (coolWeatherOpenHelper == null) {
            coolWeatherOpenHelper = new CoolWeatherOpenHelper(MyApplication.GetGlobeContent(), CoolWeatherDBName, null, version);
            db = coolWeatherOpenHelper.getWritableDatabase();
        }
        return coolWeatherOpenHelper;
    }

    private CoolWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Create_Province);
        db.execSQL(Create_City);
        db.execSQL(Create_County);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        switch (oldVersion) {
            case 1:
                break;
        }
    }

    public List<Province> LoadProvince() {

        List<Province> provinceList = new ArrayList<Province>();
        Cursor cursor = db.query("Province", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("provinceName")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("provinceCode")));
                provinceList.add(province);
            } while (cursor.moveToNext());
        }

        return provinceList;
    }

    public void SaveProvince(final Province province) {

        if (province != null) {
            ContentValues values = new ContentValues();
            values.put("provinceName", province.getProvinceName());
            values.put("provinceCode", province.getProvinceCode());

            db.insert("Province", null, values);
        }
    }

    public List<City> LoadCity(String provinceId) {

        List<City> cityList = new ArrayList<City>();
        Cursor cursor = db.query("City", null, "provinceId=? ", new String[]{provinceId}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("cityName")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("cityCode")));
                city.setProvinceId(cursor.getInt(cursor.getColumnIndex("provinceId")));
                cityList.add(city);
            } while (cursor.moveToNext());
        }

        return cityList;
    }

    public void SaveCity(City city) {

        if (city != null) {
            ContentValues values = new ContentValues();
            values.put("cityName", city.getCityName());
            values.put("cityCode", city.getCityCode());
            values.put("provinceId", city.getProvinceId());

            db.insert("City", null, values);
        }
    }

    public List<County> LoadCounty(String cityId) {

        List<County> countyList = new ArrayList<County>();
        Cursor cursor = db.query("County", null, "cityId=?", new String[]{cityId}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("countyName")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("countyCode")));
                county.setCityId(cursor.getInt(cursor.getColumnIndex("cityId")));
                countyList.add(county);
            } while (cursor.moveToNext());
        }

        return countyList;
    }

    public void SaveCounty(County county) {

        if (county != null) {
            ContentValues values = new ContentValues();
            values.put("countyName", county.getCountyName());
            values.put("countyCode", county.getCountyCode());
            values.put("cityId", county.getCityId());

            db.insert("County", null, values);
        }
    }
}
