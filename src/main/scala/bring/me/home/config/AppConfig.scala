package bring.me.home.config

import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._

import scala.concurrent.duration.FiniteDuration

case class AppConfig(server: ServerConfig, telegram: TelegramConfig, here: HereConfig)

case class ServerConfig(port: Int, timeout: FiniteDuration)

case class TelegramConfig(token: String)

case class HereConfig(app: HereApp)

case class HereApp(id: String, code: String)

object AppConfig {

  def fromTypeSafeConfig(config: Config): AppConfig = config.as[AppConfig]

}
