package com.example.moodleclient.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.moodleclient.R;
import com.example.moodleclient.pdf;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;


public class UploadFragment extends Fragment {

    private View mView;
    EditText edtNameFile;
    Button btnUpFile,btnView;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    RecyclerView rcv;
    ListView listView;
    List<pdf> uploads;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView=inflater.inflate(R.layout.fragment_upload, container, false);
        edtNameFile = mView.findViewById(R.id.edt_name_file1);
        btnUpFile = mView.findViewById(R.id.btn_upfile1);
        btnView = mView.findViewById(R.id.btn_Viewfile1);
        rcv=mView.findViewById(R.id.rcv_course1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rcv.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL);
        rcv.addItemDecoration(dividerItemDecoration);
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("Uploads");
        btnUpFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFiles();
            }
        });
        btnView.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),ShowFileActivity.class);
                startActivity(intent);
            }
        }));
        return mView;
        }

    private void selectFiles() {
        Intent intent= new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select your file..."),1 );

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){

        UploadFiles(data.getData());
    }
}

    private void UploadFiles(Uri data) {
        final ProgressDialog progressDialog= new ProgressDialog(getActivity());
        progressDialog.setTitle("Uploading..");
        progressDialog.show();

        StorageReference reference = storageReference.child("Uploads"+System.currentTimeMillis()+"pdf");
        reference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete());
                        Uri url =uriTask.getResult();
                        pdf pdf = new pdf(edtNameFile.getText().toString().trim(),url.toString());
                        databaseReference.child(databaseReference.push().getKey()).setValue(pdf);
                        Toast.makeText(getActivity(),"File uploaded",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress=(100.0*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                        progressDialog.setMessage("Uploaded:"+(int)progress+"%");
                    }

                });
    }



}