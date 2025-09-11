package com.mshdabiola.zoomable

import androidx.compose.ui.geometry.Size
import com.mshdabiola.zoomable.internal.ContentCoordinateSpace
import com.mshdabiola.zoomable.internal.ViewportCoordinateSpace
import com.mshdabiola.zoomable.spatial.CoordinateSpace
import com.mshdabiola.zoomable.spatial.CoordinateSystem
import com.mshdabiola.zoomable.spatial.SpatialRect
import kotlin.jvm.JvmSynthetic

/**
 * `Modifier.zoomable()`'s coordinate system for representing spatial offsets in
 * [CoordinateSpace.Viewport][CoordinateSpace.Companion.Viewport] and
 * [CoordinateSpace.ZoomableContent][CoordinateSpace.Companion.ZoomableContent].
 *
 * Usage example:
 *
 * ```kotlin
 * val visibleImageRegion = with(zoomableState.coordinateSystem) {
 *   contentBounds.rectIn(CoordinateSpace.ZoomableContent)
 * }
 * ```
 */
interface ZoomableCoordinateSystem : CoordinateSystem {
  /**
   * The visible bounds of the content _after_ user zoom and pan. This is calculated by applying
   * [contentScale][ZoomableState.contentScale] and [contentAlignment][ZoomableState.contentAlignment]
   * to the value passed to [ZoomableState.setContentLocation].
   *
   * This value will be [SpatialRect.Unspecified] if the content hasn't been measured yet, and it will
   * never exceed the viewport bounds.
   */
  val contentBounds: SpatialRect
    get() = contentBounds(clipToViewport = true)

  /**
   * Like [contentBounds], but _without_ any user transformations. This is the initial bounds of the
   * content, where the content is displayed prior to any zoom or pan gestures. This property is
   * intended for drawing decorations around the content that remain unaffected by zoom and pan gestures.
   */
  val unscaledContentBounds: SpatialRect
    get() = unscaledContentBounds(clipToViewport = true)

  /**
   * Size of the composable where `Modifier.zoomable()` is used.
   *
   * This value will be [Size.Zero] if the composable hasn't been measured yet.
   */
  val viewportSize: Size

  /**
   * Same as [contentBounds].
   *
   * @param clipToViewport When `true`, the bounds will be clipped to the visible area within the viewport.
   * When `false`, the bounds include the entirety of the content, even areas outside the viewport.
   */
  fun contentBounds(clipToViewport: Boolean): SpatialRect

  /**
   * Same as [unscaledContentBounds].
   *
   * @param clipToViewport When `true`, the bounds will be clipped to the visible area within the viewport.
   * When `false`, the bounds include the entirety of the content, even areas outside the viewport.
   */
  fun unscaledContentBounds(clipToViewport: Boolean): SpatialRect
}

/**
 * Represents the coordinate space of the visible viewport — the bounds of
 * the composable where `Modifier.zoomable()` is applied.
 *
 * Useful for interpreting user input or layout positions on screen.
 */
val CoordinateSpace.Companion.Viewport: CoordinateSpace
  @JvmSynthetic get() = ViewportCoordinateSpace

/**
 * Represents the coordinate space of the zoomable content (e.g., an image).
 *
 * Offsets in this space are relative to the unscaled, unpanned content bounds.
 * Useful for anchoring elements to the original content or mapping coordinates
 * from click listeners (e.g., `onClick`, `onLongClick`) on the viewport to the content.
 */
val CoordinateSpace.Companion.ZoomableContent: CoordinateSpace
  @JvmSynthetic get() = ContentCoordinateSpace
