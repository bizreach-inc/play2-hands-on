# Play 2.5 + ScalikeJDBC 2.5 ハンズオン

## インデックス

* [01.プロジェクトの作成](01_create_project.md)
* [02.IDEの準備](02_preparation_of_ide.md)
* [03.DBの準備](03_preparation_of_db.md)
* [04.ルーティングの定義](04_define_routing.md)
* [05.ユーザ一覧の実装](05_implement_user_list.md)
* [06.ユーザ登録・編集画面の実装](06_implement_user_form.md)
* [07.登録、更新処理の実装](07_implement_update_processing.md)
* [08.削除処理の実装](08_implement_delete_processing.md)
* [09.ScalikeJDBCでの実践的な検索処理](09_scalikejdbc.md)
* [10.JSON APIの準備](10_preparation_for_json.md)
* [11.ユーザ一覧APIの実装](11_implement_list_api.md)
* [12.ユーザ登録・更新APIの実装](12_implement_update_api.md)
* [13.ユーザ削除APIの実装](13_implement_delete_api.md)

## 目的

Play2 + ScalikeJBDC を使ってWebアプリケーションを作成するハンズオンです。

主な目的は以下の通りです。

* Scalaに触れてもらう
* 数時間でとりあえず動くものを作ってみる

そのため、なるべくフレームワークが提供する機能をそのまま使います。

## 構成

使用するフレームワークおよびバージョンは以下の通りです。

* Play 2.5.x
* ScalikeJDBC 2.5.x

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

![作成するアプリケーションの画面遷移図](images/flow.png)

また、後半ではこのアプリケーションと同じCRUD処理を行うJSONベースのWeb APIも作成します。

----
[プロジェクトの作成へ進む＞](01_create_project.md)
