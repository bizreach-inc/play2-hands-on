package models

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import scalikejdbc._


class UsersSpec extends Specification {

  "Users" should {

    val u = Users.syntax("u")

    "find by primary keys" in new AutoRollback {
      val maybeFound = Users.find(1L)
      maybeFound.isDefined should beTrue
    }
    "find by where clauses" in new AutoRollback {
      val maybeFound = Users.findBy(sqls.eq(u.id, 1L))
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollback {
      val allResults = Users.findAll()
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollback {
      val count = Users.countAll()
      count should be_>(0L)
    }
    "find all by where clauses" in new AutoRollback {
      val results = Users.findAllBy(sqls.eq(u.id, 1L))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollback {
      val count = Users.countBy(sqls.eq(u.id, 1L))
      count should be_>(0L)
    }
    "create new record" in new AutoRollback {
      val created = Users.create(name = "MyString")
      created should not beNull
    }
    "save a record" in new AutoRollback {
      val entity = Users.findAll().head
      // TODO modify something
      val modified = entity
      val updated = Users.save(modified)
      updated should not equalTo(entity)
    }
    "destroy a record" in new AutoRollback {
      val entity = Users.findAll().head
      val deleted = Users.destroy(entity) == 1
      deleted should beTrue
      val shouldBeNone = Users.find(1L)
      shouldBeNone.isDefined should beFalse
    }
    "perform batch insert" in new AutoRollback {
      val entities = Users.findAll()
      entities.foreach(e => Users.destroy(e))
      val batchInserted = Users.batchInsert(entities)
      batchInserted.size should be_>(0)
    }
  }

}
