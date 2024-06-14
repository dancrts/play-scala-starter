package configs

import com.typesafe.config.{Config, ConfigFactory}
import configs.AppEnvironment._

class AppConfigurationsImpl extends AppConfigurations {

    private val config: Config = ConfigFactory.defaultApplication().resolve()

    override def dbSchemaName: String = config.getString("db.schema")

    override def environment: AppEnvironment = {
        if (config.hasPath("app.environment")) {
            val env = config.getString("app.environment")
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
        override def jwksUrl: String = config.getString("oauth.jwksUrl")
    }
}
