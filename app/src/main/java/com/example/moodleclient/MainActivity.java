package com.example.moodleclient;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.moodleclient.fragment.ProfileFragment;

import com.example.moodleclient.fragment.ViewPagerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final int MY_REQUEST_CODE =10;
    private NavigationView mnavigationView;
    private static final int FRAGMENT_COURSE =0;
    private static final int FRAGMENT_ASSIGNMENT =1;
    private static final int FRAGMENT_NOTIFICATION =2;
    private static final int FRAGMENT_PROFILE=3;
    private int mCurrentFragment = FRAGMENT_COURSE;

    private TabLayout mTablayout;
    private ViewPagerAdapter mViewPagerAdapter;
    private ViewPager2 mViewPager2;

    private ProfileFragment mProfileFragment = new ProfileFragment();
    private ImageView imgAvatar;
    private TextView tvName, tvEmail;
    private  ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()== RESULT_OK){
                        Intent intent =result.getData();
                        if(intent==null){
                            return;
                        }
                        Uri uri = intent.getData();
                        mProfileFragment.setUri(uri);
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            mProfileFragment.setBitmapImageView(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
    private DrawerLayout mDrawyerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawyerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mTablayout =findViewById(R.id.tab_layout);
        mViewPager2 =findViewById(R.id.view_pager);
        mViewPagerAdapter= new ViewPagerAdapter(this);
        mViewPager2.setAdapter(mViewPagerAdapter);
        new TabLayoutMediator(mTablayout, mViewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0:
                        tab.setText(getString(R.string.nav_course));
                        break;
                    case 1:
                        tab.setText(R.string.nav_assignment);
                        break;
                    case 2:
                        tab.setText("Files");
                        break;
                    case 3:
                        tab.setText(R.string.nav_my_profile);
                        break;
                }
            }
        }).attach();

        initUi();
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(this,mDrawyerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);


        mnavigationView.setNavigationItemSelectedListener(this);


        mDrawyerLayout.addDrawerListener(toggle);
        toggle.syncState();
      /*  if(savedInstanceState == null){
            //check level user
            DocumentReference df = FirebaseFirestore.getInstance().collection(("Users"))
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid());
            df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.getString("isTeacher")!=null){
                       // getSupportFragmentManager().
                               // beginTransaction().replace(R.id.content_frame, new TeacherFragment()).commit();
                    }
                    if(documentSnapshot.getString("isStudent")!=null){
                       // getSupportFragmentManager().
                             //   beginTransaction().replace(R.id.content_frame, new StudentFragment()).commit();
                    }
                }
            });
        }
*/
        showUserInformation();
            mnavigationView.getMenu().findItem(R.id.nav_course).setChecked(true);
            mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    switch (position){
                        case 0:
                            mCurrentFragment = FRAGMENT_COURSE;
                            mnavigationView.getMenu().findItem(R.id.nav_course).setChecked(true);
                            break;
                        case 1:
                            mCurrentFragment =FRAGMENT_ASSIGNMENT;
                            mnavigationView.getMenu().findItem(R.id.nav_assignment).setChecked(true);
                            break;
                        case 2:
                            mCurrentFragment =FRAGMENT_NOTIFICATION;
                            mnavigationView.getMenu().findItem(R.id.nav_notification).setChecked(true);
                            break;
                        case 3:
                            mCurrentFragment =FRAGMENT_PROFILE;
                            mnavigationView.getMenu().findItem(R.id.nav_my_profile).setChecked(true);
                            break;

                    }
                }
            });


    }
    private void initUi(){
        mnavigationView =findViewById(R.id.navigation_view);
        imgAvatar=mnavigationView.getHeaderView(0).findViewById(R.id.img_avatar);
        tvName=mnavigationView.getHeaderView(0).findViewById(R.id.tv_name);
        tvEmail=mnavigationView.getHeaderView(0).findViewById(R.id.tv_email);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.nav_course:
                if(mCurrentFragment!= FRAGMENT_COURSE) {
                    mViewPager2.setCurrentItem(0);
                }
                break;
            case R.id.nav_assignment:
                if(mCurrentFragment!= FRAGMENT_ASSIGNMENT) {
                    mViewPager2.setCurrentItem(1);
                }
                break;
            case R.id.nav_notification:
                if(mCurrentFragment!= FRAGMENT_NOTIFICATION) {
                    mViewPager2.setCurrentItem(2);
                }
                break;
            case R.id.nav_my_profile:
                if(mCurrentFragment!= FRAGMENT_NOTIFICATION) {
                    mViewPager2.setCurrentItem(3);
                }
                break;
            case R.id.nav_change_password:
                startActivity(new Intent(getApplicationContext(),ChangePasswordActivity.class));
                break;
            case R.id.nav_log_out:
                FirebaseAuth.getInstance().signOut();;
                startActivity(new Intent(getApplicationContext(),SignInActivity.class));
                finish();
        }
        mDrawyerLayout.closeDrawer(GravityCompat.START);
        return true;
        /*int id = item.getItemId();   //error code
        if(id == R.id.nav_home){
            if(mCurrentFragment!= FRAGMENT_HOME){
                replaceFragment(new HomeFragment());
                mCurrentFragment = FRAGMENT_HOME;
            }
        }else if (id==R.id.nav_my_profile){
            if(mCurrentFragment!= FRAGMENT_Profile){
                replaceFragment(new ProfileFragment());
                mCurrentFragment = FRAGMENT_Profile;
            }

        }else if (id ==R. id.nav_change_password){
            if(mCurrentFragment!= FRAGMENT_Change_Password){
                replaceFragment(new ChangePasswordFragment());
                mCurrentFragment = FRAGMENT_Change_Password;
            }
        }else if(id == R.id.nav_log_out){
            FirebaseAuth.getInstance().signOut();;
            startActivity(new Intent(getApplicationContext(),SignInActivity.class));
            finish();
        }
        mDrawyerLayout.closeDrawer(GravityCompat.START);
        return true;
*/
    }



    @Override
    public void onBackPressed(){
        if(mDrawyerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawyerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    public void showUserInformation(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            return;
        }
        DocumentReference df = FirebaseFirestore.getInstance().collection(("Users"))
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                    String name =  documentSnapshot.getString("FullName");
                tvName.setText(name);

            }
        });
        String email =user.getEmail();
        Uri photoUrl = user.getPhotoUrl();

        tvEmail.setText(email);

        Glide.with(this).load(photoUrl).error(R.drawable.ic_avatar_default).into(imgAvatar);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode== MY_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openGallery();
            }else{
                Toast.makeText(this,"Please allow access Gallery",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent,"Select Image"));

    }
}