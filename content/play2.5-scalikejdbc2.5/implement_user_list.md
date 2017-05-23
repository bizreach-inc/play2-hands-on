---
title: ユーザ一覧の実装
layout: play25-scalikejdbc25
---

`USERS`テーブルからIDの昇順に全件取得し、ユーザ一覧画面を表示します。

## ビュー

テンプレートは`views`パッケージに作成します。appディレクトリ配下に`views.user`パッケージを作成し、以下の内容で`list.scala.html`を作成します。

```html
@* このテンプレートの引数 *@
@(users: Seq[models.Accounts])(implicit request: Request[Any])

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
def list = Action { implicit request =>
  val u = Users.syntax("u")

  DB.readOnly { implicit session =>
    // ユーザのリストを取得
    val users = withSQL {
      select.from(Users as u).orderBy(u.id.asc)
    }.map(Users(u.resultName)).list.apply()

    // 一覧画面を表示
    Ok(views.html.user.list(users))
  }
}
```

`val u = Users.syntax("u")`はScalikeJDBCのQueryDSL（SQLをタイプセーフに記述するためのDSL）を使用する際にテーブル毎に必要となるものです。クラス内の様々なメソッドで同じものを使用する場合はクラスのフィールドとして定義するようにしてもよいでしょう。

```scala
val accounts = withSQL {
  select.from(Accounts as a).orderBy(a.id.asc)
}.map(Accounts(a.resultName)).list.apply()
```

このコードの`select.from(Users as u).orderBy(u.id.asc)`という部分は以下のSQLと同じ意味になります。

```sql
SELECT * FROM USERS ORDER BY ID
```

> **POINT**
>
> * Playでは`Action { ... }`の中に処理を記述します
>   * `implicit request`はアクションの処理の中でHTTPリクエストを暗黙的に使用するために必要になる記述です
> * `Ok`に`views.html.・・・`と記述することで、表示したいHTMLのテンプレートを指定できます
>   * 引数にはテンプレートに渡すパラメータを指定します
> * ScalikeJDBCでは`DB.readOnly { ... }`で参照専用のセッションを取得することができます
>  * `withSQL { ... }`でSQLをタイプセーフに記述するQueryDSLを使用することができます

## 実行

ここまで実装したらブラウザから http://localhost:9000/user/list にアクセスします（`sbt run`を実行していない場合は実行してください）。すると以下のような画面が表示されるはずです。

![ユーザ一覧画面](../images/play2.5-scalikejdbc2.5