package com.goodinitiative.vachan_chalval;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahesh on 24-Dec-17.
 */

public class ShowBooks extends AppCompatActivity {
    ListView listView;
    List <layoutView> bookList;

    FirebaseAuth fAuth;
    DatabaseReference fDatabase, fDatabase1;
    StorageReference fStorage;
    int mode;
    int itemselectednumber;
    String itemselwctedname;
    FirebaseUser fUser;
    myAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_catalog_list);
        listView = (ListView) findViewById(R.id.list);
//        registerForContextMenu(listView);
        Bundle bundle = getIntent().getExtras();
        mode = bundle.getString("mode").equals("all")?1:0;          // 1--> all, 0--> only my uploads
        Toast.makeText(this, "mode: "+mode, Toast.LENGTH_SHORT).show();
        bookList  = new ArrayList<>();
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();

        adapter = new myAdapter(ShowBooks.this,bookList,R.layout.listitem);
//        bookList.add(new layoutView(R.drawable.alfa,"alfa"));
        if(mode==1){
            LoadBooks();
        }else if(mode==0){
            LoadMyBook();
        }
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, int i, long l) {
                itemselectednumber = i;
                itemselwctedname = bookList.get(itemselectednumber).name;
                Toast.makeText(ShowBooks.this, "Long tapped on: "+bookList.get(i).name, Toast.LENGTH_LONG).show();
                if(mode == 0) {
                    new AlertDialog.Builder(ShowBooks.this)
                            .setTitle("Action Menu")
                            .setMessage("Select an Action")
                            .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent editbook = new Intent(ShowBooks.this,EditBook.class);
                                    editbook.putExtra("bookname",itemselwctedname);
                                    editbook.putExtra("author",bookList.get(itemselectednumber).getAuthor());
                                    editbook.putExtra("charge",bookList.get(itemselectednumber).getCharge());
                                    editbook.putExtra("yop",bookList.get(itemselectednumber).getYop());
                                    editbook.putExtra("imageUri",bookList.get(itemselectednumber).getImageUri());
                                    System.out.println("***************Show Book******************\n"+bookList.get(itemselectednumber).getImageUri());
                                    dialogInterface.dismiss();
                                    startActivity(editbook);
                                }
                            })
                            .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(ShowBooks.this, "Delete Selected", Toast.LENGTH_SHORT).show();
                                    FirebaseDatabase.getInstance().getReference("Books").child(fAuth.getCurrentUser().getUid()).child(itemselwctedname)
                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ShowBooks.this, "Deleted Succesfully", Toast.LENGTH_LONG).show();
                                                FirebaseStorage.getInstance().getReferenceFromUrl(bookList.get(itemselectednumber).imageUri)
                                                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(ShowBooks.this, "Storage Delete Success", Toast.LENGTH_SHORT).show();
                                                            bookList.remove(itemselectednumber);
                                                            adapter.notifyDataSetChanged();
                                                        } else {
                                                            Toast.makeText(ShowBooks.this, "Database Delete failed!!!!!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(ShowBooks.this, "Database Delete failed", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                                    dialogInterface.dismiss();
                                }
                            }).create().show();
                }
                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(ShowBooks.this, "BookName: "+bookList.get(i).name, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void LoadMyBook() {
        fDatabase = FirebaseDatabase.getInstance().getReference("Books");
        fDatabase.child(fAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot books : dataSnapshot.getChildren()){
                    Book book = books.getValue(Book.class);
                    System.out.println(dataSnapshot.getValue());
                    System.out.println("Book Data: <<bookname:"+book.getBookname()+">> : <<Link:"+book.getCoveruri()+">>");
                    bookList.add(new layoutView(book.getCoveruri(),book.getBookname(),book.getCharge(),book.getAuthor(),book.getYop()));
//                            bookList.add(new layoutView(R.drawable.aston,"aston"+s));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void NoUserTest(){
        if(fUser!=null){
            Toast.makeText(this, "loggedIn"+fUser.getEmail(), Toast.LENGTH_SHORT).show();
        }else{
            fAuth.signInWithEmailAndPassword("mahesh.biswas07@gmail.com","99976284580522");
            fUser = fAuth.getCurrentUser();
            if(fUser!=null){
                Toast.makeText(this, "done, ok?"+fUser.getEmail(), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "kya pata bhai", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void LoadBooks(){
        fDatabase = FirebaseDatabase.getInstance().getReference("Books");
        fDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.clear();
                bookList.clear();
                fDatabase.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        for(DataSnapshot books : dataSnapshot.getChildren()){
                            Book book = books.getValue(Book.class);
                            System.out.println(dataSnapshot.getValue());
                            System.out.println("Book Data: <<bookname:"+book.getBookname()+">> : <<Link:"+book.getCoveruri()+">>");
                            bookList.add(new layoutView(book.getCoveruri(),book.getBookname(),book.getCharge(),book.getAuthor(),book.getYop()));
//                            bookList.add(new layoutView(R.drawable.aston,"aston"+s));
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
