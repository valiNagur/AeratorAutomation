package com.example.epicsrenewal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends Activity {

    String iphone,iurl;
    boolean flag=true;
    ImageView menu;
    ListView menubar;
    String[] menuitems={"Profile","Today","Statistics"};
    ArrayAdapter adpt1;
    TextView name,phone,key,aerators,address,url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        name=findViewById(R.id.name);
        phone=findViewById(R.id.phone);
        key=findViewById(R.id.key);
        aerators=findViewById(R.id.aerators);
        address=findViewById(R.id.address);
        url=findViewById(R.id.url);

        menu=findViewById(R.id.menu);
        menubar=findViewById(R.id.menubar);
        adpt1=new ArrayAdapter(Profile.this, android.R.layout.simple_list_item_1,menuitems);
        menubar.setAdapter(adpt1);

        Intent i=getIntent();
        iphone=i.getStringExtra("phone");
        iurl=i.getStringExtra("url");

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("root/clients/c"+iphone);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name.setText(snapshot.child("name").getValue().toString());
                phone.setText(snapshot.child("phone").getValue().toString());
                aerators.setText(snapshot.child("aerators").getValue().toString());
                key.setText(snapshot.child("key").getValue().toString());
                address.setText(snapshot.child("address").getValue().toString());
                url.setText(snapshot.child("url").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        menubar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item=adapterView.getItemAtPosition(i).toString();
                menubar.setVisibility(View.INVISIBLE);
                switch (item.toLowerCase()){
                    case "today":
                        Intent profile=new Intent(Profile.this,MainActivity.class);
                        profile.putExtra("phone",iphone);
                        profile.putExtra("url",iurl);
                        startActivity(profile);
                        flag=!flag;break;
                    case "statistics":
                        Intent statistics=new Intent(Profile.this,Statistics.class);
                        statistics.putExtra("url",iurl);
                        statistics.putExtra("phone",iphone);
                        startActivity(statistics);
                        flag=!flag;break;
                    case "profile": flag=!flag;break;
                }
            }
        });
    }


    public void showmenu(View view) {
        if (flag)
            menubar.setVisibility(View.VISIBLE);
        else
            menubar.setVisibility(View.INVISIBLE);
        flag = !flag;
    }
}