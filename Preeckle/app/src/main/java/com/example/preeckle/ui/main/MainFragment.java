package com.example.preeckle.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.preeckle.MainActivity;
import com.example.preeckle.R;
import com.example.preeckle.ui.post.Post;
import com.example.preeckle.ui.profile.ProfileFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {

    private ImageButton profileButton;
    private ImageButton inboxButton;
    private TextView likeIcon;
    private Toolbar mainTopBar;
    private RecyclerView postViewMain;
    private NestedScrollView nestedScrollView;
    private View viewFragment;
    private MainActivity mainActivity;
    private List<Post> list = new ArrayList<>();

    private Context context;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef, postsRef, followRef;
    String currentUserID;

    public MainFragment() {}

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        viewFragment = inflater.inflate(R.layout.fragment_main, container, false);

        nestedScrollView = (NestedScrollView) viewFragment.findViewById(R.id.scrollMain);

        postViewMain = (RecyclerView) viewFragment.findViewById(R.id.postViewMain);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        postViewMain.setLayoutManager(linearLayoutManager);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        followRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("following");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(currentUserID);
        mainActivity = new MainActivity();

        return viewFragment;
    }

    public void onStart() {
        super.onStart();
        final List followID = new ArrayList();

        FirebaseRecyclerOptions<Post> options = new FirebaseRecyclerOptions.Builder<Post>().setQuery(postsRef, Post.class).build();

        FirebaseRecyclerAdapter<Post, ProfileFragment.postsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, ProfileFragment.postsViewHolder>(options) {
            @NonNull
            @Override
            public ProfileFragment.postsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_all_posts, parent, false);
                ProfileFragment.postsViewHolder viewHolder = new ProfileFragment.postsViewHolder(layoutView);
                return viewHolder;
            }

            @Override
            protected void onBindViewHolder(@NonNull final ProfileFragment.postsViewHolder holder, int position, @NonNull final Post model) {
                followRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            for(int i = 0; i < followID.size(); i++){
                                if(!followID.get(i).equals(ds.getValue()))
                                    followID.add(ds.getValue());
                            }
                            System.out.println(followID);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(currentUserID)) {
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
        postViewMain.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
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
}
