package project.serializer

import org.json4s.jackson

trait Serialization {

  implicit val serialization: jackson.Serialization.type = jackson.Serialization

}