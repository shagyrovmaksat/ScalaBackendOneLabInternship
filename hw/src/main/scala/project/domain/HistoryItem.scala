package project.domain

case class HistoryItem(
                        id : String,
                        filter : String,
                        responce : Option[Seq[Photo]],
                        exception : Option[Throwable]
                      )
