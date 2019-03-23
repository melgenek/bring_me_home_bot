package bring.me.home.storage

import bring.me.home.model.{User, UserModel}
import slick.jdbc.{JdbcBackend, JdbcProfile}

import scala.concurrent.{ExecutionContext, Future}

trait UserStorage {

  def create(user: User): Future[Unit]

  def find(id: Int): Future[Option[User]]

}


class SlickUserStorage(val profile: JdbcProfile, db: JdbcBackend.Database)
                      (implicit ec: ExecutionContext) extends UserStorage with UserModel {

  import profile.api._

  override def create(user: User): Future[Unit] =
    db.run {
      (users += user).map(_ => ())
    }

  override def find(id: Int): Future[Option[User]] =
    db.run {
      users.filter(_.id === id.bind).result.headOption
    }

}
