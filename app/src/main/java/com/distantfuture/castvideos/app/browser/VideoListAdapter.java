
package com.distantfuture.castvideos.app.browser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.distantfuture.castvideos.app.R;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;

import java.util.List;

/**
 * An {@link ArrayAdapter} to populate the list of videos.
 */
public class VideoListAdapter extends ArrayAdapter<MediaInfo> {

  private final Context mContext;
  private final float mAspectRatio = 9f / 16f;

  public VideoListAdapter(Context context) {
    super(context, 0);
    this.mContext = context;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    ViewHolder holder;
    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    MediaMetadata mm = getItem(position).getMetadata();

    if (convertView == null) {
      convertView = inflater.inflate(R.layout.browse_row, null);
      holder = new ViewHolder();
      holder.imgView = (ImageView) convertView.findViewById(R.id.imageView1);
      holder.titleView = (TextView) convertView.findViewById(R.id.textView1);
      holder.descrView = (TextView) convertView.findViewById(R.id.textView2);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    AQuery aq = new AQuery(convertView);
    aq.id(holder.imgView)
        .width(110)
        .image(mm.getImages()
            .get(0)
            .getUrl()
            .toString(), true, true, 0, R.drawable.default_video, null, 0, mAspectRatio);
    aq.id(holder.titleView).text(mm.getString(MediaMetadata.KEY_TITLE));
    aq.id(holder.descrView).text(mm.getString(MediaMetadata.KEY_SUBTITLE));

    return convertView;
  }

  private class ViewHolder {
    TextView titleView;
    TextView descrView;
    ImageView imgView;
  }

  public void setData(List<MediaInfo> data) {
    clear();
    if (data != null) {
      for (MediaInfo item : data) {
        add(item);
      }
    }

  }
}
