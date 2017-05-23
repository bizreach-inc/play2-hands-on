---
title: JSON APIの準備
layout: play25-scalikejdbc25
---

フロントエンドがAngularJSやスマートフォンアプリの場合、サーバサイドはJSONを返却するAPIを提供する必要があります。ここまでに作成してきたユーザ情報のCRUD処理について、Play2のJSONサポート機能を使ってJSONベースのWeb APIを実装します。

## コントローラの雛形を作る

`controllers`パッケージに`JsonController`オブジェクトを以下の内容で作成します。SlickやPlay2のJSONサポートを使用するためのimport文を予め含めています。

```scala
package controllers

import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import scalikejdbc._
import models._

class JsonController extends Controller {

  /**
   * 一覧表示
   */
  def list = TODO

  /**
   * ユーザ登録
   */
  def create = TODO

  /**
   * ユーザ更新
   */
  def update = TODO

  /**
   * ユーザ削除
   */
  def remove(id: Long) = TODO
}
```

`UserController`と比べると、テンプレートでの国際化機能のために必要だった`MessagesApi`のDIや`I18nSupport`トレイトのミックスインを行っていないため、非常にシンプルなクラス定義になっています。

> **POINT**
>
> * `play.api.libs.json._`はPlay2のJSONサポート機能を使用するために必要なimport文です

## ルーティングの定義

`conf/routes`に以下の内容を追記します。

```
# JSON API
GET         /json/list              controllers.JsonController.list
POST        /json/create            controllers.JsonController.create
POST        /json/update            controllers.JsonController.update
POST        /json/remove/:id        controllers.JsonController.remove(id: Long)
```
