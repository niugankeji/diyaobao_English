package com.leman.diyaobao.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leman.diyaobao.R;
import com.leman.diyaobao.map.LocationUtil;
import com.leman.diyaobao.utils.FragmentUtils;

import static com.leman.diyaobao.utils.ActivityUtils.setChildState;

public class FoundFragment extends Fragment implements View.OnClickListener {

    private TextView title;
    private LinearLayout nearby_user_ll;
    private View nearby_user_view;
    private LinearLayout nearby_data_ll;
    private View nearby_data_view;

    private NearByUserFragment nearByUserFragment;
    private NearByDataFragment nearByDataFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_found, container, false);

        title = view.findViewById(R.id.title);
        title.setText("Find");

        nearby_user_ll = view.findViewById(R.id.nearby_user_ll);
        nearby_user_ll.setOnClickListener(this);
        nearby_user_view = view.findViewById(R.id.nearby_user_view);

        nearby_data_ll = view.findViewById(R.id.nearby_data_ll);
        nearby_data_ll.setOnClickListener(this);
        nearby_data_view = view.findViewById(R.id.nearby_data_view);


        nearByUserFragment = new NearByUserFragment();
        nearByDataFragment = new NearByDataFragment();

        setChildState(nearby_user_ll, false);
        nearby_data_view.setVisibility(View.GONE);
        FragmentUtils.replceFragment(getActivity(), R.id.frameLayout, nearByUserFragment);


        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nearby_user_ll:
                if (nearByUserFragment != null) {
                    nearByUserFragment = null;
                    nearByUserFragment = new NearByUserFragment();
                }
                setChildState(nearby_user_ll, false);
                setChildState(nearby_data_ll, true);
                nearby_user_view.setVisibility(View.VISIBLE);
                nearby_data_view.setVisibility(View.GONE);
                FragmentUtils.replceFragment(getActivity(), R.id.frameLayout, nearByUserFragment);
                break;
            case R.id.nearby_data_ll:
                if (nearByDataFragment != null) {
                    nearByDataFragment = null;
                    nearByDataFragment = new NearByDataFragment();
                }
                setChildState(nearby_data_ll, false);
                setChildState(nearby_user_ll, true);
                nearby_user_view.setVisibility(View.GONE);
                nearby_data_view.setVisibility(View.VISIBLE);
                FragmentUtils.replceFragment(getActivity(), R.id.frameLayout, nearByDataFragment);
                break;

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocationUtil.stopLocation();
    }
}
