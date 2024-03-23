package com.example.adchecker;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements MaxAdListener {

    private MaxInterstitialAd interstitialAd;
    private int retryAttempt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button loadAd = findViewById(R.id.loadAdId);

        AppLovinSdk.getInstance( this ).setMediationProvider( "max" );
        AppLovinSdk.initializeSdk( this, new AppLovinSdk.SdkInitializationListener() {
            @Override
            public void onSdkInitialized(final AppLovinSdkConfiguration configuration)
            {
                // AppLovin SDK is initialized, start loading ads
                createInterstitialAd();
            }
        } );

        loadAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: loadAd is clicked");
                if ( interstitialAd.isReady() )
                {
                    Log.d(TAG, "onClick: ads is ready");
                    interstitialAd.showAd();
                }else{
                    Log.d(TAG, "onClick: ads is still not ready");
                }
            }
        });
    }

//    private void loadAd()
//    {
//        Log.d(TAG, "loadAd: loaded is called");
//        interstitialAd = new MaxInterstitialAd( "3f5e124a3cd39cbd", this );
//        interstitialAd.setListener( this );
//
//        // Load the first ad
//        interstitialAd.loadAd();
//        Log.d(TAG, "loadAd: ads loaded");
//    }

    void createInterstitialAd()
    {
        interstitialAd = new MaxInterstitialAd( "3f5e124a3cd39cbd", this );
        interstitialAd.setListener( this );

        // Load the first ad
        interstitialAd.loadAd();
    }

    // MAX Ad Listener
    @Override
    public void onAdLoaded(final MaxAd maxAd)
    {
        // Interstitial ad is ready to be shown. interstitialAd.isReady() will now return 'true'

        // Reset retry attempt
        retryAttempt = 0;
    }

    @Override
    public void onAdLoadFailed(final String adUnitId, final MaxError error)
    {
        // Interstitial ad failed to load
        // AppLovin recommends that you retry with exponentially higher delays up to a maximum delay (in this case 64 seconds)

        retryAttempt++;
        long delayMillis = TimeUnit.SECONDS.toMillis( (long) Math.pow( 2, Math.min( 6, retryAttempt ) ) );

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                interstitialAd.loadAd();
            }
        }, delayMillis );
    }

    @Override
    public void onAdDisplayFailed(final MaxAd maxAd, final MaxError error)
    {
        // Interstitial ad failed to display. AppLovin recommends that you load the next ad.
        interstitialAd.loadAd();
    }

    @Override
    public void onAdDisplayed(final MaxAd maxAd) {}

    @Override
    public void onAdClicked(final MaxAd maxAd) {}

    @Override
    public void onAdHidden(final MaxAd maxAd)
    {
        // Interstitial ad is hidden. Pre-load the next ad
        interstitialAd.loadAd();
    }
}