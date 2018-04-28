---
title: ScalikeJDBCのテスト
---

scalikejdbcGenではソースコード生成時に対応するテストコードも自動生成してくれます。テストコードは`test/models`パッケージにあります。

このテストコードを少し編集して、実際にテストを動かしてみましょう。
今回はテスト用のDB`h2/test.mv.db`を用意していますので、こちらを使うようにします。

まず、`build.sbt`にscalikejdbc-testライブラリを追加します。

```scala
libraryDependencies += "org.scalikejdbc" %% "scalikejdbc-test" % "3.2.2" % Test
```

次に、テスト用のDBを読み込む準備をします。方法はいくつかありますが、今回は`conf/test.conf`というファイルを以下の内容で作成します。

```properties
include "application.conf"

db.default.url="jdbc:h2:tcp://localhost/test"
```

テストではこちらの設定を読み込むよう`build.sbt`に以下を追加します。

```scala
javaOptions in Test += "-Dconfig.file=conf/test.conf"
```

初期化処理をテストコードに追加します。

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

最後に、テストケースをハンズオンの形式に合わせて修正します。

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

ScalikeJDBCのユニットテストについては以下のドキュメントが参考になります。

- https://github.com/scalikejdbc/scalikejdbc-cookbook/blob/master/ja/08_unittest.md
