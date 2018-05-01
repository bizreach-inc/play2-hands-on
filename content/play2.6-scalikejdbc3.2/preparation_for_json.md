---
title: JSON APIの準備
---

フロントエンドがAngularJSやスマートフォンアプリの場合、サーバサイドはJSONを返却するAPIを提供する必要があります。ここまでに作成してきたユーザ情報のCRUD処理について、Play2のJSONサポート機能を使ってJSONベースのWeb APIを実装します。

## コントローラの雛形を作る

`controllers`パッケージに`JsonController`クラスを以下の内容で作成します。ScalikeJDBCやPlay2のJSONサポートを使用するためのimport文を予め含めています。

```scala
package controllers

import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import javax.inject.Inject
import scalikejdbc._
import models._

class JsonController @Inject()(components: ControllerComponents)
  extends AbstractController(components) {

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

`UserController`とは異なり、テンプレートでの国際化機能を使わないため`ControllerComponents`をDIし、`AbstractController`クラスを継承します。

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
