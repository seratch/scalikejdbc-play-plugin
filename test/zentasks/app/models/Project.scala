package models

import scalikejdbc._

case class Project(id: Option[Long], folder: String, name: String)

object Project {
  
  // -- Queries
  private val simple = (rs: WrappedResultSet) => Project(
    Option(rs.long("id")), 
    rs.string("folder"), 
    rs.string("name")
  )
    
  /**
   * Retrieve a Project from id.
   */
  def findById(id: Long): Option[Project] = {
    DB readOnly { implicit session =>
      SQL("select * from project where id = ?").bind(id).map(simple).single.apply()
    }
  }
  
  /**
   * Retrieve project for user
   */
  def findInvolving(user: String): Seq[Project] = {
    DB readOnly { implicit session =>
      SQL(
        """
          select * from project 
          join project_member on project.id = project_member.project_id 
          where project_member.user_email = ?
        """
      ).bind(user).map(simple).list.apply().toSeq
    }
  }
  
  /**
   * Update a project.
   */
  def rename(id: Long, newName: String) {
    DB localTx { implicit session =>
      SQL("update project set name = ? where id = ?").bind(id, newName).update.apply()
    }
  }
  
  /**
   * Delete a project.
   */
  def delete(id: Long) {
    DB localTx { implicit session => 
      SQL("delete from project where id = ?").bind(id).update.apply()
    }
  }
  
  /**
   * Delete all project in a folder
   */
  def deleteInFolder(folder: String) {
    DB localTx { implicit session => 
      SQL("delete from project where folder = ?").bind(folder).update.apply()
    }
  }
  
  /**
   * Rename a folder
   */
  def renameFolder(folder: String, newName: String) {
    DB localTx { implicit session =>
      SQL("update project set folder = ? where folder = ?").bind(folder, newName).update.apply()
    }
  }
  
  /**
   * Retrieve project member
   */
  def membersOf(project: Long): Seq[User] = {
    DB readOnly { implicit session =>
      SQL(
        """
          select user.* from user 
          join project_member on project_member.user_email = user.email 
          where project_member.project_id = ?
        """
      ).bind(project).map(User.simple).list.apply().toSeq
    }
  }
  
  /**
   * Add a member to the project team.
   */
  def addMember(project: Long, user: String) {
    DB localTx { implicit session =>
      SQL("insert into project_member values(?, ?)").bind(project, user).map(simple).update.apply()
    }
  }
  
  /**
   * Remove a member from the project team.
   */
  def removeMember(project: Long, user: String) {
    DB localTx { implicit session =>
      SQL("delete from project_member where project_id = ? and user_email = ?").bind(project, user).update.apply()
    }
  }
  
  /**
   * Check if a user is a member of this project
   */
  def isMember(project: Long, user: String): Boolean = {
    DB readOnly { implicit session =>
      SQL(
        """
          select count(user.email) = 1 as is_member from user 
          join project_member on project_member.user_email = user.email 
          where project_member.project_id = ? and user.email = ?
        """
      ).bind(project, user).map(rs => rs.boolean("is_member")).single.apply().getOrElse(false)
    }
  }
   
  /**
   * Create a Project.
   */
  def create(project: Project, members: Seq[String]): Project = {
     DB localTx { implicit session =>
       // Insert the project
       val id: Option[Long] = project.id.orElse {
         SQL("select next value for project_seq as v from dual").map(rs => rs.long("v")).single.apply()
       }
       SQL(
         """
           insert into project (id, name, folder) values (
             ?, ?, ? 
           )
         """
       ).bind(id, project.name, project.folder).update.apply()
       // Add members
       members.foreach { email =>
         SQL("insert into project_member values (?, ?)").bind(id, email).update.apply()
       }
       project.copy(id = id)
     }
  }
  
}
