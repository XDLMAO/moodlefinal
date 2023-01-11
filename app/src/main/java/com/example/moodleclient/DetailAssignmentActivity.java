package com.example.moodleclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class DetailAssignmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_assignment);

        Bundle bundle = getIntent().getExtras();
        if(bundle==null){
            return;
        }
        Assignment assignment = (Assignment) bundle.get("Object_assignment");
        EditText edtAssignmentName;
        Button btnUpAssignmentFile;
        edtAssignmentName =findViewById(R.id.edt_assignment_file);
        btnUpAssignmentFile=findViewById(R.id.btn_upAssignmentfile);
    }
}