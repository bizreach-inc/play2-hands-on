package settings

import scalikejdbc._

trait DBSettings {
  DBSettings.initialize()
}

object DBSettings {

  private var isInitialized = false

  def initialize(): Unit = this.synchronized {
    if(isInitialized) return

    config.DBsWithEnv("development").setupAll()
    isInitialized = true
    
  }
}

