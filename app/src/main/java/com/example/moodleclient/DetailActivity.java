package com.example.moodleclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Bundle bundle = getIntent().getExtras();
        if(bundle== null){
            return;
        }
        Course course = (Course) bundle.get("object_course");
        EditText edtNamefile = findViewById(R.id.edt_name_file);
        Button btnUpfile = findViewById(R.id.btn_upfile);
        //tvTitle.setText(course.getName().toString());
    }
}