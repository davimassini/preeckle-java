package com.example.preeckle.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.preeckle.MainActivity;
import com.example.preeckle.R;
import com.example.preeckle.ui.login.LoginActivity;
import com.example.preeckle.ui.post.Post;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileFragment extends Fragment {

    private RoundedImageView profileImage;
    private TextView profileName;
    private TextView profileUsername;
    private TextView profileBio;
    private TextView backToMain;
    private TextView preecklesData, lovetsData, followersData, followingData;
    private Button loggoutButton;
    private MainActivity mainActivity;
    private RecyclerView postViewProfile;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef, postsRef;
    private StorageReference userStorage, postsImagesRefrence;
    String currentUserID;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImage = (RoundedImageView) rootView.findViewById(R.id.profileImage);
        profileName = (TextView) rootView.findViewById(R.id.profileName);
        profileUsername = (TextView) rootView.findViewById(R.id.profileUsername);
        profileBio = (TextView) rootView.findViewById(R.id.profileBio);
        loggoutButton = (Button) rootView.findViewById(R.id.loggoutButton);
        backToMain = (TextView) rootView.findViewById(R.id.backToMain);

        preecklesData = (TextView) rootView.findViewById(R.id.preecklesData);
        lovetsData = (TextView) rootView.findViewById(R.id.lovetsData);
        followersData = (TextView) rootView.findViewById(R.id.followersData);
        followingData = (TextView) rootView.findViewById(R.id.followingData);

        postViewProfile = (RecyclerView) rootView.findViewById(R.id.timelineRecycler);
        postViewProfile.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        postsImagesRefrence = FirebaseStorage.getInstance().getReference();
        currentUserID = mAuth.getCurrentUser().getUid();
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(currentUserID);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mainActivity = new MainActivity();

        mainActivity = new MainActivity();

        loggoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                sendUserToLoginActivity();
            }
        });

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String currentName = dataSnapshot.child(currentUserID).child("nameFull").getValue().toString();
                    String currentUsername = dataSnapshot.child(currentUserID).child("username").getValue().toString();
                    String currentBio = dataSnapshot.child(currentUserID).child("bio").getValue().toString();
                    String currentProfile = dataSnapshot.child(currentUserID).child("profileImage").getValue().toString();

                    profileName.setText(currentName);
                    profileUsername.setText("@" + currentUsername);
                    profileBio.setText(currentBio);
                    Glide.with(ProfileFragment.this).load(currentProfile).into(profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToMainActivity();
            }
        });

        return rootView;
    }

    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Post> options = new FirebaseRecyclerOptions.Builder<Post>().setQuery(postsRef, Post.class).build();

        FirebaseRecyclerAdapter<Post, postsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, postsViewHolder>(options) {
            @NonNull
            @Override
            public postsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_all_posts, parent, false);
                postsViewHolder viewHolder = new postsViewHolder(layoutView);
                return viewHolder;
            }

            @Override
            protected void onBindViewHolder(@NonNull final postsViewHolder holder, int position, @NonNull final Post model) {
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(currentUserID)) {
                            String profileImage = dataSnapshot.child(currentUserID).child("profileImage").getValue().toString();

                            holder.setDate(model.getPostDate());
                            holder.setProfileImage((MainActivity) getContext(), profileImage);
                            holder.setPostImage((MainActivity) getContext(), model.getPostImage());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };
        postViewProfile.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long numTotal = dataSnapshot.getChildrenCount();
                preecklesData.setText("" + (int) numTotal);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        userRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long numTotalFollow = dataSnapshot.child("followers").getChildrenCount();
                long numTotalFollowing = dataSnapshot.child("following").getChildrenCount();
                long numTotalLovets = dataSnapshot.child("lovets").getChildrenCount();

                followersData.setText("" + (int) numTotalFollow);
                followingData.setText("" + (int) numTotalFollowing);
                lovetsData.setText("" + (int) numTotalLovets);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public static class postsViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public postsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setProfileImage(Context ctx, String profileimage) {
            RoundedImageView image = (RoundedImageView) mView.findViewById(R.id.profileImagePost);
            Glide.with(ctx).load(profileimage).into(image);
        }

        public void setDate(String date) {
            TextView PostDate = (TextView) mView.findViewById(R.id.calendarPost);
            PostDate.setText(date);
        }

        public void setPostImage(Context ctx1, String postimage) {
            ImageView PostImage = (ImageView) mView.findViewById(R.id.photoPost);
            Glide.with(ctx1).load(postimage).into(PostImage);
        }
    }

    public void sendUserToLoginActivity() {
        Intent loginActivity = new Intent(((MainActivity)getActivity()), LoginActivity.class);
        startActivity(loginActivity);
        mainActivity.finish();
    }

    public void sendUserToMainActivity() {
        Intent mainActivity = new Intent(((MainActivity)getActivity()), MainActivity.class);
        startActivity(mainActivity);
    }
}
