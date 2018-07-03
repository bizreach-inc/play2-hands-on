---
title: ユーザ登録・編集画面の実装
---

リクエストパラメータにIDが指定されているかどうかに応じて以下の処理を行います。

* リクエストパラメータにIDなし ⇒ 新規登録画面を表示します。
* リクエストパラメータにIDあり ⇒ `USERS`テーブルを検索し、該当のユーザ情報を初期表示した編集画面を表示します。

## フォーム

画面からの入力値を受け取るための`Form`を定義します。`Form`は必ずしもコントローラに定義する必要はないのですが、コントローラでの処理に強く依存するため特に理由がない限りコントローラクラスのコンパニオンオブジェクトに定義するとよいでしょう。

ここでは`UserController`と同じソースファイルに以下のようなコンパニオンオブジェクトを追加します。

```scala
object UserController {
  // フォームの値を格納するケースクラス
  case class UserForm(id: Option[Long], name: String, companyId: Option[Int])

  // formから送信されたデータ ⇔ ケースクラスの変換を行う
  val userForm = Form(
    mapping(
      "id"        -> optional(longNumber),
      "name"      -> nonEmptyText(maxLength = 20),
      "companyId" -> optional(number)
    )(UserForm.apply)(UserForm.unapply)
  )
}
```

コンパニオンオブジェクトとは、クラスやトレイトと同じファイル内に同じ名前で定義されたオブジェクトのことで、コンパニオンオブジェクトと対応するクラスやトレイトは互いにprivateなメンバーにアクセスできるなどの特徴があります。クラスやトレイトで使用する共通的なメソッドやクラス等を括り出したりするのに使います。

> **POINT**
>
> * コンパニオンオブジェクトとは、クラスと同じファイル内に同じ名前で定義されたオブジェクトのことです
> * `Form`はStrutsのアクションフォームのようなものです
> * マッピングに従ってバリデーション（後述）が行われます

## ビュー

続いて`views.user`パッケージに`edit.scala.html`を実装します。引数には`Form`のインスタンスと、プルダウンで選択する会社情報を格納した`Seq`を受け取ります。

```html
@(userForm: Form[controllers.UserController.UserForm], companies: Seq[models.Companies])(implicit request: MessagesRequestHeader)

@import helper._

@main("ユーザ作成") {

  @* IDがある場合は更新処理、ない場合は登録処理を呼ぶ *@
  @form(CSRF(userForm("id").value.map(x => routes.UserController.update).getOrElse(routes.UserController.create)), 'class -> "container", 'role -> "form") {
    <fieldset>
      <div class="form-group">
        @inputText(userForm("name"), '_label -> "名前")
      </div>
      <div class="form-group">
        @select(userForm("companyId"), companies.map(x => x.id.toString -> x.name).toSeq, '_label -> "会社", '_default -> "-- 会社名を選択してください --")
      </div>
      @* IDがある場合（更新の場合）のみhiddenを出力する *@
      @userForm("id").value.map { value =>
        <input type="hidden" name="id" value="@value" />
      }
      <div>
        <input type="submit" value="保存" class="btn btn-success">
      </div>
    </fieldset>
  }

}
```

このテンプレートでは暗黙的な引数として`MessagesRequestHeader`を受け取るようになっています。これはテンプレートでテキストフィールドを表示するために使用している`inputText`ヘルパーがメッセージ等の国際化のために必要とするもので、コントローラに`MessagesControllerComponents`をDIしておくと暗黙的に渡されます。このハンズオンでは特に使用しませんが、Play2アプリケーションの国際化対応に必要になるものですので覚えておくとよいでしょう。

## コントローラ

最後に`UserController`の`edit`メソッドを実装します。引数`id`が指定されていた場合は空の`Form`、指定されていた場合は`Form#fill`メソッドで`Form`に初期表示する値をセットしたうえでテンプレートを呼び出すようにします。

```scala
// コンパニオンオブジェクトに定義したFormを参照するためにimport文を追加
import UserController._

private val c = Companies.syntax("c")

def edit(id: Option[Long]) = Action { implicit request =>
  DB.readOnly { implicit session =>
    // リクエストパラメータにIDが存在する場合
    val form = id match {
      // IDが渡されなかった場合は新規登録フォーム
      case None => userForm
      // IDからユーザ情報を1件取得してフォームに詰める
      case Some(id) => {
        val user = Users.find(id).get
        userForm.fill(UserForm(Some(user.id), user.name, user.companyId))
      }
    }

    // プルダウンに表示する会社のリストを取得
    val companies = withSQL {
      select.from(Companies as c).orderBy(c.id.asc)
    }.map(Companies(c.resultName)).list().apply()

    Ok(views.html.user.edit(form, companies))
  }
}
```

上記のコードではパラメータ`id`が指定されていなかった場合（`None`の場合）は新規登録用の空フォーム、指定されていた場合（`Some(id)`の場合）は更新用フォームを生成しています。このとき、`Users.find(id)`で更新用フォームに設定するためのユーザ情報をDBから取得しています。このメソッドはscalikejdbcGenで自動生成された検索用メソッドです。このように基本的なCRUD処理はQueryDSLを使わなくても自動生成されたメソッドで実装することができます。

ちなみにこのメソッドをQueryDSLで書き直すと以下のようになります。

```scala
// 1件検索をQueryDSLで書き直した場合
withSQL {
  select.from(Users as u).where.eq(u.id, id)
}.map(Users(u.resultName)).single.apply()
```

また、以下の記述では会社選択用のプルダウンリストに表示する会社情報の一覧を取得しています。

```scala
val companies = withSQL {
  select.from(Companies as c).orderBy(c.id.asc)
}.map(Companies(c.resultName)).list().apply()
```

上記のコードの`select.from(Companies as c).orderBy(c.id.asc)`というQueryDSLは以下のSQLと同じ意味になります。

```sql
SELECT * FROM COMPANIES ORDER BY ID
```

## 実行

ここまで実装したらブラウザで一覧画面から新規作成やユーザ名のリンクをクリックし、以下のように登録画面と編集画面が表示されることを確認します。

![ユーザ登録画面](../images/play2.6-scalikejdbc3.2/register_form.png)

![ユーザ編集画面](../images/play2.6-scalikejdbc3.2/edit_form.png)
