package com.goodinitiative.vachan_chalval;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahesh on 11/9/2017.
 */

public class myAdapter extends ArrayAdapter<layoutView>{
    Activity activity;
    Context context;
    List<layoutView> list;
    List<layoutView> orig;
    int resource;
    public myAdapter(@NonNull MainActivity.catGrid catGrid, @NonNull List<layoutView> objects, int resource) {
        super(catGrid.context, resource, objects);
        this.context = catGrid.context;
        this.list = objects;
        this.resource = resource;
    }

    public myAdapter(MainActivity.catList catList, @NonNull List<layoutView> objects, int resource) {
        super(catList.context, resource, objects);
        this.context = catList.context;
        this.list = objects;
        this.resource = resource;
    }

    public myAdapter(ShowBooks bookList, @NonNull List<layoutView> books, int resource){
        super(bookList.getApplicationContext(),resource,books);
        this.context = bookList.getApplicationContext();
        this.list = books;
        this.resource = resource;
    }


    @Override
    public int getCount() {
        return super.getCount();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        context = parent.getContext();
        View mview;
        mview = LayoutInflater.from(parent.getContext()).inflate(resource,null,false);
        ImageView icon = (ImageView) mview.findViewById(R.id.icon);
        TextView name = (TextView) mview.findViewById(R.id.name);
//        icon.setImageResource(list.get(position).image);
        Glide.with(context).load(Uri.parse(list.get(position).imageUri)).placeholder(R.drawable.loadingprimary).into(icon);
        name.setText(list.get(position).name);

        return mview;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<layoutView> results = new ArrayList<>();
                if(orig == null){
                    orig = list;
                }
                if(charSequence!=null){
                    if(orig!=null && orig.size()>0){
                        for(final layoutView lv : orig){
                            if(lv.name.toLowerCase().contains(charSequence.toString().toLowerCase())){
                                results.add(lv);
                            }
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                list = (ArrayList<layoutView>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
