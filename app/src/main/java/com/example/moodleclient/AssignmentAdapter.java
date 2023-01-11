package com.example.moodleclient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.List;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder>{

    public AssignmentAdapter(List<Assignment> mListAssingments, IClickListner listener) {

        this.mListAssingments = mListAssingments;
        this.mIClickListner = listener;
    }

    private List<Assignment> mListAssingments;
    private Context mContext;
    private IClickListner mIClickListner;
    public interface IClickListner{
        void onClickEditAssignment(Assignment assignment);
        void onClickDeleteAsssignment(Assignment assignment);
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_assignment,parent,false);
        return new AssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        Assignment assignment = mListAssingments.get(position);
        if(assignment == null){
            return;
        }
        holder.textViewid.setText(assignment.getId());
        holder.textViewid.setText(assignment.getName());
        /*holder.rclView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickGoToAssigmentDetail(assignment);
            }
        });
        holder.btnEditAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            mIClickListner.onClickEditAssignment(assignment);
            }
        });
        holder.btnDeleteAssginment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIClickListner.onClickDeleteAsssignment(assignment);
            }
        });

         */
    }

    private void onClickGoToAssigmentDetail(Assignment assignment) {
        Intent intent=new Intent(mContext, DetailAssignmentActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_assignment", assignment);
        intent.putExtras(bundle);
        mContext.startActivity(intent);

    }
    public void release(){
        mContext=null;
    }

    @Override
    public int getItemCount() {
        if(mListAssingments != null){
            return mListAssingments.size();
        }
        return 0;
    }

    public class AssignmentViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewid,tvName;
        private Button btnEditAssignment,btnDeleteAssginment;
        private RecyclerView rclView;
        public AssignmentViewHolder(@NonNull View itemView) {
            super(itemView);
            rclView=itemView.findViewById(R.id.layout_item_assignment);
            textViewid=itemView.findViewById(R.id.tv_assignment_id);
            tvName=itemView.findViewById(R.id.tv_assignment_name);
            btnEditAssignment= itemView.findViewById(R.id.btn_edit_assignment);
            btnDeleteAssginment=itemView.findViewById(R.id.btn_delete_assignment);
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
                        btnEditAssignment.setVisibility(View.GONE);
                        btnDeleteAssginment.setVisibility(View.GONE);

                    }
                    if(documentSnapshot.getString("isTeacher")!= null){

                    }
                }
            });

        }



    }


}
