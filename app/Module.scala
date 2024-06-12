import com.google.inject.{AbstractModule, Provides}
import controllers.api.auth.{AuthController, AuthImplementation}
import controllers.api.chat._
import com.qrsof.libs.filesystem.{FileSystemGuiceModule, StorageConfig}
import jakarta.inject.Named

import javax.inject._
import net.codingwell.scalaguice.ScalaModule
import play.api.db.Database
import play.api.{Configuration, Environment}


class Module(environment: Environment, configuration: Configuration) extends AbstractModule with ScalaModule {

    override def configure(): Unit = {
        bind[ChatController].to[ChatImplementation].in[Singleton]()
        bind[AuthController].to[AuthImplementation].in[Singleton]()

//        install(new FileSystemGuiceModule())
    }

//    @Provides
//    @Named("jdbc-properties")
//    def provideStorageConfig(database: Database): StorageConfig = {
//        new StorageConfig(
//            database.getConnection(),
//            "chaapy"
//        )
//    }
}