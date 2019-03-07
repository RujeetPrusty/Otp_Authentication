package com.example.userauthentication;



import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;


public class VerifyNumber extends AppCompatActivity {

    private String VerificationId;
//    private Button SignIn;
    private FirebaseAuth FbAuth;
    private DatabaseReference DbRef;
    private ProgressBar progressBar;
    private EditText editText;
    public String PhoneKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_number);

        FbAuth = FirebaseAuth.getInstance();


        editText = findViewById(R.id.editTextCode);
        progressBar = findViewById(R.id.progressbar);

        String Number = getIntent().getStringExtra("Number");
        sendVerificationCode(Number);

        findViewById(R.id.signIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = editText.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6){

                    editText.setError("Enter OTP..");
                    editText.requestFocus();
                    return;
                }
                verifyCode(code);

            }
        });
    }

    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VerificationId,code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        FbAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user = task.getResult().getUser();
                     PhoneKey = user.getPhoneNumber();
                    Log.d("Firebase user : ",PhoneKey);

                    DbRef = FirebaseDatabase.getInstance().getReference().child("user");
                    if (DbRef != null) {
                        Log.e("DBREFRENCE:","is not null");
                    }
                    else{
                        Log.e("DBREFRENCE","is null");
                    }
                    DbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.e("datasnapshot",dataSnapshot.getChildren().toString());
                            if (dataSnapshot.hasChild(FbAuth.getCurrentUser().getPhoneNumber())==true)
                            {
                                Intent intent = new Intent(VerifyNumber.this,Profile.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            else {
                                Intent intent = new Intent(VerifyNumber.this,Register.class);
                                intent.putExtra("PhoneKey",PhoneKey);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

//                    Intent intent = new Intent(VerifyNumber.this,Profile.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);

                }
                else {
                    Toast.makeText(VerifyNumber.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void sendVerificationCode(String number){
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            VerificationId=s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null){
                editText.setText(code);
                verifyCode(code);
            }

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerifyNumber.this,e.getMessage(),Toast.LENGTH_LONG).show();

        }
    };
}
