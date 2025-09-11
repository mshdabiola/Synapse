package com.mshdabiola.zoomable.internal

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Rect
import com.mshdabiola.zoomable.Viewport
import com.mshdabiola.zoomable.ZoomableState
import com.mshdabiola.zoomable.spatial.CoordinateSpace
import com.mshdabiola.zoomable.spatial.isSpecified
import kotlin.jvm.JvmInline

/**
 * Used by [com.mshdabiola.zoomable.ZoomableImage] to provide a fallback value for
 * [ZoomableState.transformedContentBounds] before the full quality image is loaded. This
 * ensures that the bounds aren't empty while a placeholder image is visible.
 */
@JvmInline
internal value class PlaceholderBoundsProvider(
  private val placeholderState: ZoomableState,
) {
  @Stable
  fun calculate(): Rect? {
    return with(placeholderState.coordinateSystem) {
      val bounds = unscaledContentBounds.takeIf { it.isSpecified } ?: return null
      bounds.rectIn(CoordinateSpace.Viewport)
    }
  }
}
