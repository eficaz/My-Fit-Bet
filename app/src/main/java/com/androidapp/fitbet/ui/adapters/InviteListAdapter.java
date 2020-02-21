package com.androidapp.fitbet.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.customview.CustomProgress;
import com.androidapp.fitbet.model.Invitemembers;
import com.androidapp.fitbet.network.Constant;
import com.androidapp.fitbet.network.RetroClient;
import com.androidapp.fitbet.network.RetroInterface;
import com.androidapp.fitbet.ui.ProfileActivity;
import com.androidapp.fitbet.ui.fragments.CreateGroupFragment;
import com.androidapp.fitbet.utils.AppPreference;
import com.androidapp.fitbet.utils.CircleImageView;
import com.androidapp.fitbet.utils.Contents;
import com.androidapp.fitbet.utils.Utils;
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

import static com.androidapp.fitbet.network.Constant.BASE_APP_IMAGE__PATH;

public class InviteListAdapter extends RecyclerView.Adapter {
    Context constant;
    public ArrayList<Invitemembers> groupListModels;
    public List<Invitemembers> selected_usersList = new ArrayList<>();
    private List<Invitemembers> contactListFiltered;
    String groupId, basepath;
    private static CreateGroupFragment.RecyclerViewClickListener itemListener;

    public InviteListAdapter(Context context, ArrayList<Invitemembers> myDataset, ArrayList<Invitemembers> selectedList, String groupId, String basepath) {
        this.constant = context;
        this.groupListModels = myDataset;
        this.selected_usersList = selectedList;
        this.basepath = basepath;
        contactListFiltered = new ArrayList<>();
        contactListFiltered.addAll(groupListModels);
        this.groupId = groupId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.invite_item_list, parent, false);
        InviteListAdapter.ViewHolder vh = new InviteListAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final Invitemembers m = groupListModels.get(position);
        final InviteListAdapter.ViewHolder viewholder = (InviteListAdapter.ViewHolder) holder;
        viewholder.tv_Name.setText(m.getFirstname());
        viewholder.tv_Country.setText(m.getCountry());
        viewholder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppPreference.getPrefsHelper().savePref(Contents.USER_PROFILE_REG_KEY, m.getReg_key());
                Intent i = new Intent(constant, ProfileActivity.class);
                constant.startActivity(i);
            }
        });
        if (!m.getProfile_pic().equals("NA")) {
            if (m.getRegType().equals("normal")) {
                if (m.getRegType().equals("normal") && m.getImage_status().equals("0")) {
                    Picasso.get().
                            load(BASE_APP_IMAGE__PATH + m.getProfile_pic())
                            .placeholder(R.drawable.image_loader)
                            .into(viewholder.img_user);
                } else {
                    Picasso.get().
                            load(m.getProfile_pic())
                            .placeholder(R.drawable.image_loader)
                            .into(viewholder.img_user);
                }
               /* Glide.with(constant)
                        .load(basepath+m.getProfile_pic())
                        .centerCrop()
                        .placeholder(R.drawable.group_icons)
                        .into(viewholder.img_user);*/

               /* Glide.with(constant)
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
                viewholder.img_user.setImageDrawable(constant.getResources().getDrawable(R.drawable.user_profile_avatar));
              /*  Glide.with(constant)
                        .load(m.getProfile_pic())
                        .centerCrop()
                        .placeholder(R.drawable.group_icons)
                        .into(viewholder.img_user);*/
               /* Glide.with(constant)
                        .load(m.getProfile_pic())
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
            }

        } else {
            Drawable d = constant.getResources().getDrawable(R.drawable.user_profile_avatar);
            d.setBounds(0, 0, 80, 80);
            viewholder.img_user.setImageDrawable(d);
        }
        //viewholder.tv_km.setText(""+Double.parseDouble(m.getDistance())/1000+" Km");
        if (m.getDistance().equals("null") || m.getDistance().equals("")) {
            double i2 = Double.parseDouble(m.getDistance()) / 1000;
            //viewholder.tv_km.setText(""+i2+" km");
            viewholder.tv_km.setText(new DecimalFormat("##.##").format(i2) + " Km");
        } else {
            double i2 = Double.parseDouble(m.getDistance()) / 1000;
            viewholder.tv_km.setText(new DecimalFormat("##.##").format(i2) + " Km");
        }



       /* if(m.getDistance().equals("null") || m.getDistance().equals("")){
            double i2= Double.parseDouble("0");
            viewholder.tv_km.setText(new DecimalFormat("##.##").format(i2)+" Km");
        }else{
            double i2= Double.parseDouble(m.getDistance())/1000;
            viewholder.tv_km.setText(new DecimalFormat("##.##").format(i2)+" Km");
        }*/


        /*if(!m.getDistance().equals("")||!m.getDistance().equals(null)){
            viewholder.tv_km.setText(""+Float.parseFloat(m.getDistance())/1000+" Km");
        }*//*else{
            viewholder.tv_km.setText(""+Double.parseDouble(m.getDistance())/1000+" Km");
        }*/
        viewholder.invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomProgress.getInstance().showProgress(constant, "", false);
                callincviteApi(m.getReg_key(), groupId, position);
            }
        });
       /* if(selected_usersList.contains(groupListModels.get(position)))
            viewholder.card_view.setBackgroundColor(ContextCompat.getColor(constant, R.color.gray_1));
        else
            viewholder.card_view.setBackgroundColor(ContextCompat.getColor(constant, R.color.white));*/
    }

    private void callincviteApi(String regKey, String groupId, final int position) {
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).AddInviteMember(groupId, regKey);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    groupDetailsList(bodyString, position);
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
            String msg = jsonObject.getString("Msg");
            Utils.showCustomToastMsg(constant, msg);
            if (data.equals("Ok")) {
                CustomProgress.getInstance().hideProgress();
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
        } else {
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
        TextView tv_Name, tv_Country, tv_km;
        Button invite;
        LinearLayout row;

        //CardView card_view;
        public ViewHolder(View convertView) {
            super(convertView);
            //card_view=  convertView.findViewById(R.id.card_view);
            img_user = convertView.findViewById(R.id.img_user);
            tv_Name = convertView.findViewById(R.id.tv_Name);
            tv_km = convertView.findViewById(R.id.tv_km);
            tv_Country = convertView.findViewById(R.id.tv_Country);
            invite = convertView.findViewById(R.id.invite);
            row = convertView.findViewById(R.id.row);
            itemView.setTag(itemView);
        }
    }

    @Override
    public int getItemCount() {
        return groupListModels.size() > 0 ? groupListModels.size() : 0;
    }
}

