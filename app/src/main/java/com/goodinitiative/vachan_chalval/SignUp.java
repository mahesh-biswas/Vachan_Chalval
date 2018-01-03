package com.goodinitiative.vachan_chalval;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SignUp extends AppCompatActivity {
    EditText email,name,password,contact,addr;
    ImageView profile;
    Button submit,login,browse;
    ProgressBar progress;
    TextView progressText;
    Uri profileUri;
    DatabaseReference fDatabase;
    FirebaseAuth fAuth;
    StorageReference fStorage;
    private static final int REQUEST_CODE_FILE_CHOOSER_my = 3240;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        profile = (ImageView) findViewById(R.id.profile);
        browse = (Button) findViewById(R.id.browse);
        submit = (Button) findViewById(R.id.submit);
        login = (Button) findViewById(R.id.login);
        progress = (ProgressBar) findViewById(R.id.progress);
        progressText = (TextView) findViewById(R.id.progressText);
        email = (EditText) findViewById(R.id.email);
        name = (EditText) findViewById(R.id.name);
        password = (EditText) findViewById(R.id.password);
        contact = (EditText) findViewById(R.id.contact);
        addr = (EditText) findViewById(R.id.addr);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            startActivity(new Intent(SignUp.this,UploadBook.class));
            toast(user.getEmail()+" : "+user.getPhotoUrl());
        }



        checkFilePermission();


        profile.setImageResource(R.drawable.profile_usisex);
/****************************************************************************
 *                                      OnClickListners......................
 *
 ****************************************************************************/
        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imgIntent = new Intent();
                imgIntent.setType("image/*");
                imgIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(imgIntent,"Select an Image"),REQUEST_CODE_FILE_CHOOSER_my);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    progress.setMin(0);
                }
                progress.setMax(100);
                progress.setProgress(0);
                SignUp();
                progress.setProgress(0);
                progressText.setText(progress.getProgress()+"%");

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUp.this,Login.class));
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_FILE_CHOOSER_my && resultCode == RESULT_OK && data != null && data.getData() != null){
            profileUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),profileUri);
                profile.setImageBitmap(bitmap);
            }catch (Exception e){
                toast(e.getMessage());
            }
        }
    }

    private void checkFilePermission(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int permissioncheck = SignUp.this.checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
            permissioncheck += SignUp.this.checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
            if(permissioncheck!=0){
                this.requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
        }
    }

    @NonNull
    private String e2t(EditText editText){
        return editText.getText().toString().trim();
    }
    private void SignUp(){
        if(profileUri==null||profileUri.equals("")){
            new AlertDialog.Builder(SignUp.this)
                    .setTitle("No Profile selected")
                    .setMessage("Please Select an Image for profile.")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create().show();
            return;
        }
        Auth();
    }

    private void Auth(){
        fAuth = FirebaseAuth.getInstance();
        FirebaseUser usr = fAuth.getCurrentUser();
        if(usr != null){
            toast("user SignedIn."+usr.getEmail());
            fAuth.signOut();
            toast("user SignOut Success");
        }
        toast(e2t(email)+":"+e2t(password));
        fAuth.createUserWithEmailAndPassword(e2t(email),e2t(password))
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            toast("Auth Success");
                            FirebaseUser user = fAuth.getCurrentUser();
                            if(user!=null) {
                                Storage(user.getUid());
                            }else{
                                toast("Kya Pata be :83");
                            }
                        }else{
                            toast("Auth Failed");
                        }
                    }
                });
    }

    private void Storage(final String child){
        progress.setProgress(0);
        progressText.setText(progress.getProgress()+"%");
        if(profileUri==null||profileUri.equals("")){
            new AlertDialog.Builder(SignUp.this)
                    .setTitle("No Profile selected")
                    .setMessage("Please Select an Image for profile.")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create().show();
            return;
        }
        if(child!=null){
            fStorage = FirebaseStorage.getInstance().getReference("images");
            fStorage.child(child).child("profile").putFile(profileUri)
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double prog = (100*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                            progressText.setText(""+(int)prog+"%");
                            progress.setProgress((int)prog);
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri url = taskSnapshot.getDownloadUrl();
                            toast("Upload Success.");
                            toast(url.toString());
                            Data(child,url);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            toast("Storage Failed");
                            toast(e.getMessage());
                        }
                    });
        }
    }

    private void Data(String child, Uri url){
        if(child!=null) {
            fDatabase = FirebaseDatabase.getInstance().getReference("Users");
            fDatabase.child(child).setValue(new User(e2t(name),e2t(contact),e2t(addr),url.toString()))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        new AlertDialog.Builder(SignUp.this)
                                .setTitle("Regestration")
                                .setMessage("All Done Successfuly")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
//                                        fAuth.signOut();
//                                        toast("logOut Success");
                                        finish();
                                        startActivity(new Intent(SignUp.this,UploadBook.class));
                                    }
                                })
                                .create().show();
                    }
                });
        }
    }

    private void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
