---
title: 削除処理の実装
---

指定したIDのユーザを`USERS`テーブルから削除し、一覧画面へリダイレクトします。

## コントローラ

すでに一覧画面に「削除」ボタンは表示されているので、そこから呼び出されるコントローラのメソッドのみ実装します。

```scala
def remove(id: Long) = Action { implicit request =>
  DB.localTx { implicit session =>
    // ユーザを削除
    Users.find(id).foreach { user =>
      Users.destroy(user)
    }
    // 一覧画面へリダイレクト
    Redirect(routes.UserController.list)
  }
}
```

ここでもscalikejdbcGenで自動生成された`Users.destroy()`メソッドを使用して削除処理を行っています。これをQueryDSLで書き直すと以下のようになります。

```scala
withSQL {
  delete.from(Users).where.eq(column.id, entity.id)
}.update.apply()
```

## 実行

一覧画面から「削除」をクリックしてユーザ情報が削除されることを確認してください。
