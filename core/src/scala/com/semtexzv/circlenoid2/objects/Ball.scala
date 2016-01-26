package com.semtexzv.circlenoid2.objects

import com.badlogic.gdx.graphics.g2d.{SpriteBatch, TextureRegion}
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.{FixtureDef, BodyDef, CircleShape, World}
import com.semtexzv.circlenoid2.Manager

import scala.util.Random

/**
  * Created by Semtexzv on 1/24/2016.
  */
class Ball(world: World) extends  Entity(EntityType.Ball)
{
  final var radius = 0.3f
  var region: TextureRegion = Manager.ballRegion
  var shape = new CircleShape()

  shape.setPosition(new Vector2(0,0))
  shape.setRadius(radius)

  var bdef = new BodyDef()
  bdef.active = true
  bdef.fixedRotation =true
  bdef.position.set(0,0)
  bdef.`type` = BodyType.DynamicBody

  body = world.createBody(bdef)

  var fixt = new FixtureDef()
  fixt.filter.categoryBits = 2
  fixt.filter.maskBits =1
  fixt.shape = shape;
  fixt.density = 0.1f
  fixt.restitution = 0.8f
  fixt.friction = 0.3f

  body.createFixture(fixt)
  body.setUserData(this)

  def render(batch: SpriteBatch ): Unit = {
    body.setLinearVelocity(body.getLinearVelocity.nor().scl(Manager.Speed))

    val pos = body.getPosition
    batch.draw(region,pos.x-radius,pos.y-radius,radius*2,radius*2)
  }
  def destroy(): Unit ={
    world.destroyBody(body)
  }
}
