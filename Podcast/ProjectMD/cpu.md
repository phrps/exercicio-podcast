# Consumo de CPU

Para esse conjunto de testes foi utilizado o Android Emulator - Nexus 5X_API_26 e Android Monitor.

[    imagem  cpu_open_info_download    ]:<> 
![alt text] (https://github.com/phrps/exercicio-podcast/tree/master/Podcast/cpu_open_info_download.png)
#####Abrir App: 0-4s
#####Pedir Info: 5-8s
#####Leitura do DB para retorno a tela inicial: 9-13s
#####Download: 14-1m9s


[    imagem  cpu_downloaded_noti_play.png  ]:<> 
![alt text] (https://github.com/phrps/exercicio-podcast/tree/master/Podcast/cpu_downloaded_noti_play.png)
#####Download: 2m15s-2m.24s.
#####Atualização do DB: 2m.25s-2m.26s
#####Reabetura do app: 2m.33-2m.37s.
#####Play: 2m.42s-3m.20s.

## Abrir APP
Esse teste mostra o consumo da CPU ao abrir o app.

### Teste
Para realização desse teste foi aberto o simulador e executado o app pela primeira vez, o teste foi finalizado quando a tela inicial
da aplicação mostrava todos os episodios.
Para medir o uso da CPU foi utilizado o Android Monitor.

### Resultados & Conclusão
O app registrou dois picos de consumo um de 27% e outro de 48%.
O primeiro pico foi causado pelo download da lista de ItemFeed e subsequentemente o armazenamento do mesmo em um database, para esse armazenamento o contentProvider é chamado para cada elemento da lista
, assim gastando um alto processamento, uma solução para esse problema é a utilização de um Bulk Insert. 
O segundo pico foi causo pelo acesso deste database para recuperar as informações, . (list<ItemFeed>)


### Código

Primeiro pico:
```java
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
```	
		
		
Segundo Pico:
```java
        protected void onPostExecute(List<ItemFeed> feed) {
            Toast.makeText(getApplicationContext(), "terminando...", Toast.LENGTH_SHORT).show();

            //Adapter Personalizado
            feed = updateListView();
            XmlFeedAdapter adapter = new XmlFeedAdapter(getApplicationContext(), R.layout.itemlista, feed);

            items.setAdapter(adapter);
            items.setTextFilterEnabled(true);
        }

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
```

## Perdir informação do Episodio
Esse teste mostra o uso da CPU ao ser solicitada informações do episodio.

### Teste
Para esse teste foi dada a aplicação na tela inicial, medido o uso da CPU do momento que foi requisitada as informações do episodio até
voltar a tela inicial estar totalmente carregada.
Para medir o uso da CPU foi utilizado o Android Monitor.

### Resultados & Conclusão
O app registrou dois picos de consumo um de 25% e outro de 55%.
O primeiro pico foi causado pela chamada da activity de detalhe de episodio.
O segundo pico foi causo pelo acesso deste database para recuperar as informações. (list<ItemFeed>)

### Código

```java
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
```

## Download do Episodio
Esse teste mostra o uso da CPU ao ser solicitado o download do episodio.

### Teste 
Para realização desse teste foi aberto a aplicação e então solicitado o download de um Episodio. O teste foi finalizado, quando
o download foi finalizado e a Uri no Database atualizada.

### Resultados & Conclusão

O teste registrou um pico de 62% do consumo da CPU no instante em que é solicitado o download e um pico de 52% ao final do download.
Quando solicitado o download, o app chama um IntentService para concluir a ação o que causa esse alto consumo, uma solução seria a utilização
de um download manager, este por sua vez passa a responsabilidade do download ao sistema.
Após o fim do download o IntentService envia um broadcast que é recebido pelo broadcastReceiver para que sejá alterado
o database atualizando a Uri do episodio e notificado ao usuário. Quando a aplicação esta em segundo plano é chamada
um IntentService (NotificationService), este por sua vez fara o papel do broadcastReceiver da main_activy.

### Código

Primeiro Pico:
```java
public class DownloadService extends IntentService {

    public static final String DOWNLOAD_COMPLETE = "br.ufpe.cin.if710.services.action.DOWNLOAD_COMPLETE";


    public DownloadService() {
        super("DownloadService");
    }

    @Override
    public void onHandleIntent(Intent i) {
        try {
            Log.d("Download", "Checa Permissão");
            File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            root.mkdirs();
            File output = new File(root, i.getData().getLastPathSegment());
            if (output.exists()) {
                output.delete();
            }
            URL url = new URL(i.getData().toString());
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            FileOutputStream fos = new FileOutputStream(output.getPath());
            BufferedOutputStream out = new BufferedOutputStream(fos);
            try {
                InputStream in = c.getInputStream();
                byte[] buffer = new byte[8192];
                int len = 0;
                while ((len = in.read(buffer)) >= 0) {
                    out.write(buffer, 0, len);
                }
                out.flush();
            }
            finally {
                fos.getFD().sync();
                out.close();
                c.disconnect();
            }

            Intent downloadCompleteBroadCast = new Intent(DOWNLOAD_COMPLETE);
            // PASSA QUAL LINK DE DOWNLOAD PARA BUSCA NO DB E BUTTONS
            downloadCompleteBroadCast.putExtra("Downloaded", i.getData().toString());
            // AVISAR QUE FINALIZOU O DOWNLOAD
            LocalBroadcastManager.getInstance(this).sendBroadcast(downloadCompleteBroadCast);
            Log.d("Download", "Download finalizado");



        } catch (IOException e2) {
            Log.e(getClass().getName(), "Exception durante download", e2);
        }
    }
}
```

Segundo Pico:
```java
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
```
## Reproduzir Episodio
Esse teste mostra o uso da CPU ao ser reproduzido um episodio baixado.

### Teste

### Resultados & Conclusão

O teste registrou um pico de 26% no uso da CPU no momento que o usuário clica no play, mas durante a reprodução o uso da CPU foi abaixo dos 2%.
No momento que o usuário solicita a reprodução do episodio o objeto mediaPlayer é inicializado utilizando a uri do episodio e o butão é alterado o texto.
Por sua vez o MediaPlayer passa a responsabilidade de reprodução ao sistema, assim o uso da CPU é provido pelo sistema e não pela aplicação.


### Código

```java
if (holder.button.getText() == "Play") {
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
```