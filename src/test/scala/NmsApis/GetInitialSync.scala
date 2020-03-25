package src.test.scala.NmsApis

import io.gatling.core.scenario.Simulation
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import scala.concurrent.duration._

class GetInitialSync extends Simulation{

  private def getProperty(propertyName: String,defaultValue: String): String ={

    Option(System.getenv(propertyName))
      .orElse(Option(System.getProperty(propertyName)))
      .getOrElse(defaultValue)
  }

  def csvFilePath=getProperty("CsvPath", "csvPath")


  val httpConf=http.baseUrl("http://10.157.48.241:32452/").userAgentHeader("Apache-HttpClient/4.5.5 (Java/1.8.0_144)")

  val csvFile=csv(csvFilePath).circular

  val headers= Map("X-User-Id"->"${userId}","X-Device-Key"->"${deviceKey}","Accept-Language"->"en",
  "Content-Type"->"application/json","limit"->"50","Authorization"->"Basic ${authkey}","X-Shard-Key"->"${shardkey}",
  "X-Api-Key"->"c153b48e-d8a1-48a0-a40d-293f1dc5be0e","X-App-Secret"->"ODc0MDE2M2EtNGY0MC00YmU2LTgwZDUtYjNlZjIxZGRkZjlj")


  def nmsInitialSyncApis(): ChainBuilder ={
    feed(csvFile).exec(http("Nms APis").get("nms/sync/initial").headers(Map("X-User-Id"->"${userId}","X-Device-Key"->"${deviceKey}","Accept-Language"->"en",
      "Content-Type"->"application/json","limit"->"50","Authorization"->"Basic ${authKey}","X-Shard-Key"->"${shardKey}",
      "X-Api-Key"->"c153b48e-d8a1-48a0-a40d-293f1dc5be0e","X-App-Secret"->"ODc0MDE2M2EtNGY0MC00YmU2LTgwZDUtYjNlZjIxZGRkZjlj")).check(status.in(200))
      .check(bodyString.saveAs("responseBody")).check(jsonPath("$.objects[0].objectKey").saveAs("objectKey")))
      //.exec { session =>

        //println(println(session("objectKey").as[String]))
        //println(println(session("userid").as[String]))
        //println(println(session("responseBody").as[String]));
       // session
      //}
  }
  val scn=scenario("Nms Initial Sync Api").exec(nmsInitialSyncApis())

  setUp(
   scn.inject(constantUsersPerSec(1 ) during(30 minutes)).protocols(httpConf)).maxDuration(40 minutes)

  //scn.inject(atOnceUsers(50)).protocols(httpConf))

}
