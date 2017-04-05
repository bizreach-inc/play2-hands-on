package models

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import scalikejdbc._


class CompaniesSpec extends Specification {

  "Companies" should {

    val c = Companies.syntax("c")

    "find by primary keys" in new AutoRollback {
      val maybeFound = Companies.find(123)
      maybeFound.isDefined should beTrue
    }
    "find by where clauses" in new AutoRollback {
      val maybeFound = Companies.findBy(sqls.eq(c.id, 123))
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
      val results = Companies.findAllBy(sqls.eq(c.id, 123))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollback {
      val count = Companies.countBy(sqls.eq(c.id, 123))
      count should be_>(0L)
    }
    "create new record" in new AutoRollback {
      val created = Companies.create(id = 123, name = "MyString")
      created should not beNull
    }
    "save a record" in new AutoRollback {
      val entity = Companies.findAll().head
      // TODO modify something
      val modified = entity
      val updated = Companies.save(modified)
      updated should not equalTo(entity)
    }
    "destroy a record" in new AutoRollback {
      val entity = Companies.findAll().head
      val deleted = Companies.destroy(entity) == 1
      deleted should beTrue
      val shouldBeNone = Companies.find(123)
      shouldBeNone.isDefined should beFalse
    }
    "perform batch insert" in new AutoRollback {
      val entities = Companies.findAll()
      entities.foreach(e => Companies.destroy(e))
      val batchInserted = Companies.batchInsert(entities)
      batchInserted.size should be_>(0)
    }
  }

}
