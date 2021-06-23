package project.serializer

import org.json4s.{Formats, NoTypeHints}
import org.json4s.jackson.Serialization

trait SerializerWithoutTypeHints extends Serialization {

  implicit val formats: Formats = Serialization.formats(
    NoTypeHints
  )

}
