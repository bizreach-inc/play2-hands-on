# 11.ユーザ一覧APIの実装

`USERS`テーブルからIDの昇順に全件取得し、ユーザ一覧をJSONで返します。

## Writesの定義

Play2のJSONサポートでは、ScalaオブジェクトをJSONに変換するには`Writes`、JSONをScalaオブジェクトに変換するためには`Reads`を定義する必要があります。

ここでは`USERS`テーブルを検索して取得したケースクラスのリストをJSONに変換して返却するので、`USERS`テーブルに対応する`UsersRow`クラスに対応する`Writes`を定義しておく必要があります。画面から値を受け取る`Form`と同様、該当のコントローラ（ここでは`JsonController`）のコンパニオンオブジェクトとして以下の内容を追加します。

```scala
object JsonController {
  // UsersRowをJSONに変換するためのWritesを定義
  implicit val usersRowWritesWrites = (
    (__ \ "id"       ).write[Long]   and
    (__ \ "name"     ).write[String] and
    (__ \ "companyId").writeNullable[Int]
  )(unlift(Users.unapply))
}
```

Play2のJSONサポートが提供するDSLを使用してマッピングを定義していますが、DSLを使わずに以下のように記述することもできます。

```scala
implicit val usersWritesFormat = new Writes[Users]{
  def writes(user: Users): JsValue = {
    Json.obj(
      "id"        -> user.id,
      "name"      -> user.name,
      "companyId" -> user.companyId
    )
  }
}
```

> **POINT**
> * Play2のJSONサポートではオブジェクトとJSONの返還を行うために`Reads`や`Writes`でマッピングを定義する必要があります
> * Play2のJSONサポートは単純なケースクラスの変換だけでなく、より複雑な変換やバリデーションなどを行うこともできます

## コントローラ

`JsonController`の`list`メソッドを以下のように実装します。

```scala
// コンパニオンオブジェクトに定義したReads、Writesを参照するためにimport文を追加
import JsonController._

def list = Action { implicit request =>
  val u = Users.syntax("u")

  DB.readOnly { implicit session =>
    // ユーザのリストを取得
    val users = withSQL {
      select.from(Users as u).orderBy(u.id.asc)
    }.map(Users(u.resultName)).list.apply()

    // ユーザの一覧をJSONで返す
    Ok(Json.obj("users" -> users))
  }
}
```

`Json.obj`メソッドでケースクラスからJSONへの変換が行われますが、このときにケースクラスに対応した`Writes`が定義されていないとコンパイルエラーになります。

## 実行

コマンドラインから以下のコマンドを実行してユーザ一覧がJSONで取得できることを確認してみましょう。

```
curl -XGET http://localhost:9000/json/list
```

結果として以下のようなJSONが表示されるはずです。

```json
{"users":[{"id":1,"name":"Taro Yamada","companyId":1},{"id":2,"name":"Jiro Sato"}]}
```

----
[＜JSON APIの準備に戻る](10_preparation_for_json.md) | [ユーザ登録・更新APIの実装に進む＞](12_implement_update_api.md)
