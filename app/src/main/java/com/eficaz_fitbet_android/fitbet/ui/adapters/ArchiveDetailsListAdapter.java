package com.eficaz_fitbet_android.fitbet.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eficaz_fitbet_android.fitbet.R;
import com.eficaz_fitbet_android.fitbet.model.ArchivesDetails;
import com.eficaz_fitbet_android.fitbet.network.Constant;
import com.eficaz_fitbet_android.fitbet.ui.ArchiveListMapDetailedActivity;
import com.eficaz_fitbet_android.fitbet.ui.fragments.CreateGroupFragment;
import com.eficaz_fitbet_android.fitbet.utils.CircleImageView;
import com.eficaz_fitbet_android.fitbet.utils.Contents;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ArchiveDetailsListAdapter extends RecyclerView.Adapter  {
    Context constant;
    public ArrayList<ArchivesDetails> groupListModels;
   // public List<Archives> selected_usersList=new ArrayList<>();
    private List<ArchivesDetails> contactListFiltered;
    private static CreateGroupFragment.RecyclerViewClickListener itemListener;
    public ArchiveDetailsListAdapter(Context context, ArrayList<ArchivesDetails> myDataset) {
        this.constant = context;
        this.groupListModels = myDataset;
        //this.selected_usersList = selectedList;
        contactListFiltered = new ArrayList<>();
        contactListFiltered.addAll(groupListModels);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.archive_details_item_list, parent, false);
        ArchiveDetailsListAdapter.ViewHolder vh = new ArchiveDetailsListAdapter.ViewHolder(v);
        return vh;
    }
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final ArchivesDetails m = groupListModels.get(position);
        final ArchiveDetailsListAdapter.ViewHolder viewholder = (ArchiveDetailsListAdapter.ViewHolder) holder;
        viewholder.tv_Name.setText(m.getFirstname());
        viewholder.tv_Description.setText(m.getCountry());
        DecimalFormat newFormat = new DecimalFormat("0.00");
        viewholder.km.setText(""+Double.valueOf(newFormat.format(Double.valueOf(m.getDistance())/1000))+" KM");
        viewholder.userPostion.setText(m.getPosition());
        if (!m.getProfile_pic().equals("NA")) {
            if (m.getRegType().equals("normal")&&m.getImage_status().equals("0")){
                Picasso.get().
                        load(Constant.BASE_APP_IMAGE__PATH+m.getProfile_pic())
                        .placeholder(R.drawable.image_loader)
                        .into(viewholder.img_user);
                /*Glide.with(constant)
                        .load(Constant.BASE_APP_IMAGE__PATH+m.getProfile_pic())
                        .centerCrop()
                        .placeholder(R.drawable.user_profile_avatar)
                        .into(viewholder.img_user);*/


               /* Glide.with(constant)
                        .load(""+ Constant.BASE_APP_IMAGE__PATH+m.getProfile_pic())
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                Drawable d = constant.getResources().getDrawable(R.drawable.user_profile_avatar);
                                d.setBounds(0, 0, 80, 80);
                                viewholder.img_user.setImageDrawable(d);
                                return false;
                            }
                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .transform(new CircleTransform(constant))
                        .into(viewholder.img_user);*/


            }else{
                Picasso.get().
                        load(m.getProfile_pic())
                        .placeholder(R.drawable.image_loader)
                        .into(viewholder.img_user);
               /* Glide.with(constant)
                        .load(m.getProfile_pic())
                        .centerCrop()
                        .placeholder(R.drawable.user_profile_avatar)
                        .into(viewholder.img_user);*/

               /* Glide.with(constant)
                        .load(""+m.getProfile_pic())
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                Drawable d = constant.getResources().getDrawable(R.drawable.user_profile_avatar);
                                d.setBounds(0, 0, 80, 80);
                                viewholder.img_user.setImageDrawable(d);
                                return false;
                            }
                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .transform(new CircleTransform(constant))
                        .into(viewholder.img_user);*/
            }
        } else {
            Drawable d = constant.getResources().getDrawable(R.drawable.user_profile_avatar);
            d.setBounds(0, 0, 80, 80);
            viewholder.img_user.setImageDrawable(d);
        }
        viewholder.map_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(constant, ArchiveListMapDetailedActivity.class);
                i.putExtra(Contents.MYBETS_startlongitude,m.getStartlongitude());
                i.putExtra(Contents.MYBETS_endlongitude,m.getPositionlongitude());
                i.putExtra(Contents.MYBETS_startlatitude,m.getStartlatitude());
                i.putExtra(Contents.MYBETS_endlatitude,m.getPositionlatitude());
                i.putExtra(Contents.POSITION_LATITUDE,"");
                i.putExtra(Contents.POSITION_LONGITUDE,"");
                i.putExtra(Contents.ROUTE,m.getRoute());
                constant.startActivity(i);
            }
        });
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView img_user;
        TextView tv_Name, userPostion,tv_Description,km;
        LinearLayout rowView;
        LinearLayout map_view;
        public ViewHolder(View convertView) {
            super(convertView);
            rowView=  convertView.findViewById(R.id.row);
            map_view=  convertView.findViewById(R.id.map_view);
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

