package com.semtexzv.circlenoid2.objects

import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.graphics._
import com.badlogic.gdx.graphics.Mesh.VertexDataType
import com.badlogic.gdx.math.{Matrix4, Vector2}
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d._

import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef
import com.badlogic.gdx.utils.Disposable
import com.semtexzv.circlenoid2.{Manager, Consts}


class BlockLayer(world: World,ground:Body, var indexOrbit:Int ) extends  Disposable
{

  final val LayerOffset = 3f
  final val LayerRatio = 1.20f

  var orbit: Float = indexOrbit
  var center : Body=null
  var dirty = true
  var alive = true
  var blocks: Array[AxialBlock] = new Array[AxialBlock](Consts.BlockCount)
  val verts = new Array[Float](Consts.FloatPerBlock * Consts.BlockCount)
  var mesh: Mesh = new Mesh(VertexDataType.VertexBufferObject,true,Consts.VertexPerLayer,0,
    new VertexAttribute(Usage.Position,2,"a_position"),new VertexAttribute(Usage.ColorPacked,4,"a_color"))
  var origRotSpeed =  0f

  var shape = new CircleShape()
  shape.setPosition(new Vector2(0,0))
  shape.setRadius(0.5f)
  val fdef = new FixtureDef
  fdef.shape = shape

  fdef.density = 1f
  fdef.restitution = 1.0f
  fdef.friction = 0.1f
  fdef.filter.categoryBits =0
  fdef.filter.maskBits =0

  var bdef = new BodyDef
  bdef.`type`=BodyType.DynamicBody
  bdef.fixedRotation=true
  bdef.active=true
  bdef.angularVelocity = origRotSpeed
  bdef.angularDamping = 1

  center = world.createBody(bdef)
  center.createFixture(fdef)
  shape.dispose()

  var rev = new RevoluteJointDef
  rev.initialize(ground,center,center.getPosition)
  world.createJoint(rev)

  for (i<-0 until Consts.BlockCount){
    val angle = (math.Pi*2/Consts.BlockCount).toFloat * i
    val width = (math.Pi*2/Consts.BlockCount).toFloat*0.8f
    blocks(i) = new AxialBlock(world,this,angle,width)
  }
  def render(program:ShaderProgram,mat: Matrix4): Unit ={

    val radius: Float = LayerOffset+orbit*LayerRatio
    var upload = false
    for (i<-0 until Consts.BlockCount) {
      var block = blocks(i)
      if (block != null) {
        block.update()
        if (block.dirty || this.dirty) {
          upload = true
          if (block.lives <= 0) {
            block.destroy(verts, i * Consts.FloatPerBlock)
            Manager.blockDestroyed()
            blocks(i) = null
          }
          else {
            block.regenerate(verts, i * Consts.FloatPerBlock, radius)
          }
        }
      }
    }
    if(dirty || upload) {
      dirty=false
      mesh.setVertices(verts)
    }
    val angle =  Math.toDegrees(center.getAngle()).toFloat
    mat.rotate(0,0,1,angle)
    program.setUniformMatrix("u_projection",
      mat)
    mesh.render(program,GL20.GL_TRIANGLES)
    mat.rotate(0,0,-1,angle)
  }
  def destroy(): Unit ={
    world.destroyBody(center)
    for (i<-0 until Consts.BlockCount) {
      if (blocks(i) != null) {
        blocks(i).destroy(verts, i * Consts.FloatPerBlock)
        blocks(i) = null
      }
    }
  }

  override def dispose(): Unit = {
    mesh.dispose()
  }
}