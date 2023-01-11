package com.example.moodleclient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CourseAdapter extends  RecyclerView.Adapter<CourseAdapter.CourseViewHolder>{


    private List<Course> mListCourses;
    SignInActivity signInActivity = new SignInActivity();

    public CourseAdapter(List<Course> mListCourses, IClickListner listner) {
        this.mListCourses = mListCourses;
        this.mIClickListner = listner;
    }
    private IClickListner mIClickListner;
    public interface IClickListner{
        void onClickEditCourse(Course course);
        void onClickDeleteCourse(Course course);
    }
    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_course,parent,false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = mListCourses.get(position);
        if(course == null){
            return;
        }
        holder.tvCourseId.setText(course.getId());
        holder.tvCourseName.setText(course.getName());
        /*holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIClickListner.onClickItemCourse(course);
            }
        });

         */
        /*holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickGotoDetail(course);
            }
        });

         */
        holder.btnEditCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIClickListner.onClickEditCourse(course);
            }
        });
        holder.btnDeleteCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              mIClickListner.onClickDeleteCourse(course);
            }
        });

    }



    /*private int getRandomColor() {
        List<Integer> colorCode = new ArrayList<>();
        colorCode.add(R.color.teal_200);
        colorCode.add(R.color.greenlight);
        colorCode.add(R.color.red);
        colorCode.add(R.color.yellow);
        colorCode.add(R.color.purple_700);
        colorCode.add(R.color.lightPurple);
        Random randomColor = new Random();
        int number = randomColor.nextInt(colorCode.size());
        return colorCode.get(number);
    }

     */

    @Override
    public int getItemCount() {
        if(mListCourses != null){
            return mListCourses.size();
        }
        return 0;
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder{
        private RelativeLayout relativeLayout;
        private TextView tvCourseId, tvCourseName;
        CardView mCardView;
        private Button btnEditCourse,btnDeleteCourse;
        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);

            relativeLayout= itemView.findViewById(R.id.layout_item_course);
            tvCourseId = itemView.findViewById(R.id.tv_course_id);
            tvCourseName = itemView.findViewById(R.id.tv_course_name);
            mCardView = itemView.findViewById(R.id.cv);
            btnEditCourse= itemView.findViewById(R.id.btn_edit_course);
            btnDeleteCourse=itemView.findViewById(R.id.btn_delete_course);
            //CHECK LEVEL ACCESS
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
                        btnEditCourse.setVisibility(View.GONE);
                        btnDeleteCourse.setVisibility(View.GONE);

                    }
                    if(documentSnapshot.getString("isTeacher")!= null){

                    }
                }
            });

            }
        }
    /*private void onClickGotoDetail(Course course) {
        Intent intent = new Intent(mContext,DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("Object_course",course);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

     */
}
