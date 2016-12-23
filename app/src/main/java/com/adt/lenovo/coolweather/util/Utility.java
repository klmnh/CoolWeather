package com.adt.lenovo.coolweather.util;

import android.text.TextUtils;
import android.util.Log;

import com.adt.lenovo.coolweather.db.City;
import com.adt.lenovo.coolweather.db.CoolWeatherOpenHelper;
import com.adt.lenovo.coolweather.db.County;
import com.adt.lenovo.coolweather.db.Province;
import com.adt.lenovo.coolweather.model.CityInfo;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.StringReader;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by Lenovo on 2016/12/23 023.
 */

public class Utility {

    public synchronized static boolean HandleProvinceResponse(CoolWeatherOpenHelper coolWeatherOpenHelper, String strResponse) {
        if (!TextUtils.isEmpty(strResponse)) {

            List<CityInfo> list = null;
            try {
                list = ParseXMlString(strResponse);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (list != null && list.size() > 0) {
                for (CityInfo cityInfo : list) {
                    Province province = new Province();
                    province.setProvinceCode(cityInfo.getPyName());
                    province.setProvinceName(cityInfo.getCityName());
                    coolWeatherOpenHelper.SaveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    public synchronized static boolean HandleCityResponse(CoolWeatherOpenHelper coolWeatherOpenHelper, String strResponse, Integer provinceId) {
        if (!TextUtils.isEmpty(strResponse)) {

            List<CityInfo> list = null;
            try {
                list = ParseXMlString(strResponse);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (list != null && list.size() > 0) {

                for (CityInfo cityInfo : list) {
                    City city = new City();
                    city.setCityCode(cityInfo.getPyName());
                    city.setCityName(cityInfo.getCityName());
                    city.setProvinceId(provinceId);
                    coolWeatherOpenHelper.SaveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    public synchronized static boolean HandleCountyResponse(CoolWeatherOpenHelper coolWeatherOpenHelper, String strResponse, Integer cityId) {
        if (!TextUtils.isEmpty(strResponse)) {

            List<CityInfo> list = null;
            try {
                list = ParseXMlString(strResponse);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (list != null && list.size() > 0) {

                for (CityInfo cityInfo : list) {
                    County county = new County();
                    county.setCountyCode(cityInfo.getPyName());
                    county.setCountyName(cityInfo.getCityName());
                    county.setCityId(cityId);
                    coolWeatherOpenHelper.SaveCounty(county);
                }
                return true;
            }
        }
        return false;
    }

    public static List<CityInfo> ParseXMlString(String strXML) throws IOException {
        final List<CityInfo> cityInfolist = new ArrayList<CityInfo>();

        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        try {
            SAXParser saxparser = saxParserFactory.newSAXParser();
            XMLReader xmlReader = saxparser.getXMLReader();
            xmlReader.setContentHandler(new DefaultHandler() {
                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    if (localName.equals("city")) {
                        CityInfo cityInfo = new CityInfo();
                        boolean hasquName = true;
                        if (attributes.getIndex(uri, "quName") < 0) {
                            hasquName = false;
                        }
                        for (int i = 0; i < attributes.getLength(); i++) {
                            switch (attributes.getLocalName(i)) {
                                case "quName":
                                    cityInfo.setCityName(attributes.getValue(i));
                                    break;
                                case "cityname":
                                    if (hasquName) {
                                        cityInfo.setCenterName(attributes.getValue(i));
                                    } else {
                                        cityInfo.setCityName(attributes.getValue(i));
                                    }
                                    break;
                                case "centername":
                                    cityInfo.setCenterName(attributes.getValue(i));
                                    break;
                                case "pyName":
                                    cityInfo.setPyName(attributes.getValue(i));
                                    break;
                                case "cityX":
                                    cityInfo.setCityX(attributes.getValue(i));
                                    break;
                                case "cityY":
                                    cityInfo.setCityY(attributes.getValue(i));
                                    break;
                                case "fontColor":
                                    cityInfo.setFontColor(attributes.getValue(i));
                                    break;
                                case "state1":
                                    cityInfo.setState1(attributes.getValue(i));
                                    break;
                                case "state2":
                                    cityInfo.setState2(attributes.getValue(i));
                                    break;
                                case "stateDetailed":
                                    cityInfo.setStateDetailed(attributes.getValue(i));
                                    break;
                                case "tem1":
                                    cityInfo.setTem1(attributes.getValue(i));
                                    break;
                                case "tem2":
                                    cityInfo.setTem2(attributes.getValue(i));
                                    break;
                                case "temNow":
                                    cityInfo.setTemNow(attributes.getValue(i));
                                    break;
                                case "windState":
                                    cityInfo.setWindState(attributes.getValue(i));
                                    break;
                                case "windDir":
                                    cityInfo.setWindDir(attributes.getValue(i));
                                    break;
                                case "windPower":
                                    cityInfo.setWindPower(attributes.getValue(i));
                                    break;
                                case "humidity":
                                    cityInfo.setHumidity(attributes.getValue(i));
                                    break;
                                case "time":
                                    cityInfo.setTime(attributes.getValue(i));
                                    break;
                                case "url":
                                    cityInfo.setUrl(attributes.getValue(i));
                                    break;
                            }
                        }
                        cityInfolist.add(cityInfo);
                    }
                }
            });
            xmlReader.parse(new InputSource(new StringReader(strXML)));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        return cityInfolist;
    }

}
