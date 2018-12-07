
package com.distantfuture.castvideos.app.browser;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.google.android.gms.cast.MediaInfo;

import java.util.List;

public class VideoItemLoader extends AsyncTaskLoader<List<MediaInfo>> {

  private static final String TAG = "VideoItemLoader";
  private final String mUrl;

  public VideoItemLoader(Context context, String url) {
    super(context);
    mUrl = url;
  }

  @Override
  public List<MediaInfo> loadInBackground() {
    try {
      return VideoProvider.buildMedia(mUrl);
    } catch (Exception e) {
      Log.e(TAG, "Failed to fetch media data", e);
      return null;
    }
  }

  @Override
  protected void onStartLoading() {
    super.onStartLoading();
    forceLoad();
  }

  /**
   * Handles a request to stop the Loader.
   */
  @Override
  protected void onStopLoading() {
    // Attempt to cancel the current load task if possible.
    cancelLoad();
  }

}
