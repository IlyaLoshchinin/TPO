/**
 * @author Loshchinin Illia S13579
 */

package zad1;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Service {
    private final Weather weather;
    private double rateCustom;
    private String rateCustomName;
    private JSONObject nbpRateObj;

    public Service(String country) {
        weather = new Weather(country);
    }


    String getWeather(String city) {

        String jsonData;

        jsonData = getContentFromUrl("http://api.openweathermap.org/data/2.5/weather?q=" + city + "," + weather.getCountry() + "&APPID=ef684ca343ba36cb7f605a7c518f7f70");
        assert jsonData != null;

        if (!jsonData.contains("\"cod\":\"404\""))
            weather.initialize(jsonData);


        return jsonData;
    }

    Double getRateFor(String currencyCode) {

        double rates = 0;
        String jsonData = getContentFromUrl("http://api.fixer.io/latest?base=" + weather.getCountryCurrencyCode());

        if (jsonData != null) {
            JSONObject jsonObject = new JSONObject(jsonData);
            rates = jsonObject.getJSONObject("rates").optDouble(currencyCode);
            rateCustom = rates;
            rateCustomName = currencyCode;
        }
        return rates;
    }

    Double getNBPRate() {//zwraca kurs złotego wobec waluty danego kraju

        String xmlNBP_A = getContentFromUrl("http://www.nbp.pl/kursy/xml/a057z170322.xml");
        String xmlNBP_B = getContentFromUrl("http://www.nbp.pl/kursy/xml/b012z170322.xml");

        if (!xmlNBP_A.isEmpty() && !xmlNBP_B.isEmpty()) {
            JSONObject jsonObjectA = XML.toJSONObject(xmlNBP_A);
            JSONObject jsonObjectB = XML.toJSONObject(xmlNBP_B);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(jsonObjectA);
            jsonArray.put(jsonObjectB);
            double rateToPLN = 0.0;

//          JSONObject obj = jsonObjectA.optJSONObject(weather.getCountryCurrencyCode());
            JSONObject objCurrency = findCurrencyRateNBP(jsonArray);
            if (objCurrency != null) {//found currency
                rateToPLN = objCurrency.optDouble("kurs_sredni");
                nbpRateObj = objCurrency;
            }
            return rateToPLN;
        }
        return null;
    }

    private JSONObject findCurrencyRateNBP(JSONArray jsonArray) {
        if (weather.getCountryCurrencyCode().equals("PLN")) {
            return new JSONObject("{\"kurs_sredni\":\"1\",\"kod_waluty\":\"PLN\",\"nazwa_waluty\":\"polski zloty\",\"przelicznik\":1}");
        }
        JSONObject objTmp;
        for (int i = 0; i < jsonArray.length(); i++) {
            for (int j = 0; j < jsonArray.getJSONObject(i).optJSONObject("tabela_kursow").getJSONArray("pozycja").length(); j++) {
                objTmp = jsonArray.getJSONObject(i).optJSONObject("tabela_kursow").getJSONArray("pozycja").getJSONObject(j);
                if (objTmp.optString("kod_waluty").equals(weather.getCountryCurrencyCode())) {
                    return objTmp;
                }
            }
        }
        return null;
    }

    static public String getContentFromUrl(String url) {
        StringBuffer textData = null;
        URLConnection urlConnection;
        URL myURL;

        try {
            myURL = new URL(url);
            urlConnection = myURL.openConnection();
            urlConnection.connect();
            BufferedReader rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            textData = new StringBuffer();
            String line;

            while ((line = rd.readLine()) != null) {
                textData.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return textData != null ? textData.toString() : "";
    }

    public Weather getWeather() {
        return weather;
    }

    public JSONObject getNbpRateObj() {
        return nbpRateObj;
    }

    public double getRateCustom() {
        return rateCustom;
    }

    public String getRateCustomName() {
        return rateCustomName;
    }
}


