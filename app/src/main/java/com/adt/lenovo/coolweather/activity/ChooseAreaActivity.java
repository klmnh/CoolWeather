package com.adt.lenovo.coolweather.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.adt.lenovo.coolweather.R;
import com.adt.lenovo.coolweather.db.City;
import com.adt.lenovo.coolweather.db.CoolWeatherOpenHelper;
import com.adt.lenovo.coolweather.db.County;
import com.adt.lenovo.coolweather.db.Province;
import com.adt.lenovo.coolweather.util.HttpUtil;
import com.adt.lenovo.coolweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2016/12/23 023.
 */

public class ChooseAreaActivity extends AppCompatActivity {

    public static final int levelProvince = 0;
    public static final int levelCity = 1;
    public static final int levelCounty = 2;

    private ProgressDialog progressDialog = null;
    private TextView txtTitle = null;
    private ListView lvArea = null;
    private ArrayAdapter<String> adapter = null;
    private List<String> list = new ArrayList<String>();
    private CoolWeatherOpenHelper coolWeatherOpenHelper = null;

    private List<Province> provinceList = null;
    private List<City> cityList = null;
    private List<County> countyList = null;
    private Province selectedProvince = null;
    private City selectedCity = null;
    private County selectedCounty = null;
    private int selectedlevel = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_area);

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        lvArea = (ListView) findViewById(R.id.lvArea);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        lvArea.setAdapter(adapter);
        coolWeatherOpenHelper = CoolWeatherOpenHelper.GetInstance();
        lvArea.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (selectedlevel) {
                    case levelProvince:
                        selectedProvince = provinceList.get(position);
                        QueryCity();
                        break;
                    case levelCity:
                        selectedCity = cityList.get(position);
                        QueryCounty();
                        break;
                }
            }
        });
        QueryProvince();
    }

    @Override
    public void onBackPressed() {
        if (selectedlevel == levelCounty) {
            QueryCity();
        } else if (selectedlevel == levelCity) {
            QueryProvince();
        } else {
            finish();
        }
    }

    private void QueryProvince() {

        provinceList = coolWeatherOpenHelper.LoadProvince();
        if (provinceList.size() > 0) {
            list.clear();
            for (Province province : provinceList) {
                list.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            lvArea.setSelection(0);
            txtTitle.setText("中国");
            selectedlevel = levelProvince;
        } else {
            QueryFromServer(null, "Province");
        }
    }

    private void QueryCity() {

        cityList = coolWeatherOpenHelper.LoadCity(String.valueOf(selectedProvince.getId()));
        if (cityList.size() > 0) {
            list.clear();
            for (City city : cityList) {
                list.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            lvArea.setSelection(0);
            txtTitle.setText(selectedProvince.getProvinceName());
            selectedlevel = levelCity;
        } else {
            QueryFromServer(selectedProvince.getProvinceCode(), "City");
        }
    }

    private void QueryCounty() {

        countyList = coolWeatherOpenHelper.LoadCounty(String.valueOf(selectedCity.getId()));
        if (countyList.size() > 0) {
            list.clear();
            for (County county : countyList) {
                list.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            lvArea.setSelection(0);
            txtTitle.setText(selectedCity.getCityName());
            selectedlevel = levelCounty;
        } else {
            QueryFromServer(selectedCity.getCityCode(), "County");
        }
    }

    private void QueryFromServer(final String code, final String type) {

        String address = "";
        if (!TextUtils.isEmpty(code)) {
            address = "http://flash.weather.com.cn/wmaps/xml/" + code + ".xml";
        } else {
            address = "http://flash.weather.com.cn/wmaps/xml/china.xml";
        }
        ShowProgressDialog();
        HttpUtil.SendHttpRequest(address, new HttpUtil.HttpCallbackListener() {
            @Override
            public void OnFinish(String response) {
                boolean result = false;
                if ("Province".equals(type)) {
                    result = Utility.HandleProvinceResponse(coolWeatherOpenHelper, response);
                } else if ("City".equals(type)) {
                    result = Utility.HandleCityResponse(coolWeatherOpenHelper, response, selectedProvince.getId());
                } else if ("County".equals(type)) {
                    result = Utility.HandleCountyResponse(coolWeatherOpenHelper, response, selectedCity.getId());
                }
                if (result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CloseProgressDialog();
                            if ("Province".equals(type)) {
                                QueryProvince();
                            } else if ("City".equals(type)) {
                                QueryCity();
                            } else if ("County".equals(type)) {
                                QueryCounty();
                            }

                        }
                    });
                }
            }

            @Override
            public void OnError(Exception ee) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CloseProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void ShowProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void CloseProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}
