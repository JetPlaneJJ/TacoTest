/**
 * Name: Jay Lin
 * Tab1Fragment represents the content to be displayed in the leftmost "Home" tab.
 */

package com.example.testapplication;

import android.app.Fragment;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

public class Tab1Fragment extends Fragment {
    VideoView resultVideoView;
    MediaController mediaController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    /**
     * Handles adding images to the bottom ScrollView and setting up the main videoView
     *
     * @param view               - represents the view that was just created
     * @param savedInstanceState - represents the state app is restoring from
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Resources res = getContext().getResources();
        ConstraintLayout container = view.findViewById(R.id.constraintL);

        for (int x = 0; x < 7; x++) {
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
            params.setMargins(8, 0, 0, 0);
            CardView card = new CardView(getActivity());
            card.setId(x + 1);
            card.setRadius(dpToPixels(10)); // 10dp rounded corners

            params.topToTop = container.getId();
            params.bottomToBottom = container.getId();
            if (x == 1) {
                params.leftToLeft = 0;
            } else {
                params.leftToRight = x;
            }
            card.setLayoutParams(params);
            card.getLayoutParams().width = dpToPixels(80);
            container.addView(card);

            ImageView image = new ImageView(getActivity());
            image.setImageResource(res.getIdentifier("img_" + x,
                    "drawable", "com.example.testapplication"));
            image.setScaleType(ImageView.ScaleType.FIT_XY);
            card.addView(image);
        }
        this.resultVideoView = view.findViewById(R.id.videoView);
        this.resultVideoView.setOnPreparedListener(mp -> mp.setLooping(true)); // infinitely looping video
    }

    /**
     * Converts density independent pixels to pixels.
     *
     * @param dp - represents density independent pixel number to convert
     */
    private int dpToPixels(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    /**
     * Sets the main video displayed in this fragment.
     *
     * @param video - represents the Uri format of the video to be displayed
     */
    public void setVideo(Uri video) {
        if (video != null) {
            this.resultVideoView.setVideoURI(video);
            this.resultVideoView.start();
        }
    }
}