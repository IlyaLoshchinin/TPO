package zad1;

import org.json.JSONObject;

import java.util.Date;


public class Weather {
    private String city;
    private final String country;
    private String countryCode;
    private String countryCurrencyCode;
    private String simpleDesc;
    private String advanceDesc;
    private double temperature;
    private String atmosphericPressure;
    private String date;

    public Weather(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    private void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public String getSimpleDesc() {
        return simpleDesc;
    }

    private void setSimpleDesc(String simpleDesc) {
        this.simpleDesc = simpleDesc;
    }

    public String getAdvanceDesc() {
        return advanceDesc;
    }

    private void setAdvanceDesc(String advanceDesc) {
        this.advanceDesc = advanceDesc;
    }

    public double getTemperature() {
        return temperature;
    }

    private void setTemperature(double temperature) {
        this.temperature = temperature - 273.15;
    }

    public String getAtmosphericPressure() {
        return atmosphericPressure;
    }

    private void setAtmosphericPressure(String atmosphericPressure) {
        this.atmosphericPressure = atmosphericPressure;
    }

    public String getDate() {
        return date;
    }

    private void setDate(long dateUnix) {
        Date time = new Date(dateUnix * 1000);
        this.date = time.toString().substring(0, 16);
    }

    public String getCountryCode() {
        return countryCode;
    }

    private void setCountryCode(String countryCode) {
        this.countryCode = countryCode.toUpperCase();
    }

    private void setCountryCurrencyCode() {
        if (!countryCode.isEmpty() && countryCurrencyCode == null) {

            String jsonCurrency = Service.getContentFromUrl("http://country.io/currency.json");
            if (jsonCurrency != null) {
                JSONObject jsonObject = new JSONObject(jsonCurrency);
                countryCurrencyCode = jsonObject.getString(countryCode);
            }
        }
    }

    public String getCountryCurrencyCode() {
        return countryCurrencyCode;
    }

    void initialize(String jsonDataString) {
        if (jsonDataString != null) {
            JSONObject jsonObject = new JSONObject(jsonDataString);
            setCity(jsonObject.getString("name"));
            setCountryCode(jsonObject.getJSONObject("sys").optString("country"));
            setAdvanceDesc(jsonObject.getJSONArray("weather").getJSONObject(0).optString("description"));
            setSimpleDesc(jsonObject.getJSONArray("weather").getJSONObject(0).optString("main"));
            setAtmosphericPressure(jsonObject.getJSONObject("main").optString("pressure"));
            setTemperature(jsonObject.getJSONObject("main").optDouble("temp"));
            setDate(jsonObject.getLong("dt"));
            setCountryCurrencyCode();
        }
    }

}
