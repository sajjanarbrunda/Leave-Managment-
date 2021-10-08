package com.example.leavemngt;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class AdminActivity extends AppCompatActivity {

//      private TextView title;
        private RecyclerView recyclerView;
        private FloatingActionButton fab;
        private  Button logout ;


         private FirebaseDatabase fb;
        private DatabaseReference profileref;
        private FirebaseAuth mAuth;
        private ProgressDialog loader;


        private String post_key ="";
        private String itemname ="";
        private  String itememal = "";
        private String itemrole="";
        private  String itemloc="";
        private  String itemdep ="";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_admin);
//            title = findViewById(R.id.profile_title);
            recyclerView = findViewById(R.id.recyclerView);
            fb = FirebaseDatabase.getInstance();
            logout = findViewById(R.id.btn_logout);
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(AdminActivity.this,"logedout" ,Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AdminActivity.this,MainActivity.class));
                    finish();
                }
            });



            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setStackFromEnd(true);
            linearLayoutManager.setReverseLayout(true);


            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(linearLayoutManager);
            mAuth = FirebaseAuth.getInstance();
            profileref = FirebaseDatabase.getInstance().getReference().child("profile").child(mAuth.getCurrentUser().getUid());
            loader = new ProgressDialog(this);

            profileref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for(DataSnapshot snap : snapshot.getChildren()){
                        Data data = snap.getValue(Data.class);

//                        String  stotal = String.valueOf("profile:  "+totalleveNO);


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            fab =findViewById(R.id.fab);



            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    additem();
                }
            });

        }

        private void additem() {
            AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
            LayoutInflater inflater = LayoutInflater.from(this);
            View myView = inflater.inflate(R.layout.input_layout ,null);
            myDialog.setView(myView);

            final  AlertDialog dialog = myDialog.create();
            dialog.setCancelable(false);


            //database insertion

            final Spinner spinner = myView.findViewById(R.id.spinner);
            final EditText Name = myView.findViewById(R.id.name);
            final EditText location = myView.findViewById(R.id.location);
            final EditText email = myView.findViewById(R.id.email);
            final Button cancle = myView.findViewById(R.id.cancle);
            final Button save = myView.findViewById(R.id.save);
            final EditText role = myView.findViewById(R.id.role);

            save.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {

                    String txt_name = Name.getText().toString();
                    String txt_department = spinner.getSelectedItem().toString();
                    String txt_loct = location.getText().toString();
                    String txt_email = email.getText().toString();
                    String txt_role = role.getText().toString();


                    if(TextUtils.isEmpty(txt_name) || TextUtils.isEmpty(txt_role) || TextUtils.isEmpty(txt_loct) ){
                        Name.setError("enter the name ");
                        Name.requestFocus();
                        location.setError("enter location");
                        location.requestFocus();
                        email.setError("enter email");
                        email.requestFocus();

                        return;
                    }
                    if (spinner.equals("Select the department")){
                        Toast.makeText(AdminActivity.this , "Select a valid item" ,Toast.LENGTH_SHORT).show();
                    }
                    else{
                        loader.setMessage(" adding a info");
                        loader.setCanceledOnTouchOutside(false);
                        loader.show();



                        String id = profileref.push().getKey();
                        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        Calendar cal = Calendar.getInstance();
                        String date  = dateFormat.format(cal.getTime());
                        MutableDateTime epoch = new MutableDateTime();
                        epoch.setDate(0);
                        DateTime now = new DateTime();
                        Months months = Months.monthsBetween(epoch,now);


                        Data data = new Data(txt_name, txt_email, id,  txt_loct, null, txt_role,  date,  txt_department);
//                        Data data1 = new Data(0,0,0);
                        profileref.child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {

                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(AdminActivity.this , "info added succesfully",Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(AdminActivity.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                                }
                                loader.dismiss();
                            }
                        });

                    }
                    dialog.dismiss();
                }
            });


            cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });



            dialog.show();


        }

        @Override
        protected void onStart() {
            super.onStart();

            FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                    .setQuery(profileref , Data.class).build();

            FirebaseRecyclerAdapter<Data,MyViewHolder> adpoter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Data model) {



                    holder.setItemDate("On:" + model.getDate());
                    holder.setItemName("Name: "+model.getName());
                    holder.setItemRole("Role: " + model.getRole());
                    holder.setItemlocation("loaction: " + model.getLoct());
                    holder.setItemEmail("email: " + model.getEmail());
                    holder.setItemdepartemt("department: " +model.getDep());
                    holder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            post_key = getRef(position).getKey();


                         itemname = model.getName();
                         itememal = model.getEmail();
                         itemdep = model.getDep();
                            updateData();
                        }
                    });




                }

                @NonNull
                @Override
                public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrive_layout,parent,false);

                    return new MyViewHolder(view);
                }
            };

            recyclerView.setAdapter(adpoter);
            adpoter.startListening();
            adpoter.notifyDataSetChanged();
        }
        public  class  MyViewHolder extends  RecyclerView.ViewHolder{

            View mView;
            public ImageView imageView;
            public TextView tv_name , tv_date , tv_email , tv_role, tv_dept ,tv_loct;
            public MyViewHolder(@NonNull View itemView){
                super(itemView);

                mView = itemView;
                imageView = itemView.findViewById(R.id.imagview);
                tv_name = itemView.findViewById(R.id.ret_name);
                tv_date = itemView.findViewById(R.id.ret_date);
                tv_email = itemView.findViewById(R.id.ret_email);
                tv_role = itemView.findViewById(R.id.ret_role);
                tv_dept = itemView.findViewById(R.id.ret_department);
                tv_loct = itemView.findViewById(R.id.ret_location);


            }
            public void setItemName(String itemName){
                TextView item = mView.findViewById(R.id.ret_name);
                item.setText(itemName);
            }


            public void setItemDate(String s) {
                TextView date = mView.findViewById(R.id.ret_date);
                date.setText(s);

            }

            public void setItemlocation(String s) {
                TextView loc = mView.findViewById(R.id.ret_location);
                loc.setText(s);

            }

            public void setItemRole(String s) {
                TextView role = mView.findViewById(R.id.ret_role);
                role.setText(s);
            }

            public void setItemEmail(String s) {
                TextView email = mView.findViewById(R.id.ret_email);
                email.setText(s);
            }

            public void setItemdepartemt(String s) {
                TextView dep = mView.findViewById(R.id.ret_department);
                dep.setText(s);

            }
        }
        private  void updateData(){
            AlertDialog.Builder myDailog = new AlertDialog.Builder(this);
            LayoutInflater inflater = LayoutInflater.from(this);
            View mview  = inflater.inflate(R.layout.update_layout,null);

            myDailog.setView(mview);
            final  AlertDialog dialog = myDailog.create();

            final TextView mname = mview.findViewById(R.id.update_txtname);
            final TextView memail = mview.findViewById(R.id.update_txtemail);
            final  TextView mdep = mview.findViewById(R.id.update_txtdep);
            final EditText mlocation = mview.findViewById(R.id.update_loc);
            final  EditText mrole = mview.findViewById(R.id.update_role);

            mname.setText(itemname);
            memail.setText(itememal);
            mdep.setText(itemdep);



            Button delet = mview.findViewById(R.id.delete);
            Button update = mview.findViewById(R.id.update);

            update.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {
                    itemloc  = mlocation.getText().toString();
                    itemrole = mrole.getText().toString();

                    DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");
                    Calendar cal = Calendar.getInstance();
                    String date  = dateFormat.format(cal.getTime());
                    MutableDateTime epoch = new MutableDateTime();
                    epoch.setDate(0);
                    DateTime now = new DateTime();

                    Data data = new Data(itemname, itememal, post_key,  itemloc, null, itemrole,  date,  itemdep);
                    profileref.child(post_key).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(AdminActivity.this , "Updated succesfully",Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(AdminActivity.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                            }
                            loader.dismiss();
                        }
                    });
                    dialog.dismiss();

                }
            });

            delet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    profileref.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(AdminActivity.this , "Deleted succesfully",Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(AdminActivity.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                            }
                            loader.dismiss();
                        }
                    });
                    dialog.dismiss();
                }
            });

            dialog.show();












    }
}