package com.mshdabiola.zoomable.internal

internal enum class HostPlatform {
  Android,
  Desktop,
  iOS,
  Web,
  ;

  companion object;
}

internal expect val HostPlatform.Companion.current: HostPlatform
