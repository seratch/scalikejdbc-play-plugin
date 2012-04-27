/*
 * Copyright 2012 Kazuhiro Sera
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package scalikejdbc

import play.api._
import play.api.db.BoneCPPlugin

import java.sql.Connection
import scalikejdbc._

/**
 * The Play plugin to use ScalikeJDBC
 */
class PlayPlugin(app: Application) extends BoneCPPlugin(app) {

  override def onStart() {}

  private lazy val config = app.configuration.getConfig("db").getOrElse(Configuration.empty)

  private def configValue(name: String, key: String): String = {
    config.getString(name + "." + key) getOrElse {
      throw config.reportError(name, "Missing configuration [db." + name + "." + key + "]")
    }
  }

  config.subKeys map { name =>
    val url = configValue(name, "url")
    val user = configValue(name, "user")
    val password = configValue(name, "password")
    name match {
      case "default" => ConnectionPool.singleton(url, user, password)
      case _ => ConnectionPool.add(Symbol(name), url, user, password)
    }
  }

}

