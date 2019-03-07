package com.example.userauthentication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private Spinner spinner;
    private EditText editText;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = (Spinner)findViewById(R.id.spinner);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
                CountryData.countryNames));

        editText = (EditText)findViewById(R.id.phonenumber);

        button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String CountryCode= CountryData.countryAreaCodes[spinner.getSelectedItemPosition()];

                String PhoneNumber = editText.getText().toString().trim();

                if (PhoneNumber.isEmpty() || PhoneNumber.length()<10){
                    editText.setError("valid phone number is required!");
                    editText.requestFocus();
                    return;
                }
                String Number = "+" + CountryCode + PhoneNumber;
                Intent intent = new Intent(MainActivity.this,VerifyNumber.class);
                intent.putExtra("Number",Number);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser()!=null){
            Intent intent = new Intent(MainActivity.this,Profile.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
