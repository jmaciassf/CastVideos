
package com.distantfuture.castvideos.app;

import android.app.Application;
import android.content.Context;

import com.distantfuture.castcompanionlibrary.lib.cast.VideoCastManager;
import com.distantfuture.castcompanionlibrary.lib.utils.CastUtils;

public class CastApplication extends Application {
  private static String APPLICATION_ID;
  private static VideoCastManager mCastMgr = null;
  public static final double VOLUME_INCREMENT = 0.05;
  private static Context mAppContext;

  @Override
  public void onCreate() {
    super.onCreate();
    mAppContext = getApplicationContext();
    APPLICATION_ID = getString(R.string.app_id);
    CastUtils.saveFloatToPreference(getApplicationContext(), VideoCastManager.PREFS_KEY_VOLUME_INCREMENT, (float) VOLUME_INCREMENT);
  }

  public static VideoCastManager getCastManager(Context context) {
    if (null == mCastMgr) {
      mCastMgr = VideoCastManager.initialize(context, APPLICATION_ID, null);
      mCastMgr.enableFeatures(VideoCastManager.FEATURE_NOTIFICATION |
          VideoCastManager.FEATURE_LOCKSCREEN |
          VideoCastManager.FEATURE_DEBUGGING);

    }
    mCastMgr.setContext(context);
    String destroyOnExitStr = CastUtils.getStringFromPreference(context, CastPreference.TERMINATION_POLICY_KEY);
    mCastMgr.setStopOnDisconnect(null != destroyOnExitStr && CastPreference.STOP_ON_DISCONNECT.equals(destroyOnExitStr));
    return mCastMgr;
  }

}
