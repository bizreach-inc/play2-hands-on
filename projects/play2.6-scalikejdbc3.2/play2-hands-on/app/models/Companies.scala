package models

import scalikejdbc._

case class Companies(
  id: Int,
  name: String) {

  def save()(implicit session: DBSession = Companies.autoSession): Companies = Companies.save(this)(session)

  def destroy()(implicit session: DBSession = Companies.autoSession): Int = Companies.destroy(this)(session)

}


object Companies extends SQLSyntaxSupport[Companies] {

  override val schemaName = Some("PUBLIC")

  override val tableName = "COMPANIES"

  override val columns = Seq("ID", "NAME")

  def apply(c: SyntaxProvider[Companies])(rs: WrappedResultSet): Companies = apply(c.resultName)(rs)
  def apply(c: ResultName[Companies])(rs: WrappedResultSet): Companies = new Companies(
    id = rs.get(c.id),
    name = rs.get(c.name)
  )

  val c = Companies.syntax("c")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[Companies] = {
    withSQL {
      select.from(Companies as c).where.eq(c.id, id)
    }.map(Companies(c.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Companies] = {
    withSQL(select.from(Companies as c)).map(Companies(c.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(Companies as c)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[Companies] = {
    withSQL {
      select.from(Companies as c).where.append(where)
    }.map(Companies(c.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Companies] = {
    withSQL {
      select.from(Companies as c).where.append(where)
    }.map(Companies(c.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(Companies as c).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    id: Int,
    name: String)(implicit session: DBSession = autoSession): Companies = {
    withSQL {
      insert.into(Companies).namedValues(
        column.id -> id,
        column.name -> name
      )
    }.update.apply()

    Companies(
      id = id,
      name = name)
  }

  def batchInsert(entities: Seq[Companies])(implicit session: DBSession = autoSession): List[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'id -> entity.id,
        'name -> entity.name))
        SQL("""insert into COMPANIES(
        ID,
        NAME
      ) values (
        {id},
        {name}
      )""").batchByName(params: _*).apply[List]()
    }

  def save(entity: Companies)(implicit session: DBSession = autoSession): Companies = {
    withSQL {
      update(Companies).set(
        column.id -> entity.id,
        column.name -> entity.name
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: Companies)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(Companies).where.eq(column.id, entity.id) }.update.apply()
  }

}
