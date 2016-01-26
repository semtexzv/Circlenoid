package com.semtexzv.circlenoid2.objects

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.{TextureRegion, SpriteBatch}
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d._
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef
import com.semtexzv.circlenoid2.{Consts, Manager}
import com.semtexzv.circlenoid2.objects.EntityType.EntityType

/**
  * Created by Semtexzv on 1/23/2016.
  */
object EntityType extends  Enumeration(0)
{
  type EntityType = Value
  val Ball,Block, PowerUp = Value
}
abstract class Entity(var typ: EntityType ) {
  var body: Body = null
}



