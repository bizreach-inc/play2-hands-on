package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.json.JsValue
import play.api.test._
import play.api.test.Helpers._


class JsonControllerSpec extends PlaySpec with GuiceOneAppPerSuite with Injecting {

  "JsonController GET" should {

    "request users list from the application" in {
      val controller = inject[JsonController]
      val result = controller.list.apply(FakeRequest())

      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
      (contentAsJson(result) \ "users").as[List[JsValue]].size mustBe >(0)
    }

  }
}
