package bring.me.home.data

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import bring.me.home.data.Section.{FirstSection, IntermediateSection, LastSection}
import bring.me.home.data.Station.{IntermediateStation, NamelessStation}
import io.circe.{Decoder, HCursor}

case class HereResponse(connections: Seq[Connection])

case class Connection(sections: Seq[Section]) {
  override def toString: String =
    sections.map(_.toString).mkString(
      """
        |~~~~~~~~~~~~~~~~~
        |""".stripMargin)
}

trait Section

object Section {

  case class FirstSection(departure: NamelessStation, transport: Transport, arrival: IntermediateStation) extends Section {
    override def toString: String =
      s"""\uD83D\uDEEB *Start*: ${DateTimeFormatter.ISO_LOCAL_TIME.format(departure.time)}
         |
         |$transport
         |
         |*To*: ${arrival.name}""".stripMargin
  }

  case class IntermediateSection(departure: IntermediateStation, transport: Transport, arrival: IntermediateStation) extends Section {
    override def toString: String =
      s"""*From*: ${departure.name}
         |
         |\u23F0 ${DateTimeFormatter.ISO_LOCAL_TIME.format(departure.time)}
         |$transport
         |
         |*To*: ${arrival.name}""".stripMargin
  }

  case class LastSection(departure: IntermediateStation, transport: Transport, arrival: NamelessStation) extends Section {
    override def toString: String =
      s"""*From*: ${departure.name}
         |
         |$transport
         |
         |\uD83D\uDEEC *Finish*: ${DateTimeFormatter.ISO_LOCAL_TIME.format(arrival.time)}""".stripMargin
  }

}

trait Transport

object Transport {

  val WalkingMode = 20

  case object Walking extends Transport {
    override def toString: String =
      """Walking""".stripMargin
  }

  case class Vehicle(name: String, direction: String) extends Transport {
    override def toString: String =
      s"""*Transport*: $name
         |*Direction*: $direction""".stripMargin
  }

}

trait Station

object Station {

  case class NamelessStation(time: ZonedDateTime) extends Station

  case class IntermediateStation(time: ZonedDateTime, name: String) extends Station

}

object HereResponse {

  private implicit val decodeVehicleTransport: Decoder[Transport.Vehicle] = (c: HCursor) => for {
    name <- c.downField("name").as[String]
    direction <- c.downField("dir").as[String]
  } yield Transport.Vehicle(name, direction)

  private implicit val decodeTransport: Decoder[Transport] = (c: HCursor) => {
    val transport = c.downField("Transport")
    for {
      mode <- transport.downField("mode").as[Int]
      result <- mode match {
        case Transport.WalkingMode => Right(Transport.Walking)
        case _ => transport.as[Transport.Vehicle]
      }
    } yield result
  }

  private implicit val decodeStation: Decoder[Station] = (c: HCursor) => for {
    timeStr <- c.downField("time").as[String]
    time = ZonedDateTime.parse(timeStr, DateTimeFormatter.ISO_ZONED_DATE_TIME)

    station = c.downField("Stn")
    nameOpt <- station.downField("name").as[Option[String]]
  } yield nameOpt.map(name => IntermediateStation(time, name)).getOrElse(NamelessStation(time))

  private implicit val decodeSection: Decoder[Section] = (c: HCursor) => for {
    departure <- c.downField("Dep").as[Station]
    arrival <- c.downField("Arr").as[Station]
    transport <- c.downField("Dep").as[Transport]
  } yield (departure, arrival) match {
    case (dep: NamelessStation, arr: IntermediateStation) => FirstSection(dep, transport, arr)
    case (dep: IntermediateStation, arr: IntermediateStation) => IntermediateSection(dep, transport, arr)
    case (dep: IntermediateStation, arr: NamelessStation) => LastSection(dep, transport, arr)
    case (_, _) => throw new IllegalArgumentException("Contents of Section is invalid")
  }

  private implicit val decodeConnection: Decoder[Connection] = (c: HCursor) => {
    val sectionsData = c.downField("Sections")
    for {
      sections <- sectionsData.downField("Sec").as[Seq[Section]]
    } yield Connection(sections)
  }

  implicit val decodeHereResponse: Decoder[HereResponse] = (c: HCursor) => {
    val res = c.downField("Res")
    val connectionData = res.downField("Connections")
    for {
      connections <- connectionData.downField("Connection").as[Seq[Connection]]
    } yield HereResponse(connections)
  }

}