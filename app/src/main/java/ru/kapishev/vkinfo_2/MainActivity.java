package ru.kapishev.vkinfo_2;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import static ru.kapishev.vkinfo_2.utils.NetworkUtils.generateURL;
import static ru.kapishev.vkinfo_2.utils.NetworkUtils.getResponseFromURL;

public class MainActivity extends AppCompatActivity {
    private EditText searchField;
    private Button searchButton;
    private TextView result;
    private TextView errorMessage;
    private ProgressBar progressBar;
    private TextView httpLink;
    private String jsonResponse;

    private void showResultTextView() {
        result.setVisibility(View.VISIBLE);
        errorMessage.setVisibility(View.INVISIBLE);
    }

    private void showErrorTextView() {
        result.setVisibility(View.INVISIBLE);
        errorMessage.setVisibility(View.VISIBLE);
    }

    class VKQueryTask extends AsyncTask<URL, Void, String>{
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... urls) {
            String response = null;
            try {
                response = getResponseFromURL(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }
        @Override
        protected void onPostExecute(String response){
            String city = null;
            String street = null;
            String building = null;
            String modelCounter = null;

            if(response != null && response != "") {
                try {
                    jsonResponse = response;
                    JSONObject jsonResponse = new JSONObject(response);
                    city = jsonResponse.getString("city");
                    street = jsonResponse.getString("street");
                    building = jsonResponse.getString("building");
                    modelCounter = jsonResponse.getJSONObject("modelCounter").getString("type_name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String resultingString = "??????????: " + city + "\n" + "??????????: " + street + "\n" + "??????: " + building + "\n" + "????????????: " + modelCounter;
                result.setText(resultingString);
                showResultTextView();
            } else {
                showErrorTextView();
            }
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchField = findViewById(R.id.et_search_field);
        searchButton = findViewById(R.id.b_search_vk);
        result = findViewById(R.id.tv_result);
        errorMessage = findViewById(R.id.tv_error_message);
        progressBar = findViewById(R.id.pb_loading_indicator);
        httpLink = findViewById(R.id.tv_http_link);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                URL generatedURL = generateURL(searchField.getText().toString());
                httpLink.setText(jsonResponse);
                new VKQueryTask().execute(generatedURL);
            }
        };
        searchButton.setOnClickListener(onClickListener);
    }
}