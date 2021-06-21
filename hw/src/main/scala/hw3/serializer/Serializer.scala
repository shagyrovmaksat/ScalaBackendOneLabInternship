package hw3.serializer

import hw3.domain.Book
import org.json4s.jackson.Serialization
import org.json4s.{Formats, ShortTypeHints}

trait Serializer extends Serialization {

  implicit val formats: Formats = Serialization.formats(
    ShortTypeHints(
      List(
        classOf[Book],
      )
    )
  )

}