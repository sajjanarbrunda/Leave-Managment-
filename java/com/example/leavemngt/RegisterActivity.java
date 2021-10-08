package com.example.leavemngt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText Rusername , Rpassword , Remail , Rphonenum;
    private Button register;
    private  TextView loginPage;
    boolean valid = true;
    RadioGroup radioGroup;
    RadioButton admin , user ;
    //creating instances

    FirebaseAuth fAuth;
    FirebaseFirestore fstore;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
         //initlize the authication and firebasestore
        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();



        Rusername = findViewById(R.id.ed_Rname);
        Rpassword = findViewById(R.id.ed_Rpswd);
        Remail = findViewById(R.id.ed_Remail);
        Rphonenum = findViewById(R.id.ed_RphoneNo);
        register = findViewById(R.id.btn_register);
        loginPage = findViewById(R.id.tv_loginPage);
        radioGroup = findViewById(R.id.user_type);
        admin = findViewById(R.id.rb_Radmin);
        user = findViewById(R.id.rb_Ruser);

        loginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginpage = new Intent(RegisterActivity.this , MainActivity.class);
                startActivity(loginpage);
                finish();
            }
        });

       register.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               checkField(Rusername);
               checkField(Rpassword);
               checkField(Remail);
               checkField(Rphonenum);

               // to verfify the userlevel
               if(!(user.isChecked() || admin.isChecked())){
                   Toast.makeText(RegisterActivity.this, "Select account type", Toast.LENGTH_SHORT).show();
                   return;

               }

               if(valid){
                   //start the user registration process

                   fAuth.createUserWithEmailAndPassword(Remail.getText().toString() , Rpassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                       @Override
                       public void onSuccess(AuthResult authResult) {
                           //get info user created
                           FirebaseUser user = fAuth.getCurrentUser();
                           Toast.makeText(RegisterActivity.this ,"Account Created" ,Toast.LENGTH_SHORT).show();
                           //to form user documentation
                           DocumentReference df = fstore.collection("Users").document(user.getUid());
                           Map<String , Object> userInfo = new HashMap<>();
                           userInfo.put("FullName" ,Rusername.getText().toString());
                           userInfo.put("UserEmail" ,Remail.getText().toString());
                           userInfo.put("PhoneNumber" , Rphonenum.getText().toString());
                           userInfo.put("helath_leave" , 0);
                           userInfo.put("Casual_leave" , 0);
                           userInfo.put("edu_levave" , 0);



                           //specify if the user is admin
                           userInfo.put("isUser" ,usertype());

                           df.set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                   Toast.makeText(RegisterActivity.this , "Sucessfully Created" , Toast.LENGTH_SHORT).show();

                               }
                           }).addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e) {
                                   Toast.makeText(RegisterActivity.this ,"falied retry" ,Toast.LENGTH_SHORT).show();
                               }
                           });
                           startActivity(new Intent(getApplicationContext(),MainActivity.class));
                           finish();
                       }
                   }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           Toast.makeText(RegisterActivity.this ,"Failed to create Account" ,Toast.LENGTH_SHORT).show();

                       }
                   });
               }


           }
       });





    }

    private String usertype() {
        if (user.isChecked()) {
            return "1";
        }
        return null;
    }


    private boolean checkField(EditText rusername) {
        if(rusername.getText().toString().isEmpty()){
            rusername.setError("enter valid info");
            rusername.requestFocus();
            valid = false;
        }else{
            valid = true;
        }
        return valid ;
    }



}