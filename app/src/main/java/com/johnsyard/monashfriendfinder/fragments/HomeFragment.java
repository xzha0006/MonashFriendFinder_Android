package com.johnsyard.monashfriendfinder.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.johnsyard.monashfriendfinder.R;

/**
 * Created by xuanzhang on 1/05/2017.
 */

public class HomeFragment extends Fragment {
    View vHome;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vHome = inflater.inflate(R.layout.fragment_home, container, false);

        return vHome;
    }
}
