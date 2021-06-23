package project.serializer

import project.domain.{HistoryItem, Photo, UrlsItem}
import org.json4s.jackson.Serialization
import org.json4s.{Formats, ShortTypeHints}

trait Serializer extends SerializerWithoutTypeHints {

  override implicit val formats: Formats = Serialization.formats(
    ShortTypeHints(
      List(
        classOf[HistoryItem],
        classOf[Photo],
        classOf[UrlsItem]
      )
    )
  )

}
