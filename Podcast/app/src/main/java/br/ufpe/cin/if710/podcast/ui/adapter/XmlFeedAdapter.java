package br.ufpe.cin.if710.podcast.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import br.ufpe.cin.if710.podcast.R;
import br.ufpe.cin.if710.podcast.domain.ItemFeed;
import br.ufpe.cin.if710.podcast.ui.DownloadService;
import br.ufpe.cin.if710.podcast.ui.EpisodeDetailActivity;

public class XmlFeedAdapter extends ArrayAdapter<ItemFeed> {

    int linkResource;
    Context context;
    List<ItemFeed> itemFeedList;
    MediaPlayer mediaPlayer;

    public XmlFeedAdapter(Context context, int resource, List<ItemFeed> objects) {
        super(context, resource, objects);
        linkResource = resource;
        this.context = context;
        this.itemFeedList = objects;
    }

    /**
     * public abstract View getView (int position, View convertView, ViewGroup parent)
     * <p>
     * Added in API level 1
     * Get a View that displays the data at the specified position in the data set. You can either create a View manually or inflate it from an XML layout file. When the View is inflated, the parent View (GridView, ListView...) will apply default layout parameters unless you use inflate(int, android.view.ViewGroup, boolean) to specify a root view and to prevent attachment to the root.
     * <p>
     * Parameters
     * position	The position of the item within the adapter's data set of the item whose view we want.
     * convertView	The old view to reuse, if possible. Note: You should check that this view is non-null and of an appropriate type before using. If it is not possible to convert this view to display the correct data, this method can create a new view. Heterogeneous lists can specify their number of view types, so that this View is always of the right type (see getViewTypeCount() and getItemViewType(int)).
     * parent	The parent that this view will eventually be attached to
     * Returns
     * A View corresponding to the data at the specified position.
     */


	/*
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.itemlista, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.item_title);
		textView.setText(items.get(position).getTitle());
	    return rowView;
	}
	/**/

    //http://developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder
    static class ViewHolder {
        TextView item_title;
        TextView item_date;
        Button button;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(getContext(), linkResource, null);
            holder = new ViewHolder();

            convertView.setTag(holder);
            convertView.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                   Intent intent = new Intent(context, EpisodeDetailActivity.class);
                   intent.putExtra(EpisodeDetailActivity.ITEM_FEED, getItem(position));
                   context.startActivity(intent);
                 }
             });
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.item_title = (TextView) convertView.findViewById(R.id.item_title);
        holder.item_date = (TextView) convertView.findViewById(R.id.item_date);
        holder.item_title.setText(getItem(position).getTitle());
        holder.item_date.setText(getItem(position).getPubDate());

        holder.button = (Button) convertView.findViewById(R.id.item_action);
        ItemFeed itemFeed = getItem( position );
        if (itemFeed.getFileUri() == null || itemFeed.getFileUri().equals( "null" )) {
            Log.d("Uri","uri: " + itemFeed.getFileUri());
            holder.button.setText( "Download" );
        }
        else {
            holder.button.setText( "Play" );
        }


        holder.button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("Button","Clicou baixar");
                if (holder.button.getText() == "Download") {
                    // Baixar PODCAST

                    Intent downloadService = new Intent(context, DownloadService.class);
                    downloadService.setData(Uri.parse(itemFeedList.get(position).getDownloadLink()));

                    downloadService.addFlags(downloadService.FLAG_ACTIVITY_NEW_TASK);
                    context.startService(downloadService);

                    holder.button.setText( "Downloading..." );
                    holder.button.setEnabled(false);
                    Log.d("Download", "Startou Service");
                } else if (holder.button.getText() == "Play") {
                    File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    File audioFile = new File(root, Uri.parse(itemFeedList.get(position).getDownloadLink()).getLastPathSegment());
                    Uri audioUri = Uri.parse("file://" + audioFile.getAbsolutePath());
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer = MediaPlayer.create(context, audioUri);
                    mediaPlayer.start();
                    holder.button.setText("Pause");
                } else if (holder.button.getText() == "Pause") {
                    mediaPlayer.pause();
                    holder.button.setText("Play");
                }


        }
        });


        return convertView;
    }
}