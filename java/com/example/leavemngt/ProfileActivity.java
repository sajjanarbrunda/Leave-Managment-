package com.example.leavemngt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Tag;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.EnumMap;
import java.util.UUID;

public class    ProfileActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    TextView Name , Email, phno , educalL, healthL, casualL ;
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    ImageView cover ;

    int TAKE_IMAGE_CODE = 10001;










    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Assign variable
        drawerLayout = findViewById(R.id.drawer_layout);


        Name = findViewById(R.id.profile_name);
        Email = findViewById(R.id.profile_email);
        phno = findViewById(R.id.profile_num);
        casualL = findViewById(R.id.profile_no_of_causualL);
        healthL = findViewById(R.id.profile_no_of_healthL);
        educalL = findViewById(R.id.profile_no_of_eduLeav);
        cover = findViewById(R.id.profile_image);
//        cover.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                ImagePicker.with(ProfileActivity.this)
////                        .crop()	    			//Crop image(Optional), Check Customization for more option
////                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
////                        .maxResultSize(210, 104)	//Final image resolution will be less than 1080 x 1080(Optional)
////                        .start();
//
//                ChoosePicture();
//            }
//        });


        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = fstore.collection("Users").document(fAuth.getCurrentUser().getUid());
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
              Name.setText("Name : " + value.getString("FullName"));
              Email.setText("EMail :"+value.getString("UserEmail"));
                phno.setText("Phone Number :"+value.getString("PhoneNumber"));
                int cno = value.getLong("Casual_leave").intValue();
                int hno = value.getLong("helath_leave").intValue();
                int eno = value.getLong("edu_levave").intValue();
                casualL.setText( String.valueOf(cno));
                healthL.setText(String.valueOf(hno));
                educalL.setText(String.valueOf(eno));



            }
        });








    }

//    private void ChoosePicture() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(intent, 1);
////        
//
//    }

    @Override
  protected  void onActivityResult(int  requestcode , int resultCode , @Nullable Intent data) {
      super.onActivityResult(requestcode, resultCode, data);
    if(requestcode == TAKE_IMAGE_CODE){
        switch (resultCode){
            case RESULT_OK:
                Bitmap bitmap =(Bitmap) data.getExtras().get("data");
                cover.setImageBitmap(bitmap);
                uploadPicture(bitmap);

        }
    }

  }

    private void uploadPicture(Bitmap bitmap) {

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image ..");
        pd.show();


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG , 100,baos);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        StorageReference reference =  FirebaseStorage.getInstance().getReference()
                .child("ProfileImages")
                .child(uid + ".jpeg");


       reference.putBytes(baos.toByteArray())
               .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                   @Override
                   public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                       pd.dismiss();
                       Snackbar.make(findViewById(android.R.id.content), "Image Uploaded" , Snackbar.LENGTH_LONG).show();
                       getDownloadURL(reference);
                   }
               }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               pd.dismiss();
               Toast.makeText(ProfileActivity.this, "Failed to upload ", Toast.LENGTH_SHORT).show();
           }
       })
               .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                   @Override
                   public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                       double progressPercent = (100.00 * snapshot.getBytesTransferred()/ snapshot.getTotalByteCount());
                       pd.setMessage("Percentage  :" + (int)progressPercent+"%");

                   }
               });

    }

    private void getDownloadURL(StorageReference reference) {
        reference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                       setUserProfileURL(uri);
                    }
                });
    }

    private void setUserProfileURL(Uri uri) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();
        user.updateProfile(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ProfileActivity.this, "Updated Succesfully ", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "Profile Image FAiled... ", Toast.LENGTH_SHORT).show();

            }
        });
    }


    public void ClickMenu(View view){
        //open drawer
        Dashboard.openDrawer(drawerLayout);
    }
    public  void ClickLogo(View view){
        //close drawe
        Dashboard.closeDrawer(drawerLayout);
    }
    public void ClickHome(View view){
        //redirect activity to home
        Dashboard.redirectActivity(this,Dashboard.class);

    }
    public void  ClickDashboard(View view){
        //Recrete activity
        recreate();;

    }
    public  void ClickABoutUs(View view){
        Dashboard.redirectActivity(this, AboutUs.class);
    }
    public  void ClickLogout(View View){
        //close app
        Dashboard.logout(this);
    }
    @Override
   protected  void onPause(){
        super.onPause();
        Dashboard.closeDrawer(drawerLayout);

    }

    public void handleImageClick(View view) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent , TAKE_IMAGE_CODE);
        }
    }
}