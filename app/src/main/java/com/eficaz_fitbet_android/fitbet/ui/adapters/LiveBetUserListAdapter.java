package com.eficaz_fitbet_android.fitbet.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eficaz_fitbet_android.fitbet.R;
import com.eficaz_fitbet_android.fitbet.model.LiveBetDetails;
import com.eficaz_fitbet_android.fitbet.network.Constant;
import com.eficaz_fitbet_android.fitbet.ui.ProfileActivity;
import com.eficaz_fitbet_android.fitbet.ui.fragments.CreateGroupFragment;
import com.eficaz_fitbet_android.fitbet.utils.AppPreference;
import com.eficaz_fitbet_android.fitbet.utils.CircleImageView;
import com.eficaz_fitbet_android.fitbet.utils.Contents;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LiveBetUserListAdapter extends RecyclerView.Adapter  {
    Context constant;
    public ArrayList<LiveBetDetails> groupListModels;
   // public List<Archives> selected_usersList=new ArrayList<>();
    private List<LiveBetDetails> contactListFiltered;
    private static CreateGroupFragment.RecyclerViewClickListener itemListener;
    public LiveBetUserListAdapter(Context context, ArrayList<LiveBetDetails> myDataset) {
        this.constant = context;
        this.groupListModels = myDataset;
        //this.selected_usersList = selectedList;
        contactListFiltered = new ArrayList<>();
        contactListFiltered.addAll(groupListModels);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.live_bet_user_item_list, parent, false);
        LiveBetUserListAdapter.ViewHolder vh = new LiveBetUserListAdapter.ViewHolder(v);
        return vh;
    }
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final LiveBetDetails m = groupListModels.get(position);
        final LiveBetUserListAdapter.ViewHolder viewholder = (LiveBetUserListAdapter.ViewHolder) holder;
        viewholder.tv_Name.setText(m.getFirstname());
        viewholder.tv_Description.setText(m.getCountry());
       /* final DecimalFormat f = new DecimalFormat("##.00");
        viewholder.km.setText(""+f.format(Double.valueOf(m.getDistance())/1000)+" km");*/
       viewholder.km.setText(String.format(Locale.getDefault(), "%.2f", Double.valueOf(m.getDistance())/1000));
        viewholder.userPostion.setText(m.getPosition());
        final Context mContext = constant ;
        if (!m.getProfile_pic().equals("NA")) {
            if (m.getRegType().equals("normal")&&m.getImage_status().equals("0")){

                Picasso.get().
                        load(Constant.BASE_APP_IMAGE__PATH+m.getProfile_pic())
                        .placeholder(R.drawable.image_loader)
                        .into(viewholder.img_user);

            }else{
                Picasso.get().
                        load(m.getProfile_pic())
                        .placeholder(R.drawable.image_loader)
                        .into(viewholder.img_user);

            }
        } else {
            viewholder.img_user.setImageDrawable(mContext.getResources().getDrawable(R.drawable.user_profile_avatar));
        }


        viewholder.rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppPreference.getPrefsHelper().savePref(Contents.USER_PROFILE_REG_KEY, m.getReg_key());
                Intent i = new Intent(constant, ProfileActivity.class);
                constant.startActivity(i);
            }
        });
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView img_user;
        TextView tv_Name, userPostion,tv_Description,km;
        LinearLayout rowView;
        public ViewHolder(View convertView) {
            super(convertView);
            rowView=  convertView.findViewById(R.id.row);
            img_user = convertView.findViewById(R.id.img_user);
            tv_Name = convertView.findViewById(R.id.tv_Name);
            userPostion= convertView.findViewById(R.id.userPostion);
            tv_Description = convertView.findViewById(R.id.tv_Description);
            km = convertView.findViewById(R.id.km);
            itemView.setTag(itemView);
        }
    }
    @Override
    public int getItemCount() {
        return groupListModels.size() > 0 ? groupListModels.size() : 0;
    }
}

