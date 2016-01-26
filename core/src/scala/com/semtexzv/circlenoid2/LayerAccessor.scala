package com.semtexzv.circlenoid2

import aurelienribon.tweenengine.TweenAccessor
import com.semtexzv.circlenoid2.objects.BlockLayer

/**
  * Created by Semtexzv on 1/24/2016.
  */
class LayerAccessor extends TweenAccessor[BlockLayer]{
  final val ResizeOrbit = 1
  override def setValues(target: BlockLayer, tweenType: Int, newValues: Array[Float]): Unit = {
   tweenType match {
      case ResizeOrbit =>
        target.orbit = newValues(0);
        target.dirty = true
      case _ => ???
    }
  }

  override def getValues(target: BlockLayer, tweenType: Int, returnValues: Array[Float]): Int = {
    tweenType match {
      case ResizeOrbit =>
        returnValues(0)=target.orbit;
        1
      case _ => ???
    }
  }
}
