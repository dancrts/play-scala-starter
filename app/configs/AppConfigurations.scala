package configs

trait AppConfigurations {

        def dbSchemaName: String

        def environment: AppEnvironment

        def oauthConfigs: OauthConfigs

//        def appTackClientConfigurations: ApptackClientConfigurations
//
//        def digitalOceanConfigs: DigitalOceanConfigs
//
//        def fileSystemConfigs: FileSystemConfigs

}
