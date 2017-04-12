---
title: ユーザ登録・更新APIの実装
layout: play2.3-slick2.1
---

ユーザ情報をJSONで受け取り、登録もしくは更新を行います。

## Readsの定義

`JsonController`にユーザ情報を受け取るためのケースクラスと、JSONからそのケースクラスに変換するための`Reads`を定義します。

```scala
object JsonController extends Controller {
  ...
  // ユーザ情報を受け取るためのケースクラス
  case class UserForm(id: Option[Long], name: String, companyId: Option[Int])
  // JSONをUserFormに変換するためのReadsを定義
  implicit val userFormReads = Json.reads[UserForm]
  ...
```

## コントローラ

`JsonController`の`create`メソッドを以下のように実装します。

JSONリクエストを受け取る場合は

* `DBAction(parse.json) { ... }`
* `DBAction.transaction(parse.json) { ... }`

のようにアクションに`parse.json`を指定します。`rs.body.validate`メソッドでJSONをケースクラスに変換でき、変換に失敗した場合の処理を`recoverTotal`メソッドで行うことができます。

```scala
def create = DBAction.transaction(parse.json) { implicit rs =>
  rs.body.validate[UserForm].map { form =>
    // OKの場合はユーザを登録
    val user = UsersRow(0, form.name, form.companyId)
    Users.insert(user)
    Ok(Json.obj("result" -> "success"))

  }.recoverTotal { e =>
    // NGの場合はバリデーションエラーを返す
    BadRequest(Json.obj("result" ->"failure", "error" -> JsError.toFlatJson(e)))
  }
}
```

同様に`update`メソッドを以下のように実装します。

```scala
def update = DBAction.transaction(parse.json) { implicit rs =>
  rs.body.validate[UserForm].map { form =>
    // OKの場合はユーザ情報を更新
    val user = UsersRow(form.id.get, form.name, form.companyId)
    Users.filter(t => t.id === user.id.bind).update(user)
    Ok(Json.obj("result" -> "success"))

  }.recoverTotal { e =>
    // NGの場合はバリデーションエラーを返す
    BadRequest(Json.obj("result" ->"failure", "error" -> JsError.toFlatJson(e)))
  }
}
```

> **POINT**
> * `parse.json`はボディパーサと呼ばれるもので、リクエストボディの処理方法を決めるものです

## 実行

コマンドラインから以下のコマンドを実行してユーザ情報を登録・更新できることを確認しましょう。

登録：
```
curl -H "Content-type: application/json" -XPOST -d '{"name":"TestUser", "companyId":1}' http://localhost:9000/json/create
```

更新：
```
curl -H "Content-type: application/json" -XPOST -d '{"id":1, "name":"TestUser", "companyId":1}' http://localhost:9000/json/update
```

いずれの場合も成功すると以下のJSONが返却されます。

```json
{"result":"success"}
```

エラー時のレスポンスを確認するために、以下のように不正なJSONを送信してみましょう（プロパティ名が`name`ではなく`userName`になっている）。

```
curl -H "Content-type: application/json" -XPOST -d '{"userName":"TestUser"}' http://localhost:9000/json/create
```

すると以下のようにエラー情報を含むJSONが返却されます。

```json
{"result":"failure","error":{"obj.name":[{"msg":"error.path.missing","args":[]}]}}
```
