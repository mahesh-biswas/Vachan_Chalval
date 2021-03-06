package com.goodinitiative.vachan_chalval;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class UploadBook extends AppCompatActivity {

    private Spinner year;
    private ImageView imageView;
    private Button browse,submit;
    EditText bookname,authorname,charge;
    ProgressBar progress;
    TextView progressText;
    private Uri imguri;
    private static final int REQUEST_CODE_FILE_CHOOSER_my = 3240;
    FirebaseAuth fAuth;
    DatabaseReference fDatabase;
    StorageReference fStorage;
    FirebaseUser fUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        browse = (Button)findViewById(R.id.brows);
        imageView = (ImageView)findViewById(R.id.imageviewer);
        year = (Spinner)findViewById(R.id.year);
        bookname = (EditText)findViewById(R.id.bookname);
        authorname = (EditText)findViewById(R.id.bookauthor);
        charge = (EditText)findViewById(R.id.rentalcharge);
        submit = (Button) findViewById(R.id.submit);
        progress = (ProgressBar) findViewById(R.id.progress);
        progressText = (TextView) findViewById(R.id.progressText);
        Toolbar mtToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mtToolbar);
        checkFilePermission();
        ArrayAdapter AA =ArrayAdapter.createFromResource(UploadBook.this,R.array.yearofpurchase,android.R.layout.simple_list_item_activated_1);
        year.setAdapter(AA);









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
                if(checkFields()){
                    toast("All looks good");
                }else{
                    return;
                }
                    User();
            }
        });
    }

    private void User(){
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        if(fUser!=null){
            Storage(fUser.getUid());
        }else{
            toast("user is null. LogIn.");
            return;
        }
    }
    private void Storage(final String child){
        fStorage = FirebaseStorage.getInstance().getReference("images");
        fStorage.child(child).child("books").child(e2t(bookname)).putFile(imguri)
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double prog = (100*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                        progressText.setText(""+(int)prog+"%");
                        progress.setProgress((int)prog);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toast("failed to upload");
                        toast("check your Internet Connecion");
                        toast("check your Internet Connecion");
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri url = taskSnapshot.getDownloadUrl();
                        toast("Upload Success.");
                        toast(url.toString());
                        Database(child,url);
                    }
                });
    }
    private void Database(String child,Uri uri){
        fDatabase = FirebaseDatabase.getInstance().getReference("Books");
        fDatabase.child(child).child(e2t(bookname)).setValue(new Book(e2t(bookname),e2t(authorname),e2t(charge),year.getSelectedItem().toString(),uri.toString(),child))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            new AlertDialog.Builder(UploadBook.this)
                                    .setTitle("Result")
                                    .setMessage("Process Completed succesfully.\n Book Added to Database")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    }).create().show();
                        }else{
                            toast("task failed.");
                        }
                    }
                });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.mymenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.main_activity){
            toast("Activity No More Exist");
        }else if(item.getItemId() == R.id.logout){
            toast("logout selected");
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(UploadBook.this,Login.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkFilePermission(){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                int permissioncheck = UploadBook.this.checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
                permissioncheck += UploadBook.this.checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
                if(permissioncheck!=0){
                    this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},1);
                }
            }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_FILE_CHOOSER_my && resultCode == RESULT_OK && data != null && data.getData() != null){
            imguri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imguri);
                imageView.setImageBitmap(bitmap);
            }catch (Exception e){
                toast(e.getMessage());
            }
        }
    }
    private void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    @NonNull
    private String e2t(EditText editText){
        return editText.getText().toString().trim();
    }
//    protected  String getImageExt(Uri uri){
//        ContentResolver contentResolver = getContentResolver();
//        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
//        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
//    }
    private boolean checkFields(){
        if(imguri==null){
            toast("select an image");
            return false;
        }
        if(bookname.getText().toString().equals("")){
            toast("Enter Book Name");
            return false;
        }
        if(authorname.getText().toString().equals("")){
            toast("Enter Author Name");
            return false;
        }
        if(year.getSelectedItem().toString().equals("Select Year Of Purchase")){
            toast("Please select the year of purchase.");
            return false;
        }
        if(charge.getText().toString().equals("")){
            new AlertDialog.Builder(UploadBook.this)
                    .setTitle("Are you sure?")
                    .setMessage("is the book free of charge?")
                    .setPositiveButton("Fix It", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            toast("Book is free of cost(rental)");
                            charge.setText("0.0");
                        }
                    }).create().show();
            return false;
        }else{
            if(Double.parseDouble(charge.getText().toString())<0.0){
                toast("Sorry but negative currency does not exist.");
                return false;
            }
        }
        return true;
    }
}
