package com.example.leavemngt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private EditText username , password;
    private Button login;
    private TextView newuser;
    boolean valid = true;
    //instance of FirebaseAuth
    FirebaseAuth fAuth ;

    //FirebaseStore
    FirebaseFirestore fstore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         //Initalize Firebase Auth
        fAuth = FirebaseAuth.getInstance();
        //Initlize Firebase store
        fstore = FirebaseFirestore.getInstance();

        username = findViewById(R.id.ed_email);
        password = findViewById(R.id.ed_pswd);
        login = findViewById(R.id.btn_login);
        newuser = findViewById(R.id.tv_newuser);
        newuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newuser = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(newuser);
                finish();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkField(username);
                checkField(password);

                //if everything is filled sign up new users
                if(valid){
                    // new user
                    fAuth.signInWithEmailAndPassword(username.getText().toString(),password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            //sign in success , update Ui with signed -in user information

                         Toast.makeText(MainActivity.this,"Succesfully Loged in" ,Toast.LENGTH_SHORT).show();
                         checkuserAccessLevel(authResult.getUser().getUid());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                         Toast.makeText(MainActivity.this ,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }
        });

    }

    private void checkuserAccessLevel(String uid) {
        //document Reference is used  to extract the data and restore data into restore
        DocumentReference df = fstore.collection("Users").document(uid);
        //extract the data from document
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("TAG" ,"onSucess:" + documentSnapshot.getData());
                //idetify the user level
                //if user is admin
                if(documentSnapshot.getString("isUser") == null){
                    //user is admin
                    startActivity(new Intent(getApplicationContext(),AdminActivity.class));
                    finish();

                }
                //if user is common person
                if(documentSnapshot.getString("isUser") != null){
                    startActivity(new Intent(getApplicationContext(),Dashboard.class));
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean checkField(EditText username) {


        if(username.getText().toString().isEmpty()){
            username.setError("enter valid info");
            username.requestFocus();
            valid = false;
        }else{
            valid = true;
        }
        return valid ;
    }
    //TO check  if the user is currently signed in
    @Override
    protected void onStart(){
        super.onStart();

        //check if user is signed in and update Ui accordingly
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
           DocumentReference df = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
           df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
               @Override
               public void onSuccess(DocumentSnapshot documentSnapshot) {
                   if(documentSnapshot.getString("isUser") == null){
                       //user is admin
                       startActivity(new Intent(getApplicationContext(),AdminActivity.class));
                       finish();

                   }
                   if(documentSnapshot.getString("isUser") != null){
                       startActivity(new Intent(getApplicationContext(),Dashboard.class));
                       finish();
                   }

               }
           }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                  FirebaseAuth.getInstance().signOut();
                  startActivity(new Intent(getApplicationContext() ,MainActivity.class));
                  finish();
               }
           });

        }
    }

}