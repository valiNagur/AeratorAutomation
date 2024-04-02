package com.example.epicsrenewal;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {

    EditText temp_level;
    boolean flag=true;
    String url,phone;
    ProgressBar do_progress,temp_progress,ph_progress;
    ImageView menu;
    ListView menubar;
    EditText do_level;
    EditText ph_level;
    Switch aerator;
    String[] menuitems={"Profile","Today","Statistics"};
    ArrayAdapter adpt;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i=getIntent();
        phone=i.getStringExtra("phone");
        url=i.getStringExtra("url");

        menu=findViewById(R.id.menu);
        menubar=findViewById(R.id.menubar);
        adpt=new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1,menuitems);
        menubar.setAdapter(adpt);

        do_level=findViewById(R.id.do_level);
        do_progress=findViewById(R.id.do_progress);
        ph_level=findViewById(R.id.ph_level);
        ph_progress=findViewById(R.id.ph_progress);
        temp_level=findViewById(R.id.temp_level);
        temp_progress=findViewById(R.id.temp_progress);

        aerator=findViewById(R.id.aerator_status);


        new FetchDataFromThingSpeak().execute();


        menubar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item=adapterView.getItemAtPosition(i).toString();
                menubar.setVisibility(View.INVISIBLE);
                switch (item.toLowerCase()){
                    case "profile":
                        Intent profile=new Intent(MainActivity.this,Profile.class);
                        profile.putExtra("phone",phone);
                        profile.putExtra("url",url);
                        startActivity(profile);
                        flag=!flag;break;
                    case "statistics":
                        Intent statistics=new Intent(MainActivity.this,Statistics.class);
                        statistics.putExtra("url",url);
                        statistics.putExtra("phone",phone);
                        startActivity(statistics);
                        flag=!flag;break;
                    case "today": flag=!flag;break;
                }
            }
        });
    }

    public void showmenu(View view) {
        if(flag)
            menubar.setVisibility(View.VISIBLE);
        else
            menubar.setVisibility(View.INVISIBLE);
        flag=!flag;
    }



    class FetchDataFromThingSpeak extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String apiUrl = url+"1";
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {

                    response.append(line);
                }

                reader.close();
                return response.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String jsonArrayStr=jsonObject.getString("feeds");
                    JSONArray jsonArray = new JSONArray(jsonArrayStr);
                    JSONObject jsonObject1=jsonArray.getJSONObject(0);
                    do_level.setText(jsonObject1.getString("field1")+" mg/L");
                    //do_level.
                    do_progress.setProgress((int) Float.parseFloat(jsonObject1.getString("field1"))*100/30);
                    temp_level.setText(jsonObject1.getString("field2")+"°C" );
                    temp_progress.setProgress((int) Float.parseFloat(jsonObject1.getString("field2"))*100/65);
                    ph_level.setText(jsonObject1.getString("field3"));
                    ph_progress.setProgress((int) Float.parseFloat(jsonObject1.getString("field3"))*100/12);
                    aerator.setChecked(jsonObject1.getString("field4").equalsIgnoreCase("1")?true:false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                System.out.println("null");
            }
        }
    }
}