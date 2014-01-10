package net.invalidkeyword

import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class MyServiceActor extends Actor with MyService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}


// this trait defines our service behavior independently from the service actor
trait MyService extends HttpService {

  val data :List[Service] = List(
    Service("Core",List(
      Depends("Identity","a label"),
      Depends("SIM-User"),
      Depends("MarkLogic"),
      Depends("Track"),
      Depends("Token")
    )),
    Service("Identity", List(
      Depends("MySQL")
    )),
    Service("SIM-User", List(
      Depends("MySQL")
    )),
    Service("MySQL"),
    Service("MarkLogic"),
    Service("Track",List(
      Depends("Cassandra")
    )),
    Service("Cassandra"),
    Service("Token",List(
      Depends("SIM-User")
    ))
  )

  def services = {
    "\"services\":[" + data.map(s => "{\"id\":\"" + s.name + "\", \"healthy\":\"" + (math.random > 0.25) + "\", \"value\": { \"label\":\"" + s.name +  "\" }}").mkString(",") + "]"
  }

  def edges = {
    val edge = for {
      service <- data
      dependencies <- service.depends
    } yield "{ \"u\": \"" + service.name + "\",     \"v\": \"" + dependencies.on + "\",     \"value\": { \"label\": \"" + dependencies.label + "\" } }"

    "\"edges\":[" + edge.mkString(",") + "]"
  }

  val myRoute =
    path("") {
      get {
        respondWithMediaType(`text/html`) { // XML is marshalled to `text/xml` by default, so we simply override here
          complete {
            <html>
              <body>
                <h1>Say hello to <i>spray-routing</i> on <i>spray-can</i>!</h1>
              </body>
            </html>
          }

        }
      }
    }~
    path("services") {
      get {
        complete {
           s"{${services},${edges}}"
        }
      }
    }~
    pathPrefix("static") {
      getFromDirectory("src/main/resources/static/")
    }
}

case class Service(name: String, depends:List[Depends]=List())
case class Depends(on:String, label:String = "")
