package com.semtexzv.circlenoid2


/**
  * Created by Semtexzv on 1/24/2016.
  */
object GameState extends  Enumeration(0)
{
  type GameState = Value
  val Menu,Running,Paused,Lost = Value
}
