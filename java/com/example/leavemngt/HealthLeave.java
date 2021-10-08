package com.example.leavemngt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;

public class HealthLeave extends AppCompatActivity {
    DrawerLayout drawerLayout;
    TextView textView ;
    Button button;
    ImageView image;
    EditText emailtoadress ,emailbody;
    String pattern_email="[A-Za-z0-9._-]+@[a-z]+\\.+[a-z]+";


    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    private int no = 0;
    private boolean ans;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_leave);
        drawerLayout = findViewById(R.id.drawer_layout);

        textView = findViewById(R.id.tv_email_txt);
        button = findViewById(R.id.btn_email_sendemail);
        image = findViewById(R.id.img_email_img);
        emailtoadress = findViewById(R.id.ed_email_toaddress);
        emailbody = findViewById(R.id.ed_email_body);


        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();






        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toaddress = emailtoadress.getText().toString().trim();
                String body = emailbody.getText().toString().trim();



                if(toaddress.length() ==0 ||  body.length()==0){
                    emailtoadress.setError("empty field");
                    emailtoadress.requestFocus();

                    emailbody.setError("empty field");
                    emailbody.requestFocus();
                }
                else if(!toaddress.matches(pattern_email)){
                    emailtoadress.setError("mail invaild");
                    emailtoadress.requestFocus();

                }
                else {
                    if (isneeded()) {

                        updateLeaveNo();
                        Intent email = new Intent(Intent.ACTION_SEND);
                        email.putExtra(Intent.EXTRA_EMAIL, new String[]{toaddress});
                        email.putExtra(Intent.EXTRA_SUBJECT, " Application for  sick leave");
                        email.putExtra(Intent.EXTRA_TEXT, body);

                        email.setType("message/rfc822");

                        startActivity(Intent.createChooser(email, "choose an email client"));
                    }

                }


            }
        });

    }

    private boolean isneeded() {
        DocumentReference documentReference = fstore.collection("Users").document(fAuth.getCurrentUser().getUid());
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                int  cno = value.getLong("helath_leave").intValue();
                if(cno >= 12){
                    AlertDialog difg = new AlertDialog.Builder(HealthLeave.this)
                            .setTitle("Sorry")
                            .setMessage("You have  ALerdy used all your leaves")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(HealthLeave.this , Dashboard.class));
                                    dialog.dismiss();
                                }
                            })
                            .create();
                    difg.show();
                    ans = false;


                }else{
                    ans = true;
                }
            }
        });




        return ans;

    }

    private void updateLeaveNo() {
       DocumentReference documentReference = fstore.collection("Users").document(fAuth.getCurrentUser().getUid());
       documentReference.update("helath_leave", FieldValue.increment(1)).addOnSuccessListener(new OnSuccessListener<Void>() {
           @Override
           public void onSuccess(Void aVoid) {
               Toast.makeText(HealthLeave.this, "SUccesfuly updated", Toast.LENGTH_SHORT).show();
           }
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               Toast.makeText(HealthLeave.this, "Failed to updte", Toast.LENGTH_SHORT).show();
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
        Dashboard.redirectActivity(this,ProfileActivity.class);

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
}