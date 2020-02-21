package com.androidapp.fitbet.ui.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.camera.CameraGalleryPickerBottom;
import com.androidapp.fitbet.customview.CustomProgress;
import com.androidapp.fitbet.interfaces.CameraGalleryCapture;
import com.androidapp.fitbet.model.GroupListModel;
import com.androidapp.fitbet.network.Constant;
import com.androidapp.fitbet.network.RetroClient;
import com.androidapp.fitbet.network.RetroInterface;
import com.androidapp.fitbet.ui.CreateGroupActivity;
import com.androidapp.fitbet.ui.InviteGroupActivity;
import com.androidapp.fitbet.ui.adapters.GroupListAdapter;
import com.androidapp.fitbet.utils.AppPreference;
import com.androidapp.fitbet.utils.CircleImageView;
import com.androidapp.fitbet.utils.Contents;
import com.androidapp.fitbet.utils.RecyclerItemClickListener;
import com.androidapp.fitbet.utils.SLApplication;
import com.androidapp.fitbet.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateGroupFragment extends Fragment implements CameraGalleryCapture {


    @Bind(R.id.et_groupName)
    EditText et_groupName;

    @Bind(R.id.et_short_descreption)
    EditText et_short_descreption;

    @Bind(R.id.edit)
    ImageView edit;

    @Bind(R.id.delete)
    ImageView delete;

    @Bind(R.id.bt_creategroup)
    Button bt_creategroup;

    @Bind(R.id.bt_updategroup)
    Button bt_updategroup;

    @Bind(R.id.searchview)
    EditText searchView;

    @Bind(R.id.group_list)
    RecyclerView group_list;

    @Bind(R.id.users_count)
    TextView users_count;

    @Bind(R.id.ed_delete_row)
    TableRow ed_delete_row;

    @Bind(R.id.img_user)
    CircleImageView img_user;
    @Bind(R.id.no_data)
    TextView no_data;

    File filePath;
    ArrayList<GroupListModel> groupDetails;
    ArrayList<GroupListModel> multiselect_list = new ArrayList<>();
    GroupListAdapter groupListAdapter;
    boolean isMultiSelect = false;
    boolean isEditable = false;
    ActionMode mActionMode;
    boolean clcikable = false;
    String edName = "", edDescreption = "", edGroupId = "", edImage = "";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        intintView();
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input
                filter(editable.toString());
            }
        });
    }

    private void filter(String text) {
        if (text.equals("")) {
            no_data.setVisibility(View.GONE);
            groupListAdapter.filterList("");
        } else if (!text.equals("")) {
            groupListAdapter.filterList(text);
        }
    }

    private void intintView() {
        groupDetails = new ArrayList<>();
        //img_user.setBackground(getResources().getDrawable(R.drawable.group_icons));
        img_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialogFragment bottomSheetDialogFragment = new CameraGalleryPickerBottom();
                bottomSheetDialogFragment.setCancelable(false);
                bottomSheetDialogFragment.show(getActivity().getSupportFragmentManager(), CameraGalleryPickerBottom.class.getName());
            }
        });
        bt_creategroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isConnectedToInternet(getActivity())) {
                    regGroup();
                } else {
                    Utils.showCustomToastMsg(getActivity(), R.string.no_internet);
                }
            }
        });
        group_list.addOnItemTouchListener(new RecyclerItemClickListener(SLApplication.getContext(), group_list, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //isMultiSelect;


                if (!clcikable == true) {
                    Intent i = new Intent(getActivity(), InviteGroupActivity.class);
                    i.putExtra(Contents.GROUP_ID, groupDetails.get(position).getGroupid());
                    startActivity(i);
                    getActivity().finish();
                } else if (multiselect_list.size() == 0) {
                    Intent i = new Intent(getActivity(), InviteGroupActivity.class);
                    i.putExtra(Contents.GROUP_ID, groupDetails.get(position).getGroupid());
                    startActivity(i);
                    getActivity().finish();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    clcikable = true;
                    multiselect_list = new ArrayList<GroupListModel>();
                    isMultiSelect = true;
                } else {

                }
                multi_select(position);
            }
        }));
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deleteId = null;
                ArrayList<String> deleteIdList = new ArrayList<String>();
                if (multiselect_list.size() > 0) {
                    for (int i = 0; i < multiselect_list.size(); i++) {
                        groupDetails.remove(multiselect_list.get(i));
                        deleteId = String.valueOf(multiselect_list.get(i).getGroupid());
                        deleteIdList.add(deleteId);
                    }
                    if (Utils.isConnectedToInternet(getActivity())) {
                        CustomProgress.getInstance().showProgress(getActivity(), "", false);
                        callDeleteGroupApi(deleteIdList.toString());
                    } else {
                        Utils.showCustomToastMsg(getActivity(), R.string.no_internet);
                    }
                } else {
                    for (int i = 0; i < multiselect_list.size(); i++) {
                        groupDetails.remove(multiselect_list.get(i));
                        deleteId = String.valueOf(multiselect_list.get(i).getGroupid());
                        deleteIdList.add(deleteId);
                    }
                    if (Utils.isConnectedToInternet(getActivity())) {
                        CustomProgress.getInstance().showProgress(getActivity(), "", false);
                        callDeleteGroupApi(deleteIdList.toString());
                    } else {
                        Utils.showCustomToastMsg(getActivity(), R.string.no_internet);
                    }
                }
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditable == true) {
                    et_groupName.setText(edName);
                    et_short_descreption.setText(edDescreption);
                    Picasso.get().load(Constant.BASE_APP_GROUP_IMAGE__PATH + edImage)
                            .placeholder(R.drawable.image_loader)
                            .into(img_user);
                    bt_updategroup.setVisibility(View.VISIBLE);
                    bt_creategroup.setVisibility(View.GONE);
                    isEditable = false;
                    edit.setVisibility(View.GONE);
                    delete.setVisibility(View.GONE);
                    ed_delete_row.setVisibility(View.GONE);
                    searchView.setVisibility(View.VISIBLE);
                    multiselect_list.clear();
                    groupListAdapter.notifyDataSetChanged();
                }
            }
        });
        bt_updategroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isConnectedToInternet(getActivity())) {
                    //CustomProgress.getInstance().showProgress(getActivity(), "", false);
                    updateGroup();
                } else {
                    Utils.showCustomToastMsg(getActivity(), R.string.no_internet);
                }

            }
        });
        if (Utils.isConnectedToInternet(getActivity())) {
            CustomProgress.getInstance().showProgress(getActivity(), "", false);
            groupList();
        } else {
            Utils.showCustomToastMsg(getActivity(), R.string.no_internet);
        }
    }

    private void callDeleteGroupApi(String deletearray) {
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).DeleteGroup(deletearray);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    try {
                        JSONObject jsonObject = new JSONObject(bodyString);
                        String msg = jsonObject.getString("Msg");
                        String status = jsonObject.getString("Status");
                        Utils.showCustomToastMsg(getActivity(), msg);
                        multiselect_list.clear();
                        groupListAdapter.notifyDataSetChanged();
                        if (status.trim().equals("Ok")) {
                            CustomProgress.getInstance().hideProgress();
                        } else {
                            Utils.showCustomToastMsg(getActivity(), msg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    refreshAdapter();
                    isEditable = false;
                    edit.setVisibility(View.GONE);
                    delete.setVisibility(View.GONE);
                    ed_delete_row.setVisibility(View.GONE);
                    searchView.setVisibility(View.VISIBLE);
                    CustomProgress.getInstance().hideProgress();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    private void updateGroup() {
        if (et_groupName.getText().toString().trim().equals("")) {
            Utils.showCustomToastMsg(getActivity(), R.string.enter_group_name);
        } else if (et_short_descreption.getText().toString().trim().equals("")) {
            Utils.showCustomToastMsg(getActivity(), R.string.short_description);
        } else {
            CustomProgress.getInstance().showProgress(getActivity(), "", false);
            callupdateGroupApi(et_groupName.getText().toString(), et_short_descreption.getText().toString(), edGroupId);
        }
    }

    private void callupdateGroupApi(String name, String edDescreption, String groupId) {
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).UpdateGroup(groupId, name, edDescreption, AppPreference.getPrefsHelper().getPref(Contents.REG_KEY, ""));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    updateGroup(bodyString);
                    //signUpSucess(bodyString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    public void multi_select(int position) {
        if (multiselect_list.contains(groupDetails.get(position))) {
            multiselect_list.remove(groupDetails.get(position));
        } else {
            multiselect_list.add(groupDetails.get(position));
        }
        if (multiselect_list.size() > 0) {
            if (multiselect_list.size() > 1) {
                et_groupName.setText("");
                et_short_descreption.setText("");
                edit.setVisibility(View.GONE);
                ed_delete_row.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
                searchView.setVisibility(View.GONE);
                isEditable = false;
            } else {
                isEditable = true;
                edName = groupDetails.get(position).getName();
                edDescreption = groupDetails.get(position).getDescription();
                edImage = groupDetails.get(position).getGroupimage();
                edGroupId = groupDetails.get(position).getGroupid();
                edit.setVisibility(View.VISIBLE);
                ed_delete_row.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
                searchView.setVisibility(View.GONE);

            }
        } else {
            isEditable = false;
            edit.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            ed_delete_row.setVisibility(View.GONE);
            searchView.setVisibility(View.VISIBLE);
        }
        users_count.setText("" + multiselect_list.size());
        refreshAdapter();

    }

    public void refreshAdapter() {
        CustomProgress.getInstance().hideProgress();
        groupListAdapter.selected_usersList = multiselect_list;
        groupListAdapter.groupListModels = groupDetails;
        groupListAdapter.notifyDataSetChanged();
    }

    public interface RecyclerViewClickListener {
        public void recyclerViewListClicked(View v, int position);
    }

    private void groupList() {
        //CustomProgress.getInstance().showProgress(getActivity(), "", false);
        String search = "";
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).GroupList(AppPreference.getPrefsHelper().getPref(Contents.REG_KEY, ""), search);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    CustomProgress.getInstance().hideProgress();
                    listOrderGroup(bodyString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    private void listOrderGroup(String bodyString) {
        try {
            JSONObject jsonObject = new JSONObject(bodyString);
            String data = jsonObject.getString("Status");
            if (data.equals("Ok")) {

                isEditable = false;
                edit.setVisibility(View.GONE);
                delete.setVisibility(View.GONE);
                ed_delete_row.setVisibility(View.GONE);
                searchView.setVisibility(View.VISIBLE);

                JSONObject jsonObject1 = new JSONObject(bodyString);
                String data1 = jsonObject1.getString("Groups");
                JSONArray jsonArray = new JSONArray(data1);
                String basePath = null;
                if (jsonArray.length() == 0) {
                    no_data.setVisibility(View.VISIBLE);
                } else {
                    no_data.setVisibility(View.GONE);
                    basePath = jsonObject1.getString("basepath");
                }
                groupDetails = new ArrayList<>();
                groupDetails.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonList = jsonArray.getJSONObject(i);
                    GroupListModel model = new GroupListModel();
                    model.setReg_key(jsonList.getString(Contents.REG_KEY));
                    model.setName(jsonList.getString(Contents.NAME));
                    model.setDescription(jsonList.getString(Contents.DESCRIPTION));
                    model.setGroupimage(jsonList.getString(Contents.GROUP_IMAGE));
                    model.setGroupid(jsonList.getString(Contents.GROUP_ID));
                    model.setCreatedate(jsonList.getString(Contents.CREAT_DATE));
                    model.setUsers(jsonList.getString(Contents.USERS));
                    groupDetails.add(model);
                }
                groupListAdapter = new GroupListAdapter(getActivity(), groupDetails, multiselect_list, basePath);
                group_list.setHasFixedSize(true);
                group_list.setLayoutManager(new LinearLayoutManager(getActivity()));
                group_list.setAdapter(groupListAdapter);
                groupListAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void regGroup() {
        if (et_groupName.getText().toString().trim().equals("")) {
            Utils.showCustomToastMsg(getActivity(), R.string.enter_group_name);
        } else if (et_short_descreption.getText().toString().trim().equals("")) {
            Utils.showCustomToastMsg(getActivity(), R.string.short_description);
        } else {
            if (Utils.isConnectedToInternet(getActivity())) {
                CustomProgress.getInstance().showProgress(getActivity(), "", false);
                callAddGroupApi(et_groupName.getText().toString(), et_short_descreption.getText().toString());
            } else {
                Utils.showCustomToastMsg(getActivity(), R.string.no_internet);
            }
        }
    }

    private void callAddGroupApi(String groupName, String short_descreption) {
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).AddGroup(groupName, short_descreption, AppPreference.getPrefsHelper().getPref(Contents.REG_KEY, ""));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    signUpSucess(bodyString);
                    // groupList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_create_group, container, false);
        ButterKnife.bind(this, view);
        ((CreateGroupActivity) getActivity()).passVal(new CameraGalleryCapture() {
            @Override
            public <T> void requestFailure(int requestCode, T data) {
            }

            @Override
            public <T> File requestSuccess(File imageFile) {
                //filePath= imageFile;

                try {
                    filePath = new Compressor(getActivity()).compressToFile(imageFile);
                } catch (Exception e) {

                }

                if (!imageFile.equals("")) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
                    Bitmap myBitmap = BitmapFactory.decodeFile(filePath.getAbsolutePath(), options);
                    img_user.setImageBitmap(myBitmap);
                 /*   Glide.with(getActivity())
                            .load(filePath.toString())
                            .placeholder(R.drawable.group_icons)
                            .into(img_user);*/

                   /* Glide.with(getActivity())
                            .load(filePath.toString())
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    Drawable d = getActivity().getResources().getDrawable(R.drawable.group_icons);
                                    d.setBounds(0, 0, 80, 80);
                                    img_user.setImageDrawable(d);
                                    return false;
                                }
                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    return false;
                                }
                            })
                            .transform(new CircleTransform(getActivity()))
                            .into(img_user);*/
                } else {
                    Drawable d = getActivity().getResources().getDrawable(R.drawable.img_upload);
                    d.setBounds(0, 0, 80, 80);
                    img_user.setImageDrawable(d);
                }
                return imageFile;
            }
        });
        return view;
    }

    @Override
    public <T> void requestFailure(int requestCode, T data) {
    }

    @Override
    public File requestSuccess(File imageFile) {
        return imageFile;
    }

    private void signUpSucess(String bodyString) {
        try {
            JSONObject jsonObject = new JSONObject(bodyString);
            String msg = jsonObject.getString("Msg");
            String status = jsonObject.getString("Status");
            String groupid = jsonObject.getString("groupid");
            Utils.showCustomToastMsg(getActivity(), msg);
            if (status.trim().equals("Ok")) {
                et_groupName.setText("");
                img_user.setImageDrawable(getResources().getDrawable(R.drawable.img_upload));
                et_short_descreption.setText("");
                CustomProgress.getInstance().hideProgress();
                if (img_user.getDrawable() == null) {
                    groupList();
                } else {
                    if (Utils.isConnectedToInternet(getActivity())) {
                        if (img_user.getDrawable() == null) {

                        } else {
                            uploadImage(filePath, groupid);
                        }
                        groupList();
                    } else {
                        Utils.showCustomToastMsg(getActivity(), R.string.no_internet);
                    }

                }
                //groupList();


            } else {
                Utils.showCustomToastMsg(getActivity(), msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateGroup(String bodyString) {
        try {
            JSONObject jsonObject = new JSONObject(bodyString);
            String msg = jsonObject.getString("Msg");
            String status = jsonObject.getString("Status");
            String groupid = jsonObject.getString("groupid");
            Utils.showCustomToastMsg(getActivity(), msg);
            if (status.trim().equals("Ok")) {
                CustomProgress.getInstance().hideProgress();
                isEditable = false;
                et_groupName.setText("");
                et_short_descreption.setText("");
                bt_updategroup.setVisibility(View.GONE);
                bt_creategroup.setVisibility(View.VISIBLE);
                img_user.setImageDrawable(getResources().getDrawable(R.drawable.img_upload));
                groupListAdapter.notifyDataSetChanged();
                if (img_user.getDrawable() == null) {
                    CustomProgress.getInstance().hideProgress();
                } else {
                    uploadImage(filePath, groupid);
                }
                CustomProgress.getInstance().hideProgress();
                groupList();
            } else {
                Utils.showCustomToastMsg(getActivity(), msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadImage(File filePath, String groupid) {
        if (filePath == null) {
            filePath = new File("");
        }
        CustomProgress.getInstance().hideProgress();
        RequestBody groupId = RequestBody.create(MediaType.parse("multipart/from-data"), groupid);
        RequestBody regkey = RequestBody.create(MediaType.parse("multipart/from-data"), AppPreference.getPrefsHelper().getPref(Contents.REG_KEY, ""));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), filePath);
        MultipartBody.Part part = MultipartBody.Part.createFormData("image", filePath.getName(), requestFile);
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).updateProfile(part, groupId, regkey);
        final File finalFilePath = filePath;
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    CustomProgress.getInstance().hideProgress();
                    et_groupName.setText("");
                    et_short_descreption.setText("");
                    img_user.setImageDrawable(getResources().getDrawable(R.drawable.img_upload));
                    refreshAdapter();
                    JSONObject jsonObject1 = new JSONObject(bodyString);
                    String msg = jsonObject1.getString("Msg");
                    // Utils.showCustomToastMsg(getActivity(), msg);
                    String data1 = jsonObject1.getString("Groups");
                    JSONArray jsonArray = new JSONArray(data1);
                    String basePath = null;
                    if (jsonArray.length() == 0) {
                        no_data.setVisibility(View.VISIBLE);
                    } else {
                        no_data.setVisibility(View.GONE);
                        basePath = jsonObject1.getString("basepath");
                    }
                    groupDetails = new ArrayList<>();
                    groupDetails.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonList = jsonArray.getJSONObject(i);
                        GroupListModel model = new GroupListModel();
                        model.setReg_key(jsonList.getString(Contents.REG_KEY));
                        model.setName(jsonList.getString(Contents.NAME));
                        model.setDescription(jsonList.getString(Contents.DESCRIPTION));
                        model.setGroupimage(jsonList.getString(Contents.GROUP_IMAGE));
                        model.setGroupid(jsonList.getString(Contents.GROUP_ID));
                        model.setCreatedate(jsonList.getString(Contents.CREAT_DATE));
                        model.setUsers(jsonList.getString(Contents.USERS));
                        groupDetails.add(model);
                    }
                    groupListAdapter = new GroupListAdapter(getActivity(), groupDetails, multiselect_list, basePath);
                    group_list.setHasFixedSize(true);
                    group_list.setLayoutManager(new LinearLayoutManager(getActivity()));
                    group_list.setAdapter(groupListAdapter);
                    groupListAdapter.notifyDataSetChanged();
                    CustomProgress.getInstance().showProgress(getActivity(), "", false);
                    groupList();
                    File fdelete = new File(finalFilePath.getPath());
                    if (fdelete.exists()) {
                        if (fdelete.delete()) {
                            System.out.println("file Deleted :" + fdelete.getName());
                        } else {
                            System.out.println("file not Deleted :" + fdelete.getName());
                        }
                    }


                } catch (Exception e) {
                    CustomProgress.getInstance().hideProgress();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                CustomProgress.getInstance().hideProgress();
            }
        });

    }
}
