package bring.me.home

import slogging.LogLevel.TRACE
import slogging.{LoggerConfig, SLF4JLoggerFactory}

object App {

  def main(args: Array[String]): Unit = {
    LoggerConfig.factory = SLF4JLoggerFactory()
    LoggerConfig.level = TRACE

    new Wiring().wireAndRun()
  }

}
