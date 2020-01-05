package models

import org.scalatest._
import scalikejdbc.scalatest.AutoRollback
import scalikejdbc._


class UsersSpec extends fixture.FlatSpec with Matchers with AutoRollback {
  // 初期化処理を追加
  config.DBs.setup()

  val u = Users.syntax("u")

  behavior of "Users"

  it should "find by primary keys" in { implicit session =>
    val maybeFound = Users.find(1L)
    maybeFound.isDefined should be(true)
  }
  it should "find by where clauses" in { implicit session =>
    val maybeFound = Users.findBy(sqls.eq(u.id, 1L))
    maybeFound.isDefined should be(true)
  }
  it should "find all records" in { implicit session =>
    val allResults = Users.findAll()
    allResults.size should be >(0)
  }
  it should "count all records" in { implicit session =>
    val count = Users.countAll()
    count should be >(0L)
  }
  it should "find all by where clauses" in { implicit session =>
    val results = Users.findAllBy(sqls.eq(u.id, 1L))
    results.size should be >(0)
  }
  it should "count by where clauses" in { implicit session =>
    val count = Users.countBy(sqls.eq(u.id, 1L))
    count should be >(0L)
  }
  it should "create new record" in { implicit session =>
    val created = Users.create(name = "MyString")
    created should not be(null)
  }
  it should "save a record" in { implicit session =>
    val entity = Users.findAll().head
    // nameを変更
    val modified = entity.copy(name = "modify")
    val updated = Users.save(modified)
    updated should not equal(entity)
  }
  it should "destroy a record" in { implicit session =>
    val entity = Users.findAll().head
    val deleted = Users.destroy(entity)
    deleted should be(1)
    val shouldBeNone = Users.find(1L)
    shouldBeNone.isDefined should be(false)
  }
  it should "perform batch insert" in { implicit session =>
    val entities = Users.findAll()
    entities.foreach(e => Users.destroy(e))
    val batchInserted = Users.batchInsert(entities)
    batchInserted.size should be >(0)
  }
}
