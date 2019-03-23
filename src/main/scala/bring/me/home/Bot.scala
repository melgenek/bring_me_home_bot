package bring.me.home

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import com.bot4s.telegram.api.declarative.{Commands, InlineQueries}
import com.bot4s.telegram.api.{AkkaTelegramBot, BotBase, RequestHandler, WebRoutes}
import com.bot4s.telegram.models.Update

import scala.util.control.NonFatal

class Bot(val port: Int)(implicit val client: RequestHandler) extends AkkaTelegramBot
  with BotBase with Commands with WebRoutes with Directives with InlineQueries {

  import com.bot4s.telegram.marshalling.AkkaHttpMarshalling._
  import com.bot4s.telegram.marshalling._

  override def routes: Route = pathEndOrSingleSlash {
    entity(as[Update]) { update =>
      try {
        logger.trace("Got message: " + update.toString)
        receiveUpdate(update)
      } catch {
        case NonFatal(e) =>
          logger.error("Caught exception in update handler", e)
      }
      complete(StatusCodes.OK)
    }
  } ~ super.routes

}

