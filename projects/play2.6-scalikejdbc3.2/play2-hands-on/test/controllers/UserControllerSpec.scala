package controllers

import _root_.play.api.inject.guice.GuiceApplicationBuilder
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import org.scalatest.TestSuite
import play.api.test._
import play.api.test.Helpers._
import play.api.test.CSRFTokenHelper._
import play.api.{Play, Application}
import scalikejdbc.PlayModule

/**
 * 
 * 
 */
class UserControllerSpec extends PlaySpec with GuiceOneAppPerSuite {

  override def fakeApplication(): Application = {
    val configdev = play.api.Configuration(scalikejdbc.config.TypesafeConfigReaderWithEnv("development").config)
    new GuiceApplicationBuilder()
      .configure(configdev)
      .bindings(new PlayModule)
      .build()
  }
  
  "UserController GET" should {
    
    "render the index page from a new instance of controller" in {
      val controller = new UserController(stubControllerComponents())
      val home = controller.list.apply(FakeRequest().withCSRFToken)

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      //contentAsString(home) must include ("Welcome to Play")
    }

    "render the index page from the application" in {
      val controller = app.injector.instanceOf[UserController]
      val home = controller.list.apply(FakeRequest()withCSRFToken)

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      //contentAsString(home) must include ("Welcome to Play")
    }

  }
}
