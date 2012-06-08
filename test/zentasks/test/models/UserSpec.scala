package models

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import play.api.Play.current

class UserSpec extends Specification {

  "User" should {
    "have #create and #findByEmail" in {
      running(FakeApplication()) {
        val user = User.findByEmail("seratch@gmail.com").getOrElse {
          User.create(
            User(
              email = "seratch@gmail.com", 
              name = "seratch",
              password = "play20"
            )
          )
        }
        user.name must equalTo("seratch")
      }
    }
  }

}

