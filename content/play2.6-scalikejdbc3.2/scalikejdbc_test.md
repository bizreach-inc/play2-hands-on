---
title: ScalikeJDBCのユニットテスト
---

ここまでscalikejdbcGenで自動生成されたメソッドやシンプルなQueryDSLによる処理をみてきましたが、scalikejdbcGenではソースコード生成時に対応するテストコードも自動生成してくれています。
テストコードは`test/models`パッケージにあります。

このテストコードを少し編集して、実際にテストを動かしてみましょう。

## scalikejdbc-testライブラリを追加

`build.sbt`にライブラリを追加します。

```scala
libraryDependencies += "org.scalikejdbc" %% "scalikejdbc-test" % "3.2.2" % Test
```

これで、自動生成されたテストコードのコンパイルが通るようになります。

## 初期化処理を追加

DBの接続情報を読み込む方法はいくつかありますが、今回は既に`conf/application.conf`にある`db.default.*`の設定をテストにも流用します。
`UsersSpec`と`CompaniesSpec`に初期化処理を追加します。

```scala
class UsersSpec extends fixture.FlatSpec with Matchers with AutoRollback {
  // 初期化処理を追加
  config.DBs.setup()

  val u = Users.syntax("u")
  ...
}
```

```scala
class CompaniesSpec extends fixture.FlatSpec with Matchers with AutoRollback {
  // 初期化処理を追加
  config.DBs.setup()

  val c = Companies.syntax("c")
  ...
}
```

## テストを修正

自動生成はあくまで機械的な生成のため、状況に応じてテストケースを修正する必要があります。

`UsersSpec`にある`save`メソッドのテストは、以下のようにTODOになっているところがあります。

```scala
it should "save a record" in { implicit session =>
  val entity = Users.findAll().head
  // TODO modify something
  val modified = entity
  val updated = Users.save(modified)
  updated should not equal(entity)
}
```

値を変更するように修正します。

```scala
it should "save a record" in { implicit session =>
  val entity = Users.findAll().head
  // nameを変更
  val modified = entity.copy(name = "modify")
  val updated = Users.save(modified)
  updated should not equal(entity)
}
```

`CompaniesSpec`も同様に修正します。

```scala
it should "save a record" in { implicit session =>
  val entity = Companies.findAll().head
  // nameを変更
  val modified = entity.copy(name = "modify")
  val updated = Companies.save(modified)
  updated should not equal(entity)
}
```

加えて`CompaniesSpec`には、存在しないIDで検索するなど既存のままでは失敗するテストがいくつかあるので、テストフィクスチャを使ってこれを解消します。
ScalikeJDBCでは以下のように`fixture`メソッドをoverrideすることでテストフィクスチャを設定できます。

```scala
class CompaniesSpec extends fixture.FlatSpec with Matchers with AutoRollback {
  config.DBs.setup()

  // テスト終了時にすべてロールバックしてくれる
  override def fixture(implicit session: DBSession) {
    SQL("insert into COMPANIES values (?, ?)").bind(123, "test_company1").update.apply()
    SQL("insert into COMPANIES values (?, ?)").bind(234, "test_company2").update.apply()
  }

  ...

  it should "create new record" in { implicit session =>
    // 一意なIDを指定
    val created = Companies.create(id = 999, name = "MyString")
    created should not be(null)
  }

  ...

  it should "destroy a record" in { implicit session =>
    // フィクスチャで生成したデータが削除対象
    val entity = Companies.find(123).get
    val deleted = Companies.destroy(entity)
    deleted should be(1)
    val shouldBeNone = Companies.find(123)
    shouldBeNone.isDefined should be(false)
  }
  it should "perform batch insert" in { implicit session =>
    // フィクスチャで生成したデータをバッチ更新
    val entities = Companies.findAllBy(sqls.in(c.id, Seq(123, 234)))
    entities.foreach(e => Companies.destroy(e))
    val batchInserted = Companies.batchInsert(entities)
    batchInserted.size should be >(0)
  }
}
```

## 実行

ここまで修正したらテストを実行します。

```
sbt test
```

すると以下のようにすべてのテストが成功するはずです。

```
[info] Total number of tests run: 23
[info] Suites: completed 3, aborted 0
[info] Tests: succeeded 23, failed 0, canceled 0, ignored 0, pending 0
[info] All tests passed.
[info] Passed: Total 23, Failed 0, Errors 0, Passed 23
```

特定のテストクラスのみ実行することもできます。

```
sbt "testOnly models.CompaniesSpec"
```

## 参考資料

ScalikeJDBCの詳細については以下のドキュメントが参考になります。

- https://github.com/scalikejdbc/scalikejdbc-cookbook/blob/master/ja/08_unittest.md
