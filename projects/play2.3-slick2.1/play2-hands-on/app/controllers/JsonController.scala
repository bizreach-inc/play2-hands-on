package controllers

import play.api.mvc._

import play.api.db.slick._
import models.Tables._
import profile.simple._

import play.api.libs.json._

object JsonController extends Controller {

  // UsersRowをJSONに変換するためのWritesを定義
  implicit val usersRowWritesFormat = Json.writes[UsersRow]

  // ユーザ情報を受け取るためのケースクラス
  case class UserForm(id: Option[Long], name: String, companyId: Option[Int])
  // JSONをUserFormに変換するためのReadsを定義
  implicit val userFormFormat = Json.reads[UserForm]

  /**
   * 一覧表示
   */
  def list = DBAction { implicit rs =>
    // IDの昇順にすべてのユーザ情報を取得
    val users = Users.sortBy(t => t.id).list

    // ユーザの一覧をJSONで返す
    Ok(Json.obj("users" -> users))
  }

  /**
   * ユーザ登録
   */
  def create = DBAction.transaction(parse.json) { implicit rs =>
    rs.body.validate[UserForm].map { form =>
      // OKの場合はユーザを登録
      val user = UsersRow(0, form.name, form.companyId)
      Users.insert(user)
      Ok(Json.obj("result" -> "success"))

    }.recoverTotal { e =>
      // NGの場合はバリデーションエラーを返す
      BadRequest(Json.obj("result" ->"failure", "error" -> JsError.toFlatJson(e)))
    }
  }

  /**
   * ユーザ更新
   */
  def update = DBAction.transaction(parse.json) { implicit rs =>
    rs.body.validate[UserForm].map { form =>
      // OKの場合はユーザ情報を更新
      val user = UsersRow(form.id.get, form.name, form.companyId)
      Users.filter(t => t.id === user.id.bind).update(user)
      Ok(Json.obj("result" -> "success"))

    }.recoverTotal { e =>
      // NGの場合はバリデーションエラーを返す
      BadRequest(Json.obj("result" ->"failure", "error" -> JsError.toFlatJson(e)))
    }
  }

  /**
   * ユーザ削除
   */
  def remove(id: Long) = DBAction.transaction { implicit rs =>
    // ユーザを削除
    Users.filter(t => t.id === id.bind).delete

    Ok(Json.obj("result" -> "success"))
  }

}
