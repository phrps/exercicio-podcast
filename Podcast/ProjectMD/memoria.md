# Consumo de Memória

Para esse conjunto de testes foi utilizado o Android Emulator - Nexus 5X_API_26 e o Android Monitor. 

(TODO: Adicionar imagem do Android Monitor (Memory))

## Abrir App

## Perdir informação do Episodio

## Download do Episodio

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


## Abrir Settings