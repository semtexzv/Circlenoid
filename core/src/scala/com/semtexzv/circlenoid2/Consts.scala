package com.semtexzv.circlenoid2

/**
  * Created by Semtexzv on 1/23/2016.
  */
object Consts{
  final var BlockCount = 12
  final val BlockThick = 0.8f
  final val VertexPerSegment = 6
  final val SegmentPerBlock = 3
  final val FloatPerVertex = 3 // x,y,color
  final val VertexPerPhysSegment = 4

  final val FloatPerSegment = FloatPerVertex*VertexPerSegment

  final val FloatPerBlock = VertexPerSegment * SegmentPerBlock * FloatPerVertex

  final val VertexPerLayer = FloatPerBlock * BlockCount

}