package com.goodinitiative.vachan_chalval;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.opengl.EGLExt;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pkmmte.view.CircularImageView;

/**
 * Created by mahesh on 11/11/2017.
 */

public class NavTemp extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    Button logout;
    CircularImageView civ;
    ImageView profile;
//    CircularImageView profile;
    TextView name,email;
    View navigationHeader;
    DatabaseReference fDatabase;
    FirebaseAuth fAuth;
    FirebaseUser user;
    Toolbar toolbar;
    Uri profileurl;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navtemplate);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);
        navigationView = (NavigationView)findViewById(R.id.navigation);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationHeader = navigationView.getHeaderView(0);
        logout = (Button) navigationHeader.findViewById(R.id.logout);
        profile = (ImageView) navigationHeader.findViewById(R.id.profile);
//        profile = (CircularImageView) navigationHeader.findViewById(R.id.profile);
        name = (TextView) navigationHeader.findViewById(R.id.name);
        email = (TextView) navigationHeader.findViewById(R.id.email);
        toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setHomeButtonEnabled(true);
//        toolbar.setNavigationIcon(R.drawable.ic_receipt_white_48dp);
        toolbar.setTitleMarginStart(150);

//        toolbar.setAnimation(new Animation() {
//        });



        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        if(user!=null){
            toast(user.getEmail());
            email.setText(user.getEmail());
        }/*else{
            fAuth.signInWithEmailAndPassword("mahesh.biswas07@gmail.com","99976284580522");
            System.out.print("mai karra signin");
            user = fAuth.getCurrentUser();
        }*/

        fDatabase = FirebaseDatabase.getInstance().getReference("Users");

        fDatabase.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User usr = dataSnapshot.getValue(User.class);
                if(usr!=null){
                    toast(usr.getName());
                    name.setText(usr.getName());
                    profileurl = Uri.parse(usr.getProfileurl());
                    Glide.with(getApplicationContext())
                            .load(profileurl)
                            .placeholder(android.R.drawable.progress_horizontal)
                            .into(profile);
                    toast("glide?");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




/**
 *                                      <----------------Click Actions----------------------------->
 */

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.books :
                        Intent showbooksall = new Intent(NavTemp.this,ShowBooks.class);
                        showbooksall.putExtra("mode","all");
                        startActivity(showbooksall);
                        break;
                    case R.id.manage:
                        Intent showbooksonlymy = new Intent(NavTemp.this,ShowBooks.class);
                        showbooksonlymy.putExtra("mode","my");
                        startActivity(showbooksonlymy);
                        break;
                    case R.id.test_activity:
                        finish();
                        startActivity(new Intent(NavTemp.this,TestActivity.class));
                        break;
                    case R.id.main_activity:
                        finish();
                        startActivity(new Intent(NavTemp.this,MainActivity.class));
                        break;
                    case R.id.account:
                        toast("My Account");
                        break;
                    case R.id.signout:
                        SignOut();
                        break;
                        default: toast("item selected");
                }
                return false;
            }
        });
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    public void SignOut() {
        toast("SignOut Requested");
        if(fAuth.getCurrentUser()!=null) {
            fAuth.signOut();
        }
        finish();
        startActivity(new Intent(NavTemp.this,Login.class));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }
}
