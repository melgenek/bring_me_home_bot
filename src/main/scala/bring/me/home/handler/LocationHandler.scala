package bring.me.home.handler

import java.time._

import bring.me.home.client.HereClient
import bring.me.home.model.{Location, User}
import bring.me.home.storage.UserStorage
import com.bot4s.telegram.api.RequestHandler
import com.bot4s.telegram.api.declarative.Messages
import com.bot4s.telegram.methods.ParseMode
import com.bot4s.telegram.models
import com.bot4s.telegram.models.Message
import slogging.StrictLogging

import scala.concurrent.{ExecutionContext, Future}

class LocationHandler(userStorage: UserStorage, hereClient: HereClient)
                     (implicit val client: RequestHandler, ec: ExecutionContext) extends Messages with StrictLogging {

  val onLocation: Message => Future[_] = { implicit message: Message =>
    logger.debug(s"Message from user ${message.from}")

    (message.location, message.from) match {
      case (Some(location), Some(user)) =>
        for {
          savedUserOpt <- userStorage.find(user.id)
          _ <- savedUserOpt
            .map(findDirections(location, _))
            .getOrElse(createNewUser(location, user))
        } yield ()
      case _ => Future.unit
    }
  }

  private def findDirections(location: models.Location, savedUser: User)
                            (implicit message: Message): Future[Unit] = {
    for {
      response <- hereClient.directions(
        Location(location.latitude, location.longitude),
        savedUser.homeLocation,
        ZonedDateTime.ofInstant(Instant.ofEpochSecond(message.date), ZoneOffset.UTC)
      )
      connectionOpt = response.connections.headOption
      responseString = connectionOpt
        .map(_.toString)
        .getOrElse(
          """
            |Sorry, I wasn't able to find any routes.
          """.stripMargin
        )
      _ <- reply(responseString, parseMode = Some(ParseMode.Markdown))
    } yield ()
  }

  private def createNewUser(location: models.Location, user: models.User)
                           (implicit message: Message): Future[Unit] =
    for {
      _ <- userStorage.create(User(user.id, Location(location.latitude, location.longitude)))
      _ <- reply(
        """
          |Your home location was saved.
          |Now you can search for directions.
        """.stripMargin)
    } yield ()

}
