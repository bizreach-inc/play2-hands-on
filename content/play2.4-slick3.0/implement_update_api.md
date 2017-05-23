---
title: ユーザ登録・更新APIの実装
layout: play24-slick30
---

ユーザ情報をJSONで受け取り、登録もしくは更新を行います。

## Readsの定義

`JsonController`のコンパニオンオブジェクトにユーザ情報を受け取るためのケースクラスと、JSONからそのケースクラスに変換するための`Reads`を定義します。

```scala
object JsonController {
  ...

  // ユーザ情報を受け取るためのケースクラス
  case class UserForm(id: Option[Long], name: String, companyId: Option[Int])

  // JSONをUserFormに変換するためのReadsを定義
  implicit val userFormFormat = (
    (__ \ "id"       ).readNullable[Long] and
    (__ \ "name"     ).read[String]       and
    (__ \ "companyId").readNullable[Int]
  )(UserForm)
}
```

前述の`Writes`と同様、DSLを使わずに以下のように記述することもできます。

```scala
implicit val userFormFormat = new Reads[UserForm]{
  def reads(js: JsValue): UserForm = {
    UserForm(
      id        = (js \ "id"       ).asOpt[Long],
      name      = (js \ "name"     ).as[String],
      companyId = (js \ "companyId").asOpt[Int]
    )
  }
}
```

`Reads`や`Writes`は上記のように明示的にマッピングを定義する方法に加え、以下のようにマクロを使ってシンプルに記述することもできます（`Json.reads`や`Json.writes`はコンパイル時に上記のようなマッピングを自動生成してくれるマクロです）。

```scala
implicit val userFormReads  = Json.reads[UserForm]
implicit val userFormWrites = Json.writes[UserForm]
```

また、`Reads`と`Writes`の両方が必要な場合は`Json.format`マクロを使うことができます。`Format`を定義しておくと`Reads`と`Writes`の両方を定義したのと同じ意味になります。

```scala
implicit val userFormFormat = Json.format[UserForm]
```

## コントローラ

`JsonController`の`create`メソッドを以下のように実装します。

JSONリクエストを受け取る場合は

* `Action.async(parse.json) { ... }`

のようにアクションに`parse.json`を指定します。`rs.body.validate`メソッドでJSONをケースクラスに変換でき、変換に失敗した場合の処理を`recoverTotal`メソッドで行うことができます。

```scala
def create = Action.async(parse.json) { implicit rs =>
  rs.body.validate[UserForm].map { form =>
    // OKの場合はユーザを登録
    val user = UsersRow(0, form.name, form.companyId)
    db.run(Users += user).map { _ =>
      Ok(Json.obj("result" -> "success"))
    }
  }.recoverTotal { e =>
    // NGの場合はバリデーションエラーを返す
    Future {
      BadRequest(Json.obj("result" ->"failure", "error" -> JsError.toJson(e)))
    }
  }
}
```

同様に`update`メソッドを以下のように実装します。

```scala
def update = Action.async(parse.json) { implicit rs =>
  rs.body.validate[UserForm].map { form =>
    // OKの場合はユーザ情報を更新
    val user = UsersRow(form.id.get, form.name, form.companyId)
    db.run(Users.filter(t => t.id === user.id.bind).update(user)).map { _ =>
      Ok(Json.obj("result" -> "success"))
    }
  }.recoverTotal { e =>
    // NGの場合はバリデーションエラーを返す
    Future {
      BadRequest(Json.obj("result" ->"failure", "error" -> JsError.toJson(e)))
    }
  }
}
```

> **POINT**
>
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
