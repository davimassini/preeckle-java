package com.example.preeckle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.preeckle.customWidgets.CustomViewPager;
import com.example.preeckle.ui.inbox.InboxFragment;
import com.example.preeckle.ui.login.LoginActivity;
import com.example.preeckle.ui.main.MainFragment;
import com.example.preeckle.ui.populars.PopularsFragment;
import com.example.preeckle.ui.post.PostActivity;
import com.example.preeckle.ui.profile.ProfileFragment;
import com.example.preeckle.ui.setup.SetupActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends FragmentActivity {

    private ImageView mainNavigation;
    private ImageView popularNavigation;
    private ImageView notificationNavigation;
    private CircleImageView profileNavigation;
    private CircleImageView circleWhiteAdd;
    private MainFragment mainFragment;
    private PopularsFragment popularsFragment;
    private ProfileFragment profileFragment;
    private static final int NUM_PAGES = 4;
    private CustomViewPager mPager;
    private PagerAdapter pagerAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private StorageReference userStorage;
    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainNavigation = (ImageView) findViewById(R.id.mainNavigation);
        popularNavigation = (ImageView) findViewById(R.id.popularNavigation);
        notificationNavigation = (ImageView) findViewById(R.id.notificationNavigation);
        profileNavigation = (CircleImageView) findViewById(R.id.profileNavigation);
        circleWhiteAdd = (CircleImageView) findViewById(R.id.circleWhiteAdd);

        mainFragment = new MainFragment();
        popularsFragment = new PopularsFragment();
        profileFragment = new ProfileFragment();

        mAuth = FirebaseAuth.getInstance();
        userStorage = FirebaseStorage.getInstance().getReference().child("profileImages");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        onStart();
        getPager();
        eventOnClickMenuBottom();

        mainNavigation.setImageResource(R.drawable.home_icon_light_active);
    }

    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            sendUserToLoginActivity();
        }else {
            checkUserExistence();
        }
    }

    private void checkUserExistence() {
        currentUserID = mAuth.getCurrentUser().getUid();

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(currentUserID)){
                        if(!dataSnapshot.child(currentUserID).hasChild("username")){
                            sendUserToSetupActivity();
                        }else if(dataSnapshot.child(currentUserID).hasChild("profileImage")) {
                            String currentProfile = dataSnapshot.child(currentUserID).child("profileImage").getValue().toString();
                            Glide.with(MainActivity.this).load(currentProfile).into(profileNavigation);
                        }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    public CustomViewPager getPager() {
        mPager = (CustomViewPager) findViewById(R.id.pager);
        mPager.setPagingEnabled(false);
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(pagerAdapter);

        return mPager;
    }

    public void eventOnClickMenuBottom() {
        mainNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mPager.setCurrentItem(0);
                mainNavigation.setImageResource(R.drawable.home_icon_light_active);
                popularNavigation.setImageResource(R.drawable.popular_icon_light);
                notificationNavigation.setImageResource(R.drawable.notification_icon_light);
                profileNavigation.setBorderWidth(0);
            }
        });

        popularNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mPager.setCurrentItem(1);
                mainNavigation.setImageResource(R.drawable.home_icon_light);
                popularNavigation.setImageResource(R.drawable.popular_icon_light_active);
                notificationNavigation.setImageResource(R.drawable.notification_icon_light);
                profileNavigation.setBorderWidth(0);

            }
        });

        notificationNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mPager.setCurrentItem(2);
                mainNavigation.setImageResource(R.drawable.home_icon_light);
                popularNavigation.setImageResource(R.drawable.popular_icon_light);
                notificationNavigation.setImageResource(R.drawable.notification_icon_active_light);
                profileNavigation.setBorderWidth(0);
            }
        });

        profileNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mPager.setCurrentItem(3);
                mainNavigation.setImageResource(R.drawable.home_icon_light);
                popularNavigation.setImageResource(R.drawable.popular_icon_light);
                notificationNavigation.setImageResource(R.drawable.notification_icon_light);
                profileNavigation.setBorderWidth(2);
            }
        });

        circleWhiteAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToPostActivity();
            }
        });


    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new MainFragment();
                case 1:
                    return new PopularsFragment();
                case 2:
                    return new InboxFragment();
                case 3:
                    return new ProfileFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    public void sendUserToSetupActivity() {
        Intent setupActivity = new Intent(MainActivity.this, SetupActivity.class);
        startActivity(setupActivity);
        finish();
    }

    public void sendUserToLoginActivity() {
        Intent loginActivity = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginActivity);
        finish();
    }

    public void sendUserToPostActivity() {
        Intent postActivity = new Intent(MainActivity.this, PostActivity.class);
        startActivity(postActivity);
    }
}
