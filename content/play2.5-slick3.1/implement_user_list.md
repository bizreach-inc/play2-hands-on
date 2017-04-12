---
title: ユーザ一覧の実装
layout: play2.5-slick3.1
---

`USERS`テーブルからIDの昇順に全件取得し、ユーザ一覧画面を表示します。

## ビュー

テンプレートは`views`パッケージに作成します。appディレクトリ配下に`views.user`パッケージを作成し、以下の内容で`list.scala.html`を作成します。

```html
@* このテンプレートの引数 *@
@(users: Seq[models.Tables.UsersRow])(implicit request: Request[Any])

@* テンプレートで利用可能なヘルパーをインポート *@
@import helper._

@* main.scala.htmlを呼び出す *@
@main("ユーザ一覧") {

<div>
  <a href="@routes.UserController.edit()" class="btn btn-success" role="button">新規作成</a>
</div>

<div class="col-xs-6">
  <table class="table table-hover">
    <thead>
      <tr>
        <th>ID</th>
        <th>名前</th>
        <th>&nbsp;</th>
      </tr>
    </thead>
    <tbody>
    @* ユーザの一覧をループで出力 *@
    @users.map { user =>
      <tr>
        <td>@user.id</td>
        <td><a href="@routes.UserController.edit(Some(user.id))">@user.name</a></td>
        <td>@helper.form(CSRF(routes.UserController.remove(user.id))){
          <input type="submit" value="削除" class="btn btn-danger btn-xs"/>
        }
        </td>
      </tr>
    }
    </tbody>
  </table>
</div>

}
```

テンプレート一行目に`(implicit request: Request[Any])`という引数が定義されていますが、これはテンプレート中で使用している`CSRF`というヘルパーを使用するために必要なものです。PlayではデフォルトでCSRF対策のためのフィルタが有効になっており、フォームの送信先を指定する際に`@helper.form(CSRF(...))`のように指定するだけで自動的にトークンを使用したCSRF機能を利用することができます（`CSRF`ヘルパーを指定しないとフォームの送信時に403エラーになってしまいます）。

> **POINT**
>
> * テンプレートの1行目にはコントローラから受け取る引数を記述します
> * テンプレートには`@`でScalaのコードを埋め込むことができます
> * `@import`でインポート文を記述することができます。`@import helper._`でPlayが提供する標準ヘルパー（フォームなどを出力する関数）をインポートしています
> * テンプレートには`@*...*@`でコメントを記述することができます
> * リンクやフォームのURLは、`@routes.・・・`と記述することでルーティングから生成することができます
> * デフォルトでCSRFフィルタが有効になっているため、フォームの送信先は`CSRF(...)`で囲む必要があります

## コントローラ

`UserController`の`list`メソッドを以下のように実装します。

```scala
def list = Action.async { implicit rs =>
  // IDの昇順にすべてのユーザ情報を取得
  db.run(Users.sortBy(t => t.id).result).map { users =>
    // 一覧画面を表示
    Ok(views.html.user.list(users))
  }
}
```

`Action.async`はアクションの処理結果を`Future`で非同期に返却します。Slick 3.0ではSQLの実行処理を`Future`で返すことができるため、これを利用してPlayのアクションも`Future`でレスポンスを返すようにしています。

上記のコードでは以下の記述でユーザの一覧を取得する`DBIOAction`を生成しています。

```scala
Users.sortBy(t => t.id).result
```

これは以下のSQLと同じ意味になります。

```sql
SELECT * FROM USERS ORDER BY ID
```

`db.run`で生成した`DBIOAction`を実行してデータベースから結果を取得する`Future`が返ります。この`Future`に対して`map`メソッドで、データベースから取得した値をテンプレートに渡してレンダリング結果を返す`Future`に変換しています。最終的にこのアクションの戻り値は「DBの検索結果をテンプレートに渡し、そのテンプレートのレンダリング結果を返す`Future`」になります。

> **POINT**
>
> * Playの標準では`Action { ... }`の中に処理を記述しますが、レスポンスをFutureで返す場合は`Action.async { ... }`に処理を記述します
>   * `implicit rs`はアクションの処理の中でHTTPリクエストやDBのセッションを暗黙的に使用するために必要になる記述です
> * `Ok`に`views.html.・・・`と記述することで、表示したいHTMLのテンプレートを指定できます
>   * 引数にはテンプレートに渡すパラメータを指定します
> * Slick 3のクエリは`DBIOAction`を生成します。`DBIOAction`を`db.run`で実行すると検索結果を返す`Future`を取得できます

## 実行

ここまで実装したらブラウザから http://localhost:9000/user/list にアクセスします（`activator run`を実行していない場合は実行してください）。すると以下のような画面が表示されるはずです。

![ユーザ一覧画面](../images/play2.5-slick3.1/user_list.png)
