package br.ufpe.cin.if710.podcast.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class PodcastProvider extends ContentProvider {
    private PodcastDBHelper dbHelper;

    public PodcastProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int nDeletedRows = 0;
        // Verifica se uri é valida para recuperar quantidade de linhas deletadas
        if (validUri(uri)) {
            nDeletedRows = dbHelper.getWritableDatabase().delete(PodcastDBHelper.DATABASE_TABLE, selection, selectionArgs);
        } /*else {
            throw new UnsupportedOperationException("Not yet implemented");
        }*/

        return nDeletedRows;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //Insere no DB recuperando o identificador se a uri é valida.
        Uri returnUri = null;
        if (validUri(uri)) {
            long ID = dbHelper.getWritableDatabase().insert(PodcastDBHelper.DATABASE_TABLE, null, values);
            //Recupera a uri de acesso do item criado
            returnUri = Uri.withAppendedPath(PodcastProviderContract.EPISODE_LIST_URI, Long.toString(ID));
        } /*else {
            throw new UnsupportedOperationException("Not yet implemented");
        }*/

        return returnUri;
    }

    @Override
    public boolean onCreate() {
        dbHelper = PodcastDBHelper.getInstance(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        //Realiza a query se a uri for valida
        if (validUri(uri)) {
            cursor = dbHelper.getReadableDatabase().query(PodcastDBHelper.DATABASE_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
        } /*else {
            throw new UnsupportedOperationException("Not yet implemented");
        }*/

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int nUpdatedRows = 0;
        // Verifica se uri é valida para recuperar quantidade de linhas atualizadas
        if (validUri(uri)) {
            nUpdatedRows = dbHelper.getWritableDatabase().update(PodcastDBHelper.DATABASE_TABLE, values, selection, selectionArgs);
        } /*else {
            throw new UnsupportedOperationException("Not yet implemented");
        }*/

        return nUpdatedRows;
    }

    private boolean validUri(Uri uri){
        return uri.getLastPathSegment().equals(PodcastProviderContract.EPISODE_TABLE);
    }
}
