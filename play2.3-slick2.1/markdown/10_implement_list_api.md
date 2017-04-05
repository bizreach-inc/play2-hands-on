# 10.ユーザ一覧APIの実装

`USERS`テーブルからIDの昇順に全件取得し、ユーザ一覧をJSONで返します。

## Writesの定義

Play2のJSONサポートでは、ScalaオブジェクトをJSONに変換するには`Writes`、JSONをScalaオブジェクトに変換するためには`Reads`を定義する必要があります。

ここでは`USERS`テーブルを検索して取得したケースクラスのリストをJSONに変換して返却するので、`USERS`テーブルに対応する`UsersRow`クラスに対応する`Writes`を定義しておく必要があります。

```scala
object JsonController extends Controller {
  // UsersRowをJSONに変換するためのWritesを定義
  implicit val usersRowWrites = Json.writes[UsersRow]
  ...
```

> **POINT**
> * Play2のJSONサポートは単純なケースクラスの変換だけでなく、より複雑な変換やバリデーションなどを行うこともできます
> * `Json.writes`や`Json.reads`の代わりに`Json.format`で`Format`を定義することで`Writes`と`Reads`を同時に定義することができます。同じケースクラスを読み込みと書き出しの両方に使う場合は`Json.format`を使うとよいでしょう

## コントローラ

`JsonController`の`list`メソッドを以下のように実装します。

```scala
def list = DBAction { implicit rs =>
  // IDの昇順にすべてのユーザ情報を取得
  val users = Users.sortBy(t => t.id).list

  // ユーザの一覧をJSONで返す
  Ok(Json.obj("users" -> users))
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
[＜JSON APIの準備に戻る](09_preparation_for_json.md) | [ユーザ登録・更新APIの実装に進む＞](11_implement_update_api.md)
