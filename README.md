# Play2 + Slickハンズオン

## インデックス

* [01.プロジェクトの作成](https://github.com/bizreach/play2-hands-on/wiki/01.%E3%83%97%E3%83%AD%E3%82%B8%E3%82%A7%E3%82%AF%E3%83%88%E3%81%AE%E4%BD%9C%E6%88%90)
* [02.IDEの準備](https://github.com/bizreach/play2-hands-on/wiki/02.IDE%E3%81%AE%E6%BA%96%E5%82%99)
* [03.DBの準備](https://github.com/bizreach/play2-hands-on/wiki/03.DB%E3%81%AE%E6%BA%96%E5%82%99)
* [04.ルーティングの定義](https://github.com/bizreach/play2-hands-on/wiki/04.%E3%83%AB%E3%83%BC%E3%83%86%E3%82%A3%E3%83%B3%E3%82%B0%E3%81%AE%E5%AE%9A%E7%BE%A9)
* [05.ユーザ一覧の実装](https://github.com/bizreach/play2-hands-on/wiki/05.%E3%83%A6%E3%83%BC%E3%82%B6%E4%B8%80%E8%A6%A7%E3%81%AE%E5%AE%9F%E8%A3%85)
* [06.ユーザ登録・編集画面の実装](https://github.com/bizreach/play2-hands-on/wiki/06.%E3%83%A6%E3%83%BC%E3%82%B6%E7%99%BB%E9%8C%B2%E3%83%BB%E7%B7%A8%E9%9B%86%E7%94%BB%E9%9D%A2%E3%81%AE%E5%AE%9F%E8%A3%85)
* [07.登録、更新処理の実装](https://github.com/bizreach/play2-hands-on/wiki/07.%E7%99%BB%E9%8C%B2%E3%80%81%E6%9B%B4%E6%96%B0%E5%87%A6%E7%90%86%E3%81%AE%E5%AE%9F%E8%A3%85)
* [08.削除処理の実装](https://github.com/bizreach/play2-hands-on/wiki/08.%E5%89%8A%E9%99%A4%E5%87%A6%E7%90%86%E3%81%AE%E5%AE%9F%E8%A3%85)
* [09.Tips](https://github.com/bizreach/play2-hands-on/wiki/09.Tips)

## 目的

Play2 + Slickを使ってWebアプリケーションを作成するハンズオンです。

主な目的は以下の通りです。

* Scalaに触れてもらう
* 数時間でとりあえず動くものを作ってみる

そのため、なるべくフレームワークが提供する機能をそのまま使います。

## 構成

使用するフレームワークおよびバージョンは以下の通りです。

* Play 2.3.x
* Slick 2.0.x

ただし、特にSlickに関してはアグレッシブに変更が行われる傾向があるため、バージョンが上がると使い方が変わる可能性があります。随時、最新の内容に更新していきます。

## 前提条件

このハンズオンを実施するにあたっての前提条件は以下の通りです。

* JavaおよびWebアプリケーションの開発に関する基本的な知識を持っていること
* JDK 1.7以降がインストールされていること
* EclipseもしくはIntelliJ IDEAの最新版がインストールされていること

## 内容

ユーザ情報のCRUDを行う簡単なアプリケーションを作成します。

* ユーザ一覧を表示する
* 新規ユーザ登録を行う
* ユーザ情報を編集する
* ユーザを削除する

![作成するアプリケーションの画面遷移](https://github.com/bizreach/play2-hands-on/wiki/images/flow.png)
