package services.files.dto

import org.apache.pekko.http.scaladsl.common.StrictForm.FileData

case class UpdatedFileResource(
                                  oldResourceKey: String,
                                  newContent: FileData,
                                  newFolder: String
                              )
