package models

case class Response(key: String, detail: Option[String])

object Response {
    def apply(key: String, detail: Option[String]): Response = new Response(key, detail)

    def unapply(response: Response): Option[(String, Option[String])] = Some((response.key, response.detail))
}
