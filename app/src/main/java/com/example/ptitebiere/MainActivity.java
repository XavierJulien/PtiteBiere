package com.example.ptitebiere;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.Manifest.permission.SEND_SMS;

public class MainActivity extends AppCompatActivity {

    Button send;
    ArrayList<String> phoneNumbers;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        send = findViewById(R.id.button);

        this.phoneNumbers = Preferences.getArrayPrefs("phoneNumbers",this);
        this.message = Preferences.getPrefs("Message",this).equals("notfound") ? "P\'tite Bière ?" : Preferences.getPrefs("Message",this);
        send.setEnabled(false);
        requestPermission();
        if(phoneNumbers.isEmpty()){
            TextView tv = findViewById(R.id.missing);
            tv.setText("Ajoute des contacts en appuyant sur le bouton \"...\"");
        }else{
            send.setEnabled(true);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("RESUME","resume "+phoneNumbers);
        if(!phoneNumbers.isEmpty()){
            TextView tv = findViewById(R.id.missing);
            tv.setText(message);
            send.setEnabled(true);
        }else{
            TextView tv = findViewById(R.id.missing);
            tv.setText("Ajoute des personnes à ta liste de contacts en appuyant sur \"...\"");
        }
    }

    public void sendMessage(View v){
        SmsManager smsManager = SmsManager.getDefault();
        for(String phoneNumber : phoneNumbers){
            String[] contact_num = phoneNumber.split(" : ");
            smsManager.sendTextMessage(contact_num[1],null,message,null,null);
        }
        Toast.makeText(this,"Message Sent!",Toast.LENGTH_SHORT).show();
    }

    public void goOptionsActivity(View v){
        Intent intent = new Intent(this,OptionsActivity.class);
        intent.putStringArrayListExtra("Numeros",phoneNumbers);
        intent.putExtra("Message",message);
        startActivityForResult(intent,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100){
            if(resultCode == RESULT_OK){
                phoneNumbers = data.getStringArrayListExtra("Numeros");
                message = data.getStringExtra("Message");
                Preferences.setArrayPrefs("phoneNumbers",phoneNumbers,this);
                Preferences.setPrefs("Message",data.getStringExtra("Message"),this);
            }
        }
    }

    private void requestPermission() {
        if(!(ActivityCompat.checkSelfPermission(this,SEND_SMS) == PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(this,new String[]{SEND_SMS}, 100);
        }
    }



    @Override
    protected void onStop() {
        super.onStop();

    }
}
