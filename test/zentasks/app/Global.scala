import play.api._

import models._

object Global extends GlobalSettings {
  
  override def onStart(app: Application) {
    InitialData.createTables()
    InitialData.insert()
  }
  
}

/**
 * Initial set of data to be imported 
 * in the sample application.
 */
object InitialData {
  
  def date(str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(str)

  def createTables() = {

    import scalikejdbc._
    val ddl = """
drop table user if exists;
create table user (
  email                     varchar(255) not null primary key,
  name                      varchar(255) not null,
  password                  varchar(255) not null
);

drop table project if exists;
create table project (
  id                        bigint not null primary key,
  name                      varchar(255) not null,
  folder                    varchar(255) not null
);

drop sequence project_seq if exists;
create sequence project_seq start with 1000;

drop table project_member if exists;
create table project_member (
  project_id                bigint not null,
  user_email                varchar(255) not null,
  foreign key(project_id)   references project(id) on delete cascade,
  foreign key(user_email)   references user(email) on delete cascade
);

drop table task if exists;
create table task (
  id                        bigint not null primary key,
  title                     varchar(255) not null,
  done                      boolean,
  due_date                  timestamp,
  assigned_to               varchar(255),
  project                   bigint not null,
  folder                    varchar(255),
  foreign key(assigned_to)  references user(email) on delete set null,
  foreign key(project)      references project(id) on delete cascade
);

drop sequence task_seq if exists;
create sequence task_seq start with 1000;
"""

    DB autoCommit { implicit session =>
      try {
        SQL("select * from user").map(rs => rs).list.apply()
      } catch { case e => 
        SQL(ddl).execute.apply()
      }
    }

  }
  
  def insert() = {
    
    if(User.findAll.isEmpty) {
      
      Seq(
        User("guillaume@sample.com", "Guillaume Bort", "secret"),
        User("maxime@sample.com", "Maxime Dantec", "secret"),
        User("sadek@sample.com", "Sadek Drobi", "secret"),
        User("erwan@sample.com", "Erwan Loisant", "secret")
      ).foreach(User.create)
      
      Seq(
        Project(Some(1), "Play framework", "Play 2.0") -> Seq("guillaume@sample.com", "maxime@sample.com", "sadek@sample.com", "erwan@sample.com"),
        Project(Some(2), "Play framework", "Play 1.2.4") -> Seq("guillaume@sample.com", "erwan@sample.com"),
        Project(Some(3), "Play framework", "Website") -> Seq("guillaume@sample.com", "maxime@sample.com"),
        Project(Some(4), "Zenexity", "Secret project") -> Seq("guillaume@sample.com", "maxime@sample.com", "sadek@sample.com", "erwan@sample.com"),
        Project(Some(5), "Zenexity", "Playmate") -> Seq("maxime@sample.com"),
        Project(Some(6), "Personal", "Things to do") -> Seq("guillaume@sample.com"),
        Project(Some(7), "Zenexity", "Play samples") -> Seq("guillaume@sample.com", "maxime@sample.com"),
        Project(Some(8), "Personal", "Private") -> Seq("maxime@sample.com"),
        Project(Some(9), "Personal", "Private") -> Seq("guillaume@sample.com"),
        Project(Some(10), "Personal", "Private") -> Seq("erwan@sample.com"),
        Project(Some(11), "Personal", "Private") -> Seq("sadek@sample.com")
      ).foreach {
        case (project,members) => Project.create(project, members)
      }

      Seq(
        Task(Some(1), "Todo", 1, "Fix the documentation", false, None, Some("guillaume@sample.com")),
        Task(Some(2), "Urgent", 1, "Prepare the beta release", false, Some(date("2011-11-15")), None),
        Task(Some(3), "Todo", 9, "Buy some milk", false, None, None),
        Task(Some(4), "Todo", 2, "Check 1.2.4-RC2", false, Some(date("2011-11-18")), Some("guillaume@sample.com")),
        Task(Some(5), "Todo", 7, "Finish zentask integration", true, Some(date("2011-11-15")), Some("maxime@sample.com")),
        Task(Some(6), "Todo", 4, "Release the secret project", false, Some(date("2012-01-01")), Some("sadek@sample.com"))
      ).foreach(Task.create)
      
    }
    
  }
  
}
