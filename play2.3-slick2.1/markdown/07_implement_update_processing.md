入力値のバリデーションを行い、エラーの有無に応じて以下の処理を行います。

* エラーあり ⇒ フォームにエラー情報をセットして入力フォームに戻ります。
* エラーなし ⇒ DBへの登録・更新処理を行い、一覧画面へリダイレクトします。

## コントローラ

`UserController`のメソッドのうち、登録処理を行う`create`メソッドと更新処理を行う`update`メソッドを実装します。

入力フォームの値を受け取るには、`userForm.bindFromRequest`メソッドでリクエストの内容をFormにバインドし、`fold`メソッドでエラーがあった場合の処理と、OKの場合の処理を記述します。以下は`create`メソッドの実装例です。

```scala
def create = DBAction.transaction { implicit rs =>
  // リクエストの内容をバインド
  userForm.bindFromRequest.fold(
    // エラーの場合は登録画面に戻す
    error => BadRequest(views.html.user.edit(error, Companies.sortBy(t => t.id).list)),

    // OKの場合は登録を行い一覧画面にリダイレクトする
    form  => {
      // ユーザを登録
      val user = UsersRow(0, form.name, form.companyId)
      Users.insert(user)

      // 一覧画面にリダイレクト
      Redirect(routes.UserController.list)
    }
  )
}
```

`update`メソッドも同じように実装します。

```scala
def update = DBAction.transaction { implicit rs =>
  // リクエストの内容をバインド
  userForm.bindFromRequest.fold(
    // エラーの場合
    error => BadRequest(views.html.user.edit(error, Companies.sortBy(t => t.id).list)),

    // OKの場合
    form  => {
      // ユーザ情報を更新
      val user = UsersRow(form.id.get, form.name, form.companyId)
      Users.filter(_.id === user.id.bind).update(user)

      // 一覧画面へリダイレクト
      Redirect(routes.UserController.list)
    }
  )
}
```

上記のコードではそれぞれ以下の記述でユーザ情報の登録、更新を行っています。

```scala
// ユーザを登録
val user = UsersRow(0, form.name, form.companyId)
Users.insert(user)

// ユーザ情報を更新
val user = UsersRow(form.id.get, form.name, form.companyId)
Users.filter(_.id is user.id.bind).update(user)
```

これはそれぞれ以下のSQLに該当します。

```sql
// ユーザを登録
INSERT INTO USERS (ID, NAME, COMPANY_ID) VALUES (?, ?, ?)

// ユーザ情報を更新
UPDATE USERS SET NAME = ?, COMPANY_ID = ? WHERE ID = ?
```

登録時は`UsersRow`のIDカラムに対応するプロパティに0を指定していますが、自動採番のカラムの場合、Slickはプロパティにセットされた値を無視して自動採番された値でインサートします（なので実はインサート時は0以外の値をセットしても構いません）。

> **POINT**
> * トランザクション制御が必要な場合は、`DBAction.transaction { ... }`の中に処理を記述します
> * HTMLテンプレートでのリンク先と同様、リダイレクト先も`routes.・・・`でタイプセーフに指定することができます

## 実行

ここまで実装したら、登録画面や編集画面からユーザ情報の登録、編集を行えることを確認しましょう。ユーザ名を空欄や20文字以上で登録しようとするとエラーメッセージが表示され、バリデーションが働いていることも確認できるはずです。

**バリデーションエラー時の表示：**
[[images/validation.png]]

----
[[＜ユーザ登録・編集画面の実装に戻る|06.ユーザ登録・編集画面の実装]] | [[削除処理の実装に進む＞|08.削除処理の実装]]
