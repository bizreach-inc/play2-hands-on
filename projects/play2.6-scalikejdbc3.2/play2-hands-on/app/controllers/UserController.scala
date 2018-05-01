package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import javax.inject.Inject
import scalikejdbc._
import models._

object UserController {

  // フォームの値を格納するケースクラス
  case class UserForm(id: Option[Long], name: String, companyId: Option[Int])

  // formから送信されたデータ ⇔ ケースクラスの変換を行う
  val userForm = Form(
    mapping(
      "id"        -> optional(longNumber),
      "name"      -> nonEmptyText(maxLength = 20),
      "companyId" -> optional(number)
    )(UserForm.apply)(UserForm.unapply)
  )

}

class UserController @Inject()(components: MessagesControllerComponents)
  extends MessagesAbstractController(components) {
  import UserController._

  private val u = Users.syntax("u")
  private val c = Companies.syntax("c")

  /**
   * 一覧表示
   */
  def list = Action { implicit request =>
    DB.readOnly { implicit session =>
      // ユーザのリストを取得
      val users = withSQL {
        select.from(Users as u).leftJoin(Companies as c).on(u.companyId, c.id).orderBy(u.id.asc)
      }.map { rs =>
        (Users(u)(rs), rs.intOpt(c.resultName.id).map(_ => Companies(c)(rs)))
      }.list.apply()

      // 一覧画面を表示
      Ok(views.html.user.list(users))
    }
  }

  /**
   * 編集画面表示
   */
  def edit(id: Option[Long]) = Action { implicit request =>
    DB.readOnly { implicit session =>
      // リクエストパラメータにIDが存在する場合
      val form = id match {
        // IDが渡されなかった場合は新規登録フォーム
        case None => userForm
        // IDからユーザ情報を1件取得してフォームに詰める
        case Some(id) => Users.find(id) match {
          case Some(user) => userForm.fill(UserForm(Some(user.id), user.name, user.companyId))
          case None => userForm
        }
      }

      // プルダウンに表示する会社のリストを取得
      val companies = withSQL {
        select.from(Companies as c).orderBy(c.id.asc)
      }.map(Companies(c.resultName)).list().apply()

      Ok(views.html.user.edit(form, companies))
    }
  }

  /**
   * 登録実行
   */
  def create = Action { implicit request =>
    DB.localTx { implicit session =>
      // リクエストの内容をバインド
      userForm.bindFromRequest.fold(
        // エラーの場合
        error => {
          BadRequest(views.html.user.edit(error, Companies.findAll()))
        },
        // OKの場合
        form  => {
          // ユーザを登録
          Users.create(form.name, form.companyId)
          // 一覧画面へリダイレクト
          Redirect(routes.UserController.list)
        }
      )
    }
  }

  /**
   * 更新実行
   */
  def update = Action { implicit request =>
    DB.localTx { implicit session =>
      // リクエストの内容をバインド
      userForm.bindFromRequest.fold(
        // エラーの場合は編集画面に戻す
        error => {
          BadRequest(views.html.user.edit(error, Companies.findAll()))
        },
        // OKの場合は更新を行い一覧画面にリダイレクトする
        form => {
          // ユーザ情報を更新
          Users.find(form.id.get).foreach { account =>
            Users.save(account.copy(name = form.name, companyId = form.companyId))
          }
          // 一覧画面にリダイレクト
          Redirect(routes.UserController.list)
        }
      )
    }
  }

  /**
   * 削除実行
   */
  def remove(id: Long) = Action { implicit request =>
    DB.localTx { implicit session =>
      // ユーザを削除
      Users.find(id).foreach { user =>
        Users.destroy(user)
      }
      // 一覧画面へリダイレクト
      Redirect(routes.UserController.list)
    }
  }

}
