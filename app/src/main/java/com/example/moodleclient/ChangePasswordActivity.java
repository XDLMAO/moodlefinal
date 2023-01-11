package com.example.moodleclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText edtOldPass;
    private EditText edtNewPass;
    private Button btnChangePass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initUi();
        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickChangePassword();
            }
        });
    }
    private void initUi(){
        edtOldPass=findViewById(R.id.edt_opassword);
        edtNewPass=findViewById(R.id.edt_npassword);
        btnChangePass=findViewById(R.id.btn_change_password);

    }
    private void onClickChangePassword() {
        String strNewPassword = edtNewPass.getText().toString().trim();
        String strOldPassword = edtOldPass.getText().toString().trim();
        updatePassword(strOldPassword,strNewPassword);
    }

    private void updatePassword(String oldPassword, String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //re-authenticate before change password
        AuthCredential authCredential = EmailAuthProvider.
                getCredential(user.getEmail(),oldPassword);
        user.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //successfully authenticated, update

                user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //password updated

                        Toast.makeText(ChangePasswordActivity.this,"Password Updated",Toast.LENGTH_SHORT).show();
                        edtNewPass.setText("");
                        edtOldPass.setText("");

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChangePasswordActivity.this,"Update Failed"+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //failed
                Toast.makeText(ChangePasswordActivity.this,"Failed authenticated You enter wrong password"+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}