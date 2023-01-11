package com.example.moodleclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Script;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {


    private EditText edtEmail, edtPassword, edtName;
    private ProgressDialog progressDialog;
    private Button btnSignUp;
    boolean valid = true;
    RadioButton isTeacherBox, isStudentBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        edtEmail=findViewById(R.id.edt_email);
        edtPassword=findViewById(R.id.edt_password);
        edtName=findViewById(R.id.edt_name);
        btnSignUp=findViewById(R.id.btn_sign_up);
        isStudentBox=findViewById(R.id.isStudent);
        isTeacherBox=findViewById(R.id.isTeacher);
        //checkbox logic
        isStudentBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    isTeacherBox.setChecked(false);
                }
            }
        });
        isTeacherBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    isStudentBox.setChecked(false);
                }
            }
        });
        initUi();
        initListener();

    }
    private void initUi(){
        edtEmail=findViewById(R.id.edt_email);
        edtPassword=findViewById(R.id.edt_password);
        btnSignUp=findViewById(R.id.btn_sign_up);
        progressDialog = new ProgressDialog(this);
    }
    private void initListener(){
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSignUp();
            }
        });
    }

    public boolean checkField(EditText textField){
        if(textField.getText().toString().isEmpty()){
            textField.setError("error");
            valid = false;
        }else{
            valid= true;
        }
        return valid;
    }
    private void onClickSignUp() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore fstore =FirebaseFirestore.getInstance();
        String strEmail =  edtEmail.getText().toString().trim();
        String strPassword = edtPassword.getText().toString().trim();
        String strName = edtName.getText().toString().trim();
        //checkbox
        progressDialog.show();
        if(!(isTeacherBox.isChecked() || isStudentBox.isChecked())){
            Toast.makeText(SignUpActivity.this,"Select Account Type", Toast.LENGTH_SHORT).show();
            return;
        }
        if(checkField(edtEmail)&&checkField(edtPassword)&&checkField(edtName)==true) {
            auth.createUserWithEmailAndPassword(strEmail, strPassword)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {

                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user = auth.getCurrentUser();
                            Toast.makeText(SignUpActivity.this,"Account Created",Toast.LENGTH_SHORT).show();
                            DocumentReference df = fstore.collection("Users").document(user.getUid());
                            Map<String,Object> userInfo = new HashMap<>();
                            userInfo.put("FullName",edtName.getText().toString());
                            userInfo.put("UserEmail",edtEmail.getText().toString());
                            //specify type of account
                            if(isStudentBox.isChecked()) {
                                userInfo.put("isStudent", "1");
                            }
                            if(isTeacherBox.isChecked()){
                                userInfo.put("isTeacher","1");
                            }
                            df.set(userInfo);
                            if(isStudentBox.isChecked()) {
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                finishAffinity();
                            }
                            if(isTeacherBox.isChecked()){
                                if(isTeacherBox.isChecked()){
                                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                    finishAffinity();
                                }
                            }
                            progressDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        ;
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignUpActivity.this,"Failed to Sign Up",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                    }

                    );

        }
        }


}

