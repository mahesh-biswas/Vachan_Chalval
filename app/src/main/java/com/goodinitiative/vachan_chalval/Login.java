package com.goodinitiative.vachan_chalval;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class Login extends AppCompatActivity {
    EditText email,password;
    Button submit,signup;
    ProgressDialog progressDialog;
    DatabaseReference fDatabase;
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = (EditText)findViewById(R.id.email);
        password=(EditText) findViewById(R.id.password);
        submit = (Button) findViewById(R.id.submit);
        signup = (Button) findViewById(R.id.signup);


        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setTitle("Logging In");
        progressDialog.setMessage("Loading...");



/****************************************************************************
 *                                      OnClickListners......................
 *
 ****************************************************************************/
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                Login();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(Login.this,SignUp.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        fAuth = FirebaseAuth.getInstance();
        FirebaseUser user = fAuth.getCurrentUser();
        if(user!=null){
            finish();
            startActivity(new Intent(Login.this,NavTemp.class));
        }
    }

    private void Login(){
        if(!haveAll(email,password)){
            return;
        }
        fAuth = FirebaseAuth.getInstance();
        fAuth.signInWithEmailAndPassword(e2t(email),e2t(password)).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isComplete()){
                    progressDialog.dismiss();
                    if(task.isSuccessful()){
                        toast("Login Succesfull");
                        FirebaseUser user = fAuth.getCurrentUser();
                        toast("Welcome email:"+user.getEmail());
                        finish();
                        startActivity(new Intent(Login.this,NavTemp.class));
                    }else{
                        toast("Login Failed");
                    }
                }
            }
        });
    }


    @NonNull
    private String e2t(EditText editText){
        return editText.getText().toString().trim();
    }

    private void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    private void checkAll(int x){

    }
    private boolean haveAll(EditText... params){
        for(int x=0;x<params.length;x++){
            if(params[x].getText().toString().equals("")){
                toast("The Field Cannot be Empty: "+x);
                toast("@"+x);
                return false;
            }
        }
        return true;
    }
}
