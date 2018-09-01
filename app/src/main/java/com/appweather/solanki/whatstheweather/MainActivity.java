package com.appweather.solanki.whatstheweather;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView resultTextView;

    public void getWeather(View view){
        try {
            DownloadTask task = new DownloadTask();
            String encodedCityName = URLEncoder.encode(editText.getText().toString(),"UTF-8");
            task.execute("https://openweathermap.org/data/2.5/weather?q="+ editText.getText().toString() +"&appid=b6907d289e10d714a6e88b30761fae22");

            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(editText.getWindowToken(),0);
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editText);
        resultTextView = (TextView) findViewById(R.id.result);

    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);

                String weatherInfo = jsonObject.getString("weather");

                JSONArray arr = new JSONArray(weatherInfo);
                String message = "";

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject object = arr.getJSONObject(i);
                    String main = object.getString("main");
                    String description = object.getString("description");

                    if (!main.equals("") && !description.equals("")){
                        message += main + ": " + description + "\r\n" ;
                    }
                }

                String temp = jsonObject.getJSONObject("main").get("temp").toString();
                String pressure = jsonObject.getJSONObject("main").get("pressure").toString();
                String humidity = jsonObject.getJSONObject("main").get("humidity").toString();
                String visibility = jsonObject.get("visibility").toString();
                String speed = jsonObject.getJSONObject("wind").get("speed").toString();

                if (!message.equals("")){
                    message += message + "\r\ntemperature: " + temp + "\r\npressure: " + pressure + "\r\nhumidity: " + humidity + "\r\nvisibility: " + visibility + "\r\nwind speed: " + speed;
                    resultTextView.setText(message);
                } else {
                    Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
            }
        }
    }
}