package br.ufpe.cin.if710.podcast.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import br.ufpe.cin.if710.podcast.R;
import br.ufpe.cin.if710.podcast.domain.ItemFeed;


public class EpisodeDetailActivity extends Activity  {

    public static String ITEM_FEED = "itemFeed";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode_detail);

        ItemFeed itemFeed = (ItemFeed) getIntent().getSerializableExtra(ITEM_FEED);
        TextView title = (TextView) findViewById(R.id.pcTitle);
        title.setText(itemFeed.getTitle());
        TextView description = (TextView) findViewById(R.id.pcDecription);
        description.setText(itemFeed.getDescription());
        TextView data = (TextView) findViewById(R.id.pcData);
        data.setText(itemFeed.getPubDate());


    }
}
