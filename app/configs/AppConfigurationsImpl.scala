package configs

import com.typesafe.config.{Config, ConfigFactory}
import configs.AppEnvironment._
import play.api.Configuration

import javax.inject.{Inject, Singleton}

@Singleton
class AppConfigurationsImpl @Inject()(config: Configuration) extends AppConfigurations {

    override def dbSchemaName: String = config.get[String]("db.schema")

    override def environment: AppEnvironment = {
        if (config.has("app.environment")) {
            println("HasEnv")
            val env = config.get[String]("app.environment")
            env match {
                case "production" => Production
                case "sandbox" => Sandbox
                case "development" => Development
                case "test" => Test
                case _ => throw new RuntimeException("Environment not found")
            }
        } else {
            Development
        }
    }

    override def oauthConfigs: OauthConfigs = new OauthConfigs {
        override def jwksUrl: String = config.get[String]("oauth.jwksUrl")
    }
}
