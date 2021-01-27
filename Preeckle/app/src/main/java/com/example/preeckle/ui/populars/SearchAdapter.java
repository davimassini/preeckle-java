package com.example.preeckle.ui.populars;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.preeckle.R;
import com.example.preeckle.ui.register.User;
import com.github.siyamed.shapeimageview.RoundedImageView;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyHolder>{

    Context context;
    List<User> list;

    public SearchAdapter(Context context, List<User> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(context).inflate(R.layout.layout_all_searchs, parent, false);

        return new MyHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String userFullname = list.get(position).getNameFull();
        String profileImage = list.get(position).getProfileImage();

        holder.profileNameSearch.setText(userFullname);
        holder.profileNameSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        try{
            Glide.with(context).load(profileImage).into(holder.profileImageSearch);
        }catch (Exception e){}
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        RoundedImageView profileImageSearch;
        TextView profileNameSearch;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            profileImageSearch = itemView.findViewById(R.id.profileImageSearch);
            profileNameSearch = itemView.findViewById(R.id.fullnameSearch);
        }
    }
}
