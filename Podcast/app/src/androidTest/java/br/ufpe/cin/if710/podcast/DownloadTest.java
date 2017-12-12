package br.ufpe.cin.if710.podcast;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import br.ufpe.cin.if710.podcast.ui.DownloadService;

import static org.junit.Assert.assertNotEquals;


@RunWith(AndroidJUnit4.class)
public class DownloadTest {
    @Test
    public void createDownloadIntent() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        Intent downloadService = new Intent(appContext, DownloadService.class);
        downloadService.setData( Uri.parse("http://dstats.net/download/http://www6.ufrgs.br/frontdaciencia/arquivos/Fronteiras_da_Ciencia-T08E29-Mario.Bunge.1-18.09.2017.mp3"));

        downloadService.addFlags(downloadService.FLAG_ACTIVITY_NEW_TASK);
        appContext.startService(downloadService);

        assertNotEquals( null, downloadService );
    }
}
