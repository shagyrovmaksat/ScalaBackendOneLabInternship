package project.domain.dto

import project.domain.Photo

case class HistoryItemDto(
                           filter : String,
                           responce : Option[Seq[Photo]],
                           exception: Option[Throwable]
                         )