package com.example.leavemngt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class Dashboard extends AppCompatActivity {
    //intiliaze the variable
    DrawerLayout drawerLayout;
    Button buttonHL , buttonCL, buttonProfile , buttonEL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        drawerLayout= findViewById(R.id.drawer_layout);
        buttonHL = findViewById(R.id.buttonHL);

        buttonCL = findViewById(R.id.buttonCL);
        buttonProfile = findViewById(R.id.buttonProfile);
        buttonEL = findViewById(R.id.buttonEL);
        buttonEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentEL = new Intent(Dashboard.this , EducationLeave.class);
                startActivity(intentEL);
                finish();
            }
        });
        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentprofile = new Intent(Dashboard.this , ProfileActivity.class);
                startActivity(intentprofile);
                finish();
            }
        });
        buttonCL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCl = new Intent(Dashboard.this , CasualLeave.class);
                startActivity(intentCl);
                finish();
            }
        });
        buttonHL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHl = new Intent(Dashboard.this ,HealthLeave.class);
                startActivity(intentHl);
                finish();
            }
        });




    }
    public void ClickMenu(View view){
        //open drawer

        openDrawer(drawerLayout);
    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        //open drawer Layout
        drawerLayout.openDrawer(GravityCompat.START);

    }

    public  void ClickLogo(View view){
        //close Drawer
        closeDrawer(drawerLayout);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        //close drawer layout
        //chcek condition
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            //when  drawer is open
            //close drawer
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }
    public void ClickHome(View view){
        //Recreatr activity
        recreate();
    }
    public  void ClickDashboard(View view){
        //redirect activity to dashboard
        redirectActivity(this, ProfileActivity.class);
    }

    public void ClickABoutUs(View view){
        redirectActivity(this, AboutUs.class);

    }
    public void ClickLogout(View view){
        //close app
        logout(this);
    }
    public static void logout(Activity activity){
        //Intilize alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        //set title
        builder.setTitle("LogOut");
        //set Message
        builder.setMessage("Are you Sure ?");
        //positive yes button
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                FirebaseAuth.getInstance().signOut();
                redirectActivity(activity,MainActivity.class);
                       }
        });
        //negivty No
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //DIsmiss dialog
                dialog.dismiss();
            }
        });

        //show dialog
        builder.show();

    }
    public static void redirectActivity(Activity activity , Class aclass) {
        //Initialize intent
        Intent intent = new Intent(activity,aclass);
        //set flag
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //start activity
        activity.startActivity(intent);


    }
    @Override
    protected  void onPause(){
        super.onPause();
        //close drawer
        closeDrawer(drawerLayout);
    }
}
