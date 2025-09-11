@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.mshdabiola.zoomable.internal

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
internal annotation class AndroidParcelize

// TODO: make this internal once K2 is enabled
// https://youtrack.jetbrains.com/issue/KT-37316
expect interface AndroidParcelable
