package com.example.preeckle.ui.post;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.preeckle.MainActivity;
import com.example.preeckle.R;
import com.github.siyamed.shapeimageview.mask.PorterShapeImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostActivity extends AppCompatActivity {

    private ImageButton closeActivityAction;
    private CircleImageView profileImageAddPost;
    private ImageView addPhotoAction;
    private PorterShapeImageView photoSelected;
    private FloatingActionButton addPostButton;
    private EditText addWriteSpace;
    private boolean hasPhoto = false;

    private Uri imageUri;
    private final static int galleryPic = 1;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef, postsRef;
    private StorageReference userStorage, postsImagesRefrence;
    private String saveCurrentDate, saveCurrentTime, postRandomName, downloadUrl, currentUserID, postDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        closeActivityAction = (ImageButton) findViewById(R.id.closeActivityAction);
        profileImageAddPost = (CircleImageView) findViewById(R.id.profileImageAddPost);
        addPhotoAction = (ImageView) findViewById(R.id.addPhotoAction);
        addPostButton = (FloatingActionButton) findViewById(R.id.addPostButton);
        addWriteSpace = (EditText) findViewById(R.id.addWriteSpace);
        photoSelected = (PorterShapeImageView) findViewById(R.id.photoSelected);

        mAuth = FirebaseAuth.getInstance();
        postsImagesRefrence = FirebaseStorage.getInstance().getReference();
        currentUserID = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(currentUserID);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        buttonActivity();
        checkProfileImage();
    }

    private void checkProfileImage() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(currentUserID).hasChild("profileImage")){
                    String currentProfile = dataSnapshot.child(currentUserID).child("profileImage").getValue().toString();
                    Glide.with(PostActivity.this).load(currentProfile).into(profileImageAddPost);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void buttonActivity() {
        closeActivityAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToMainActivity();
            }
        });

        addPhotoAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storingImageToFirebase();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == galleryPic && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();

            photoSelected.setImageURI(imageUri);
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, galleryPic);
    }

    private void storingImageToFirebase() {
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calFordDate.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;

        StorageReference filePath = postsImagesRefrence.child("postImages").child(currentUserID).child(imageUri.getLastPathSegment() + postRandomName + ".jpg");

        filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()) {
                    Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();
                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadUrl = uri.toString();
                            savingPostInformationToDatabase();
                        }
                    });
                }else {
                    String message = task.getException().getMessage();
                    Toast.makeText(PostActivity.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void savingPostInformationToDatabase() {
        postDescription = addWriteSpace.getText().toString();
        userRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    HashMap postsMap = new HashMap();
                    postsMap.put("postDate", saveCurrentDate);
                    postsMap.put("postTime", saveCurrentTime);
                    postsMap.put("postDescription", postDescription);
                    postsMap.put("postImage", downloadUrl);
                    postsRef.child(currentUserID + postRandomName).updateChildren(postsMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                sendToMainActivity();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    public void sendToMainActivity(){
        Intent mainActivity = new Intent(PostActivity.this, MainActivity.class);
        startActivity(mainActivity);
        finish();
    }
}
