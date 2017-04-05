# 09.ScalikeJDBCでの実践的な検索処理

ここまでの例ではscalikejdbcGenで自動生成されたメソッドや単一テーブルに対するシンプルなQueryDSLで処理できる例ばかりでしたが、ここではもう少し実践的な検索処理の実装方法について紹介します。

## INNER JOIN

INNER JOINの場合はシンプルに`innerJoin`メソッドを使用します。検索結果の取得もシンプルです。以下の例では`USERS`テーブルと`COMPANIES`テーブルをINNER JOINし、検索結果をそれぞれのモデルクラスのタプルのシーケンスで取得しています。

```scala
val users: Seq[(Users, Companies)] = withSQL {
  select.from(Users as u).innerJoin(Companies as c).on(u.companyId, c.id)
}.map { rs =>
  (Users(u)(rs), Companies(c)(rs))
}.list.apply()
```

## LEFT JOIN

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

## SQLを直接記述する

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

## 参考資料

ScalikeJDBCの詳細については以下のドキュメントが参考になります。

- http://scalikejdbc.org/documentation/query-dsl.html

----
[＜削除処理の実装に戻る](08_implement_delete_processing.md) | [JSON APIの準備に進む＞](10_preparation_for_json.md)
