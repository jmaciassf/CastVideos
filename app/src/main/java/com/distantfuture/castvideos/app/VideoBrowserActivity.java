
package com.distantfuture.castvideos.app;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.media.MediaRouter.RouteInfo;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.distantfuture.castcompanionlibrary.lib.cast.VideoCastManager;
import com.distantfuture.castcompanionlibrary.lib.cast.callbacks.IVideoCastConsumer;
import com.distantfuture.castcompanionlibrary.lib.cast.callbacks.VideoCastConsumerImpl;
import com.distantfuture.castcompanionlibrary.lib.utils.MiniController;

public class VideoBrowserActivity extends FragmentActivity {

  private static final String TAG = "VideoBrowserActivity";
  private VideoCastManager mCastManager;
  private IVideoCastConsumer mCastConsumer;
  private MiniController mMini;
  private MenuItem mediaRouteMenuItem;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    VideoCastManager.checkGooglePlayServices(this);
    setContentView(R.layout.video_browser);
    ActionBar actionBar = getActionBar();

    mCastManager = CastApplication.getCastManager(this);

    // -- Adding MiniController
    mMini = (MiniController) findViewById(R.id.miniController1);
    mCastManager.addMiniController(mMini);

    mCastConsumer = new VideoCastConsumerImpl() {

      @Override
      public void onFailed(int resourceId, int statusCode) {

      }

      @Override
      public void onConnectionSuspended(int cause) {
        Log.d(TAG, "onConnectionSuspended() was called with cause: " + cause);
        Utils.
            showToast(VideoBrowserActivity.this, R.string.connection_temp_lost);
      }

      @Override
      public void onConnectivityRecovered() {
        Utils.
            showToast(VideoBrowserActivity.this, R.string.connection_recovered);
      }

      @Override
      public void onCastDeviceDetected(final RouteInfo info) {
        if (!CastPreference.isFtuShown(VideoBrowserActivity.this)) {
          CastPreference.setFtuShown(VideoBrowserActivity.this);

          Log.d(TAG, "Route is visible: " + info);
          new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
              if (mediaRouteMenuItem.isVisible()) {
                Log.d(TAG, "Cast Icon is visible: " + info.getName());
                showFtu();
              }
            }
          }, 1000);
        }
      }
    };

    setupActionBar(actionBar);
    mCastManager.reconnectSessionIfPossible(this, false);
  }

  private void setupActionBar(ActionBar actionBar) {
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    getActionBar().setIcon(R.drawable.actionbar_logo_castvideos);
    getActionBar().setDisplayShowTitleEnabled(false);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.main, menu);

    mediaRouteMenuItem = mCastManager.
        addMediaRouterButton(menu, R.id.media_route_menu_item, this);

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_settings:
        Intent i = new Intent(VideoBrowserActivity.this, CastPreference.class);
        startActivity(i);
        break;
    }
    return true;
  }

  private void showFtu() {
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (!mCastManager.isConnected()) {
      return super.onKeyDown(keyCode, event);
    } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
      changeVolume(CastApplication.VOLUME_INCREMENT);
    } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
      changeVolume(-CastApplication.VOLUME_INCREMENT);
    } else {
      return super.onKeyDown(keyCode, event);
    }
    return true;
  }

  private void changeVolume(double volumeIncrement) {
    if (mCastManager == null) {
      return;
    }
    try {
      mCastManager.incrementVolume(volumeIncrement);
    } catch (Exception e) {
      Log.e(TAG, "onVolumeChange() Failed to change volume", e);
      Utils.handleException(this, e);
    }
  }

  @Override
  protected void onResume() {
    Log.d(TAG, "onResume() was called");
    mCastManager = CastApplication.getCastManager(this);
    if (null != mCastManager) {
      mCastManager.addVideoCastConsumer(mCastConsumer);
      mCastManager.incrementUiCounter();
    }

    super.onResume();
  }

  @Override
  protected void onPause() {
    mCastManager.decrementUiCounter();
    mCastManager.removeVideoCastConsumer(mCastConsumer);
    super.onPause();
  }

  @Override
  protected void onDestroy() {
    if (null != mCastManager) {
      mMini.removeOnMiniControllerChangedListener(mCastManager);
    }
    super.onDestroy();
  }

}
