package com.semtexzv.circlenoid2.objects

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.joints.{PrismaticJointDef, RevoluteJointDef, DistanceJointDef, WeldJointDef}
import com.badlogic.gdx.physics.box2d._
import com.semtexzv.circlenoid2.{Manager, Consts}

import scala.util.Random

/**
  * Created by Semtexzv on 1/23/2016.
  * */
class AxialBlock(world: World, var layer: BlockLayer, angle: Float, angleWidth: Float) extends  Entity(EntityType.Block) {

  var fixtures: Array[Fixture] = new Array[Fixture](Consts.SegmentPerBlock)
  var lives:Int = 1//Random.nextInt(3)
  var dirty = true
  var hit = false

  var bdef = new BodyDef
  bdef.`type` = BodyType.DynamicBody
  bdef.fixedRotation = false
  bdef.active = true

  body = world.createBody(bdef)
  body.setUserData(this)

  var weld = new WeldJointDef
  weld.bodyA = layer.center
  weld.bodyB = body
  weld.dampingRatio = 10f
  world.createJoint(weld)

  def regenerate(vertices: Array[Float],baseOffset:Int,radius:Float): Unit ={
    dirty = false
    val minRads = angle - angleWidth * 0.5f
    val angleIncr = angleWidth / Consts.SegmentPerBlock
    val inner = radius - Consts.BlockThick / 2
    val outer = radius + Consts.BlockThick / 2
    val segOff = Consts.FloatPerSegment

    val fdef = new FixtureDef
    fdef.density = 0.1f
    fdef.restitution = 1.0f
    fdef.filter.categoryBits = 1
    fdef.filter.maskBits = 2
    fdef.friction = 0.1f
    val shape = new PolygonShape
    fdef.shape = shape
    val verts = new Array[Float](2 * Consts.VertexPerPhysSegment) // 2 floats per phys vertex

    for (i <- 0 until Consts.SegmentPerBlock) {
      val off = baseOffset + i * segOff
      val c = Manager.colors(lives).toFloatBits

      val rads = minRads + i * angleIncr
      val x1 = Math.cos(rads).toFloat
      val y1 = math.sin(rads).toFloat
      val x2 = Math.cos(rads + angleIncr).toFloat
      val y2 = Math.sin(rads + angleIncr).toFloat
      //v1
      vertices(off + 0) = inner * x1
      vertices(off + 1) = inner * y1
      vertices(off + 2) = c
      //v2
      vertices(off + 3) = inner * x2
      vertices(off + 4) = inner * y2
      vertices(off + 5) = c
      //v3
      vertices(off + 6) = outer * x2
      vertices(off + 7) = outer * y2
      vertices(off + 8) = c
      //v4
      vertices(off + 9) = outer * x1
      vertices(off + 10) = outer * y1
      vertices(off + 11) = c
      //v1
      vertices(off + 12) = inner * x1
      vertices(off + 13) = inner * y1
      vertices(off + 14) = c
      //v3
      vertices(off + 15) = outer * x2
      vertices(off + 16) = outer * y2
      vertices(off + 17) = c

      if(fixtures(i)!= null){
        body.destroyFixture(fixtures(i))
      }
      verts(0) = vertices(off + 0)
      verts(1) = vertices(off + 1)

      verts(2) = vertices(off + 3)
      verts(3) = vertices(off + 4)

      verts(4) = vertices(off + 6)
      verts(5) = vertices(off + 7)

      verts(6) = vertices(off + 9)
      verts(7) = vertices(off + 10)
      shape.set(verts)

      fixtures(i) = body.createFixture(fdef)
    }
  }
  def destroy(vertices: Array[Float],baseOffset:Int): Unit ={
    val segOff = Consts.FloatPerSegment
    for (i <- 0 until Consts.SegmentPerBlock) {
      val off = baseOffset + i * segOff
      for (j <- 0 until Consts.FloatPerSegment) {
        vertices(off + j) = 0
      }
      if (fixtures(i) != null) {
        body.destroyFixture(fixtures(i))
        fixtures(i)=null
      }
    }
    world.destroyBody(body)
  }
  def update(): Unit ={
    if(hit){
      lives-=1
      hit=false
      dirty=true
    }
  }
}


