package com.semtexzv.circlenoid2

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap.Format
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.graphics._
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.{FrameBuffer, ShaderProgram}
import com.badlogic.gdx.math.{Matrix4, Vector3}
import com.badlogic.gdx.utils.Disposable

/**
  * Created by Semtexzv on 1/23/2016.
  */
class GradientBG extends Disposable{
  final var Radius = 1.5f
  final var Segments = 12
  final var FloatPerSegment = 3
  var width = 0f
  var height = 0f

  var buffer: FrameBuffer = null
  var mesh: Mesh = new Mesh(true, Segments+2, 0,
    new VertexAttribute(Usage.Position, 2,"a_position"),
    new VertexAttribute(Usage.ColorPacked,4, "a_color"))

  def redraw(center:Color,edge:Color): Unit ={
    width = Gdx.graphics.getWidth
    height = Gdx.graphics.getHeight
    if(buffer != null) {
      buffer.dispose()
    }
    buffer =  new FrameBuffer(Format.RGB888, Gdx.graphics.getWidth/4,Gdx.graphics.getHeight/4, false)

    val vertices = new Array[Float](FloatPerSegment*(Segments+2))
    vertices(0)=0
    vertices(1)=0
    vertices(2)=center.toFloatBits
    for (i<- 0 to Segments){
      val angle = (math.Pi*2/Consts.BlockCount).toFloat * i
      val x1 = Radius *math.cos(angle).toFloat
      val y1 = Radius *math.sin(angle).toFloat
      vertices(i*FloatPerSegment+3)=x1
      vertices(i*FloatPerSegment+4)=y1
      vertices(i*FloatPerSegment+5)=edge.toFloatBits
    }
    mesh.setVertices(vertices)
    buffer.begin()
    Manager.program.begin()
    var m = new Matrix4()
    if(width > height) {
      m.scale(1,width/height,1)
    }
    else {
      m.scale(height/width,1,1)
    }

    Manager.program.setUniformMatrix("u_projection", m)
    mesh.render(Manager.program,GL20.GL_TRIANGLE_FAN)
    Manager.program.end()
    buffer.end()
  }
  def render(batch:SpriteBatch,w:Float,h:Float): Unit ={
    batch.draw(buffer.getColorBufferTexture,0,0,w,h)

  }

  override def dispose(): Unit = {
    buffer.dispose()
    mesh.dispose()
  }
}
