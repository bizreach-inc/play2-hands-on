---
title: ユーザ一覧の実装
layout: play23-slick21
---

`USERS`テーブルからIDの昇順に全件取得し、ユーザ一覧画面を表示します。

## ビュー

テンプレートは`views`パッケージに作成します。appディレクトリ配下に`views.user`パッケージを作成し、以下の内容で`list.scala.html`を作成します。

```html
@* このテンプレートの引数 *@
@(users: List[models.Tables.UsersRow])

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
        <td>@helper.form(routes.UserController.remove(user.id)){
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

> **POINT**
>
> * テンプレートの1行目にはコントローラから受け取る引数を記述します
> * テンプレートには`@`でScalaのコードを埋め込むことができます
> * テンプレートには`@*...*@`でコメントを記述することができます
> * リンクやフォームのURLは、`@routes.・・・`と記述することでルーティングから生成することができます

## コントローラ

まずインポートに以下を追加します。これで`UserController`から`models`パッケージで定義されたモデルクラスを使用したDBアクセスが可能になります。

```scala
import play.api.db.slick._
import models.Tables._
import profile.simple._
```

`list`メソッドを実装します。

```scala
def list = DBAction { implicit rs =>
  // ユーザをIDの昇順でソートして取得
  val users = Users.sortBy(t => t.id).list

  // テンプレートをレンダリングしてレスポンスを返却
  Ok(views.html.user.list(users))
}
```

上記のコードでは以下の記述でユーザの一覧を取得しています。この部分がSlickを使用したコードになります。

```scala
val users = Users.sortBy(t => t.id).list
```

これは以下のSQLと同じ意味になります。

```sql
SELECT * FROM USERS ORDER BY ID
```

> **POINT**
>
> * Playの標準では`Action { ... }`の中に処理を記述しますが、DBを使用する場合は`DBAction { ... }`に処理を記述します
>   * `implicit rs`はアクションの処理の中でHTTPリクエストやDBのセッションを暗黙的に使用するために必要になる記述です
> * `Ok`に`views.html.・・・`と記述することで、表示したいHTMLのテンプレートを指定できます
>   * 引数にはテンプレートに渡すパラメータを指定します

## 実行

ここまで実装したらブラウザから http://localhost:9000/user/list にアクセスします（`sbt run`を実行していない場合は実行してください）。すると以下のような画面が表示されるはずです。

![ユーザ一覧画面](../images/play2.3-slick2.1/user_list.png)
