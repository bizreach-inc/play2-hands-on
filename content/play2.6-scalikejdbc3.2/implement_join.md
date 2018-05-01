---
title: ジョインの必要な処理
---

ここまではscalikejdbcGenで自動生成されたメソッドやシンプルなQueryDSLを使用してきましたが、テーブルのジョインが必要なケースについても実装してみましょう。

ユーザの一覧画面に以下のように会社名を表示するようにしてみます。

![ユーザ一覧画面に会社名を表示](../images/play2.6-scalikejdbc3.2/user_list_companies.png)

## コントローラの修正

まずは`UserController`の`list`メソッドを以下のように修正します。

```scala
def list = Action { implicit request =>
  DB.readOnly { implicit session =>
    // ユーザのリストを取得
    val users = withSQL {
      select.from(Users as u).leftJoin(Companies as c).on(u.companyId, c.id).orderBy(u.id.asc)
    }.map { rs =>
      (Users(u)(rs), rs.intOpt(c.resultName.id).map(_ => Companies(c)(rs)))
    }.list.apply()

    // 一覧画面を表示
    Ok(views.html.user.list(users))
  }
}
```

外部結合したテーブルの値を取得する場合、`map()`メソッドで`Option`型に変換する必要があるという点に注意してください。以下のコードは、まず結果セットから`COMPANIES`テーブルの`ID`カラムを`intOpt`メソッドで`Option[Int]`型として取得し、値が取得できた場合のみ`Companies`クラスにマッピングするという処理を行っています。

```scala
rs.intOpt(c.resultName.id).map(_ => Companies(c)(rs))
```

なお、内部結合の場合は`leftJoin`の代わりに`innerJoin`メソッドを使用します。この場合、`map()`メソッドで`Option`型に変換する必要はありません。

## ビューの修正

続いて`list.scala.html`を以下のように修正します。検索結果の型が`List[Users]`から`List[(Users, Option[Companies])]`に変わり、表に「会社名」という列を追加しています。

```html
@(users: Seq[(models.Users, Option[models.Companies])])(implicit request: RequestHeader)
@import helper._
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
        <th>会社名</th>
        <th>&nbsp;</th>
      </tr>
    </thead>
    <tbody>
    @users.map { case (user, company) =>
      <tr>
        <td>@user.id</td>
        <td><a href="@routes.UserController.edit(Some(user.id))">@user.name</a></td>
        <td>@company.map(_.name)</td>
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

ユーザ一覧画面にアクセスすると冒頭のキャプチャのような画面が表示されるはずです。

## SQLを直接記述する

ここではタイプセーフなDSLを使ったジョインを使用しましたが、集計処理といった少し複雑なSQLの場合は直接記述したいことがあります。

`sql` interpolationを使うと文字列リテラルで生SQLを記述することができます。ただし、SQLを完全に記述するだけでなく、自動生成されたクラスを使って記述を補助することができます。

```scala
val users: Seq[(Users, Companies)] = sql"""
  |SELECT ${u.result.*}, ${c.result.*}
  |FROM ${Users.as(u)} INNER JOIN ${Companies.as(c)}
  |ON ${u.companyId} = ${c.id}
""".stripMargin.map { rs =>
  (Users(u)(rs), Companies(c)(rs))
}.list.apply()
```

SELECT句に大量のカラムを記述する必要がなかったり、テーブル名やカラム名のタイプミスを防ぐことができます。また、`sql` interpolationを使う場合は`withSQL { ... }`で囲む必要はありません。`map()`メソッド以降はQueryDSLの場合と同じです。

## 参考資料

ScalikeJDBCにおける検索処理の詳細については以下のドキュメントが参考になります。

- http://scalikejdbc.org/documentation/query-dsl.html
- http://scalikejdbc.org/documentation/sql-interpolation.html
