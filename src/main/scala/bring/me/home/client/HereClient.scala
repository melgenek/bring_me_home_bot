package bring.me.home.client

import java.time.ZonedDateTime

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import bring.me.home.config.HereConfig
import bring.me.home.data.HereResponse
import bring.me.home.model.Location
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport

import scala.concurrent.{ExecutionContext, Future}

class HereClient(config: HereConfig)
                (implicit actorSystem: ActorSystem, ec: ExecutionContext, materializer: Materializer) extends FailFastCirceSupport {

  private val http = Http()

  def directions(departure: Location, arrival: Location, time: ZonedDateTime): Future[HereResponse] = {
    val request = HttpRequest(uri = Uri(HereClient.BaseUri).withQuery(Query(
      "app_id" -> config.app.id,
      "app_code" -> config.app.code,
      "dep" -> s"${departure.lat},${departure.lng}",
      "arr" -> s"${arrival.lat},${arrival.lng}",
      "time" -> time.toString,
      "max" -> 1.toString,
      "strict" -> 1.toString,
      "details" -> 0.toString,
      "routingMode" -> "realtime"
    )))

    http.singleRequest(request).flatMap(r => Unmarshal(r.entity).to[HereResponse])
  }

}

object HereClient {

  final val BaseUri = "https://transit.api.here.com/v3/route.json"

}
