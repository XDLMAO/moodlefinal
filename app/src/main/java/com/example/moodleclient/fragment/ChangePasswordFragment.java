package com.example.moodleclient.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.moodleclient.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordFragment extends Fragment {

    private View mView;
    private EditText edtOldPass;
    private EditText edtNewPass;
    private Button btnChangePass;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_password,container, false);
        initUi();
        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickChangePassword();
            }
        });

        return mView;

    }



    private void initUi(){
        edtOldPass=mView.findViewById(R.id.edt_opassword);
        edtNewPass=mView.findViewById(R.id.edt_npassword);
        btnChangePass=mView.findViewById(R.id.btn_change_password);

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

                        Toast.makeText(getActivity(),"Password Updated",Toast.LENGTH_SHORT).show();
                        edtNewPass.setText("");
                        edtOldPass.setText("");

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(),"Update Failed"+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //failed
                Toast.makeText(getActivity(),"Failed authenticated You enter rong password"+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}