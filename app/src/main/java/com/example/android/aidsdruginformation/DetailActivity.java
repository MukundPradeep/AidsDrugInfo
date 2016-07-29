package com.example.android.aidsdruginformation;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class DetailActivity extends AppCompatActivity {
    private Tracker mTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        //Log.i(TAG, "Setting screen name: " + name);
        mTracker.setScreenName("Image~" + "Detail Activity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        DrugData dg = (DrugData) getIntent().getSerializableExtra("MyClass");

        TextView approvalStatus = (TextView)findViewById(R.id.approvalStatus);
        TextView drugClass = (TextView)findViewById(R.id.drugClass);
        TextView company = (TextView)findViewById(R.id.company);
        TextView approvedUse = (TextView)findViewById(R.id.approvedUse);

        approvalStatus.setText(dg.approval_status);
        drugClass.setText(dg.drug_class);
        company.setText(dg.company);
        approvedUse.setText(dg.approved_use);

        final LinearLayout ll = (LinearLayout)findViewById(R.id.backgroundLayout);

        Glide.with(getApplicationContext()).load(dg.image_url).asBitmap().into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                Drawable drawable = new BitmapDrawable(resource);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    if (ll != null) {
                        ll.setBackground(drawable);
                    }
                }
            }
        });
    }
}
