package bring.me.home.model

import slick.jdbc.JdbcProfile

case class User(id: Int, homeLocation: Location)

case class Location(lat: Double, lng: Double)


trait UserModel {

  val profile: JdbcProfile

  import profile.api._

  class Users(t: Tag) extends Table[User](t, "users") {
    def id = column[Int]("id", O.PrimaryKey)

    def lat = column[Double]("lat", O.Unique)

    def lng = column[Double]("lng", O.Unique)

    def location = (lat, lng) <> (Location.tupled, Location.unapply)

    def * = (id, location) <> (User.tupled, User.unapply)
  }

  val users = TableQuery[Users]

}
