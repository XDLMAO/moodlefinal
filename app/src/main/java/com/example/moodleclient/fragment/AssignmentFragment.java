package com.example.moodleclient.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moodleclient.Assignment;
import com.example.moodleclient.AssignmentAdapter;
import com.example.moodleclient.Course;
import com.example.moodleclient.CourseAdapter;
import com.example.moodleclient.R;
import com.example.moodleclient.SignInActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class AssignmentFragment extends Fragment {


    private View mView;
    private EditText edtAssignmentId, edtAssignmentName;
    private Button btnAddAssignment;
    private RecyclerView rcvAssignment;
    private AssignmentAdapter  mAssignmentAdapter;
    private List<Assignment> mListAssignments;
    SignInActivity signInActivity = new SignInActivity();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView= inflater.inflate(R.layout.fragment_assignment, container, false);


        edtAssignmentId=mView.findViewById(R.id.edt_assignment_id);
        edtAssignmentName=mView.findViewById(R.id.edt_assignment_name);
        btnAddAssignment=mView.findViewById(R.id.btn_add_assignment);
        rcvAssignment=mView.findViewById(R.id.rcv_assignment);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rcvAssignment.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL);
        rcvAssignment.addItemDecoration(dividerItemDecoration);
        mListAssignments = new ArrayList<>();

        initListener();
        mAssignmentAdapter = new AssignmentAdapter(mListAssignments, new AssignmentAdapter.IClickListner() {
            @Override
            public void onClickEditAssignment(Assignment assignment) {
               openDialogUpdateAssignment(assignment);
            }

            @Override
            public void onClickDeleteAsssignment(Assignment assignment) {
                onClickDeleteData(assignment);
            }
        });
        rcvAssignment.setAdapter(mAssignmentAdapter);
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
                    btnAddAssignment.setVisibility(View.GONE);

                }
                if(documentSnapshot.getString("isTeacher")!= null){

                }
            }
        });

        getListAssignmentFromRealTimeDatabse();
        return mView;

    }
    private void initListener() {
        btnAddAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = edtAssignmentId.getText().toString().trim();
                String name = edtAssignmentName.getText().toString().trim();
                if(!(id.isEmpty()||name.isEmpty())){
                    Assignment assignment = new Assignment(id,name);
                    onClickAddAssignment(assignment);
                }else{
                    Toast.makeText(getActivity(),"Pls Enter Inf",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }



    private void onClickAddAssignment(Assignment assignment) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myfRef = database.getReference("list_assignments");
        String pathObject = String.valueOf(assignment.getId());
        myfRef.child(pathObject).setValue(assignment, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(getActivity(),"Success",Toast.LENGTH_SHORT).show();
                edtAssignmentId.setText("");
                edtAssignmentName.setText("");
            }
        });

    }
    private void getListAssignmentFromRealTimeDatabse(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myfRef = database.getReference("list_assignments");

        /*myfRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(mListAssignments != null){
                    mListAssignments.clear();
                }
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Assignment assignment = dataSnapshot.getValue(Course.class);
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
                Assignment assignment = snapshot.getValue(Assignment.class);
                if(assignment!=null){
                    mListAssignments.add(assignment);
                  mAssignmentAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Assignment assignment = snapshot.getValue(Assignment.class);
                if(assignment==null|| mListAssignments==null||mListAssignments.isEmpty()){
                    return;
                }
                for(int i = 0; i <mListAssignments.size(); i++){
                    if(assignment.getId()== mListAssignments.get(i).getId()){
                        mListAssignments.set(i, assignment);
                        break;
                    }
                }

              mAssignmentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Assignment assignment = snapshot.getValue(Assignment.class);
                if(assignment==null|| mListAssignments==null||mListAssignments.isEmpty()){
                    return;
                }
                for(int i = 0; i <mListAssignments.size(); i++){
                    if(assignment.getId()== mListAssignments.get(i).getId()){
                        mListAssignments.remove(mListAssignments.get(i));
                        break;
                    }
                }
               mAssignmentAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    private void openDialogUpdateAssignment(Assignment assignment) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_edit_assignment);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        EditText edtUpdateAssignmentName = dialog.findViewById(R.id.edt_edit_assignment_name);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnUpdateAssignmentName = dialog.findViewById(R.id.btn_update_assignment_name);

        edtUpdateAssignmentName.setText(assignment.getName());
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnUpdateAssignmentName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myfRef = database.getReference("list_assigments");
                String newName=edtUpdateAssignmentName.getText().toString().trim();
                assignment.setName(newName);
                myfRef.child(String.valueOf(assignment.getId())).updateChildren(assignment.toMap(), new DatabaseReference.CompletionListener() {
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
    private void onClickDeleteData(Assignment assignment) {
        new AlertDialog.Builder(getActivity()).
                setTitle(getString(R.string.app_name)).
                setMessage("Do you want to delete").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myfRef = database.getReference("list_assignments");

                        myfRef.child(String.valueOf(assignment.getId())).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                Toast.makeText(getActivity(),"Success",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel",null).show();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mAssignmentAdapter != null){
            mAssignmentAdapter.release();
        }
    }
}