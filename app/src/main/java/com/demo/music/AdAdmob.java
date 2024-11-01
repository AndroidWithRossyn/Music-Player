package com.demo.music;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;


public class AdAdmob {

    String BannerAdID = "/6499/example/banner", FullscreenAdID = "/6499/example/interstitial";

    ProgressDialog ProgressDialog;

    public AdAdmob(Activity activity) {


        MobileAds.initialize(activity, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus
                                                         initializationStatus) {
            }
        });


    }


    public void BannerAd(final RelativeLayout Ad_Layout, Activity activity) {


        AdView mAdView = new AdView(activity);
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(BannerAdID);
        AdRequest adore = new AdRequest.Builder().build();
        mAdView.loadAd(adore);
        Ad_Layout.addView(mAdView);


        mAdView.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                Ad_Layout.setVisibility(View.VISIBLE);
                super.onAdLoaded();

                Log.e("ddddd", "dddd");
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                Ad_Layout.setVisibility(View.GONE);
                Log.e("ddddd1", "dddd");

            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                mAdView.destroy();
                Ad_Layout.setVisibility(View.GONE);
                Log.e("ddddd2", "dddd" + loadAdError.getMessage());

            }
        });


    }

    public void FullscreenAd(final Activity activity) {
        // TODO Auto-generated method stub

        Ad_Popup(activity);

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(activity, FullscreenAdID, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {

                        interstitialAd.show(activity);
                        ProgressDialog.dismiss();

                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        ProgressDialog.dismiss();
                    }
                });


    }


    private void Ad_Popup(Context mContext) {
        // TODO Auto-generated method stub

        ProgressDialog = ProgressDialog.show(mContext, "", "Ad Loading . . .", true);
        ProgressDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        ProgressDialog.setCancelable(true);
        ProgressDialog.show();

    }


}