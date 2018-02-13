package models

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import scalikejdbc._


class CompaniesSpec extends Specification with settings.DBSettings {

  "Companies" should {

    val c = Companies.syntax("c")

    "find by primary keys" in new AutoRollback {
      val maybeFound = Companies.find(1)
      maybeFound.isDefined should beTrue
    }
    "find by where clauses" in new AutoRollback {
      val maybeFound = Companies.findBy(sqls.eq(c.id, 1))
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollback {
      val allResults = Companies.findAll()
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollback {
      val count = Companies.countAll()
      count should be_>(0L)
    }
    "find all by where clauses" in new AutoRollback {
      val results = Companies.findAllBy(sqls.eq(c.id, 1))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollback {
      val count = Companies.countBy(sqls.eq(c.id, 1))
      count should be_>(0L)
    }
    "create new record" in new AutoRollback {
      val created = Companies.create(id = 100, name = "MyString")
      created should not beNull
    }
    "save a record" in new AutoRollback {
      val entity = Companies.findAll().head
      // TODO modify something
      val modified = entity.copy(name = "test")
      val updated = Companies.save(modified)
      updated should not equalTo(entity)
    }
    "destroy a record" in new AutoRollback {
      val created = Companies.create(id = 100, name = "MyString")
      val maybeFound = Companies.findBy(sqls.eq(c.id, 100))
      val deleted = Companies.destroy(maybeFound.get) == 1
      deleted should beTrue
      val shouldBeNone = Companies.find(100)
      shouldBeNone.isDefined should beFalse
    }
    "perform batch insert" in new AutoRollback {
      (0 to 100).foreach { i =>
        val created = Companies.create(id = 100 + i, name = "MyString")
      }

      val entities = Companies.findAllBy(sqls.ge(c.id, 100))
      entities.foreach(e => Companies.destroy(e))
      val batchInserted = Companies.batchInsert(entities)
      batchInserted.size should be_>(0)
    }
  }

}
