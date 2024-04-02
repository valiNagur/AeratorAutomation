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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Statistics extends Activity {

    String selection[]={"5","10","20","30"};
    boolean flag=true;
    ImageView menu;
    ListView menubar;
    String[] menuitems={"Profile","Today","Statistics"};
    ArrayAdapter adpt1;
    Spinner select;
    ChartView ChartView1,ChartView2,ChartView3;
    static  String s,url,phone;
    ArrayAdapter adpt;
    TextView message;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        menu=findViewById(R.id.menu);
        menubar=findViewById(R.id.menubar);
        adpt1=new ArrayAdapter(Statistics.this, android.R.layout.simple_list_item_1,menuitems);
        menubar.setAdapter(adpt1);

        Intent i=getIntent();
        url=i.getStringExtra("url");
        phone=i.getStringExtra("phone");
        System.out.println(url);

        menubar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item=adapterView.getItemAtPosition(i).toString();
                menubar.setVisibility(View.INVISIBLE);
                switch (item.toLowerCase()){
                    case "profile":
                        Intent profile=new Intent(Statistics.this,Profile.class);
                        profile.putExtra("phone",phone);
                        profile.putExtra("url",url);
                        startActivity(profile);
                        flag=!flag;break;
                    case "today":
                        Intent statistics=new Intent(Statistics.this,MainActivity.class);
                        statistics.putExtra("url",url);
                        statistics.putExtra("phone",phone);
                        startActivity(statistics);
                        flag=!flag;break;
                    case "statistics": flag=!flag;break;
                }
            }
        });


        select=findViewById(R.id.readings);
        adpt=new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,selection);
        select.setAdapter(adpt);
        select.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                s=select.getSelectedItem().toString();
                System.out.println(s);
                new FetchDataFromThingSpeak().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(1000,700);
        params.setMargins(10,10,20,20);
        ChartView1 = findViewById(R.id.chartView1);
        ChartView2 = findViewById(R.id.chartView2);
        ChartView3 = findViewById(R.id.chartView3);
        ChartView1.setLayoutParams(params);
        ChartView2.setLayoutParams(params);
        ChartView3.setLayoutParams(params);

        message=findViewById(R.id.message);
    }

    class FetchDataFromThingSpeak extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String apiUrl = "https://api.thingspeak.com/channels/2268286/feeds.json?results="+Statistics.s;
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
                    ArrayList<Float> ar1=new ArrayList<>(),ar2=new ArrayList<>(),ar3=new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(result);
                    String jsonArrayStr=jsonObject.getString("feeds");
                    JSONArray jsonArray = new JSONArray(jsonArrayStr);
                    float j=0,k=0,l=0,m=0,n=0,p=0;
                    float doavg=0,phavg=0,tempavg=0;
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject object=jsonArray.getJSONObject(i);
                        doavg+=Float.parseFloat(object.getString("field1"));
                        phavg+=Float.parseFloat(object.getString("field2"));
                        tempavg+=Float.parseFloat(object.getString("field3"));
                        ar1.add(Float.parseFloat(object.getString("field1"))+(j+=(7))*10);
                        ar1.add(Float.parseFloat(object.getString("field1"))+(k+=2)*10);
                        ar1.add(Float.parseFloat(object.getString("field1"))+(j+=(7))*10);
                        ar1.add(Float.parseFloat(object.getString("field1"))+(k+=2)*10);
                        ar2.add(Float.parseFloat(object.getString("field2"))+(l+=(7))*10);
                        ar2.add(Float.parseFloat(object.getString("field2"))+(m+=2)*10);
                        ar2.add(Float.parseFloat(object.getString("field2"))+(l+=(7))*10);
                        ar2.add(Float.parseFloat(object.getString("field2"))+(m+=2)*10);
                        ar3.add(Float.parseFloat(object.getString("field3"))+(n+=(5))*10);
                        ar3.add(Float.parseFloat(object.getString("field3"))+(p+=3)*10);
                        ar3.add(Float.parseFloat(object.getString("field3"))+(n+=(5))*10);
                        ar3.add(Float.parseFloat(object.getString("field3"))+(p+=3)*10);
                    }
                    ChartView1.setLineDataPoints(ar1);
                    ChartView2.setLineDataPoints(ar2);
                    ChartView3.setLineDataPoints(ar3);
                    if(doavg/jsonArray.length()>=7.5&&tempavg/jsonArray.length()>=26&&phavg/jsonArray.length()>6.5)
                            message.setText("Note: Your pond is well-aerated, ensuring optimal conditions for fish and other aquatic organisms.");
                    else
                        message.setText("Note: Your pond has lower dissolved oxygen levels. Consider increasing aeration to support fish and prevent potential issues.");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void showmenu(View view) {
        if (flag)
            menubar.setVisibility(View.VISIBLE);
        else
            menubar.setVisibility(View.INVISIBLE);
        flag = !flag;
    }
}
