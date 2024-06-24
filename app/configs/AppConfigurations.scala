package configs

import com.qrsof.apptack.client.ApptackClientConfigurations
import com.qrsof.jwt.validation.JwksConfigs

trait AppConfigurations {

        def dbSchemaName: String

        def environment: AppEnvironment

        def oauthConfigs: OauthConfigs

        def appTackClientConfigurations: ApptackClientConfigurations

        def digitalOceanConfigs: DigitalOceanConfigs

        def fileSystemConfigs: FileSystemConfigs

        def jwksConfigs: JwksConfigs

}
