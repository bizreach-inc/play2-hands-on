---
title: ユーザ削除APIの実装
layout: play25-slick31
---

指定したIDのユーザを`USERS`テーブルから削除します。

## コントローラ

`JsonController`の`remove`メソッドを以下のように実装します。

```scala
def remove(id: Long) = Action.async { implicit rs =>
  // ユーザを削除
  db.run(Users.filter(t => t.id === id.bind).delete).map { _ =>
    Ok(Json.obj("result" -> "success"))
  }
}
```

## 実行

コマンドラインから以下のコマンドを実行してユーザが削除されることを確認してください。

```
curl -XPOST http://localhost:9000/json/remove/1
```
