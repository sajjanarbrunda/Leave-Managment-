package com.example.leavemngt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AboutUs extends AppCompatActivity {
    DrawerLayout drawerLayout;
    TextView message;
    String mes=" ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        drawerLayout = findViewById(R.id.drawer_layout);
        message = findViewById(R.id.message);
        mes = "\t\t\t\t\t\t\t\t get_Leave  mission is to  track leave numbers of teachers with their profile and to send mail to  authority and to store their information.\n\t we are team of 4 members namely Bhavana , Brunda , kavita  and kavya we joined together to develop this application with an intesion of making it helpful  for teacher and we will continue to work on it and keep updating .";
   message.setText(mes);


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
        //redirect to Leave class
        Dashboard.redirectActivity(this, ProfileActivity.class);

    }
    public  void ClickABoutUs(View view){

        //Recrete activity
        recreate();
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