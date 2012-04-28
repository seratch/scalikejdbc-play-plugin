package models

import java.util.Date

import scalikejdbc._

case class NewTask(
  folder: String, 
  project: Long, 
  title: String, 
  done: Boolean, 
  dueDate: Option[Date], 
  assignedTo: Option[String]
)

case class Task(
  id: Long, 
  folder: String,
  project: Long,
  title: String,
  done: Boolean,
  dueDate: Option[Date],
  assignedTo: Option[String]
)

object Task {
  
  // -- Parsers
  
  /**
   * Parse a Task from a ResultSet
   */
  val simple = (rs: WrappedResultSet) => Task(
     rs.long("id"), 
     rs.string("folder"), 
     rs.long("project"), 
     rs.string("title"), 
     rs.boolean("done"), 
     Option(rs.timestamp("due_date")), 
     Option(rs.string("assigned_to"))
   )
  
  val withProject = (rs: WrappedResultSet) => (
    Task(
      rs.long("task.id"),
      rs.string("task.folder"), 
      rs.long("task.project"), 
      rs.string("task.title"), 
      rs.boolean("task.done"), 
      Option(rs.timestamp("task.due_date")), 
      Option(rs.string("task.assigned_to"))
    ), 
    Project(
      rs.long("project.id"), 
      rs.string("project.folder"), 
      rs.string("project.name")
    )
  )
  
  // -- Queries
  
  /**
   * Retrieve a Task from the id.
   */
  def findById(id: Long): Option[Task] = {
    DB readOnly { implicit session =>
      SQL("select * from task where id = ?").bind(id).map(simple).single.apply()
    }
  }
  
  /**
   * Retrieve todo tasks for the user.
   */
  def findTodoInvolving(user: String): Seq[(Task,Project)] = {
    DB readOnly { implicit session =>
      SQL(
        """
          select * from task 
          join project_member on project_member.project_id = task.project 
          join project on project.id = project_member.project_id
          where task.done = false and project_member.user_email = ?
        """
      ).bind(user).map(withProject).list.apply().toSeq
    }
  }
  
  /**
   * Find tasks related to a project
   */
  def findByProject(project: Long): Seq[Task] = {
    DB readOnly { implicit session =>
      SQL(
        """
          select * from task 
          where task.project = ?
        """
      ).bind(project).map(simple).list.apply().toSeq
    }
  }

  /**
   * Delete a task
   */
  def delete(id: Long) {
    DB localTx { implicit session =>
      SQL("delete from task where id = ?").bind(id).update.apply()
    }
  }
  
  /**
   * Delete all task in a folder.
   */
  def deleteInFolder(projectId: Long, folder: String) {
    DB localTx { implicit session =>
      SQL("delete from task where project = ? and folder = ?")
        .bind(projectId, folder).update.apply()
    }
  }
  
  /**
   * Mark a task as done or not
   */
  def markAsDone(taskId: Long, done: Boolean) {
    DB localTx { implicit session =>
      SQL("update task set done = ? where id = ?").bind(taskId, done).update.apply()
    }
  }
  
  /**
   * Rename a folder.
   */
  def renameFolder(projectId: Long, folder: String, newName: String) {
    DB localTx { implicit session =>
      SQL("update task set folder = ? where folder = ? and project = ?")
        .bind(folder, newName, projectId).update.apply()
    }
  }
  
  /**
   * Check if a user is the owner of this task
   */
  def isOwner(task: Long, user: String): Boolean = {
    DB readOnly { implicit session =>
      SQL(
        """
          select count(task.id) = 1 as v from task 
          join project on task.project = project.id 
          join project_member on project_member.project_id = project.id 
          where project_member.user_email = ? and task.id = ?
        """
      ).bind(user, task).map(rs => rs.boolean("v")).single.apply().getOrElse(false)
    }
  }

  /**
   * Create a Task.
   */
  def create(task: NewTask): Task = {
    DB localTx { implicit session =>
      val newId = SQL("select next value for task_seq as v from dual").map(rs => rs.long("v")).single.apply().get
      SQL(
        """
          insert into task (id, folder, project, title, done, due_date, assigned_to) values (
            ?, ?, ?, ?, ?, ?, ?
          )
        """
      ).bind(
        newId,
        task.folder, 
        task.project, 
        task.title, 
        task.done,
        task.dueDate,
        task.assignedTo
      ).update.apply()

      Task(
        id = newId,
        folder = task.folder,
        project = task.project,
        title = task.title,
        done = task.done,
        dueDate = task.dueDate,
        assignedTo = task.assignedTo
      )
    }
  }
  
}
