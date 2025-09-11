package com.mshdabiola.zoomable.spatial


/**
 * Identifies a coordinate space (e.g., viewport or zoomable image) that provides context
 * to [SpatialOffset] values. The conversion between coordinate spaces is provided by a
 * [CoordinateSystem].
 */
interface CoordinateSpace {
  companion object {
    val Unspecified: CoordinateSpace = object : CoordinateSpace {}
  }
}
