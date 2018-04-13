---
title: ルーティングの定義
---

## Bootstrapを使うための準備

`sbt new`コマンドで作成されたプロジェクトにはデフォルトのレイアウトテンプレートとして`app/views/main.scala.html`が生成されています。ここにBootstrapで使用するCSSとJavaScriptを追加します。

```html
@(title: String)(content: Html)

<!DOCTYPE html>
<html lang="en">
  <head>
    <title>@title</title>
    <link rel="stylesheet" media="screen" href="@routes.Assets.versioned("stylesheets/main.css")">
    <link rel="shortcut icon" type="image/png" href="@routes.Assets.versioned("images/favicon.png")">
    @* ↓↓↓↓ここから追加↓↓↓↓ *@
    <link rel="stylesheet" media="screen" href="//netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap-theme.min.css">
    <link rel="stylesheet" media="screen" href="//netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css">
    <script src="//netdna.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js" type="text/javascript"></script>
    @* ↑↑↑↑ここまで追加↑↑↑↑ *@
  </head>
  <body>
    @content
    <script src="@routes.Assets.versioned("javascripts/main.js")" type="text/javascript"></script>
  </body>
</html>
```

また、デフォルトでは`Content-Security-Policy`ヘッダが`default-src 'self'`を返すため上記で指定した外部CDNのCSSファイルやJavaScriptファイルを読み込むことができません。そこで`conf/application.conf`に以下の設定を追加して`Content-Security-Policy`ヘッダが出力されないようにしておきます。

```
play.filters.headers.contentSecurityPolicy=null
```

## コントローラの雛形を作る

`controllers`パッケージに`UserController`クラスを以下のように作成します。

```scala
package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import javax.inject.Inject
import scalikejdbc._
import models._

class UserController @Inject()(components: MessagesControllerComponents)
  extends MessagesAbstractController(components) {

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

コンストラクタに`@Inject`アノテーションと2つの引数が定義されていますが、これはPlay 2.4から導入されたGoogle GuiceによるDI機能を使用するためのものです。

上記のコントローラではDI機能を以下の目的で使用しています。

- `MessagesControllerComponents` ... Playの国際化機能を使用するため（本ハンズオンで作成するアプリケーションでは国際化機能は使用しませんが、後述するテンプレート内で使用するヘルパーがi18n関連のインスタンスを必要とするため）

また、実際にコントローラ内でデータベースアクセスや国際化機能を利用するために、`MessagesAbstractController`クラスを継承します。

> **POINT**
>
> * `@Inject`はDIのためのアノテーションです
> * 国際化機能を使用するにはコントローラに`MessagesControllerComponents`をDIし、`MessagesAbstractController`クラスを継承します
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
