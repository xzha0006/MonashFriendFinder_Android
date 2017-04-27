//package com.johnsyard.monashfriendfinder;
//
//import android.app.Fragment;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.widget.DrawerLayout;
//import android.support.v7.app.ActionBarDrawerToggle;
//import android.support.v7.widget.Toolbar;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
///**
// * Created by xuanzhang on 27/04/2017.
// */
//
//public class LoginFragment extends Fragment{
//    View vLogin;
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        vLogin = inflater.inflate(R.layout.activity_login, container, false);
//        Toolbar toolbar = (Toolbar) vLogin.findViewById(R.id.toolbar);
//        DrawerLayout drawer = (DrawerLayout) vLogin.findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
//
//        return vLogin;
//    }
//}
