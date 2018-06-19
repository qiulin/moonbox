package moonbox.catalyst.adapter.jdbc

import java.util.Properties

object JDBCUtils {
  val MONGO_URL_PREFIX: String = "jdbc:mongo://"
  val ES_URL_PREFIX: String = "jdbc:es://"
  //  val MYSQL_URL_PREFIX: String = "jdbc:mysql://"
  //  val ORACLE_URL_PREFIX: String = "jdbc:oracle://"
  val OTHER_URL_PREFIX: String = "jdbc:other://"
  val EXECUTOR_KEY = "executor"
  val HOST_PORT_KEY = "nodes"
  val DATABASE_KEY = "database"
  val TABLE_KEY = "table"
  val HOST_KEY = "host"
  val PORT_KEY = "port"

  val urlPrefixes = Seq(MONGO_URL_PREFIX, ES_URL_PREFIX, OTHER_URL_PREFIX)

  def parseURL(url: String, defaults: Properties): Properties = {
    val resProps = new Properties()
    if (defaults != null) {
      resProps.putAll(defaults)
    }
    if (url != null && urlPrefixes.exists(v => url.toUpperCase().startsWith(v.toUpperCase))) {
      val props = url.substring(url.indexOf("//") + 2).split("\\?")
      val hpAndDB = props(0).split("/")
      resProps.setProperty(HOST_PORT_KEY, hpAndDB(0))
      if (hpAndDB.length == 2) {
        val dbAndTable = hpAndDB(1).split("\\.")
        resProps.setProperty(DATABASE_KEY, dbAndTable(0))
        if (dbAndTable.length == 2) resProps.setProperty(TABLE_KEY, dbAndTable(1))
      }
      if (props.length == 2) {
        props(1).split("&").map { kv: String =>
          val temp = kv.split("=")
          if (temp.length == 2)
            (temp(0).toLowerCase(), temp(1))
          else null
        }.filter(_ != null).foreach(kv => resProps.setProperty(kv._1, kv._2))
      }
    }
    resProps
  }

  /**
    *
    * @param hostsAndPorts e.g. host1:port1,host2:port2,host3:port3
    * @return e.g. [(host1,port1),(host2,port2),(host3,port3)] or null
    */
  def parseHostsAndPorts(hostsAndPorts: String): Seq[(String, String)] = {
    if (hostsAndPorts != null && hostsAndPorts.length > 0)
      hostsAndPorts.split(",").map { hp =>
        val h_p = hp.split(":")
        if (h_p.length == 2) {
          (h_p(0).trim, h_p(1).trim)
        } else null
      }.filter(_ != null).toSeq
    else null
  }

}
