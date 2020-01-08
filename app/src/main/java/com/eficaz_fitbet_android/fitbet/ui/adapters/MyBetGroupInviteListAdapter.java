package com.eficaz_fitbet_android.fitbet.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.eficaz_fitbet_android.fitbet.R;
import com.eficaz_fitbet_android.fitbet.customview.CustomProgress;
import com.eficaz_fitbet_android.fitbet.model.BetGroupList;
import com.eficaz_fitbet_android.fitbet.network.Constant;
import com.eficaz_fitbet_android.fitbet.network.RetroClient;
import com.eficaz_fitbet_android.fitbet.network.RetroInterface;
import com.eficaz_fitbet_android.fitbet.ui.fragments.CreateGroupFragment;
import com.eficaz_fitbet_android.fitbet.utils.CircleImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyBetGroupInviteListAdapter extends RecyclerView.Adapter  {
    Context constant;
    public ArrayList<BetGroupList> groupListModels;
    public List<BetGroupList> selected_usersList=new ArrayList<>();
    private List<BetGroupList> contactListFiltered;
    String betid;
    private static CreateGroupFragment.RecyclerViewClickListener itemListener;
    public MyBetGroupInviteListAdapter(Context context, ArrayList<BetGroupList> myDataset,String betid) {
        this.constant = context;
        this.groupListModels = myDataset;
        //this.selected_usersList = selectedList;
        contactListFiltered = new ArrayList<>();
        contactListFiltered.addAll(groupListModels);
        this.betid=betid;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mybet_invite_group_item_list, parent, false);
        MyBetGroupInviteListAdapter.ViewHolder vh = new MyBetGroupInviteListAdapter.ViewHolder(v);
        return vh;
    }
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final BetGroupList m = groupListModels.get(position);
        final MyBetGroupInviteListAdapter.ViewHolder viewholder = (MyBetGroupInviteListAdapter.ViewHolder) holder;
        viewholder.tv_Name.setText(m.getName());
        viewholder.tv_Designation.setText(m.getDescription());
        viewholder.tv_groupCount.setText(m.getTotal());

      /*  if(selected_usersList.contains(groupListModels.get(position)))
            viewholder.rowView.setBackgroundColor(ContextCompat.getColor(constant, R.color.gray_1));
        else
            viewholder.rowView.setBackgroundColor(ContextCompat.getColor(constant, R.color.white));*/

        viewholder.invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callincviteApi(m.getReg_key(),betid,groupListModels.get(position).getGroupid(),position);
                CustomProgress.getInstance().showProgress(constant, "", false);
            }
        });

    }


    private void callincviteApi(String regKey, String betid,String groupid, final int position) {
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).Addbetgroup(betid,groupid);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    groupDetailsList(bodyString,position);
                    CustomProgress.getInstance().hideProgress();
                    //listOrderGroup(bodyString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                CustomProgress.getInstance().hideProgress();
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
            for (BetGroupList wp : contactListFiltered) {
                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    groupListModels.add(wp);
                }
            }
        }
        //this.groupListModels = filterdNames;
        notifyDataSetChanged();
    }
   /* public void filterList(ArrayList<GroupListModel> filterdNames) {
            this.groupListModels = filterdNames;
            notifyDataSetChanged();
    }*/

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView img_user;
        TextView tv_Name, tv_Designation,tv_groupCount;
        ConstraintLayout rowView;
        Button invite;
        public ViewHolder(View convertView) {
            super(convertView);
            rowView=  convertView.findViewById(R.id.row);
            img_user = convertView.findViewById(R.id.img_user);
            tv_Name = convertView.findViewById(R.id.tv_Name);
            invite= convertView.findViewById(R.id.invite);
            tv_groupCount = convertView.findViewById(R.id.tv_groupCount);
            tv_Designation = convertView.findViewById(R.id.tv_Designation);
            itemView.setTag(itemView);
           /*
            img_user = convertView.findViewById(R.id.img_user);
            tv_Name = convertView.findViewById(R.id.tv_Name);
            tv_Designation = convertView.findViewById(R.id.tv_Designation);
            itemView.setTag(itemView);*/
        }
    }
    @Override
    public int getItemCount() {
        return groupListModels.size() > 0 ? groupListModels.size() : 0;
    }
}

