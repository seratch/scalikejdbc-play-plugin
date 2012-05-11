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
import scalikejdbc._

/**
 * The Play plugin to use ScalikeJDBC
 */
class PlayPlugin(app: Application) extends Plugin {

  private lazy val config = app.configuration.getConfig("db").getOrElse(Configuration.empty)

  private def configValueOptional(name: String, key: String): Option[String] = {
    config.getString(name + "." + key)
  }

  private def configValue(name: String, key: String): String = {
    config.getString(name + "." + key) getOrElse {
      throw config.reportError(name, "Missing configuration [db." + name + "." + key + "]")
    }
  }

  config.subKeys map { name =>

    def load(name: String): (String, String, String, ConnectionPoolSettings) = {
      // load the jdbc driver
      configValueOptional(name, "driver") map { driver => Class.forName(driver) }

      val url = configValue(name, "url")
      val user = configValueOptional(name, "user") getOrElse ("")
      val password = configValueOptional(name, "password") getOrElse ("")

      val defaultSettings = new ConnectionPoolSettings
      val poolInitialSize: Int = configValueOptional(name, "poolInitialSize") map (v => v.toInt) getOrElse (defaultSettings.initialSize)
      val poolMaxSize: Int = configValueOptional(name, "poolMaxSize") map (v => v.toInt) getOrElse (defaultSettings.maxSize)
      val poolValidationQuery = configValueOptional(name, "poolValidationQuery") getOrElse (defaultSettings.validationQuery)
      val settings = new ConnectionPoolSettings(
        initialSize = poolInitialSize,
        maxSize = poolMaxSize,
        validationQuery = poolValidationQuery)
      (url, user, password, settings)
    }

    name match {
      case "global" =>
        // loading GlobalSettings
        val globalLoggingSQLAndTime = "global.loggingSQLAndTime"
        configValueOptional(globalLoggingSQLAndTime, "enabled").map(_.toBoolean) map { enabled =>
          val default = new LoggingSQLAndTimeSettings
          GlobalSettings.loggingSQLAndTime = new LoggingSQLAndTimeSettings(
            enabled = enabled,
            logLevel = configValueOptional(globalLoggingSQLAndTime, "logLevel").map(v => Symbol(v)).getOrElse(default.logLevel),
            warningEnabled = configValueOptional(globalLoggingSQLAndTime, "warningEnabled").map(_.toBoolean).getOrElse(default.warningEnabled),
            warningThresholdMillis = configValueOptional(globalLoggingSQLAndTime, "warningThresholdMillis").map(_.toLong).getOrElse(default.warningThresholdMillis),
            warningLogLevel = configValueOptional(globalLoggingSQLAndTime, "warningLogLevel").map(v => Symbol(v)).getOrElse(default.warningLogLevel)
          )
        }
      case "default" =>
        val (url, user, password, settings) = load(name)
        ConnectionPool.singleton(url, user, password, settings)
      case _ =>
        val (url, user, password, settings) = load(name)
        ConnectionPool.add(Symbol(name), url, user, password, settings)
    }

  }

}

