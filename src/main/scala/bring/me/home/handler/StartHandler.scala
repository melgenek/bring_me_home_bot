package bring.me.home.handler

import com.bot4s.telegram.api.RequestHandler
import com.bot4s.telegram.api.declarative.Messages
import com.bot4s.telegram.models.Message
import slogging.StrictLogging

import scala.concurrent.Future

class StartHandler(implicit val client: RequestHandler) extends Messages with StrictLogging {

  val onStart: Message => Future[_] = { implicit message: Message =>
    logger.debug(s"Start was called. User: ${message.from}")
    reply(
      """
        |Hi! I will help you get home!
        |Please send me your home location.
      """.stripMargin)

  }

}
