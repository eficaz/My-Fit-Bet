package com.androidapp.fitbet.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.fitbet.R;
import com.androidapp.fitbet.customview.CustomProgress;
import com.androidapp.fitbet.interfaces.CameraGalleryCapture;
import com.androidapp.fitbet.interfaces.SearchNotFound;
import com.androidapp.fitbet.model.Archives;
import com.androidapp.fitbet.network.Constant;
import com.androidapp.fitbet.network.RetroClient;
import com.androidapp.fitbet.network.RetroInterface;
import com.androidapp.fitbet.ui.ArchiveDetailsActivity;
import com.androidapp.fitbet.ui.DashBoardActivity;
import com.androidapp.fitbet.ui.adapters.ArchivesListAdapter;
import com.androidapp.fitbet.utils.AppPreference;
import com.androidapp.fitbet.utils.Contents;
import com.androidapp.fitbet.utils.RecyclerItemClickListener;
import com.androidapp.fitbet.utils.SLApplication;
import com.androidapp.fitbet.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.androidapp.fitbet.utils.Contents.DASH_BOARD_POSICTION;
import static com.androidapp.fitbet.utils.Contents.DESCRIPTION;
import static com.androidapp.fitbet.utils.Contents.DISTANCE;
import static com.androidapp.fitbet.utils.Contents.MYBETS;
import static com.androidapp.fitbet.utils.Contents.MYBETS_betid;
import static com.androidapp.fitbet.utils.Contents.MYBETS_betname;
import static com.androidapp.fitbet.utils.Contents.MYBETS_bettype;
import static com.androidapp.fitbet.utils.Contents.MYBETS_createdate;
import static com.androidapp.fitbet.utils.Contents.MYBETS_createdby;
import static com.androidapp.fitbet.utils.Contents.MYBETS_credit;
import static com.androidapp.fitbet.utils.Contents.MYBETS_date;
import static com.androidapp.fitbet.utils.Contents.MYBETS_enddate;
import static com.androidapp.fitbet.utils.Contents.MYBETS_endlatitude;
import static com.androidapp.fitbet.utils.Contents.MYBETS_endlocation;
import static com.androidapp.fitbet.utils.Contents.MYBETS_endlongitude;
import static com.androidapp.fitbet.utils.Contents.MYBETS_route;
import static com.androidapp.fitbet.utils.Contents.MYBETS_startlatitude;
import static com.androidapp.fitbet.utils.Contents.MYBETS_startlocation;
import static com.androidapp.fitbet.utils.Contents.MYBETS_startlongitude;
import static com.androidapp.fitbet.utils.Contents.MYBETS_status;
import static com.androidapp.fitbet.utils.Contents.MYBETS_winner;
import static com.androidapp.fitbet.utils.Contents.STATUS_A;
import static com.androidapp.fitbet.utils.Contents.TOTAL;

public class ArchivesListFragment extends Fragment implements CameraGalleryCapture, SearchNotFound {

    @Bind(R.id.invite_group_list)
    RecyclerView invite_group_list;

    @Bind(R.id.btn_back)
    TableRow btn_back;

    @Bind(R.id.no_data)
    TextView no_data;

    @Bind(R.id.searchview)
    EditText searchView;

    ArrayList<Archives> archivesListDetails;

    ArchivesListAdapter archivesListAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        System.out.println("onViewCreated ArchivesListFragment");
        AppPreference.getPrefsHelper().savePref(DASH_BOARD_POSICTION, "3");
        intintView();
    }

    private void intintView() {

        archivesGroupList();
        CustomProgress.getInstance().showProgress(getActivity(), "", false);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DashBoardActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
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
                if (editable.toString().equals("")) {
                    filter("");
                } else {
                    filter(editable.toString());
                }
            }
        });
        invite_group_list.addOnItemTouchListener(new RecyclerItemClickListener(SLApplication.getContext(), invite_group_list, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (Integer.parseInt(archivesListDetails.get(position).getTotal()) > 1) {
                    Intent i = new Intent(getActivity(), ArchiveDetailsActivity.class);
                    i.putExtra(Contents.MYBETS_betid, archivesListDetails.get(position).getBetid());
                    startActivity(i);
                } else {
                    Utils.showCustomToastMsg(getActivity(), R.string.any_one_not_participate);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        }));
    }

    private void filter(String text) {
        if (archivesListAdapter != null)
            if (text.equals("")) {
                no_data.setVisibility(View.GONE);
                archivesListAdapter.filterList("");
            } else {
                if (archivesListAdapter.getItemCount() == 0) {
                    no_data.setVisibility(View.VISIBLE);
                } else {
                    no_data.setVisibility(View.GONE);
                }
                archivesListAdapter.filterList(text);
            }
    }

    private void archivesGroupList() {
        String search = "";
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).Archivebet(AppPreference.getPrefsHelper().getPref(Contents.REG_KEY, ""));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    System.out.println("Archives grouplist == " + bodyString);
                    groupDetailsList(bodyString);
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

    private void groupDetailsList(String bodyString) {
        try {
            final JSONObject jsonObject = new JSONObject(bodyString);
            String data = jsonObject.getString(STATUS_A);
            if (data.equals("Ok")) {
                JSONObject jsonObject1 = new JSONObject(bodyString);
                String data1 = jsonObject1.getString(MYBETS);
                JSONArray jsonArray = new JSONArray(data1);
                if (jsonArray.length() == 0) {
                    no_data.setVisibility(View.VISIBLE);
                } else {
                    no_data.setVisibility(View.GONE);
                }
                archivesListDetails = new ArrayList<>();
                archivesListDetails.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonList = jsonArray.getJSONObject(i);
                    Archives model = new Archives();
                    model.setDistance(jsonList.getString(DISTANCE));
                    model.setDescription(jsonList.getString(DESCRIPTION));
                    model.setCreatedate(jsonList.getString(MYBETS_createdate));
                    model.setRoute(jsonList.getString(MYBETS_route));
                    model.setCredit(jsonList.getString(MYBETS_credit));
                    model.setWinner(jsonList.getString(MYBETS_winner));
                    model.setStatus(jsonList.getString(MYBETS_status));
                    model.setCreatedby(jsonList.getString(MYBETS_createdby));
                    model.setDate(jsonList.getString(MYBETS_date));
                    model.setBetid(jsonList.getString(MYBETS_betid));
                    model.setBetname(jsonList.getString(MYBETS_betname));
                    model.setStartlocation(jsonList.getString(MYBETS_startlocation));
                    model.setEndlocation(jsonList.getString(MYBETS_endlocation));
                    model.setEndlongitude(jsonList.getString(MYBETS_endlongitude));
                    model.setEndlatitude(jsonList.getString(MYBETS_endlatitude));
                    model.setStartlatitude(jsonList.getString(MYBETS_startlatitude));
                    model.setStartlongitude(jsonList.getString(MYBETS_startlongitude));
                    model.setBettype(jsonList.getString(MYBETS_bettype));
                    model.setEnddate(jsonList.getString(MYBETS_enddate));
                    model.setTotal(jsonList.getString(TOTAL));
                    archivesListDetails.add(model);
                }
                archivesListAdapter = new ArchivesListAdapter(getActivity(), archivesListDetails);
                invite_group_list.setHasFixedSize(true);
                invite_group_list.setLayoutManager(new LinearLayoutManager(getActivity()));
                invite_group_list.setAdapter(archivesListAdapter);
                CustomProgress.getInstance().hideProgress();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_archives_list, container, false);
        ButterKnife.bind(this, view);

        return view;
    }


    @Override
    public <T> void requestFailure(int requestCode, T data) {

    }

    @Override
    public <T> File requestSuccess(File imageFile) {

        return imageFile;
    }

    @Override
    public <T> void search_not_found(boolean item) {
        if (item == true) {
            //no_data.setVisibility(View.VISIBLE);
        }
    }


}
