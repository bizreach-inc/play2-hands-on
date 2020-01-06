---
title: DBの準備
---

## ツールプロジェクトの準備

[h2.zip](../downloads/h2.zip) をダウンロードし、以下のように`play2-hands-on`プロジェクトと同じディレクトリに展開します。

```
+-/play2-hands-on
|   |
|   +-/app
|   |
|   +-/conf
|   |
|   +-...
|
+-/h2
    |
    +-create.sql
    |
    +-data.mv.db
    |
    +-...
```

## H2の起動

**Windowsの場合**

まず、`h2/start.bat`をダブルクリックしてH2データベースを起動します。データベースには以下のスキーマのテーブルが作成済みの状態になっています。

**Macの場合**

```
cd h2/
sh start.sh
```
※起動後、そのターミナルは閉じないでください。

![アプリケーションで使用するER図](../images/play2.8-scalikejdbc3.4/er_diagram.png)

## モデルの自動生成

ScalikeJDBCではタイプセーフなAPIを使用するためにモデルクラスを用意する必要がありますが、ScalikeJDBCがsbtプラグインとして提供しているジェネレータを使用することでDBスキーマから自動生成することができます。

`play2-hands-on`プロジェクトでScalikeJDBCの自動生成ツールを使えるようにします。`project/plugins.sbt`に以下の設定を追加します。

```scala
libraryDependencies += "com.h2database" % "h2" % "1.4.200"
addSbtPlugin("org.scalikejdbc" %% "scalikejdbc-mapper-generator" % "3.4.0")
```

また、`project/scalikejdbc.properties`というファイルを以下の内容で作成します。

```properties
# ---
# jdbc settings

jdbc.driver=org.h2.Driver
jdbc.url=jdbc:h2:tcp://localhost/data
jdbc.username=sa
jdbc.password=sa
jdbc.schema=PUBLIC

# ---
# source code generator settings

generator.packageName=models
# generator.lineBreak: LF/CRLF
generator.lineBreak=LF
# generator.template: interpolation/queryDsl
generator.template=queryDsl
# generator.testTemplate: specs2unit/specs2acceptance/ScalaTestFlatSpec
generator.testTemplate=ScalaTestFlatSpec
generator.encoding=UTF-8
# When you're using Scala 2.11 or higher, you can use case classes for 22+ columns tables
generator.caseClassOnly=true
# Set AutoSession for implicit DBSession parameter's default value
generator.defaultAutoSession=true
# Use autoConstruct macro (default: false)
generator.autoConstruct=false
# joda-time (org.joda.time.DateTime) or JSR-310 (java.time.ZonedDateTime java.time.OffsetDateTime)
generator.dateTimeClass=java.time.OffsetDateTime
```

最後に`buils.sbt`に以下の記述を追加します。これでこのプロジェクトで`scalikejdbcGen`タスクが使用できるようになります。

```scala
enablePlugins(ScalikejdbcPlugin)
```

ではコードを自動生成してみましょう。`play2-hands-on`プロジェクトのルートディレクトリで以下のコマンドを実行します。

```
sbt "scalikejdbcGenAll"
```

すると`play2-hands-on`プロジェクトの`app/models`パッケージにモデルクラスが生成されます。

## DB接続の設定

`play2-hands-on`プロジェクトの`conf/application.conf`に以下の設定を追加します。データベースの接続情報に加え、PlayとScalikeJDBCを連携させるための設定が含まれています。

```properties
# Database configuration
# ~~~~~
# You can declare as many datasources as you want.
# By convention, the default datasource is named `default`
db.default.driver=org.h2.Driver
db.default.url="jdbc:h2:tcp://localhost/data"
db.default.username=sa
db.default.password=sa

# ScalikeJDBC original configuration
#db.default.poolInitialSize=10
#db.default.poolMaxSize=10
#db.default.poolValidationQuery=

scalikejdbc.global.loggingSQLAndTime.enabled=true
scalikejdbc.global.loggingSQLAndTime.singleLineMode=false
scalikejdbc.global.loggingSQLAndTime.logLevel=debug
scalikejdbc.global.loggingSQLAndTime.warningEnabled=true
scalikejdbc.global.loggingSQLAndTime.warningThresholdMillis=5
scalikejdbc.global.loggingSQLAndTime.warningLogLevel=warn

play.modules.enabled += "scalikejdbc.PlayModule"
# scalikejdbc.PlayModule doesn't depend on Play's DBModule
play.modules.disabled += "play.api.db.DBModule"
```
