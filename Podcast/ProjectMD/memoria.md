# Consumo de Memória

Para esse conjunto de testes foi utilizado o Android Emulator - Nexus 5X_API_26 e o Android Monitor. 

![alt text](https://github.com/phrps/exercicio-podcast/blob/master/Podcast/ProjectImg/memoria_open_info_download.png)
##### Abrir App: 0-4s
##### Pedir Info: 5-8s
##### Leitura do DB para retorno a tela inicial: 9-13s
##### Download: 14-1m9s


![alt text](https://github.com/phrps/exercicio-podcast/blob/master/Podcast/ProjectImg/memoria_downloaded_noti_play.png)
##### Download: 2m15s-2m.24s.
##### Atualização do DB: 2m.25s-2m.26s
##### Reabetura do app: 2m.33-2m.37s.
##### Play: 2m.42s-3m.20s.

## Perdir informação do Episodio
Esse teste mostra o consumo de Memoria ao abrir tela de informações de um episodio.

### Teste

### Resultados && Conclusões
O teste registrou dois aumentos no uso da memória, o primeiro relacionado ao acessar as informações do episodio (EpisodioDetailActivity), mas sendo apenas um pequeno aumento não significativo, pórem
, o segundo aumento do uso da memória foi relevante, este esta relacionado a volta a MainActivity. Este aumento é justicado pelo carregamento da MainActivity e destruição do EpisodioDetailActivity.

## Reproduzir Episodio
Esse teste mostra o consumo de Memoria ao reproduzir um episodio.

### Teste

### Resultados & Conclusões

O teste registrou que a memória usada durante todo o processo de reprodução de musica ficou constante consumindo
2,59 MB, mas após o pause na musica a memória não foi liberada.
O uso de memória excessivo vem do fato de quando o usuário solicita a reprodução do episodio, é criado um objeto MediaPlayer passando a uri
do episodio desejado. Após o fim do episodio ou pause do episodio, o mesmo, não é liberado, assim gastando memória de forma excessiva. Uma solução para esse problema
seria o liberamento da memória sempre que pausado ou finalizado, quando pausado armazenando o tempo no database.

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

## LeakCanary

Não foi encontrado nenhum leak usando o LeakCanary, porém acredita-se que possa ser um erro no setup deste.