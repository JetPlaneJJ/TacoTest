/**
 * Name: Jay Lin
 * This class handles the main functionality of the Test Application.
 */

package com.example.testapplication;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    // keys
    public static final String KEY = "MainActivityDebug";
    public static final String VIDEO_BUNDLE_KEY = "video";
    // models
    private final VideoModel mVideoModel = new VideoModel();
    // fields (general)
    private BottomNavigationView mBtmView;
    private int mMenuId; // keeps track of current enabled menu item
    // App fragments (contents of each tab to be displayed)
    private Tab1Fragment Tab1;
    private Fragment Tab2;
    private Fragment Tab4;
    private Fragment Tab5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.main);
        super.onCreate(savedInstanceState);
        mBtmView = (BottomNavigationView) findViewById(R.id.bottom_navigation_menu);
        Log.i(KEY, "reached here? NavigationView is: " + R.id.bottom_navigation_menu);
        mBtmView.setOnNavigationItemSelectedListener(this);
        mBtmView.getMenu().findItem(R.id.home).setChecked(true); // home tab is selected by default

        this.Tab1 = new Tab1Fragment();
        this.Tab2 = new Tab2Fragment();
        this.Tab4 = new Tab4Fragment();
        this.Tab5 = new Tab5Fragment();

        loadFragment(Tab1); // loads home page by default
        if (!mVideoModel.getVideoAsString().equals("")) { // loads video from state
            Tab1.setVideo(mVideoModel.getVideo());
        }
    }

    /**
     * Handles functionality for when a user selects a menu item on the bottom navigation bar.
     *
     * @param selected - represents the currently selected menu item.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem selected) {
        this.mMenuId = selected.getItemId();
        // uncheck other menu items
        for (int i = 0; i < mBtmView.getMenu().size(); i++) {
            MenuItem menuItem = mBtmView.getMenu().getItem(i);
            if (menuItem.getItemId() != selected.getItemId()) {
                menuItem.setChecked(false);
            }
        }
        switch (mMenuId) {
            case R.id.home: {
                Log.i(KEY, "Home button pressed.");
                mBtmView.getMenu().findItem(R.id.home).setChecked(true);
                if (!mVideoModel.getVideoAsString().equals("")) {
                    Tab1.setVideo(mVideoModel.getVideo());
                }
                loadFragment(Tab1);
                break;
            }
            case R.id.search: {
                Log.i(KEY, "Search button pressed.");
                mBtmView.getMenu().findItem(R.id.search).setChecked(true);
                loadFragment(Tab2);
                break;
            }
            case R.id.taco: { // Note: there is no fragment for this tab. It's just a "button".
                mBtmView.getMenu().findItem(R.id.taco).setChecked(true);
                Log.i(KEY, "Camera button pressed.");
                takeVideoIntent();
                mBtmView.getMenu().findItem(R.id.taco).setChecked(false); // go back to the home page
                mBtmView.getMenu().findItem(R.id.home).setChecked(true);
                loadFragment(Tab1);
                break;
            }
            case R.id.chat: {
                mBtmView.getMenu().findItem(R.id.chat).setChecked(true);
                Log.i(KEY, "Chat button pressed.");
                loadFragment(Tab4);
                break;
            }
            case R.id.user: {
                mBtmView.getMenu().findItem(R.id.user).setChecked(true);
                Log.i(KEY, "Profile button pressed.");
                loadFragment(Tab5);
                break;
            }
        }
        return false;
    }

    /**
     * Replaces current fragment without another fragment when switching between tabs.
     *
     * @param fragment - represents the fragment to be loaded.
     */
    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    /**
     * Creates video capturing intent and starts the recording activity.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void takeVideoIntent() {
        Intent i = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        // handles case where no app can handle the intent and crashes
        if (i.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(i, 1); // for now, video request code = 1
        }
    }

    /**
     * Sets the video view in the home page after user is done recording and updates video model.
     *
     * @param requestCode - is equal to 1 if the user has just finished taking a video in the app
     * @param resultCode  - is equal to -1 if the result from the Activity is valid
     * @param data        - represents the intent in which data should be pulled from
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("attempt", "attempt at video adding");
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();
            Tab1.resultVideoView.setVideoURI(videoUri);
            Tab1.resultVideoView.start();
            mVideoModel.setVideo(videoUri.toString());
        }
    }

    /**
     * Invoked when the activity may be temporarily destroyed, save the instance state here.
     *
     * @param outState State to save out through the bundle
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(VIDEO_BUNDLE_KEY, mVideoModel.getVideoAsString());
    }

    /**
     * This callback is called only when there is a saved instance that is previously saved by using
     * onSaveInstanceState().   We restore some state in onCreate(),
     *
     * @param savedInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        setStartingVideo(savedInstanceState);
    }

    /**
     * Sets the video to be shown on the home page
     *
     * @param state - represents the state the app is restoring from. May be null.
     */
    private void setStartingVideo(Bundle state) {
        if (state != null) {
            String videoUri = state.getString(VIDEO_BUNDLE_KEY, null);
            mVideoModel.setVideo(videoUri);
            Tab1.resultVideoView.setMediaController(Tab1.mediaController);
            Tab1.resultVideoView.setVisibility(View.VISIBLE);
            Tab1.resultVideoView.setVideoURI(mVideoModel.getVideo());
            Tab1.resultVideoView.start();
        } else {
            mVideoModel.setVideo("");
        }
    }

    /**
     * Private inner class to hold the big video that is being displayed in the app
     */
    private static class VideoModel {
        private String videoUri = "";

        public Uri getVideo() {
            return Uri.parse(videoUri);
        }

        public void setVideo(String u) {
            this.videoUri = u;
        }

        public String getVideoAsString() {
            return videoUri;
        }
    }

}
