---
title: Play2のテスト
---

Play2はPlay2で開発されたアプリケーションのユニットテストのための機能を提供しています。デフォルトで`HomeController`に対するテストケースが生成されていますが、ここではハンズオンで作成したJSON APIに対するテストを作成してみましょう。

`test/controllers`に以下の内容で`JsonControllerSpec`を作成します。

```scala
package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.json._
import play.api.test._
import play.api.test.Helpers._

class JsonControllerSpec extends PlaySpec with GuiceOneAppPerSuite with Injecting {
  "JsonController GET" should {
    "request users list from the application" in {
      // Guiceからコントローラーのインスタンスを取得
      val controller = inject[JsonController]
      // FakeRequestを使ってコントローラーのメソッドを呼び出す
      val result = controller.list.apply(FakeRequest())
      
      // レスポンスのステータスを確認
      status(result) mustBe OK
      // レスポンスのContent-Typeを確認
      contentType(result) mustBe Some("application/json")
      //  レスポンスのJSONを確認
      val resultJson = contentAsJson(result)
      val expectedJson = Json.parse(
        """{"users":[{"id":1,"name":"Taro Yamada","companyId":1},{"id":2,"name":"Jiro Sato"}]}"""
      )
      resultJson mustEqual expectedJson
    }
  }
}
```

このテストケースでは実際にHTTPリクエストを送信するのではなく、`FakeRequest`というダミーのリクエストを使用してコントローラーを呼び出しています。テストケース内ではテスト用のヘルパーメソッドでレスポンスの情報を取得することができるので、それらの情報が期待通りかどうかを確認することでテストを行うことができます。

以下のようにしてテストを実行します。

```
sbt test
```

テストが成功すると以下のように表示されるはずです。

```
[info] Total number of tests run: 24
[info] Suites: completed 4, aborted 0
[info] Tests: succeeded 24, failed 0, canceled 0, ignored 0, pending 0
[info] All tests passed.
[info] Passed: Total 24, Failed 0, Errors 0, Passed 24
```

特定のテストのみ実行することもできます。

```
sbt "testOnly controllers.JsonControllerSpec"
```

ここではアプリケーションの実行時と同じDBを使用してテストを実行しているため、DBの状態が初期状態から変わっているとテストが失敗してしまいます。実際にはテスト専用のDBを用いたり、テスト毎にDBの内容をロールバックするといった工夫も必要になってきます。

