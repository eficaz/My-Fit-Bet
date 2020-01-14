package com.androidapp.fitbet.ui.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.model.Invitemembers;
import com.androidapp.fitbet.network.Constant;
import com.androidapp.fitbet.network.RetroClient;
import com.androidapp.fitbet.network.RetroInterface;
import com.androidapp.fitbet.ui.fragments.CreateGroupFragment;
import com.androidapp.fitbet.utils.CircleImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InviteDetailsListAdapter extends RecyclerView.Adapter  {
    Context constant;
    public static ArrayList<Invitemembers> groupListModels;
    public static List<Invitemembers> selected_usersList=new ArrayList<>();
    private static List<Invitemembers> contactListFiltered;
    String groupId;
    String basepath;
    private static CreateGroupFragment.RecyclerViewClickListener itemListener;
    public InviteDetailsListAdapter(Context context, ArrayList<Invitemembers> myDataset, ArrayList<Invitemembers> selectedList, String groupId, String basepath) {
        this.constant = context;
        this.groupListModels = myDataset;
        this.selected_usersList = selectedList;
        this.basepath=basepath;
        contactListFiltered = new ArrayList<>();
        contactListFiltered.addAll(groupListModels);
        this.groupId=groupId;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.invite_details_item_list, parent, false);
        InviteDetailsListAdapter.ViewHolder vh = new InviteDetailsListAdapter.ViewHolder(v);
        return vh;
    }
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final Invitemembers m = groupListModels.get(position);
        final InviteDetailsListAdapter.ViewHolder viewholder = (InviteDetailsListAdapter.ViewHolder) holder;
        viewholder.tv_Name.setText(m.getFirstname());
        viewholder.tv_Country.setText(m.getCountry());
        if(m.getDistance().equals("null") || m.getDistance().equals("")){
            double i2= Double.parseDouble("0");
            viewholder.tv_km.setText(new DecimalFormat("##.##").format(i2)+" Km");
        }else{
            double i2= Double.parseDouble(m.getDistance())/1000;
            viewholder.tv_km.setText(new DecimalFormat("##.##").format(i2)+" Km");
        }
        if (!m.getProfile_pic().equals("NA")) {
            Picasso.get().
                    load(basepath+m.getProfile_pic())
                    .placeholder(R.drawable.image_loader)
                    .into(viewholder.img_user);

           /* Glide.with(constant)
                    .load(basepath+m.getProfile_pic())
                    .centerCrop()
                    .placeholder(R.drawable.group_icons)
                    .into(viewholder.img_user);*/
            /*Glide.with(constant)
                    .load(basepath+m.getProfile_pic())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            Drawable d = constant.getResources().getDrawable(R.drawable.group_icons);
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
        } else {
            Drawable d = constant.getResources().getDrawable(R.drawable.group_icons);
            d.setBounds(0, 0, 80, 80);
            viewholder.img_user.setImageDrawable(d);
        }
       /* if(selected_usersList.contains(groupListModels.get(position)))
            viewholder.rowView.setBackgroundColor(ContextCompat.getColor(constant, R.color.gray_1));
        else
            viewholder.rowView.setBackgroundColor(ContextCompat.getColor(constant, R.color.white));*/
        if(selected_usersList.contains(groupListModels.get(position))){
            viewholder.rowView.setBackground(constant.getDrawable(R.drawable.layout_gray_border));}
        else{
            viewholder.rowView.setBackground(constant.getDrawable(R.drawable.layout_whight_border));}
    }

    private void callincviteApi(String regKey, String groupId, final int position) {

        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).AddInviteMember(groupId,regKey);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    groupDetailsList(bodyString,position);
                    //listOrderGroup(bodyString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });

    }

    private void groupDetailsList(String bodyString, int position) {
        final JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(bodyString);
            String data = jsonObject.getString("Status");
            if(data.equals("Ok")){
                groupListModels.remove(position);
                notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void filterList(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        groupListModels.clear();
        if (charText.length() == 0) {
            groupListModels.addAll(contactListFiltered);
        }else {
            for (Invitemembers wp : contactListFiltered) {
                if (wp.getFirstname().toLowerCase(Locale.getDefault()).contains(charText)) {
                    groupListModels.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView img_user;
        TextView tv_Name, tv_Country,tv_km;
        ConstraintLayout rowView;
        public ViewHolder(View convertView) {
            super(convertView);
            rowView=  convertView.findViewById(R.id.row);
            img_user = convertView.findViewById(R.id.img_user);
            tv_Name = convertView.findViewById(R.id.tv_Name);
            tv_km = convertView.findViewById(R.id.tv_km);
            tv_Country = convertView.findViewById(R.id.tv_Country);
            itemView.setTag(itemView);
        }
    }
    @Override
    public int getItemCount() {
        return groupListModels.size() > 0 ? groupListModels.size() : 0;
    }
}

