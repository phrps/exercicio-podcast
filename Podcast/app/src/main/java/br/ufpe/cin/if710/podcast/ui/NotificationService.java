package br.ufpe.cin.if710.podcast.ui;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import br.ufpe.cin.if710.podcast.db.PodcastProviderContract;
import br.ufpe.cin.if710.podcast.domain.ItemFeed;

public class NotificationService extends Service {

    public Intent intent;
    public NotificationCompat.Builder notificationBuilder;

    public void onCreate() {
        super.onCreate();
        notificationBuilder = new NotificationCompat.Builder( this ).setContentTitle("Podcast").setContentText("Download Complete").setVibrate(new long[] { 1000, 1000});

        intent = new Intent( this, MainActivity.class );

        PendingIntent pendingIntent = PendingIntent.getActivity( this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );

        notificationBuilder.setContentIntent( pendingIntent );

        IntentFilter f = new IntentFilter(DownloadService.DOWNLOAD_COMPLETE);
    }

    private BroadcastReceiver onDownloadCompleteEvent=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent i) {
            Toast.makeText(context, "Download finalizado!", Toast.LENGTH_LONG).show();


            List<ItemFeed> db = updateListView();
            ItemFeed item = null;

            for (int j = 0; j < db.size() && item == null; ++j) {
                Log.d("Atualizar Uri", "Achou no db");
                if (db.get(j).getDownloadLink().equals(i.getStringExtra("Downloaded"))) {


                    File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    File audioFile = new File(root, Uri.parse(i.getStringExtra("Downloaded")).getLastPathSegment());

                    item = db.get(j);
                    Log.d("Update", "Uri Antiga" + "file://" + item.getFileUri());
                    ItemFeed itemNew = new ItemFeed(item.getTitle(),item.getLink(),item.getPubDate(), item.getDescription(),
                            item.getDownloadLink(), Uri.parse("file://" + audioFile.getAbsolutePath()).toString());
                    Log.d("Update", "Uri Nova" + item.getFileUri());
                    ContentValues contentValues = itemFeedToContentValue(itemNew);

                    NotificationManager notif = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notif.notify( 1, notificationBuilder.build());

                    String selection =
                            PodcastProviderContract.DOWNLOAD_LINK + " =?";

                    String[] selectionArgs = {item.getDownloadLink()};

                    int row = getContentResolver().update(PodcastProviderContract.EPISODE_LIST_URI, contentValues, selection, selectionArgs);
                    Log.d("Update Row", String.valueOf( row ));

                }
            }
        }
    };

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

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException( "Not yet implemented" );
    }
}
