//object Tables extends{ // or just use object demo.Tables, which is hard-wired to the driver stated during generation
//  val profile = scala.slick.driver.H2Driver  
//} with demo.Tables
//import Tables._
//import Tables.profile.simple._

object Example extends App {  
//  // connection info for a pre-populated throw-away, in-memory db for this demo, which is freshly initialized on every run
//  val url = "jdbc:h2:mem:test;INIT=runscript from 'src/main/sql/create.sql'"
//  val db = Database.forURL(url, driver = "org.h2.Driver")
//
//  // Using generated code. Our Build.sbt makes sure they are generated before compilation.
//  val q = Companies.join(Computers).on(_.id === _.manufacturerId)
//                   .map{ case (co,cp) => (co.name, cp.name) }
//
//  db.withTransaction { implicit session =>
//    println( q.run.groupBy{ case (co,cp) => co }
//                  .mapValues(_.map{ case (co,cp) => cp })
//                  .mkString("\n")
//    )
//  }
}
