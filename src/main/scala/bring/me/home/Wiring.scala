package bring.me.home

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import bring.me.home.client.HereClient
import bring.me.home.config.AppConfig
import bring.me.home.handler.{LocationHandler, StartHandler}
import bring.me.home.storage.SlickUserStorage
import com.bot4s.telegram.api.declarative.CommandImplicits
import com.bot4s.telegram.clients.AkkaHttpClient
import com.typesafe.config.ConfigFactory
import slick.jdbc.{JdbcBackend, JdbcProfile, PostgresProfile}
import slogging.StrictLogging

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{Await, ExecutionContextExecutor, Future}

class Wiring extends StrictLogging with CommandImplicits {

  def wireAndRun(): Unit = {
    ConfigFactory.invalidateCaches()
    val config = ConfigFactory.load()
    val appConfig = AppConfig.fromTypeSafeConfig(config)

    implicit val system: ActorSystem = ActorSystem()
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    implicit val client: AkkaHttpClient = new AkkaHttpClient(appConfig.telegram.token)
    val bot = new Bot(appConfig.server.port)

    val startHandler = new StartHandler

    val profile: JdbcProfile = PostgresProfile
    val db: JdbcBackend.DatabaseDef = JdbcBackend.Database.forConfig("db")
    val userStorage = new SlickUserStorage(profile, db)
    val hereClient = new HereClient(appConfig.here)
    val locationHandler = new LocationHandler(userStorage, hereClient)

    val toUnit: Future[_] => Unit = future2Unit(appConfig.server.timeout)
    bot.onCommand("start")(startHandler.onStart andThen toUnit)
    bot.onMessage(locationHandler.onLocation andThen toUnit)

    logger.info("Starting bot")
    bot.run()
  }

  private def future2Unit(timeout: FiniteDuration)(f: Future[_]): Unit = {
    Await.result(f, timeout)
  }

}
