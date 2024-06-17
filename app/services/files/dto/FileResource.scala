package services.files.dto

import java.io.InputStream

case class FileResource(
                           key: String,
                           content: InputStream,
                           name: String,
                           contentType: String,
                           path: String
                       )

