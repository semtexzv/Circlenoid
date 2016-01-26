package com.semtexzv.circlenoid2

import aurelienribon.tweenengine.{TweenManager, Tween}
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.{Matrix4, Vector2}
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d._
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef
import com.semtexzv.circlenoid2.objects.{BlockLayer, Ball}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.util.Random

/**
  * Created by Semtexzv on 1/23/2016.
  */
class Level
{
  final val LayerCount = 3
  var world = new World(new Vector2(0,0),false)
  world.setContactListener(new BallContactListener)
  var ground: Body = null
  var renderer = new Box2DDebugRenderer()
  var balls: ArrayBuffer[Ball] = new mutable.ArrayBuffer[Ball]
  var batch: SpriteBatch = new SpriteBatch()
  var layers: Array[BlockLayer] = new Array[BlockLayer](LayerCount)

  var rotSpeed = 0f

  var tweenmgr : TweenManager = new TweenManager

  var left: Boolean = false
  var right: Boolean = false
  var started = false
  def start(): Unit = {
    if (!started) {
      started = true
      balls.foreach(a => if (a != null) a.body.setLinearVelocity(Random.nextFloat(), Random.nextFloat()))
    }
  }
  def lost = balls.forall(a => a != null && a.body.getPosition.len() > 10 )

  def set(): Unit ={
    clear()
    val gndDef = new BodyDef()
    gndDef.fixedRotation = false
    gndDef.position.set(0,0)
    gndDef.`type` = BodyType.StaticBody
    ground = world.createBody(gndDef)

    val shape = new CircleShape
    shape.setPosition(new Vector2(0,0))
    shape.setRadius(10)

    val fdef = new FixtureDef
    fdef.shape = shape
    fdef.density = 100
    fdef.filter.maskBits = 0
    fdef.filter.categoryBits = 0
    ground.createFixture(fdef)
    shape.dispose()

    balls += new Ball(world)
    for (i<- 0 until LayerCount) {
      layers(i) = new BlockLayer(world, ground, i)
    }
  }
  def clear(): Unit ={
    started = false
    for (i<- 0 until LayerCount) {
      if(layers(i)!=null){
        layers(i).destroy()
        layers(i) = null
      }
    }
    balls.foreach(_.destroy())
    balls.clear()
  }

  def render(delta:Float): Unit ={
    if(delta < 0.5f && Manager.game.state == GameState.Running){
      world.step(delta,3,3)
      tweenmgr.update(delta)
    }
    if (Gdx.input.isKeyPressed(Keys.LEFT) || left) {
      rotSpeed = math.min(rotSpeed + 0.2, 2.2).toFloat
      start()
    }
    else if (Gdx.input.isKeyPressed(Keys.RIGHT) || right) {
      rotSpeed = math.max(rotSpeed - 0.2, -2.2).toFloat
      start()
    }
    else
    {
      rotSpeed *= 0.90f;
    }
    val camera = Manager.gamePort.getCamera
    batch.setProjectionMatrix(camera.projection)
    batch.setTransformMatrix(camera.view)
    batch.begin()
    balls.foreach(a => a.render(batch))
    batch.end()
    Manager.program.begin()

    for (i<-0 until LayerCount) {
      var old = layers(i)
      if (old != null) {
        old.center.setAngularVelocity(old.origRotSpeed + rotSpeed)
        old.render(Manager.program, camera.combined)
        if (old.blocks.forall(_ == null)) {
          layers(i) = null

          old.destroy()
          Manager.layerDestroyed()

          layers.foreach(a => {
            if (a != null) {
              if (a.indexOrbit > old.indexOrbit) {
                a.indexOrbit -= 1
                Tween.to(a, 1, 3).target(a.indexOrbit).setUserData(a).start(tweenmgr)
              }
            }
          })
          /*Generate new layer*/
          var maxIndex = layers.maxBy((a) => if (a != null) a.indexOrbit else 0).indexOrbit
          var layer = new BlockLayer(world, ground, 15)
          layer.indexOrbit = maxIndex+1
          //todo, progressive layer speeds
          layer.origRotSpeed = Random.nextFloat()*Manager.RingSpeed * (if(Random.nextBoolean()){1}else{-1})

          Tween.to(layer, 1, 3).target(layer.indexOrbit).start(tweenmgr)
          layers(layers.indexOf(null)) = layer
        }
      }
    }
    Manager.program.end()
  }


}