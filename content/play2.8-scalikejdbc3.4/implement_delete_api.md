---
title: ユーザ削除APIの実装
---

指定したIDのユーザを`USERS`テーブルから削除します。

## コントローラ

`JsonController`の`remove`メソッドを以下のように実装します。

```scala
def remove(id: Long) = Action { implicit request =>
  DB.localTx { implicit session =>
    // ユーザを削除
    Users.find(id).foreach { user =>
      Users.destroy(user)
    }
    Ok(Json.obj("result" -> "success"))
  }
}
```

## 実行

コマンドラインから以下のコマンドを実行してユーザが削除されることを確認してください。

```
curl -XPOST http://localhost:9000/json/remove/1
```
