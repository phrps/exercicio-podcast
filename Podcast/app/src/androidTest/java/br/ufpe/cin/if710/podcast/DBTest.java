package br.ufpe.cin.if710.podcast;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import br.ufpe.cin.if710.podcast.db.PodcastProviderContract;

import static org.junit.Assert.assertNotEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DBTest {
    @Test
    public void openDB() throws  Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        Cursor cursor = appContext.getContentResolver().query( PodcastProviderContract.EPISODE_LIST_URI, null, null, null, null);
        assertNotEquals( null, cursor );
    }
    @Test
    public void storeDB() throws  Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PodcastProviderContract.DATE, "a");
        contentValues.put(PodcastProviderContract.DESCRIPTION, "b");
        contentValues.put(PodcastProviderContract.DOWNLOAD_LINK, "c");
        contentValues.put(PodcastProviderContract.EPISODE_URI, "d");
        contentValues.put(PodcastProviderContract.EPISODE_LINK, "e");
        contentValues.put(PodcastProviderContract.TITLE, "f");
        appContext.getContentResolver().insert( PodcastProviderContract.EPISODE_LIST_URI, contentValues );

        String[] selectionArgs = {"c"};
        String selection = PodcastProviderContract.DOWNLOAD_LINK + " =?";

        Cursor cursor = appContext.getContentResolver().query( PodcastProviderContract.EPISODE_LIST_URI, null, selection, selectionArgs, null);

        assertNotEquals( null, cursor );
    }
    @Test
    public void updateDb() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();

        ContentValues contentValues = new ContentValues();
        contentValues.put( PodcastProviderContract.DATE, "a" );
        contentValues.put( PodcastProviderContract.DESCRIPTION, "b" );
        contentValues.put( PodcastProviderContract.DOWNLOAD_LINK, "c" );
        contentValues.put( PodcastProviderContract.EPISODE_URI, "d" );
        contentValues.put( PodcastProviderContract.EPISODE_LINK, "e" );
        contentValues.put( PodcastProviderContract.TITLE, "f" );
        appContext.getContentResolver().insert( PodcastProviderContract.EPISODE_LIST_URI, contentValues );

        ContentValues newContentValues = new ContentValues();
        newContentValues.put( PodcastProviderContract.DATE, "a" );
        newContentValues.put( PodcastProviderContract.DESCRIPTION, "b" );
        newContentValues.put( PodcastProviderContract.DOWNLOAD_LINK, "c" );
        newContentValues.put( PodcastProviderContract.EPISODE_URI, "d" );
        newContentValues.put( PodcastProviderContract.EPISODE_LINK, "e" );
        newContentValues.put( PodcastProviderContract.TITLE, "g" );

        String[] selectionArgs = {"g"};
        String selection = PodcastProviderContract.TITLE + " =?";
        appContext.getContentResolver().update( PodcastProviderContract.EPISODE_LIST_URI, newContentValues, selection, selectionArgs );
        Cursor cursor = appContext.getContentResolver().query( PodcastProviderContract.EPISODE_LIST_URI, null, selection, selectionArgs, null);

        assertNotEquals( null, cursor );
    }
}