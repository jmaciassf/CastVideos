
package com.distantfuture.castvideos.app.browser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;

import com.distantfuture.castcompanionlibrary.lib.utils.CastUtils;
import com.distantfuture.castvideos.app.R;
import com.distantfuture.castvideos.app.LocalPlayerActivity;
import com.google.android.gms.cast.MediaInfo;

import java.util.List;

public class VideoBrowserListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<MediaInfo>> {
  private VideoListAdapter mAdapter;

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    getListView().setFastScrollEnabled(true);

    mAdapter = new VideoListAdapter(getActivity());
    setEmptyText(getString(R.string.no_video_found));
    setListAdapter(mAdapter);
    setListShown(false);
    getLoaderManager().initLoader(0, null, this);
  }

  @Override
  public void onLoadFinished(Loader<List<MediaInfo>> arg0, List<MediaInfo> data) {
    mAdapter.setData(data);
    if (isResumed()) {
      setListShown(true);
    } else {
      setListShownNoAnimation(true);
    }
  }

  @Override
  public void onLoaderReset(Loader<List<MediaInfo>> arg0) {
    mAdapter.setData(null);
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    MediaInfo selectedMedia = mAdapter.getItem(position);
    handleNavigation(selectedMedia, false);
  }

  private void handleNavigation(MediaInfo info, boolean autoStart) {
    Intent intent = new Intent(getActivity(), LocalPlayerActivity.class);
    intent.putExtra("media", CastUtils.fromMediaInfo(info));
    intent.putExtra("shouldStart", autoStart);
    getActivity().startActivity(intent);
  }

  @Override
  public Loader<List<MediaInfo>> onCreateLoader(int arg0, Bundle arg1) {
    final String CATALOG_URL = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/" + "videos-enhanced-b.json";

    return new VideoItemLoader(getActivity(), CATALOG_URL);
  }
}
