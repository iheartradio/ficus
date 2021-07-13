package net.ceedubs.ficus

import org.specs2.mutable.Specification
import com.typesafe.config.{Config, ConfigFactory}
import Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import net.ceedubs.ficus.readers.EnumerationReader._
import net.ceedubs.ficus.readers.ValueReader

case class ServiceConfig(urls: Set[String], maxConnections: Int, httpsRequired: Boolean = false)

object Country extends Enumeration {
  val DE = Value("DE")
  val IT = Value("IT")
  val NL = Value("NL")
  val US = Value("US")
  val GB = Value("GB")
}

class ExampleSpec extends Specification {

  // an example config snippet for us to work with
  val config = ConfigFactory.parseString("""
      |services {
      |  users {
      |    urls = ["localhost:8001"]
      |    maxConnections = 100
      |    httpsRequired = true
      |  }
      |  analytics {
      |    urls = ["localhost:8002", "localhost:8003"]
      |    maxConnections = 25
      |  }
      |}
      |countries = [DE, US, GB]
    """.stripMargin)

  "Ficus config" should {
    "make Typesafe config more Scala-friendly" in {
      val userServiceConfig = config.as[Config]("services.users")
      userServiceConfig.as[Set[String]]("urls") must beEqualTo(Set("localhost:8001"))
      userServiceConfig.as[Int]("maxConnections") must beEqualTo(100)
      userServiceConfig.as[Option[Boolean]]("httpsRequired") must beSome(true)

      val analyticsServiceConfig        = config.as[Config]("services.analytics")
      analyticsServiceConfig.as[List[String]]("urls") must beEqualTo(List("localhost:8002", "localhost:8003"))
      val analyticsServiceRequiresHttps = analyticsServiceConfig.as[Option[Boolean]]("httpsRequired") getOrElse false
      analyticsServiceRequiresHttps must beFalse

      config.as[Seq[Country.Value]]("countries") must be equalTo Seq(Country.DE, Country.US, Country.GB)
    }

    "Automagically be able to hydrate arbitrary types from config" in {
      // Tkere are a few restrictions on types that can be read. See README file in root of project
      val analyticsConfig = config.as[ServiceConfig]("services.analytics")
      analyticsConfig.maxConnections must beEqualTo(25)
      // since this value isn't in the config, it will fall back to the default for the case class
      analyticsConfig.httpsRequired must beFalse
    }

    "Be easily extensible" in {
      // If we want a value reader that defaults httpsRequired to true instead of false (the default on the case
      // class) we can define a custom value reader for ServiceConfig
      implicit val serviceConfigReader: ValueReader[ServiceConfig] = ValueReader.relative { serviceConfig =>
        ServiceConfig(
          urls = serviceConfig.as[Set[String]]("urls"),
          maxConnections = serviceConfig.getInt("maxConnections"), // the old-fashioned way is fine too!
          httpsRequired = serviceConfig.as[Option[Boolean]]("httpsRequired") getOrElse true
        )
      }

      // so we don't have to add a "services." prefix for each service
      val servicesConfig = config.as[Config]("services")

      val analyticsServiceConfig: ServiceConfig = servicesConfig.as[ServiceConfig]("analytics")
      // the analytics service config doesn't define an "httpsRequired" value, but the serviceConfigReader defaults
      // to true if it is empty with its 'getOrElse true' on the extracted Option
      analyticsServiceConfig.httpsRequired must beTrue

      val userServiceConfig: ServiceConfig = servicesConfig.as[ServiceConfig]("users")

      val servicesMap = config.as[Map[String, ServiceConfig]]("services")
      servicesMap must beEqualTo(Map("users" -> userServiceConfig, "analytics" -> analyticsServiceConfig))
    }
  }
}
