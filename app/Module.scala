import com.google.inject.{AbstractModule, Provides}

import javax.inject._
import net.codingwell.scalaguice.ScalaModule
import play.api.{Configuration, Environment}
import com.qrsof.apptack.client.impl.ApptackModule
import com.qrsof.apptack.client.ApptackClientConfigurations
import com.qrsof.jwt.validation.{JwksConfigs, JwtValidationService, JwtValidationServiceImpl, RSAJwksKeyProvider, RSAKeyProvider}
import configs.{AppConfigurations, AppConfigurationsImpl}
import controllers.api.auth.{AuthController, AuthImplementation}
import controllers.api.chat._
import controllers.api.onboarding.{OnboardingController, OnboardingImplementation}

class Module(environment: Environment, configuration: Configuration) extends AbstractModule with ScalaModule {

    override def configure(): Unit = {

        bind[JwtValidationService].to[JwtValidationServiceImpl].in[Singleton]()
        bind[RSAKeyProvider].to[RSAJwksKeyProvider].in[Singleton]()

        bind[AppConfigurations].to[AppConfigurationsImpl].in[Singleton]()

        bind[AuthController].to[AuthImplementation].in[Singleton]()
        bind[ChatController].to[ChatImplementation].in[Singleton]()
        bind[OnboardingController].to[OnboardingImplementation].in[Singleton]()

        install(new ApptackModule)

//        install(new FileSystemGuiceModule())
    }

    val configs = new AppConfigurationsImpl(configuration)

    @Provides
    def jwkConfigs(): JwksConfigs = {
        configs.jwksConfigs
    }

    @Provides
    def apptackClientConfigurations: ApptackClientConfigurations = {
        configs.appTackClientConfigurations
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