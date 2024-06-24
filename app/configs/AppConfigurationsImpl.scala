package configs

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import com.qrsof.apptack.client.ApptackClientConfigurations
import com.qrsof.jwt.validation.JwksConfigs
import configs.AppEnvironment._

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

    override def appTackClientConfigurations: ApptackClientConfigurations = {
        new ApptackClientConfigurations {
            override def url: String = config.get[String]("apptack.url")

            override def appKey: String = config.get[String]("apptack.appKey")

            override def appSecret: String = config.get[String]("apptack.secretKey")
        }
    }

    override def digitalOceanConfigs: DigitalOceanConfigs = {
        DigitalOceanConfigs(
            region = config.get[String]("digitalocean.region"),
            secretKey = config.get[String]("digitalocean.secretKey"),
            accessKey = config.get[String]("digitalocean.accessKey"),
            bucketName = config.get[String]("digitalocean.bucketName"),
            schema = config.get[String]("db.schema")
        )
    }

    override def fileSystemConfigs: FileSystemConfigs = {
        FileSystemConfigs(
            schema = config.get[String]("db.schema")
        )
    }

    override def jwksConfigs: JwksConfigs = {
        new JwksConfigs {
            override def jwkUlr: String = oauthConfigs.jwksUrl
        }
    }
}
