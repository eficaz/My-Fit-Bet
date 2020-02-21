package com.androidapp.fitbet.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.model.GroupListModel;
import com.androidapp.fitbet.ui.fragments.CreateGroupFragment;
import com.androidapp.fitbet.utils.CircleImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.androidapp.fitbet.network.Constant.BASE_APP_GROUP_IMAGE__PATH;

public class GroupListAdapter extends RecyclerView.Adapter {
    Context constant;
    public ArrayList<GroupListModel> groupListModels;
    public List<GroupListModel> selected_usersList = new ArrayList<>();
    private List<GroupListModel> contactListFiltered;
    private static CreateGroupFragment.RecyclerViewClickListener itemListener;
    String basePath = "";

    public GroupListAdapter(Context context, ArrayList<GroupListModel> myDataset, ArrayList<GroupListModel> selectedList, String basePath) {
        this.constant = context;
        this.groupListModels = myDataset;
        this.selected_usersList = selectedList;
        contactListFiltered = new ArrayList<>();
        contactListFiltered.addAll(groupListModels);
        this.basePath = basePath;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_item_list, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final GroupListModel m = groupListModels.get(position);
        final ViewHolder viewholder = (ViewHolder) holder;
        viewholder.tv_Name.setText(m.getName());
        viewholder.tv_Designation.setText(m.getDescription());
        viewholder.tv_groupCount.setText(m.getUsers());
        if (selected_usersList.contains(groupListModels.get(position))) {
            viewholder.rowView.setBackground(constant.getDrawable(R.drawable.layout_gray_border));
        } else {
            viewholder.rowView.setBackground(constant.getDrawable(R.drawable.layout_whight_border));
        }

        if (!m.getGroupimage().equals("NA")) {
            Picasso.get().
                    load(BASE_APP_GROUP_IMAGE__PATH + m.getGroupimage())
                    .placeholder(R.drawable.image_loader)
                    .into(viewholder.img_user);

        } else {
            viewholder.img_user.setImageDrawable(constant.getResources().getDrawable(R.drawable.group_icons));
        }
    }

    public void filterList(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());
        groupListModels.clear();
        if (charText.length() == 0) {
            groupListModels.addAll(contactListFiltered);
        } else {
            for (GroupListModel wp : contactListFiltered) {
                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    groupListModels.add(wp);
                }

            }
        }
        notifyDataSetChanged();
    }

    /* public void filterList(String charText) {
         charText = charText.toLowerCase(Locale.getDefault());
         groupListModels.clear();
         if (charText.length() == 0) {
             groupListModels.addAll(contactListFiltered);
         }else {
             for (GroupListModel wp : contactListFiltered) {
                 if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                     groupListModels.add(wp);
                 }
             }
         }
         notifyDataSetChanged();
     }*/
  /* public void filterList(String charText) {
       charText = charText.toLowerCase(Locale.getDefault());
       groupListModels.clear();
       if (charText.length() == 0) {
           groupListModels.addAll(contactListFiltered);
       }else {
           for (GroupListModel wp : contactListFiltered) {
               if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                   groupListModels.add(wp);
               }
           }
       }
            //this.groupListModels = filterdNames;
            notifyDataSetChanged();
    }*/
   /* public void filterList(ArrayList<GroupListModel> filterdNames) {
            this.groupListModels = filterdNames;
            notifyDataSetChanged();
    }*/
    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView img_user;
        TextView tv_Name, tv_Designation, tv_groupCount;
        ConstraintLayout rowView;

        public ViewHolder(View convertView) {
            super(convertView);
            rowView = convertView.findViewById(R.id.row);
            img_user = convertView.findViewById(R.id.img_user);
            tv_Name = convertView.findViewById(R.id.tv_Name);
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
