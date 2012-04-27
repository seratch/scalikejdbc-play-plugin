package models

import scalikejdbc._

case class User(email: String, name: String, password: String)

object User {
  
  // -- Parsers
  
  /**
   * Parse a User from a ResultSet
   */
  val simple = (rs: WrappedResultSet) => User(
    rs.string("user.email"), 
    rs.string("user.name"), 
    rs.string("user.password")
  )
  
  // -- Queries
  
  /**
   * Retrieve a User from email.
   */
  def findByEmail(email: String): Option[User] = {
    DB readOnly { implicit session =>
      SQL("select * from user where email = ?").bind(email).map(simple).single.apply()
    }
  }
  
  /**
   * Retrieve all users.
   */
  def findAll: Seq[User] = {
    DB readOnly { implicit session =>
      SQL("select * from user").map(simple).list.apply().toSeq
    }
  }
  
  /**
   * Authenticate a User.
   */
  def authenticate(email: String, password: String): Option[User] = {
    DB readOnly { implicit session =>
      SQL(
        """
         select * from user where 
         email = ? and password = ?
        """
      ).bind(email, password).map(simple).single.apply()
    }
  }
   
  /**
   * Create a User.
   */
  def create(user: User): User = {
    DB readOnly { implicit session =>
      SQL(
        """
          insert into user values (
            ?, ?, ?
          )
        """
      ).bind(user.email, user.name, user.password).update.apply()
      user
    }
  }
  
}
