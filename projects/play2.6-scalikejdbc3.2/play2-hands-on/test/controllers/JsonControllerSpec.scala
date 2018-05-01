package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.json._
import play.api.test._
import play.api.test.Helpers._


class JsonControllerSpec extends PlaySpec with GuiceOneAppPerSuite with Injecting {

  "JsonController GET" should {

    "request users list from the application" in {
      val controller = inject[JsonController]
      val result = controller.list.apply(FakeRequest())

      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")

      val resultJson = contentAsJson(result)
      val expectedJson = Json.parse("""{"users":[{"id":1,"name":"Taro Yamada","companyId":1},{"id":2,"name":"Jiro Sato"}]}""")

      resultJson mustEqual expectedJson
    }

  }
}
