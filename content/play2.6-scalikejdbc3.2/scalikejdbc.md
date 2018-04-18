---
title: ScalikeJDBCの実践的なトピック
---

ここまでscalikejdbcGenで自動生成されたメソッドやシンプルなQueryDSLによる処理をみてきましたが、ここではもう少し実践的なトピックについて紹介します。

## 検索処理

複数のテーブルを結合したいケースはよくあります。

INNER JOINはシンプルに`innerJoin`メソッドを使用します。検索結果の取得もシンプルです。以下の例では`USERS`テーブルと`COMPANIES`テーブルをINNER JOINし、検索結果をそれぞれのモデルクラスのタプルのシーケンスで取得しています。

```scala
val users: Seq[(Users, Companies)] = withSQL {
  select.from(Users as u).innerJoin(Companies as c).on(u.companyId, c.id)
}.map { rs =>
  (Users(u)(rs), Companies(c)(rs))
}.list.apply()
```

LEFT JOINの場合は`leftJoin`メソッドを使用します。

```scala
val users: Seq[(Users, Option[Companies])] = withSQL {
  select.from(Users as u).leftJoin(Companies as c).on(u.companyId, c.id)
}.map { rs =>
  (Users(u)(rs), rs.intOpt(c.resultName.id).map(_ => Companies(c)(rs)))
}.list.apply()
```

LEFT JOINしたテーブルの値を取得する場合、`map()`メソッドで`Option`型に変換する必要があるという点に注意してください。以下のコードは、まず結果セットから`COMPANIES`テーブルの`ID`カラムを`intOpt`メソッドで`Option[Int]`型として取得し、値が取得できた場合のみ`Companies`クラスにマッピングするという処理を行っています。

```scala
rs.intOpt(c.resultName.id).map(_ => Companies(c)(rs))
```

また、集計処理といった少し複雑なSQLの場合は直接記述したいことがあります。

`sql` interpolationを使うと文字列リテラルで生SQLを記述することができます。ただし、SQLを完全に記述するだけでなく、自動生成されたクラスを使って記述を補助することができます。

```scala
val users: Seq[(Users, Companies)] = sql"""
  |SELECT ${u.result.*}, ${c.result.*}
  |FROM ${Users.as(u)} INNER JOIN ${Companies.as(c)}
  |ON ${u.companyId} = ${c.id}
""".stripMargin.map { rs =>
  (Users(u)(rs), Companies(c)(rs))
}.list.apply()
```

SELECT句に大量のカラムを記述する必要がなかったり、テーブル名やカラム名のタイプミスを防ぐことができます。また、`sql` interpolationを使う場合は`withSQL { ... }`で囲む必要はありません。`map()`メソッド以降はQueryDSLの場合と同じです。


## ユニットテスト

scalikejdbcGenではソースコード生成時に対応するテストコードも自動生成してくれます。テストコードは`test/models`パッケージにあります。

このテストコードを少し編集して、実際にテストを動かしてみましょう。

まず、`build.sbt`にscalikejdbc-testライブラリを追加します。

```scala
libraryDependencies += "org.scalikejdbc" %% "scalikejdbc-test" % "3.2.2" % Test
```

次に、DBの接続情報を読み込む処理をテストコードに追加します。方法はいくつかありますが、今回は既に`conf/application.conf`にある`db.default.*`の設定をテストにも流用します。

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

## 参考資料

ScalikeJDBCの詳細については以下のドキュメントが参考になります。

- http://scalikejdbc.org/documentation/query-dsl.html
- https://github.com/scalikejdbc/scalikejdbc-cookbook/blob/master/ja/08_unittest.md
