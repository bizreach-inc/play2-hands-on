package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._
import play.api.test.CSRFTokenHelper._


class UserControllerSpec extends PlaySpec with GuiceOneAppPerSuite with Injecting {

  "UserController GET" should {

    "render the list page from the application" in {
      val controller = inject[UserController]
      val result = controller.list.apply(FakeRequest().withCSRFToken)

      status(result) mustBe OK
      contentType(result) mustBe Some("text/html")
      contentAsString(result) must include ("ユーザ一覧")
    }

  }
}
