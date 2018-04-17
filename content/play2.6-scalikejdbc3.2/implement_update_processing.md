---
title: 登録・更新処理の実装
---

入力値のバリデーションを行い、エラーの有無に応じて以下の処理を行います。

* エラーあり ⇒ フォームにエラー情報をセットして入力フォームに戻ります。
* エラーなし ⇒ DBへの登録・更新処理を行い、一覧画面へリダイレクトします。

## コントローラ

`UserController`のメソッドのうち、登録処理を行う`create`メソッドと更新処理を行う`update`メソッドを実装します。

入力フォームの値を受け取るには、`userForm.bindFromRequest`メソッドでリクエストの内容をFormにバインドし、`fold`メソッドでエラーがあった場合の処理と、OKの場合の処理を記述します。以下は`create`メソッドの実装例です。

```scala
def create = Action { implicit request =>
  DB.localTx { implicit session =>
    // リクエストの内容をバインド
    userForm.bindFromRequest.fold(
      // エラーの場合
      error => {
        BadRequest(views.html.user.edit(error, Companies.findAll()))
      },
      // OKの場合
      form  => {
        // ユーザを登録
        Users.create(form.name, form.companyId)
        // 一覧画面へリダイレクト
        Redirect(routes.UserController.list)
      }
    )
  }
}
```

`update`メソッドも同じように実装します。

```scala
def update = Action { implicit request =>
  DB.localTx { implicit session =>
    // リクエストの内容をバインド
    userForm.bindFromRequest.fold(
      // エラーの場合は編集画面に戻す
      error => {
        BadRequest(views.html.user.edit(error, Companies.findAll()))
      },
      // OKの場合は更新を行い一覧画面にリダイレクトする
      form => {
        // ユーザ情報を更新
        Users.find(form.id.get).foreach { user =>
          Users.save(user.copy(name = form.name, companyId = form.companyId))
        }
        // 一覧画面にリダイレクト
        Redirect(routes.UserController.list)
      }
    )
  }
}
```

INSERTに対応する`Users.create()`や、UPDATEに対応する`Users.save()`など、ここでもscalikejdbcGenによって自動生成されたメソッドを使用しています。これらをQueryDSLを使って書き直すと以下のようになります。

```scala
// INSERTをQueryDSLで書き直した場合
val generatedKey = withSQL {
  val column = Users.column
  insert.into(Users).namedValues(
    column.name -> form.name,
    column.companyId -> form.companyId
  )
}.updateAndReturnGeneratedKey.apply()

// UPDATEをQueryDSLで書き直した場合
withSQL {
  val column = Users.column
  QueryDSL.update(Users).set(
    column.name -> form.name,
    column.companyId -> form.companyId
  ).where.eq(column.id, user.id)
}.update.apply()
```

> **POINT**
>
> * HTMLテンプレートでのリンク先と同様、リダイレクト先も`routes.・・・`でタイプセーフに指定することができます
> * `DB.localTx { ... }`でトランザクション管理されたセッションを取得することができます
>   * この中の処理が正常に終了した場合はコミットされ、例外が発生した場合は自動的にロールバックされます

## 実行

ここまで実装したら、登録画面や編集画面からユーザ情報の登録、編集を行えることを確認しましょう。ユーザ名を空欄や20文字以上で登録しようとするとエラーメッセージが表示され、バリデーションが効いていることも確認できるはずです。

**バリデーションエラー時の表示：**

![バリデーションエラー時の表示](../images/play2.6-scalikejdbc3.2/validation.png)
