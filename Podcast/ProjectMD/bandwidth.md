# Consumo de Rede

Para esse conjunto de testes foi utilizado o Android Emulator - Nexus 5X_API_26.

![alt text](https://github.com/phrps/exercicio-podcast/blob/master/Podcast/ProjectImg/banda_open_info_download.png)
##### Abrir App: 0-4s
##### Pedir Info: 5-8s
##### Leitura do DB para retorno a tela inicial: 9-13s
##### Download: 14-1m9s


![alt text](https://github.com/phrps/exercicio-podcast/blob/master/Podcast/ProjectImg/banda_downloaded_noti_play.png)
##### Download: 2m15s-2m.24s.
##### Atualização do DB: 2m.25s-2m.26s
##### Reabetura do app: 2m.33-2m.37s.
##### Play: 2m.42s-3m.20s.

## Abrir App
Todo momento que a MainActivity esta sendo inicializada, os episodios estavam sendo baixados novamente, apesar de já estarem salvos no bancos de dados.
Para solucionar isso podemos alterar o código para verificar se o db esta preenchido antes de baixar.

Antigo código:
```java
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
```

Novo código:
```java
     @Override
        protected List<ItemFeed> doInBackground(String... params) {
            List<ItemFeed> itemList = new ArrayList<>();
            try {
                itemList = updateListView();
                if (itemList.isEmpty()) {
                    itemList = XmlFeedParser.parse( getRssFeed( params[0] ) );
                    // Armazena lista de itemFeed no db
                    storesList( itemList );
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
            }
            return itemList;
        }
```
