---
title: ルーティングの定義
layout: play2.3-slick2.1
---

## Bootstrapを使うための準備

`play new`コマンドで作成されたプロジェクトにはデフォルトのレイアウトテンプレートとして`app/views/main.scala.html`が生成されています。ここにBootstrapで使用するCSSとJavaScriptを追加します。

```html
@(title: String)(content: Html)

<!DOCTYPE html>

<html>
  <head>
    <title>@title</title>
    <link rel="stylesheet" media="screen" href="@routes.Assets.at("stylesheets/main.css")">
    <link rel="shortcut icon" type="image/png" href="@routes.Assets.at("images/favicon.png")">
    <script src="@routes.Assets.at("javascripts/hello.js")" type="text/javascript"></script>
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

`controllers`パッケージに`UserController`オブジェクトを以下のように作成します。

```scala
package controllers

import play.api.mvc._

object UserController extends Controller {

  /**
   * 一覧表示
   */
  def list = TODO

  /**
   * 登録・編集画面表示
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

> **POINT**
>
> * `object`はシングルトンなオブジェクトを定義するときに使います
> * メソッドは`def`キーワードで定義します
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
