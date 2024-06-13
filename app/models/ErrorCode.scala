package models

class ErrorCode {
    val code: String = ""
    val title: String = ""
    val detail: Option[String] = None

    override def toString: String = s"""|(code:$code,title:$title,detail:$detail)|""".stripMargin

    override def equals(obj: Any): Boolean = {
        obj match {
            case errorCode: ErrorCode => errorCode.detail.equals(this.detail) && errorCode.code.equals(this.code) && errorCode.detail.equals(this.detail)
            case _ => false
        }
    }
}

object ErrorCode {
    def apply(codeValue: String, titleValue: String, detailValue: Option[String]): ErrorCode = new ErrorCode {
        override val code: String = codeValue
        override val title: String = titleValue
        override val detail: Option[String] = detailValue
    }

}
