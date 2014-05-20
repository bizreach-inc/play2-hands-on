package models
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = scala.slick.driver.H2Driver
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: scala.slick.driver.JdbcProfile
  import profile.simple._
  import scala.slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import scala.slick.jdbc.{GetResult => GR}
  
  /** DDL for all tables. Call .create to execute. */
  lazy val ddl = Companies.ddl ++ Users.ddl
  
  /** Entity class storing rows of table Companies
   *  @param id Database column ID PrimaryKey
   *  @param name Database column NAME  */
  case class CompaniesRow(id: Int, name: String)
  /** GetResult implicit for fetching CompaniesRow objects using plain SQL queries */
  implicit def GetResultCompaniesRow(implicit e0: GR[Int], e1: GR[String]): GR[CompaniesRow] = GR{
    prs => import prs._
    CompaniesRow.tupled((<<[Int], <<[String]))
  }
  /** Table description of table COMPANIES. Objects of this class serve as prototypes for rows in queries. */
  class Companies(tag: Tag) extends Table[CompaniesRow](tag, "COMPANIES") {
    def * = (id, name) <> (CompaniesRow.tupled, CompaniesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, name.?).shaped.<>({r=>import r._; _1.map(_=> CompaniesRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID PrimaryKey */
    val id: Column[Int] = column[Int]("ID", O.PrimaryKey)
    /** Database column NAME  */
    val name: Column[String] = column[String]("NAME")
  }
  /** Collection-like TableQuery object for table Companies */
  lazy val Companies = new TableQuery(tag => new Companies(tag))
  
  /** Entity class storing rows of table Users
   *  @param id Database column ID AutoInc, PrimaryKey
   *  @param name Database column NAME 
   *  @param companyId Database column COMPANY_ID  */
  case class UsersRow(id: Long, name: String, companyId: Option[Int])
  /** GetResult implicit for fetching UsersRow objects using plain SQL queries */
  implicit def GetResultUsersRow(implicit e0: GR[Long], e1: GR[String], e2: GR[Option[Int]]): GR[UsersRow] = GR{
    prs => import prs._
    UsersRow.tupled((<<[Long], <<[String], <<?[Int]))
  }
  /** Table description of table USERS. Objects of this class serve as prototypes for rows in queries. */
  class Users(tag: Tag) extends Table[UsersRow](tag, "USERS") {
    def * = (id, name, companyId) <> (UsersRow.tupled, UsersRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, name.?, companyId).shaped.<>({r=>import r._; _1.map(_=> UsersRow.tupled((_1.get, _2.get, _3)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column ID AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("ID", O.AutoInc, O.PrimaryKey)
    /** Database column NAME  */
    val name: Column[String] = column[String]("NAME")
    /** Database column COMPANY_ID  */
    val companyId: Column[Option[Int]] = column[Option[Int]]("COMPANY_ID")
    
    /** Foreign key referencing Companies (database name IDX_USERS_FK0) */
    lazy val companiesFk = foreignKey("IDX_USERS_FK0", companyId, Companies)(r => r.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Restrict)
  }
  /** Collection-like TableQuery object for table Users */
  lazy val Users = new TableQuery(tag => new Users(tag))
}