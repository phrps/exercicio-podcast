# Testes

Para esse conjunto de testes foi utilizado o Android Emulator - Nexus 5X_API_26.

## JUnit

Para realização dos testes JUnit foi utizada a API de instrumentação, para assim ter acesso as informações do Context do aplicativo teste.
Foram executadas duas baterias de testes, a primeira foi referente a funções que utilizam o database e são executadas no app, sendo essas funções como:
Acesso ao Database, Inserir no Database e Atualizar a Uri do Database. A segunda baterias de testes foi referente ao DownloadIntent, com verificação de permissão e verificando
se a criação da intent se dava correta utilizando de um URL valida. Ambos os projetos de testes se encontram em androidTest, sendo o primeiro DBTest.java e DownloadTest.java.

## Espresso

Os testes com Espresso também foram feitos utilizando a API de instrumentação.
O código dos testes foi feito com a funcionalidade 'Record Espresso Test' do Android Studio.
O propósito desses testes é avaliar a UI e portanto foram verificados os possíveis clicks do applicativo, como visualizar detalhes de um podcast e ir para a tela de settings.
Não foi possível testar os clicks nos botões de download, pois o Espresso acusava ambiguidade de itens e não se conseguiu resolver esse problema (o código ficou comentado).