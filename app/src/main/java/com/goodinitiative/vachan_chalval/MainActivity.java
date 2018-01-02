package com.goodinitiative.vachan_chalval;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    Toolbar toolbar;
    String[] tabs = {"Tab1","Tab2","Tab3","Tab4"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Setting selected", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(MainActivity.this,NavTemp.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch(position){
                case 1:     return new Test();
                case 2:     return new catGrid();
                case 3:     return new catList();
            }
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }
    }

    public static class Test extends Fragment{

        private static final int REQUEST_CODE_FILE_CHOOSER_my = 3240;
        ImageView imageView;
        Button browse;
        private Uri imguri;
        Context context;
        Toolbar toolbar;
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            context = inflater.getContext();
            checkFilePermission();
            View newView = inflater.inflate(R.layout.activity_test,container,false);
            imageView = (ImageView) newView.findViewById(R.id.imageviewer);
            browse = (Button) newView.findViewById(R.id.brows);
            toolbar = (Toolbar) newView.findViewById(R.id.toolbar);
            toolbar.setVisibility(View.GONE);
            browse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent imgIntent = new Intent();
                    imgIntent.setType("image/*");
                    imgIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(imgIntent,"Select an Image"),REQUEST_CODE_FILE_CHOOSER_my);
                }
            });

            return newView;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if(requestCode == REQUEST_CODE_FILE_CHOOSER_my && resultCode == RESULT_OK && data != null && data.getData() != null){
                imguri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(),imguri);
                    imageView.setImageBitmap(bitmap);
                }catch (Exception e){
                    toast(e.getMessage());
                }
            }
        }
        private void toast(String msg){
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
        private void checkFilePermission(){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                int permissioncheck = context.checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
                permissioncheck += context.checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
                if(permissioncheck!=0){
                    this.requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},1);
                }
            }
        }
    }

    public static class catGrid extends Fragment{
        GridView grid;
        String []data = {"Star Wars","Star Trek","I Robot","Flash","Green Arrow","Iron Man"};
        String []name = {"alfa","aston","audi","bentley","bmw","bugatti","carbon","chevy","dodge","ford","jaguar","koen","lambo","maserati","mazda","mclaren","merc","mitsu","nissan","pragani","porsche","subaru"};
        int []icons = {R.drawable.alfa,R.drawable.aston,R.drawable.audi,R.drawable.bentley,R.drawable.bmw,R.drawable.bugatti,R.drawable.carbon,
                R.drawable.chevy,R.drawable.dodge,R.drawable.ford,R.drawable.jag,R.drawable.koen,R.drawable.lambo,R.drawable.maserati,R.drawable.mazda,
                R.drawable.mclaren,R.drawable.merc,R.drawable.mitsu,R.drawable.nissan,R.drawable.pagani,R.drawable.porsche,R.drawable.subaru};
        Context context;
        List<layoutView> list;
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            context = inflater.getContext();
            View newView = inflater.inflate(R.layout.frag_catalog_grid,container,false);
            grid = (GridView) newView.findViewById(R.id.grid);
            list  = new ArrayList<>();
            for(int i = 0 ; i<icons.length;i++){
                list.add(i,new layoutView(icons[i],name[i]));
            }
//            ArrayAdapter<String> adapter = new ArrayAdapter<String>(inflater.getContext(),android.R.layout.simple_list_item_1,data);
//            grid.setAdapter(adapter);
            myAdapter adapter = new myAdapter(this,list,R.layout.griditem);
            grid.setAdapter(adapter);
            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    toast("Item name is: "+list.get(i).name);
                }
            });
            return newView;
        }
        private void toast(String msg){
            Toast.makeText(this.context, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public static class catList extends Fragment{
        ListView listView;
        SearchView search;
        String []data = {"Star Wars","Star Trek","I Robot","Flash","Green Arrow","Iron Man"};
        Context context;
        List<layoutView> list;
        String []name = {"alfa","aston","audi","bentley","bmw","bugatti","carbon","chevy","dodge","ford","jaguar","koen","lambo","maserati","mazda","mclaren","merc","mitsu","nissan","pragani","porsche","subaru"};
        int []icons = {R.drawable.alfa,R.drawable.aston,R.drawable.audi,R.drawable.bentley,R.drawable.bmw,R.drawable.bugatti,R.drawable.carbon,
                R.drawable.chevy,R.drawable.dodge,R.drawable.ford,R.drawable.jag,R.drawable.koen,R.drawable.lambo,R.drawable.maserati,R.drawable.mazda,
                R.drawable.mclaren,R.drawable.merc,R.drawable.mitsu,R.drawable.nissan,R.drawable.pagani,R.drawable.porsche,R.drawable.subaru};
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            context = inflater.getContext();
            View newView = inflater.inflate(R.layout.frag_catalog_list,container,false);
            listView = (ListView) newView.findViewById(R.id.list);
            search = (SearchView) newView.findViewById(R.id.search);
            list  = new ArrayList<>();
//            for(int i = 0 ; i<icons.length;i++){
//                list.add(i,new layoutView(icons[i],name[i]));
//            }
//            ArrayAdapter<String> adapter = new ArrayAdapter<String>(inflater.getContext(),android.R.layout.simple_list_item_1,data);
            final myAdapter adapter = new myAdapter(this,list,R.layout.listitem);
//            listView.setAdapter(adapter);
            new thread().execute(adapter);
/*
 *           search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
 *               @Override
 *               public boolean onQueryTextSubmit(String s) {
 *                   return false;
 *               }
*
*                @Override
*                public boolean onQueryTextChange(String s) {
*                    String text = s;
*                    adapter.getFilter().filter(text);
*                    return false;
*                }
*            });
 */
            return newView;
        }
        private void toast(String msg){
            Toast.makeText(this.context, msg, Toast.LENGTH_SHORT).show();
        }
        class thread extends AsyncTask<myAdapter, Integer, String> {
            myAdapter adapter;

            @Override
            protected void onPreExecute() {

            }

            @Override
            protected void onPostExecute(String aVoid) {
                toast(aVoid);
                toast("All Set.");
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        toast("Item name is: "+list.get(i).name);
                    }
                });
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                list.add(values[0],new layoutView(icons[values[0]],name[values[0]]));
            }

            @Override
            protected String doInBackground(myAdapter... params) {
                adapter = params[0];
                for(int i = 0 ; i<icons.length;i++){
                    publishProgress(i);
                }
                return "Done";
            }
        }
    }
    private void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
