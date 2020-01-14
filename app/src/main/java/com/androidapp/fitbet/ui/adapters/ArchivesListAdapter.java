package com.androidapp.fitbet.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.interfaces.SearchNotFound;
import com.androidapp.fitbet.model.Archives;
import com.androidapp.fitbet.ui.fragments.CreateGroupFragment;
import com.androidapp.fitbet.utils.AppPreference;
import com.androidapp.fitbet.utils.CircleImageView;
import com.androidapp.fitbet.utils.Contents;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ArchivesListAdapter extends RecyclerView.Adapter {
    Context constant;
    public ArrayList<Archives> groupListModels;
    private List<Archives> contactListFiltered;
    private static CreateGroupFragment.RecyclerViewClickListener itemListener;
    SearchNotFound searchNotFound;
    public ArchivesListAdapter(Context context, ArrayList<Archives> myDataset) {
        this.constant = context;
        this.groupListModels = myDataset;
        //this.selected_usersList = selectedList;
        contactListFiltered = new ArrayList<>();
        contactListFiltered.addAll(groupListModels);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.archives_item_list, parent, false);
        ArchivesListAdapter.ViewHolder vh = new ArchivesListAdapter.ViewHolder(v);
        return vh;
    }
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final Archives m = groupListModels.get(position);
        final ArchivesListAdapter.ViewHolder viewholder = (ArchivesListAdapter.ViewHolder) holder;
        viewholder.tv_Name.setText(m.getBetname());
        DecimalFormat newFormat = new DecimalFormat("0.00");
        viewholder.km.setText(""+Double.valueOf(newFormat.format(Double.valueOf(m.getDistance())/1000))+" KM");
        try{
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = df.parse(m.getDate());
            df.setTimeZone(TimeZone.getDefault());
            String formattedDate = df.format(date);

            DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
            String output = null;
            output = outputformat.format(date);
            viewholder.tv_Description.setText(output);
        }catch (Exception e){
            e.printStackTrace();
        }
        ///viewholder.tv_Description.setText(m.getDescription());
        double i2= Double.parseDouble(m.getDistance())/1000;
        viewholder.left_days.setText(""+i2+" km");
        viewholder.tv_groupCount.setText(m.getCredit());
        if(m.getWinner().equals(AppPreference.getPrefsHelper().getPref(Contents.REG_KEY,""))){
            viewholder.row_main.setBackground(constant.getDrawable(R.drawable.won_list_bg));
        }
    }
    public void filterList(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        groupListModels.clear();
        if (charText.length() == 0) {
            groupListModels.addAll(contactListFiltered);
        }else {
            for (Archives wp : contactListFiltered) {
                if (wp.getBetname().toLowerCase(Locale.getDefault()).contains(charText)) {
                    groupListModels.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView img_user;
        TextView tv_Name, tv_Description,tv_groupCount,left_days,km;
        LinearLayout rowView;
        ConstraintLayout row_main;
        public ViewHolder(View convertView) {
            super(convertView);
            rowView=  convertView.findViewById(R.id.row);
            row_main=  convertView.findViewById(R.id.row_main);
            img_user = convertView.findViewById(R.id.img_user);
            tv_Name = convertView.findViewById(R.id.tv_Name);
            km= convertView.findViewById(R.id.km);
            left_days= convertView.findViewById(R.id.left_days);
            tv_groupCount = convertView.findViewById(R.id.users_count);
            tv_Description = convertView.findViewById(R.id.tv_Description);
            itemView.setTag(itemView);
        }
    }
    @Override
    public int getItemCount() {
        return groupListModels.size() > 0 ? groupListModels.size() : 0;
    }
}

