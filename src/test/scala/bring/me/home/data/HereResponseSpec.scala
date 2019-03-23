package bring.me.home.data

import java.time.{ZoneOffset, ZonedDateTime}

import bring.me.home.data.Section.{FirstSection, IntermediateSection, LastSection}
import bring.me.home.data.Station.{IntermediateStation, NamelessStation}
import bring.me.home.data.Transport.{Vehicle, Walking}
import io.circe.parser.decode
import org.scalatest.{FlatSpec, Matchers}

import scala.io.Source

class HereResponseSpec extends FlatSpec with Matchers {

  "response decoder" should "produce HereResponse" in {
    val json = Source.fromFile(getClass.getClassLoader.getResource("directions.json").toURI).mkString

    val result = decode[HereResponse](json)

    result shouldBe Right(HereResponse(List(Connection(List(
      FirstSection(
        NamelessStation(ZonedDateTime.of(2019, 3, 23, 3, 8, 0, 0, ZoneOffset.ofHours(1))),
        Walking,
        IntermediateStation(
          ZonedDateTime.of(2019, 3, 23, 3, 18, 0, 0, ZoneOffset.ofHours(1)),
          "S+U Alexanderplatz [U8]"
        )
      ),
      IntermediateSection(
        IntermediateStation(
          ZonedDateTime.of(2019, 3, 23, 3, 18, 0, 0, ZoneOffset.ofHours(1)),
          "S+U Alexanderplatz [U8]"
        ),
        Vehicle("U8", "S+U Wittenau"),
        IntermediateStation(
          ZonedDateTime.of(2019, 3, 23, 3, 25, 0, 0, ZoneOffset.ofHours(1)),
          "S+U Gesundbrunnen Bhf"
        )
      ),
      IntermediateSection(
        IntermediateStation(
          ZonedDateTime.of(2019, 3, 23, 3, 32, 0, 0, ZoneOffset.ofHours(1)),
          "S+U Gesundbrunnen Bhf"
        ),
        Vehicle("S42", "S+U Hermannstr."),
        IntermediateStation(
          ZonedDateTime.of(2019, 3, 23, 3, 34, 0, 0, ZoneOffset.ofHours(1)),
          "S+U Wedding"
        )
      ),
      LastSection(
        IntermediateStation(
          ZonedDateTime.of(2019, 3, 23, 3, 34, 0, 0, ZoneOffset.ofHours(1)),
          "S+U Wedding"
        ),
        Walking,
        NamelessStation(ZonedDateTime.of(2019, 3, 23, 3, 42, 0, 0, ZoneOffset.ofHours(1)))
      )
    )))))
  }

}
