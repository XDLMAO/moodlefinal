package com.example.moodleclient.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moodleclient.Course;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moodleclient.CourseAdapter;
import com.example.moodleclient.R;
import com.example.moodleclient.SignInActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;


public class CourseFragment extends Fragment {

    private View mView;
    private EditText edtCourseId, edtCourseName;
    private Button btnAddCourse;
    private RecyclerView rcvCourse;
    private CourseAdapter mCourseAdapter;
    private List<Course> mListCourses;
    SignInActivity signInActivity = new SignInActivity();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView= inflater.inflate(R.layout.fragment_course, container, false);


        edtCourseId=mView.findViewById(R.id.edt_course_id);
        edtCourseName=mView.findViewById(R.id.edt_course_name);
        btnAddCourse=mView.findViewById(R.id.btn_add_course);
        rcvCourse=mView.findViewById(R.id.rcv_course);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rcvCourse.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL);
        rcvCourse.addItemDecoration(dividerItemDecoration);
        mListCourses = new ArrayList<>();
        mCourseAdapter= new CourseAdapter(mListCourses, new CourseAdapter.IClickListner() {
            @Override
            public void onClickEditCourse(Course course) {
                openDialogUpdateCourse(course);
            }

            @Override
            public void onClickDeleteCourse(Course course) {
                onClickDeleteData(course);
            }


        });
        rcvCourse.setAdapter(mCourseAdapter);
        initListener();
        //check user level
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore store = FirebaseFirestore.getInstance();
        String uid = user.getUid();
        DocumentReference df = store.collection("Users").document(uid);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("TAG","OnSuccess: "+ documentSnapshot.getData());
                if(documentSnapshot.getString("isStudent")!=null){
                    btnAddCourse.setVisibility(View.GONE);

                }
                if(documentSnapshot.getString("isTeacher")!= null){

                }
            }
        });

        getListCourseFromRealTimeDatabse();
        return mView;
    }


    private void initListener() {
        btnAddCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = edtCourseId.getText().toString().trim();
                String name = edtCourseName.getText().toString().trim();
                if(!(id.isEmpty()||name.isEmpty())){
                    Course course = new Course(id,name);
                    onClickAddCourse(course);
                }else{
                    Toast.makeText(getActivity(),"Pls Enter Inf",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }



    private void onClickAddCourse(Course course) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myfRef = database.getReference("list_courses");
        String pathObject = String.valueOf(course.getId());
        myfRef.child(pathObject).setValue(course, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(getActivity(),"Success",Toast.LENGTH_SHORT).show();
                edtCourseId.setText("");
                edtCourseName.setText("");
            }
        });

    }
    private void getListCourseFromRealTimeDatabse(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myfRef = database.getReference("list_courses");

        /*myfRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(mListCourses != null){
                    mListCourses.clear();
                }
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Course course = dataSnapshot.getValue(Course.class);
                    mListCourses.add(course);
                }

                mCourseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(),"Get data failed",Toast.LENGTH_SHORT).show();
            }
        });
        */
        myfRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Course course = snapshot.getValue(Course.class);
                if(course!=null){
                    mListCourses.add(course);
                    mCourseAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Course course = snapshot.getValue(Course.class);
                if(course==null|| mListCourses==null||mListCourses.isEmpty()){
                    return;
                }
                for(int i = 0; i <mListCourses.size(); i++){
                    if(course.getId()== mListCourses.get(i).getId()){
                        mListCourses.set(i, course);
                        break;
                    }
                }

                mCourseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Course course = snapshot.getValue(Course.class);
                if(course==null|| mListCourses==null||mListCourses.isEmpty()){
                    return;
                }
                for(int i = 0; i <mListCourses.size(); i++){
                    if(course.getId()== mListCourses.get(i).getId()){
                        mListCourses.remove(mListCourses.get(i));
                        break;
                    }
                }
                mCourseAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    private void openDialogUpdateCourse(Course course) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_edit_course);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        EditText edtUpdateCourseName = dialog.findViewById(R.id.edt_edit_course_name);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnUpdateCourseName = dialog.findViewById(R.id.btn_update_course_name);

        edtUpdateCourseName.setText(course.getName());
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnUpdateCourseName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myfRef = database.getReference("list_courses");
                String newName=edtUpdateCourseName.getText().toString().trim();
                course.setName(newName);
                myfRef.child(String.valueOf(course.getId())).updateChildren(course.toMap(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        Toast.makeText(getActivity(),"Success",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }
    private void onClickDeleteData(Course course) {
        new AlertDialog.Builder(getActivity()).
                setTitle(getString(R.string.app_name)).
                setMessage("Do you want to delete").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myfRef = database.getReference("list_courses");

                        myfRef.child(String.valueOf(course.getId())).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                Toast.makeText(getActivity(),"Success",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel",null).show();


    }

}