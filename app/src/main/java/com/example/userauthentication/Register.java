package com.example.userauthentication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Register extends AppCompatActivity {
    private EditText Name;
    private Button Register;
    private DatabaseReference DbRef;
    private FirebaseAuth FbAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FbAuth=FirebaseAuth.getInstance();
        DbRef= FirebaseDatabase.getInstance().getReference().child("user");

        Name=findViewById(R.id.name);
        Register=findViewById(R.id.register);

        Intent intent = getIntent();
        final String PhonKEY=intent.getStringExtra("PhoneKey");
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=Name.getText().toString();

                DbRef.child(PhonKEY).child("Name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"data stored",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Register.this,Profile.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"data not stored",Toast.LENGTH_LONG).show();
                        }

                    }
                });

            }
        });
    }
}
