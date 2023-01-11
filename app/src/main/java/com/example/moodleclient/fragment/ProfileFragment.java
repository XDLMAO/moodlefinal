package com.example.moodleclient.fragment;

import static android.content.ContentValues.TAG;
import static com.example.moodleclient.MainActivity.MY_REQUEST_CODE;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.moodleclient.MainActivity;
import com.example.moodleclient.R;
import com.example.moodleclient.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;


public class ProfileFragment extends Fragment {


    private View mView;
    private ImageView imgAvatar;
    private EditText edtFullName;
    private TextView tvEmail;
    private Button btnUpdateProfile;
    private Uri mUri ;
    private MainActivity mMainActivity;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_profile,container,false);
        initUi();

        progressDialog = new ProgressDialog(getActivity());
        mMainActivity = (MainActivity) getActivity();
        setUserInformation();
        initListener();
        return mView;

    }

    private void initListener() {
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRequestPermission();
            }
        });
        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickUpdateProfile();
            }
        });
    }


    private void onClickRequestPermission() {
        if(mMainActivity == null){
            return;

        }
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            mMainActivity.openGallery();
            return;
        }
        if(getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)==
                PackageManager.PERMISSION_GRANTED){
            mMainActivity.openGallery();
        }else{
            String [] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
            getActivity().requestPermissions(permission,MY_REQUEST_CODE);
        }
    }



    private void setUserInformation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }
        //edtFullName.setText((user.getDisplayName()));
        //setName
        DocumentReference df = FirebaseFirestore.getInstance().collection("Users").document(user.getUid());
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("TAG","OnSuccess: "+ documentSnapshot.getData());
                edtFullName.setText(documentSnapshot.getString("FullName"));
            }
        });
        tvEmail.setText(user.getEmail());
        Glide.with(getActivity()).load(user.getPhotoUrl()).error(R.drawable.ic_avatar_default).into(imgAvatar);
    }

    private void initUi(){
        edtFullName=mView.findViewById(R.id.edt_fullname);
        tvEmail=mView.findViewById(R.id.tv_emailp);
        btnUpdateProfile=mView.findViewById(R.id.btn_update_profile);
        imgAvatar= mView.findViewById(R.id.img_avatar);
    }
    public void setBitmapImageView(Bitmap bitmapImageView){
        //imgAvatar.setImageBitmap(bitmapImageView);
        imgAvatar.setImageBitmap(bitmapImageView);
    }

    public void setUri(Uri mUri) {
        this.mUri = mUri;
    }


    private void onClickUpdateProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            return;
        }
        progressDialog.show();

        // Update name
        String strFullName =edtFullName.getText().toString().trim();
        DocumentReference df = FirebaseFirestore.getInstance().collection("Users").document(user.getUid());
        Map<String,Object> userInfo = new HashMap<>();
        userInfo.put("FullName",strFullName);
        df.update(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "onSuccess: yay");
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: ",e );
            }
        });

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(mUri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(),"Update successfully!",Toast.LENGTH_SHORT).show();
                            mMainActivity.showUserInformation();
                            progressDialog.dismiss();
                        }
                    }
                });
    }
}