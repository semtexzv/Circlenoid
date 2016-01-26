package com.semtexzv.circlenoid2

import com.badlogic.gdx.physics.box2d.{ContactImpulse, Manifold, Contact, ContactListener}
import com.semtexzv.circlenoid2.objects.{Ball, AxialBlock, EntityType, Entity}
/**
  * Created by Semtexzv on 1/24/2016.
  */
class BallContactListener extends ContactListener{
  override def postSolve(contact: Contact, impulse: ContactImpulse): Unit = {
  }
  var oldBlock : AxialBlock = null
  override def endContact(contact: Contact): Unit = {
    var A  = contact.getFixtureA().getBody().getUserData().asInstanceOf[Entity]
    var B  = contact.getFixtureB().getBody().getUserData().asInstanceOf[Entity]
    if(A!=null && B!=null) {
      if (A.typ == EntityType.Block && B.typ == EntityType.Ball) {
        val t = A
        A = B
        B = t
      }
      if (A.typ == EntityType.Ball && B.typ == EntityType.Block) {
        var ball = A.asInstanceOf[Ball]
        var block = B.asInstanceOf[AxialBlock]
        block.hit=true
        ball.body.setLinearVelocity(ball.body.getLinearVelocity.nor().scl(Manager.Speed))
      }
    }

  }

  override def beginContact(contact: Contact): Unit = {


  }

  override def preSolve(contact: Contact, oldManifold: Manifold): Unit = {

  }
}
