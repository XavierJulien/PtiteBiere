package com.example.ptitebiere;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class OptionsActivity extends AppCompatActivity {

    ArrayList<String> phoneNumbers;
    private ListView mListView;
    ArrayAdapter<String> adapter;
    EditText edit_message;
    String message = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        phoneNumbers = getIntent().getStringArrayListExtra("Numeros");
        mListView = (ListView) findViewById(R.id.listView);
        adapter = new ArrayAdapter<String>(OptionsActivity.this,
                android.R.layout.simple_list_item_1, phoneNumbers);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = phoneNumbers.get(position);
                phoneNumbers.remove(item);
                adapter.notifyDataSetChanged();
                Toast.makeText(OptionsActivity.this, "Tu as supprimé : "+item, Toast.LENGTH_SHORT).show();
            }
        });
        edit_message = findViewById(R.id.editable_text);
        message = getIntent().getStringExtra("Message");
        edit_message.setText(getIntent().getStringExtra("Message"));
        edit_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //rien à faire
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                message = s.toString();
            }
        });
    }

    public void addUser(View v){
        Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        pickContact.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(pickContact, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri contactData = data.getData();
        String num = "";
        String contact = "";
        Cursor c = getContentResolver().query(contactData, null, null, null, null);
        if (c.moveToFirst()) {
            int phoneIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            contact = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Nickname.DISPLAY_NAME));
            num = c.getString(phoneIndex);
        }
        if(num.matches("^(0|\\+33)[ ]?[1-9]([-. ]?[0-9]{2}){4}$")){
            if(!phoneNumbers.contains(contact+";"+num)){
                phoneNumbers.add(contact+" : "+num);
            }
            adapter.notifyDataSetChanged();
            Toast.makeText(OptionsActivity.this, "Numéro ajouté : "+contact, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(OptionsActivity.this, "Numéro inconnu : "+num, Toast.LENGTH_SHORT).show();
        }
    }

    public void goMainActivity(View v){
        Intent intent = getIntent();
        intent.putStringArrayListExtra("Numeros",phoneNumbers);
        intent.putExtra("Message",message);
        Log.d("Message", message);
        setResult(RESULT_OK, intent);
        finish();
    }

}
