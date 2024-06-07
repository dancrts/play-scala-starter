import com.google.inject.AbstractModule
import controllers.api.auth.{AuthController, AuthImplementation}
import controllers.api.chat._

import javax.inject._
import net.codingwell.scalaguice.ScalaModule
import play.api.{Configuration, Environment}

class Module(environment: Environment, configuration: Configuration) extends AbstractModule with ScalaModule {

    override def configure(): Unit = {
        bind[ChatController].to[ChatImplementation].in[Singleton]()
        bind[AuthController].to[AuthImplementation].in[Singleton]()
    }
}