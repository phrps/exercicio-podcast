#Consumo de CPU

Para esse conjunto de testes foi utilizado o Android Emulator - Nexus 5X_API_26.

![Consumo CPU [Inicialização APP | Informação do Episodio | Download](https://github.com/phrps/exercicio-podcast/tree/master/Podcast/ProjectImg/open_info_download.png) 

##Abrir APP
Esse teste mostra o consumo da CPU ao abrir o app.

### Teste
Para realização desse teste foi aberto o simulador e executado o app pela primeira vez, então usando o Android Monitor para medir
o uso de CPU para esse caso.

### Resultados
O app registrou dois picos de consumo um de 36% e outro de 55%, o primeiro esta relacionado ao download da lista de ItemFeed
após o download o mesmo é armazenado no database, o segundo pico esta relacionado a o acesso ao database para recuperar a lista de ItemFeed


###Código

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

##Perdir informação do Episodio

### Teste

### Resultados

##Download do Episodio

##Reproduzir Episodio

##Scroll