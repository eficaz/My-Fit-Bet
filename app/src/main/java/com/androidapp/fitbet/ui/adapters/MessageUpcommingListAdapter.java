package com.androidapp.fitbet.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.model.MessageChatListUpCommingBets;
import com.androidapp.fitbet.ui.MessageActivity;
import com.androidapp.fitbet.ui.fragments.CreateGroupFragment;
import com.androidapp.fitbet.utils.CircleImageView;
import com.androidapp.fitbet.utils.Contents;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MessageUpcommingListAdapter extends RecyclerView.Adapter {
    Context constant;
    public ArrayList<MessageChatListUpCommingBets> groupListModels;
    // public List<Archives> selected_usersList=new ArrayList<>();
    private List<MessageChatListUpCommingBets> contactListFiltered;
    private static CreateGroupFragment.RecyclerViewClickListener itemListener;

    public MessageUpcommingListAdapter(Context context, ArrayList<MessageChatListUpCommingBets> myDataset) {
        this.constant = context;
        this.groupListModels = myDataset;
        //this.selected_usersList = selectedList;
        contactListFiltered = new ArrayList<>();
        contactListFiltered.addAll(groupListModels);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_archives_item_list, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final MessageChatListUpCommingBets m = groupListModels.get(position);
        final ViewHolder viewholder = (ViewHolder) holder;
        viewholder.tv_Name.setText(m.getBetname());
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = df.parse(m.getDate());
            df.setTimeZone(TimeZone.getDefault());
            String formattedDate = df.format(date);
            DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
            String output = null;
            output = outputformat.format(date);
            viewholder.tv_Description.setText(output);
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewholder.left_days.setText("" + Double.parseDouble(m.getDistance()) / 1000 + "km");
        viewholder.rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(constant, MessageActivity.class);
                i.putExtra(Contents.MYBETS_betid, m.getBetid());
                i.putExtra(Contents.MYBETS_betname, m.getBetname());
                i.putExtra(Contents.TOTAL_PARTICIPANTS, m.getTotal());
                constant.startActivity(i);
            }
        });
        viewholder.tv_groupCount.setText(m.getCredit());





        /*if (!m.getProfile_pic().equals("NA")) {
            if (m.getReg_key().equals("normal")&&m.getImage_status().equals("0")){
                //Picasso.get().load(Constant.BASE_APP_IMAGE__PATH+jsonObject1.getString(PROFILE_PIC)).into(img_user);
                Picasso.get().load(Constant.BASE_APP_IMAGE__PATH+m.getProfile_pic())
                        .placeholder(R.drawable.image_loader)
                        .into(viewholder.img_user);
            }else{
                Picasso.get().load(m.getProfile_pic())
                        .placeholder(R.drawable.image_loader)
                        .into(viewholder.img_user);
            }
        }
        else{
            viewholder.img_user.setImageDrawable(constant.getResources().getDrawable(R.drawable.user_profile_avatar));
        }*/


    }

    public void filterList(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        groupListModels.clear();
        if (charText.length() == 0) {
            groupListModels.addAll(contactListFiltered);
        } else {
            for (MessageChatListUpCommingBets wp : contactListFiltered) {
                if (wp.getBetname().toLowerCase(Locale.getDefault()).contains(charText)) {
                    groupListModels.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView img_user;
        TextView tv_Name, tv_Description, left_days, tv_groupCount;
        LinearLayout rowView;

        public ViewHolder(View convertView) {
            super(convertView);
            rowView = convertView.findViewById(R.id.row);
            img_user = convertView.findViewById(R.id.img_user);
            tv_Name = convertView.findViewById(R.id.tv_Name);
            left_days = convertView.findViewById(R.id.left_days);
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

