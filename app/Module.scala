import com.google.inject.{AbstractModule, Provides}
import com.qrsof.jwt.validation.{JwksConfigs, JwtValidationService, JwtValidationServiceImpl, RSAJwksKeyProvider, RSAKeyProvider}
import configs.{AppConfigurations, AppConfigurationsImpl}
import controllers.api.auth.{AuthController, AuthImplementation}
import controllers.api.chat._

import javax.inject._
import net.codingwell.scalaguice.ScalaModule
import play.api.{Configuration, Environment}


class Module(environment: Environment, configuration: Configuration) extends AbstractModule with ScalaModule {

    override def configure(): Unit = {

        bind[JwtValidationService].to[JwtValidationServiceImpl].in[Singleton]()
        bind[RSAKeyProvider].to[RSAJwksKeyProvider].in[Singleton]()

        bind[AppConfigurations].to[AppConfigurationsImpl].in[Singleton]()

        bind[ChatController].to[ChatImplementation].in[Singleton]()
        bind[AuthController].to[AuthImplementation].in[Singleton]()

//        install(new FileSystemGuiceModule())
    }

    val configs = new AppConfigurationsImpl(configuration)

    @Provides
    def jwkConfigs(): JwksConfigs = {
        new JwksConfigs {
            override def jwkUlr: String = configs.oauthConfigs.jwksUrl
        }
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