package com.example.epicsrenewal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login extends Activity {

    EditText phone;
    Button login;
    Intent loginintent;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        phone=findViewById(R.id.phone);
        login=findViewById(R.id.login);
        login.setClickable(true);
        loginintent=new Intent(this,MainActivity.class);
        if(!NetworkCheck.isNetworkAvailable(this))
        {
            startActivity(new Intent(this,Retry.class));
            login.setClickable(false);
        }
    }

    public void login(View view) {

        String ph=phone.getText().toString();
        if(ph.isEmpty())
            Toast.makeText(this, "Enter registered number", Toast.LENGTH_SHORT).show();
        else
        {
            DatabaseReference reference= FirebaseDatabase.getInstance().getReference("root/clients/c"+ph);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        loginintent.putExtra("phone",snapshot.child("phone").getValue().toString());
                        loginintent.putExtra("url",snapshot.child("url").getValue().toString());
                        startActivity(loginintent);
                    }
                    else
                        Toast.makeText(login.this, "No user Found", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        phone.setText("");
    }
}
class  NetworkCheck
{
    static boolean isNetworkAvailable(Context context)
    {
        ConnectivityManager connectivityManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager!=null) {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            return info!=null &&info.isConnected();
        }
        return false;
    }
}