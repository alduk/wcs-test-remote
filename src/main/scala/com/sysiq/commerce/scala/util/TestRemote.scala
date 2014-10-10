package com.sysiq.commerce.scala.util

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration.DurationInt
import scala.concurrent.ExecutionContext.Implicits.global
import com.typesafe.config.ConfigFactory
import com.typesafe.config.Config
import java.net._


object TestRemote extends App {
  val path = "akka.tcp://WCSSystem@saas-gen-dev-01.kiev.ecofabric.com:6666/user/proxy"

  class TestActor extends Actor with ActorLogging {

    val registries = context.actorSelection(path)
    println(registries)
    def receive = {
      case "test" => sender ! "ok"
      case x => registries.forward(x)
    }
  }

  val hostName = InetAddress.getLocalHost.getCanonicalHostName
  
  println(s"hostname=$hostName")

  val hostConf = ConfigFactory.parseString(s"akka.remote.netty.hostname = $hostName")

  implicit val system = ActorSystem("testRemote", ConfigFactory.load("client").withFallback(hostConf))

  implicit val timeout = Timeout(5.seconds)

  val testActor = system.actorOf(Props[TestActor])

  println(testActor)
  println(testActor.path.address)

  val f1 = testActor ? "test"

  f1 onComplete { x =>
    println(x)
  }
  
  val f2 = testActor ? "test2"

  f2 onComplete { x =>
    println(x)
  }
  /*  val f2 = testActor ? Command

  val f3 = f1.zip(f2)

  f3 onComplete { x =>
    println(x)
  }

  val ak = system.actorSelection(path)
  println(ak)
  val f4 = ak ? Command
  f4 onComplete { x =>
    println(x)
  }*/
}