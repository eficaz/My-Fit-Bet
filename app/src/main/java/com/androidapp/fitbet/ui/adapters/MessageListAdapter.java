package com.androidapp.fitbet.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.model.Message;
import com.androidapp.fitbet.network.Constant;
import com.androidapp.fitbet.network.RetroClient;
import com.androidapp.fitbet.network.RetroInterface;
import com.androidapp.fitbet.ui.fragments.CreateGroupFragment;
import com.androidapp.fitbet.utils.AppPreference;
import com.androidapp.fitbet.utils.CircleImageView;
import com.androidapp.fitbet.utils.Contents;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.androidapp.fitbet.network.Constant.BASE_APP_IMAGE__PATH;

public class MessageListAdapter extends RecyclerView.Adapter {
    Context constant;
    public static ArrayList<Message> groupListModels;
    private static List<Message> contactListFiltered;
    String groupId;
    private static CreateGroupFragment.RecyclerViewClickListener itemListener;

    public MessageListAdapter(Context context, ArrayList<Message> myDataset, String betid) {
        this.constant = context;
        this.groupListModels = myDataset;
        contactListFiltered = new ArrayList<>();
        contactListFiltered.addAll(groupListModels);
        this.groupId = groupId;
    }

    public MessageListAdapter(Context context, ArrayList<Message> moviesList) {
        this.constant = context;
        this.groupListModels = moviesList;
        contactListFiltered = new ArrayList<>();
        contactListFiltered.addAll(groupListModels);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_item, parent, false);
        MessageListAdapter.ViewHolder vh = new MessageListAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final Message m = groupListModels.get(position);
        final MessageListAdapter.ViewHolder viewholder = (MessageListAdapter.ViewHolder) holder;

        if (m.getSender().equals(AppPreference.getPrefsHelper().getPref(Contents.REG_KEY, ""))) {
            viewholder.row_group_users_message.setVisibility(View.GONE);
            viewholder.row_selfMessage.setVisibility(View.VISIBLE);
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

                   /* Glide.with(constant)
                            .load( BASE_APP_IMAGE__PATH+m.getProfile_pic())
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    viewholder.img_user.setImageDrawable(constant.getResources().getDrawable(R.drawable.user_profile_avatar));
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


                   /* Glide.with(constant)
                            .load(m.getProfile_pic())
                            .centerCrop()
                            .placeholder(R.drawable.user_profile_avatar)
                            .into(viewholder.img_user);*/
                    /*Glide.with(constant)
                            .load( m.getProfile_pic())
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    viewholder.img_user.setImageDrawable(constant.getResources().getDrawable(R.drawable.user_profile_avatar));
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
                viewholder.img_user.setImageDrawable(constant.getResources().getDrawable(R.drawable.user_profile_avatar));
            }

        } else {
            viewholder.row_group_users_message.setVisibility(View.VISIBLE);
            viewholder.row_selfMessage.setVisibility(View.GONE);


            if (!m.getProfile_pic().equals("NA")) {
                if (m.getRegType().equals("normal") && m.getImage_status().equals("0")) {

                    Picasso.get().
                            load(BASE_APP_IMAGE__PATH + m.getProfile_pic())
                            .placeholder(R.drawable.image_loader)
                            .into(viewholder.img_user);


                   /* Glide.with(constant)
                            .load(BASE_APP_IMAGE__PATH+m.getProfile_pic())
                            .centerCrop()
                            .placeholder(R.drawable.user_profile_avatar)
                            .into(viewholder.img_user);*/

                    /*Glide.with(constant)
                            .load( BASE_APP_IMAGE__PATH+m.getProfile_pic())
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    viewholder.img_user1.setImageDrawable(constant.getResources().getDrawable(R.drawable.user_profile_avatar));
                                    return false;
                                }
                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    return false;
                                }
                            })
                            .transform(new CircleTransform(constant))
                            .into(viewholder.img_user1);*/
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
                            .load( m.getProfile_pic())
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    viewholder.img_user1.setImageDrawable(constant.getResources().getDrawable(R.drawable.user_profile_avatar));
                                    return false;
                                }
                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    return false;
                                }
                            })
                            .transform(new CircleTransform(constant))
                            .into(viewholder.img_user1);*/
                }


            } else {
                viewholder.img_user.setImageDrawable(constant.getResources().getDrawable(R.drawable.user_profile_avatar));
            }

        }
        viewholder.tv_Name.setText(m.getFirstname());
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = df.parse(m.getSenddate());
            df.setTimeZone(TimeZone.getDefault());
            String formattedDate = df.format(date);

            DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
            String output = null;
            output = outputformat.format(date);
            viewholder.tv_Date.setText(output);
            viewholder.tv_Date1.setText(output);
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewholder.tv_Message.setText(m.getMessage());
        viewholder.tv_Name1.setText(m.getFirstname());
        viewholder.tv_Message1.setText(m.getMessage());
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
        CircleImageView img_user, img_user1;
        TextView tv_Name, tv_Date, tv_Name1, tv_Date1;
        AutoCompleteTextView tv_Message, tv_Message1;
        ConstraintLayout rowView;
        LinearLayout row_group_users_message, row_selfMessage;

        public ViewHolder(View convertView) {
            super(convertView);
            rowView = convertView.findViewById(R.id.row);
            img_user = convertView.findViewById(R.id.img_user);
            img_user1 = convertView.findViewById(R.id.img_user1);
            tv_Name = convertView.findViewById(R.id.tv_Name);
            tv_Name1 = convertView.findViewById(R.id.tv_Name1);
            tv_Date = convertView.findViewById(R.id.tv_Date);
            tv_Date1 = convertView.findViewById(R.id.tv_Date1);
            tv_Message = convertView.findViewById(R.id.tv_Message);
            tv_Message1 = convertView.findViewById(R.id.tv_Message1);
            row_group_users_message = convertView.findViewById(R.id.row_group_users_message);
            row_selfMessage = convertView.findViewById(R.id.row_selfMessage);
            itemView.setTag(itemView);
        }
    }

    @Override
    public int getItemCount() {
        return groupListModels.size() > 0 ? groupListModels.size() : 0;
    }
}
