package com.androidapp.fitbet.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.model.BetDetails;
import com.androidapp.fitbet.network.Constant;
import com.androidapp.fitbet.network.RetroClient;
import com.androidapp.fitbet.network.RetroInterface;
import com.androidapp.fitbet.ui.ProfileActivity;
import com.androidapp.fitbet.ui.fragments.CreateGroupFragment;
import com.androidapp.fitbet.utils.AppPreference;
import com.androidapp.fitbet.utils.CircleImageView;
import com.androidapp.fitbet.utils.Contents;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.androidapp.fitbet.network.Constant.BASE_APP_IMAGE__PATH;

public class MyBetDetailsAdapter extends RecyclerView.Adapter {
    Context constant;
    public static ArrayList<BetDetails> groupListModels;
    private static List<BetDetails> contactListFiltered;
    String groupId;
    private static CreateGroupFragment.RecyclerViewClickListener itemListener;

    public MyBetDetailsAdapter(Context context, ArrayList<BetDetails> myDataset, String betid) {
        this.constant = context;
        this.groupListModels = myDataset;
        contactListFiltered = new ArrayList<>();
        contactListFiltered.addAll(groupListModels);
        this.groupId = groupId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mybet_details_item_list, parent, false);
        MyBetDetailsAdapter.ViewHolder vh = new MyBetDetailsAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final BetDetails m = groupListModels.get(position);
        final MyBetDetailsAdapter.ViewHolder viewholder = (MyBetDetailsAdapter.ViewHolder) holder;
        viewholder.tv_Name.setText(m.getFirstname());
        viewholder.tv_Country.setText(m.getCountry());
        if (!m.getProfile_pic().equals("NA")) {
            if (m.getRegType().equals("normal") && m.getImage_status().equals("0")) {
                Picasso.get().
                        load(BASE_APP_IMAGE__PATH + m.getProfile_pic())
                        .placeholder(R.drawable.image_loader)
                        .into(viewholder.img_user);

              /*  Glide.with(constant)
                        .load(BASE_APP_IMAGE__PATH+m.getProfile_pic())
                        .centerCrop()
                        .placeholder(R.drawable.user_profile_avatar)
                        .into(viewholder.img_user);*/

                /*Glide.with(constant)
                        .load(""+Constant.BASE_APP_IMAGE__PATH+m.getProfile_pic())
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
            } else {
                Picasso.get().
                        load(m.getProfile_pic())
                        .placeholder(R.drawable.image_loader)
                        .into(viewholder.img_user);


             /*   Glide.with(constant)
                        .load(m.getProfile_pic())
                        .centerCrop()
                        .placeholder(R.drawable.user_profile_avatar)
                        .into(viewholder.img_user);*/

               /* Glide.with(constant)
                        .load(m.getProfile_pic())
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
        viewholder.rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppPreference.getPrefsHelper().savePref(Contents.USER_PROFILE_REG_KEY, m.getReg_key());
                Intent i = new Intent(constant, ProfileActivity.class);
                constant.startActivity(i);
            }
        });
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
            if (data.equals("Ok")) {
                groupListModels.remove(position);
                notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView img_user;
        TextView tv_Name, tv_Country;
        ConstraintLayout rowView;

        public ViewHolder(View convertView) {
            super(convertView);
            rowView = convertView.findViewById(R.id.row);
            img_user = convertView.findViewById(R.id.img_user);
            tv_Name = convertView.findViewById(R.id.tv_Name);
            tv_Country = convertView.findViewById(R.id.tv_Country);
            itemView.setTag(itemView);
        }
    }

    @Override
    public int getItemCount() {
        return groupListModels.size() > 0 ? groupListModels.size() : 0;
    }
}


