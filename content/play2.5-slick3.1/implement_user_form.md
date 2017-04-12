---
title: ユーザ登録・編集画面の実装
layout: play2.5-slick3.1
---

リクエストパラメータにIDが指定押されているかどうかに応じて以下の処理を行います。

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
@(userForm: Form[controllers.UserController.UserForm], companies: Seq[models.Tables.CompaniesRow])(implicit request: Request[Any], messages: Messages)

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

このテンプレートではテンプレートの暗黙的な引数として`request`だけでなく`message`を受け取るようになっています。これはテンプレートでテキストフィールドを表示するために使用している`inputText`ヘルパーがメッセージ等の国際化のために必要とするもので、コントローラにDIした`MessagesApi`によって暗黙的に渡されます。このハンズオンでは特に使用しませんが、Play2アプリケーションの国際化対応に必要になるものですので覚えておくとよいでしょう。

## コントローラ

最後に`UserController`の`edit`メソッドを実装します。引数`id`が指定されていた場合は空の`Form`、指定されていた場合は`Form#fill`メソッドで`Form`に初期表示する値をセットしたうえでテンプレートを呼び出すようにします。

```scala
// コンパニオンオブジェクトに定義したFormを参照するためにimport文を追加
import UserController._

def edit(id: Option[Long]) = Action.async { implicit rs =>
  // リクエストパラメータにIDが存在する場合
  val form = if(id.isDefined) {
    // IDからユーザ情報を1件取得
    db.run(Users.filter(t => t.id === id.get.bind).result.head).map { user =>
      // 値をフォームに詰める
      userForm.fill(UserForm(Some(user.id), user.name, user.companyId))
    }
  } else {
    // リクエストパラメータにIDが存在しない場合
    Future { userForm }
  }

  form.flatMap { form =>
    // 会社一覧を取得
    db.run(Companies.sortBy(_.id).result).map { companies =>
      Ok(views.html.user.edit(form, companies))
    }
  }
}
```

上記のコードでは以下の記述で会社情報の一覧を取得しています。

```scala
Companies.sortBy(_.id).result
```

これは以下のSQLと同じ意味になります。

```sql
SELECT * FROM COMPANIES ORDER BY ID
```

このアクションも`Action.async`で`Future`を返しますが、リクエストパラメータにIDが存在しない場合は以下のようにして自分で`userForm`を返す`Future`を作成している点に注意してください。

```scala
Future { userForm }
```

## 実行

ここまで実装したらブラウザで一覧画面から新規作成やユーザ名のリンクをクリックし、以下のように登録画面と編集画面が表示されることを確認します。

![ユーザ登録画面](../images/play2.5-slick3.1/register_form.png)

![ユーザ編集画面](../images/play2.5-slick3.1/edit_form.png)
