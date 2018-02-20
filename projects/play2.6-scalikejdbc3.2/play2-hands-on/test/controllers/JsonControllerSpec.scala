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
class JsonControllerSpec extends PlaySpec with GuiceOneAppPerSuite {

  override def fakeApplication(): Application = {
    val configdev = play.api.Configuration(scalikejdbc.config.TypesafeConfigReaderWithEnv("development").config)
    new GuiceApplicationBuilder()
      .configure(configdev)
      .bindings(new PlayModule)
      .build()
  }
  
  "JsonController GET" should {
    
    "render the index page from a new instance of controller" in {
      val controller = new JsonController(stubControllerComponents())
      val home = controller.list.apply(FakeRequest().withCSRFToken)

      status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
      //contentAsString(home) must include ("Welcome to Play")
    }

    "render the index page from the application" in {
      val controller = app.injector.instanceOf[JsonController]
      val home = controller.list.apply(FakeRequest()withCSRFToken)

      status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
      //contentAsString(home) must include ("Welcome to Play")
    }

  }
}
