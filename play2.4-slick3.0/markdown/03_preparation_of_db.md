# 03.DBの準備

## ツールプロジェクトの準備

[slick-codegen-play2.4-slick3.0.zip](https://github.com/bizreach/play2-hands-on/raw/master/downloads/slick-codegen-play2.4-slick3.0.zip) をダウンロードし、以下のように`play2-hands-on`プロジェクトと同じディレクトリに展開します。

```
+-/play2-hands-on
|   |
|   +-/app
|   |
|   +-/conf
|   |
|   +-...
|
+-/slick-codegen
    |
    +-/project
    |
    +-/src
    |
    +-...
```

## H2の起動

- [Windows]

まず、`slick-codegen`プロジェクトの`h2/start.bat`をダブルクリックしてH2データベースを起動します。データベースには以下のスキーマのテーブルが作成済みの状態になっています。

- [Mac]
```
cd slick-codegen/h2/
sh start.sh
```
※起動後、そのターミナルは閉じないでください。

![アプリケーションで使用するER図](images/er_diagram.png)

## モデルの自動生成

SlickではタイプセーフなAPIを使用するために

* タイプセーフなクエリで使うテーブル定義
* エンティティオブジェクト

を用意する必要がありますが、これらはSlickが標準で提供しているジェネレータを使用することでDBスキーマから自動生成することができます。

`slick-codegen`プロジェクトのルートディレクトリで以下のコマンドを実行します。

- [Windows]
```
sbt gen-tables
```

- [Mac]
```
./sbt.sh gen-tables
```

すると`play2-hands-on`プロジェクトの`app/models`パッケージにモデルクラスが生成されます。

![生成されたモデル](images/gen_model.png)

## DB接続の設定

`play2-hands-on`プロジェクトの`conf/application.conf`にDB接続のため以下の設定を追加します。

```properties
slick.dbs.default.driver="slick.driver.H2Driver$"
slick.dbs.default.db.driver=org.h2.Driver
slick.dbs.default.db.url="jdbc:h2:tcp://localhost/data"
slick.dbs.default.db.user=sa
slick.dbs.default.db.password=sa
```

----
[＜IDEの準備に戻る](02_preparation_of_ide.md) | [ルーティングの定義に進む＞](04_define_routing.md)
