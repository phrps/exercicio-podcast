package br.ufpe.cin.if710.podcast.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import br.ufpe.cin.if710.podcast.R;
import br.ufpe.cin.if710.podcast.db.PodcastProviderContract;
import br.ufpe.cin.if710.podcast.domain.ItemFeed;
import br.ufpe.cin.if710.podcast.domain.XmlFeedParser;
import br.ufpe.cin.if710.podcast.ui.adapter.XmlFeedAdapter;

public class MainActivity extends Activity {

    //ao fazer envio da resolucao, use este link no seu codigo!
    private final String RSS_FEED = "http://leopoldomt.com/if710/fronteirasdaciencia.xml";
    //TODO teste com outros links de podcast

    private ListView items;
    Intent notificationServiceIntent;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        items = (ListView) findViewById(R.id.items);
        notificationServiceIntent = new Intent( this, NotificationService.class );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new DownloadXmlTask().execute(RSS_FEED);
    }

    @Override
    protected void onStop() {
        super.onStop();
        XmlFeedAdapter adapter = (XmlFeedAdapter) items.getAdapter();
        adapter.clear();
    }

    private class DownloadXmlTask extends AsyncTask<String, Void, List<ItemFeed>> {
        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "Iniciando app...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected List<ItemFeed> doInBackground(String... params) {
            List<ItemFeed> itemList = new ArrayList<>();
            try {
                itemList = XmlFeedParser.parse(getRssFeed(params[0]));
                // Armazena lista de itemFeed no db
                storesList(itemList);


            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
            }
            return itemList;
        }

        @Override
        protected void onPostExecute(List<ItemFeed> feed) {
            Toast.makeText(getApplicationContext(), "terminando...", Toast.LENGTH_SHORT).show();

            //Adapter Personalizado
            feed = updateListView();
            XmlFeedAdapter adapter = new XmlFeedAdapter(getApplicationContext(), R.layout.itemlista, feed);

            items.setAdapter(adapter);
            items.setTextFilterEnabled(true);

            /*
            items.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    XmlFeedAdapter adapter = (XmlFeedAdapter) parent.getAdapter();
                    ItemFeed item = adapter.getItem(position);
                    String msg = item.getTitle() + " " + item.getLink();
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                }
            });
            /**/
        }
    }



    //TODO Opcional - pesquise outros meios de obter arquivos da internet
    private String getRssFeed(String feed) throws IOException {
        InputStream in = null;
        String rssFeed = "";
        try {
            URL url = new URL(feed);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            in = conn.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int count; (count = in.read(buffer)) != -1; ) {
                out.write(buffer, 0, count);
            }
            byte[] response = out.toByteArray();
            rssFeed = new String(response, "UTF-8");
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return rssFeed;
    }

    public String isValidString(String string) {
        if (string == null) {
            return ("null");
        }
        return string;
    }

    public void storesList(List<ItemFeed> feedList) {
        boolean exist = false;
        for (ItemFeed itemFeed : feedList) {

            ContentValues contentValues = new ContentValues();
            // Armazenar o item no db.
            contentValues.put(PodcastProviderContract.DATE, isValidString(itemFeed.getPubDate()));
            contentValues.put(PodcastProviderContract.DESCRIPTION, isValidString(itemFeed.getDescription()));
            contentValues.put(PodcastProviderContract.DOWNLOAD_LINK, isValidString(itemFeed.getDownloadLink()));
            contentValues.put(PodcastProviderContract.EPISODE_URI, isValidString(itemFeed.getFileUri()));
            contentValues.put(PodcastProviderContract.EPISODE_LINK, isValidString(itemFeed.getLink()));
            contentValues.put(PodcastProviderContract.TITLE, isValidString(itemFeed.getTitle()));


            String[] selectionArgs = {itemFeed.getDownloadLink()};

            String selection = PodcastProviderContract.DOWNLOAD_LINK + " =?";

            Cursor cursor = getContentResolver().query(PodcastProviderContract.EPISODE_LIST_URI, null, selection, selectionArgs, null);

            if (cursor != null && cursor.moveToFirst()) {
                exist = true;
                cursor.close();
            }

            if (exist == false) {
                Log.d("DB insert", "Inseriu");
                Log.d("Uri","uri: " + isValidString(itemFeed.getFileUri()));
                getContentResolver().insert( PodcastProviderContract.EPISODE_LIST_URI, contentValues );
            }
            else {
                Log.d("Uri","uri: " + isValidString(itemFeed.getFileUri()));
                Log.d("DB insert", "Já tem");
            }
            exist = false;
        }
    }

    // Atualiza a listView utilizando o DB
    public List<ItemFeed> updateListView() {
        List<ItemFeed> list = new ArrayList<>();
        // uso de cursor para pegar os dados no bd.
        Cursor cursor = getContentResolver().query(PodcastProviderContract.EPISODE_LIST_URI,null,null,null,null);
        if (cursor != null && cursor.moveToFirst()) {
                while (cursor.moveToNext()) {
                    list.add(new ItemFeed(cursor.getString(cursor.getColumnIndex(PodcastProviderContract.TITLE)),
                                            cursor.getString(cursor.getColumnIndex(PodcastProviderContract.EPISODE_LINK)),
                                            cursor.getString(cursor.getColumnIndex(PodcastProviderContract.DATE)),
                                            cursor.getString(cursor.getColumnIndex(PodcastProviderContract.DESCRIPTION)),
                                            cursor.getString(cursor.getColumnIndex(PodcastProviderContract.DOWNLOAD_LINK)),
                                            cursor.getString(cursor.getColumnIndex(PodcastProviderContract.EPISODE_URI))));
                }
            cursor.close();
        }

        return list;
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter f = new IntentFilter(DownloadService.DOWNLOAD_COMPLETE);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(onDownloadCompleteEvent, f);
        // Desliga o intent do notification Service
        stopService( notificationServiceIntent );
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(onDownloadCompleteEvent);
        // Inicia o intent do notification Service
        startService( notificationServiceIntent );
    }

    private BroadcastReceiver onDownloadCompleteEvent=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent i) {
            Toast.makeText(context, "Download finalizado!", Toast.LENGTH_LONG).show();
            Button button = (Button) findViewById(R.id.item_action);


            List<ItemFeed> db = updateListView();
            ItemFeed item = null;

            for (int j = 0; j < db.size() && item == null; ++j) {
                Log.d("Atualizar Uri", "Achou no db");
                if (db.get(j).getDownloadLink().equals(i.getStringExtra("Downloaded"))) {
                    // Atualiza DB e ativa botão de play
                    File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    File audioFile = new File(root, Uri.parse(i.getStringExtra("Downloaded")).getLastPathSegment());

                    item = db.get(j);
                    Log.d("Update", "Uri Antiga" + "file://" + item.getFileUri());
                    ItemFeed itemNew = new ItemFeed(item.getTitle(),item.getLink(),item.getPubDate(), item.getDescription(),
                                                    item.getDownloadLink(), Uri.parse("file://" + audioFile.getAbsolutePath()).toString());
                    Log.d("Update", "Uri Nova" + item.getFileUri());
                    ContentValues contentValues = itemFeedToContentValue(itemNew);

                    String selection =
                            PodcastProviderContract.DOWNLOAD_LINK + " =?";

                    String[] selectionArgs = {item.getDownloadLink()};

                    int row = getContentResolver().update(PodcastProviderContract.EPISODE_LIST_URI, contentValues, selection, selectionArgs);
                    Log.d("Update Row", String.valueOf( row ));
                    
                }
            }
    }
    };

    public ContentValues itemFeedToContentValue(ItemFeed i) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PodcastProviderContract.TITLE, i.getTitle());
        contentValues.put(PodcastProviderContract.DATE, i.getPubDate());
        contentValues.put(PodcastProviderContract.DESCRIPTION, i.getDescription());
        contentValues.put(PodcastProviderContract.EPISODE_LINK, i.getLink());
        contentValues.put(PodcastProviderContract.DOWNLOAD_LINK, i.getDownloadLink());
        contentValues.put(PodcastProviderContract.EPISODE_URI, i.getFileUri());

        return contentValues;
    }

    public  boolean checkConnection() {
        boolean connected;
        ConnectivityManager conectivtyManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            connected = true;
        } else {
            connected = false;
        }
        return connected;
    }
}
