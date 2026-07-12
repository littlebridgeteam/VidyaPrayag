package com.littlebridge.enrollplus.ui.tokens

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween

object VMotion {
    val ease = CubicBezierEasing(0.2f, 0f, 0f, 1f)
    val easeEmphasized = CubicBezierEasing(0.2f, 0f, 0f, 1f)
    val durFast = 150
    val durDefault = 250
    val durSlow = 400
    val durSlower = 700

    fun <T> tweenFast() = tween<T>(durFast, easing = ease)
    fun <T> tweenDefault() = tween<T>(durDefault, easing = ease)
    fun <T> tweenSlow() = tween<T>(durSlow, easing = ease)
    fun <T> tweenSlower() = tween<T>(durSlower, easing = ease)
}
