---
title: ユーザ登録・編集画面の実装
layout: play2.3-slick2.1
---

リクエストパラメータにIDが指定押されているかどうかに応じて以下の処理を行います。

* リクエストパラメータにIDなし ⇒ 新規登録画面を表示します。
* リクエストパラメータにIDあり ⇒ `USERS`テーブルを検索し、該当のユーザ情報を初期表示した編集画面を表示します。

## フォーム

画面からの入力値を受け取るための`Form`をコントローラに定義します。`Form`は必ずしもコントローラに定義する必要はないのですが、コントローラでの処理に強く依存するため特に理由がない限りコントローラ内に定義するとよいでしょう。

まず、`UserController`の冒頭に以下のインポート文を追加します。これで`Form`の定義や基本的なバリデーションができるようになります。

```scala
import play.api.data._
import play.api.data.Forms._
```

登録・編集画面からの入力値を受け取るための`Form`を定義します。

```scala
object UserController extends Controller {

  // フォームの値を格納する
  case class UserForm(id: Option[Long], name: String, companyId: Option[Int])

  // formのデータ⇔ケースクラスの変換を行う
  val userForm = Form(
    mapping(
      "id"        -> optional(longNumber),
      "name"      -> nonEmptyText(maxLength = 20),
      "companyId" -> optional(number)
    )(UserForm.apply)(UserForm.unapply)
  )

  ...
```

> **POINT**
>
> * `Form`はStrutsのアクションフォームのようなものです
> * マッピングに従ってバリデーション（後述）が行われます

## ビュー

続いて`views.user`パッケージに`edit.scala.html`を実装します。引数には`Form`のインスタンスと、プルダウンで選択する会社情報を格納した`List`を受け取ります。

```html
@(userForm: Form[controllers.UserController.UserForm], companies: List[models.Tables.CompaniesRow])

@* テンプレートで利用可能なヘルパーをインポート *@
@import helper._

@main("ユーザ作成") {

  @* IDがある場合は更新処理、ない場合は登録処理を呼ぶ *@
  @form(userForm("id").value.map(x => routes.UserController.update).getOrElse(routes.UserController.create), 'class -> "container", 'role -> "form") {
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

> **POINT**
> * テンプレートでも`@import`でインポート文を記述することができます
> * `@import helper._`でPlayが提供する標準ヘルパー（フォームなどを出力する関数）を使用できるようになります

## コントローラ

最後に`UserController`の`edit`メソッドを実装します。引数`id`が指定されていた場合は空の`Form`、指定されていた場合は`Form#fill`メソッドで`Form`に初期表示する値をセットしたうえでテンプレートを呼び出すようにします。

```scala
def edit(id: Option[Long]) = DBAction { implicit rs =>
  // リクエストパラメータにIDが存在する場合
  val form = if(id.isDefined) {
    // IDからユーザ情報を1件取得
    val user = Users.filter(_.id === id.get.bind).first

    // 値をフォームに詰める
    userForm.fill(UserForm(Some(user.id), user.name, user.companyId))
  } else {
    // リクエストパラメータにIDが存在しない場合
    userForm
  }

  // 会社一覧を取得
  val companies = Companies.sortBy(t => t.id).list

  Ok(views.html.user.edit(form, companies))
}
```

> **POINT**
>
> * Scalaでは、`if`式は値を返します（ブロックの最後に評価した値が`if`式の戻り値になります）

上記のコードでは以下の記述で会社情報の一覧を取得しています。

```scala
val companies = Companies.sortBy(t => t.id).list
```

これは以下のSQLと同じ意味になります。

```sql
SELECT * FROM COMPANIES ORDER BY ID
```

## 実行

ここまで実装したらブラウザで一覧画面から新規作成やユーザ名のリンクをクリックし、以下のように登録画面と編集画面が表示されることを確認します。

![ユーザ登録画面](../images/play2.3-slick2.1/register_form.png)

![ユーザ編集画面](../images/play2.3-slick2.1/edit_form.png)
