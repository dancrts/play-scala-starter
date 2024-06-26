package services.files

import com.qrsof.libs.storage.{NewResource, ResourceKey, StorageService, UpdateResource}
import io.scalaland.chimney.dsl.into
import org.apache.pekko.http.scaladsl.common.StrictForm.FileData
import services.files.dto.{FileKey, FileResource, UpdatedFileResource}

import scala.jdk.OptionConverters._
import java.io.ByteArrayInputStream
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class FileManagementService @Inject()(storageService: StorageService)(implicit ex: ExecutionContext) {

    def save(folder: String, fileData: FileData): Future[Option[FileKey]] = Future {
        val byteArrayInputStreamFileData = new ByteArrayInputStream(fileData.entity.data.toArray)

        println(fileData)

        val newResource = new NewResource(
            byteArrayInputStreamFileData,
            s"workspace/$folder",
            fileData.filename.get
        )
        Some(storageService.save(newResource).into[FileKey].withFieldComputed(_.key, _.key()).transform)
    }

    def get(resourceKey: String): Future[Option[FileResource]] = Future {
        val maybeResource = storageService.retrieve(new ResourceKey(resourceKey)).toScala

        println(maybeResource)

        maybeResource match {
            case Some(resource) =>
                val originalName: String = resource.originalName
                val contentType = originalName.split('.').tail.head
                Some(
                    FileResource(
                        key = resource.key,
                        content = resource.content,
                        name = resource.originalName,
                        contentType = contentType,
                        path = resource.path()
                    )
                )
            case None => None
        }
    }

    def update(updateFileResource: UpdatedFileResource): Future[Option[FileKey]] = Future {
        val byteArrayInputStreamFileData = new ByteArrayInputStream(updateFileResource.newContent.entity.data.toArray)
        val key = updateFileResource.oldResourceKey
        val updateResource = new UpdateResource(
            key,
            byteArrayInputStreamFileData,
            updateFileResource.newContent.filename.get
        )
        Some(storageService.update(updateResource).into[FileKey].withFieldConst(_.key, key).transform)
    }

    def delete(resourceKey: String): Future[FileKey] = Future {
        storageService.delete(new ResourceKey(resourceKey))
        FileKey(resourceKey)
    }
}
