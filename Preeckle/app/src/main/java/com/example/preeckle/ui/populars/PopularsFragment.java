package com.example.preeckle.ui.populars;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.preeckle.MainActivity;
import com.example.preeckle.R;
import com.example.preeckle.ui.register.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PopularsFragment extends Fragment {

    private EditText searchField;
    private ImageButton buttonSearchAction;
    private ImageButton searchIcon;
    private NestedScrollView scrollSearch;
    private RecyclerView searchRecycler;
    private SearchAdapter searchAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    String currentUserID;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_populars, container, false);

        buttonSearchAction = (ImageButton) root.findViewById(R.id.buttonSearchAction);
        scrollSearch = (NestedScrollView) root.findViewById(R.id.scrollSearch);
        searchRecycler = (RecyclerView) root.findViewById(R.id.searchRecycler);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        searchField = (EditText) root.findViewById(R.id.searchField);
        searchField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                scrollSearch.setVisibility(View.VISIBLE);
                searchIcon.setVisibility(View.VISIBLE);

                buttonSearchAction.setImageResource(R.drawable.arrow_back);
                buttonSearchAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendToMainActivity();
                        buttonSearchAction.setImageResource(R.drawable.search_icon);
                    }
                });
            }
        });

        searchIcon = (ImageButton) root.findViewById(R.id.searchIcon);
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameSearched = searchField.getText().toString();
                searchAllUsers(nameSearched);
            }
        });

        return root;
    }

    private void searchAllUsers(final String nameSearched) {
        userRef.addValueEventListener(new ValueEventListener() {
            List<User> list = new ArrayList<>();
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User search = postSnapshot.getValue(User.class);

                    if(!postSnapshot.getKey().equals(currentUserID)){
                        if (search.getNameFull().toLowerCase().contains(nameSearched.toLowerCase())){
                            list.add(search);
                        }
                    }
                    searchAdapter = new SearchAdapter((MainActivity)getContext(), list);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    searchRecycler.setLayoutManager(linearLayoutManager);
                    searchAdapter.notifyDataSetChanged();
                    searchRecycler.setAdapter(searchAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void sendToMainActivity(){
        Intent mainActivity = new Intent(((MainActivity)getActivity()), MainActivity.class);
        startActivity(mainActivity);
    }

}
