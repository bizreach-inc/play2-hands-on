package controllers

import play.api.mvc._

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.i18n.{MessagesApi, I18nSupport}
import play.api.db.slick._
import slick.driver.JdbcProfile
import models.Tables._
import javax.inject.Inject
import scala.concurrent.Future
import slick.driver.H2Driver.api._

import play.api.libs.json._

object JsonController {
  // ユーザ情報を受け取るためのケースクラス
  case class UserForm(id: Option[Long], name: String, companyId: Option[Int])
  
  // JSONをUserFormに変換するためのReadsを定義
  implicit val userFormFormat = Json.reads[UserForm]
  
  // UsersRowをJSONに変換するためのWritesを定義
  implicit val usersRowWritesFormat = Json.writes[UserForm]
}

class JsonController @Inject()(val dbConfigProvider: DatabaseConfigProvider) extends Controller
    with HasDatabaseConfigProvider[JdbcProfile] {
  import JsonController._

  /**
   * 一覧表示
   */
  def list = Action.async { implicit rs =>
    // IDの昇順にすべてのユーザ情報を取得
    db.run(Users.sortBy(t => t.id).result).map { users =>
      // ユーザの一覧をJSONで返す
      Ok(Json.obj("users" -> users.map { user =>
        // TODO UsersRowそのままだとWritesの型があわないので別のケースクラスに詰め直している
        UserForm(id = Some(user.id), name = user.name, companyId = user.companyId)
      }))
    }
  }
  
  /**
   * ユーザ登録
   */
  def create = Action.async(parse.json) { implicit rs =>
    rs.body.validate[UserForm].map { form =>
      // OKの場合はユーザを登録
      val user = UsersRow(0, form.name, form.companyId)
      db.run(Users += user).map { _ =>
        Ok(Json.obj("result" -> "success"))
      }
    }.recoverTotal { e =>
      // NGの場合はバリデーションエラーを返す
      Future {
        BadRequest(Json.obj("result" ->"failure", "error" -> JsError.toJson(e)))
      }
    }
  }
  
  /**
   * ユーザ更新
   */
  def update = Action.async(parse.json) { implicit rs =>
    rs.body.validate[UserForm].map { form =>
      // OKの場合はユーザ情報を更新
      val user = UsersRow(form.id.get, form.name, form.companyId)
      db.run(Users.filter(t => t.id === user.id.bind).update(user)).map { _ =>
        Ok(Json.obj("result" -> "success"))
      }
    }.recoverTotal { e =>
      // NGの場合はバリデーションエラーを返す
      Future {
        BadRequest(Json.obj("result" ->"failure", "error" -> JsError.toJson(e)))
      }
    }
  }
  
  /**
   * ユーザ削除
   */
  def remove(id: Long) = Action.async { implicit rs =>
    // ユーザを削除
    db.run(Users.filter(t => t.id === id.bind).delete).map { _ =>
      Ok(Json.obj("result" -> "success"))
    }
  }

}
