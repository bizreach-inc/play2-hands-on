---
title: ルーティングの定義
layout: play24-slick30
---

## Bootstrapを使うための準備

`play new`コマンドで作成されたプロジェクトにはデフォルトのレイアウトテンプレートとして`app/views/main.scala.html`が生成されています。ここにBootstrapで使用するCSSとJavaScriptを追加します。

```html
@(title: String)(content: Html)

<!DOCTYPE html>

<html>
  <head>
    <title>@title</title>
    <link rel="stylesheet" media="screen" href="@routes.Assets.versioned("stylesheets/main.css")">
    <link rel="shortcut icon" type="image/png" href="@routes.Assets.versioned("images/favicon.png")">
    <script src="@routes.Assets.versioned("javascripts/hello.js")" type="text/javascript"></script>
    @* ↓↓↓↓ここから追加↓↓↓↓ *@
    <link rel="stylesheet" media="screen" href="//netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap-theme.min.css">
    <link rel="stylesheet" media="screen" href="//netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css">
    <script src="//netdna.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js" type="text/javascript"></script>
    @* ↑↑↑↑ここまで追加↑↑↑↑ *@
  </head>
  <body>
    @content
  </body>
</html>
```

## コントローラの雛形を作る

`controllers`パッケージに`UserController`クラスを以下のように作成します。

```scala
package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.i18n.{MessagesApi, I18nSupport}
import play.api.db.slick._
import slick.driver.JdbcProfile
import models.Tables._
import javax.inject.Inject
import scala.concurrent.Future
import slick.driver.H2Driver.api._

class UserController @Inject()(val dbConfigProvider: DatabaseConfigProvider,
                               val messagesApi: MessagesApi) extends Controller
    with HasDatabaseConfigProvider[JdbcProfile] with I18nSupport {

  /**
   * 一覧表示
   */
  def list = TODO

  /**
   * 編集画面表示
   */
  def edit(id: Option[Long]) = TODO

  /**
   * 登録実行
   */
  def create = TODO

  /**
   * 更新実行
   */
  def update = TODO

  /**
   * 削除実行
   */
  def remove(id: Long) = TODO

}
```

Play 2.3まではコントローラはオブジェクトとして実装する必要がありましたが、Play 2.4ではクラスとして実装します。コンストラクタに`@Inject`アノテーションと2つの引数が定義されていますが、これはPlay 2.4から導入されたDI機能を使用するためのものです。

上記のコントローラではDI機能を以下のような目的で使用しています。

- `DatabaseConfigProvider` ... コントローラ内でデータベースアクセスを行うため
- `MessagesApi` ... Playの国際化機能を使用するため（本ハンズオンで作成するアプリケーションでは国際化機能は使用しませんが、後述するテンプレート内で使用するヘルパーが`MessagesApi`のインスタンスを必要とするため）

また、実際にコントローラ内でデータベースアクセスや国際化機能を利用するためにはDIで上記のインスタンスを取得するだけでなく、それぞれ`HasDatabaseConfigProvider`トレイト、`I18nSupport`トレイトをミックスインする必要があります。

> **POINT**
>
> * Play 2.4ではコントローラはクラスとして実装します
> * `@Inject`はDIのためのアノテーションです
> * データベースアクセスを行うにはコントローラに`DatabaseConfigProvider`をDIし、`HasDatabaseConfigProvider`トレイトをミックスインします
> * 国際化機能を使用するにはコントローラに`MessagesApi`をDIし、`I18nSupport`トレイトをミックスインします
> * `TODO`メソッドは`Action not implemented yet.`という`501 NOT_IMPLEMENTED`レスポンスを返します

## ルーティングの設定

クライアントから送信されたリクエストは、`conf/routes`の設定に従ってコントローラのメソッドへルーティングされます。
以下の設定を追記します。

```bash
# Mapping to /user/list
GET     /user/list                  controllers.UserController.list
# Mapping to /user/edit or /user/edit?id=<number>
GET     /user/edit                  controllers.UserController.edit(id: Option[Long] ?= None)
# Mapping to /user/create
POST    /user/create                controllers.UserController.create
# Mapping to /user/update
POST    /user/update                controllers.UserController.update
# Mapping to /user/remove/<number>
POST    /user/remove/:id            controllers.UserController.remove(id: Long)
```

> **POINT**
>
> * マッピング定義で引数の型を省略すると、`String`になります
> * routesのコメントに日本語を記述するとコンパイルエラーになることがあります
