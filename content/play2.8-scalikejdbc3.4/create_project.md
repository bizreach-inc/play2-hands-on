---
title: プロジェクトの作成
---

## sbtのインストール

まずはsbtをインストールします。sbtはScalaの標準的なビルドツールです。

**Windowsの場合**

以下のリンクから最新のmsiファイルをダウンロードしてインストールします。

https://github.com/sbt/sbt/releases

**Macの場合**

Homebrewでインストールします。

```
brew update
brew install sbt
```

どちらの場合もインストール後以下のようにしてsbtコマンドが使えること、sbtのバージョンが1.3以降であることを確認してください。

```
sbt sbtVersion
[info] 1.3.4
```

## 新規プロジェクト作成

コマンドプロンプトで以下のコマンドを実行します。

```
sbt new playframework/play-scala-seed.g8 --branch 2.8.x
```

プロジェクト名などを聞かれますが、ここではプロジェクト名を`play2-hands-on`とし、他の項目は初期値のままプロジェクトを作成するものとします。

![プロジェクトの作成](../images/play2.8-scalikejdbc3.4/create_project.png)

`play2-hands-on`ディレクトリの`build.sbt`にORMとしてScalikeJDBCを使用するための設定を行います。

```scala
name := """play2-hands-on"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.1"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test

// ↓↓↓↓ここから追加↓↓↓↓
libraryDependencies ++= Seq(
  "com.h2database" % "h2" % "1.4.200",
  "org.scalikejdbc" %% "scalikejdbc" % "3.4.0",
  "org.scalikejdbc" %% "scalikejdbc-config" % "3.4.0",
  "org.scalikejdbc" %% "scalikejdbc-play-initializer" % "2.8.0-scalikejdbc-3.4"
)
// ↑↑↑↑ここまで追加↑↑↑↑

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
```

## 起動確認

作成した`play2-hands-on`ディレクトリに移動し、以下のコマンドでプロジェクトを実行します。

```
sbt run
```

ブラウザから http://localhost:9000/ にアクセスし、以下の画面が表示されることを確認します。

![Play2のウェルカム画面](../images/play2.8-scalikejdbc3.4/welcome.png)

`Welcome to Play!` が書かれていることを確認して下さい。

> **POINT**
>
> * `sbt run`で実行している間はホットデプロイが有効になっているため、ソースを修正するとすぐに変更が反映されます
> * CTRL+Dで`sbt run`での実行を終了することができます
> * `sbt  run`で実行中に何度も修正を行っているとヒープが不足してプロセスが終了してしまったりエラーが出たまま応答がなくなってしまう場合があります
> * プロセスが終了してしまった場合は再度`sbt run`を実行してください
> * 応答しなくなってしまった場合は一度コマンドプロンプトを閉じ、再度起動して`sbt run`を実行してください
