package com.anwesh.uiprojects.circlestepclipview

/**
 * Created by anweshmishra on 22/10/18.
 */

import android.view.View
import android.view.MotionEvent
import android.content.Context
import android.graphics.*

val nodes : Int = 5

val steps : Int = 10

fun Canvas.drawCSCNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = w / (nodes + 1)
    val r : Float = gap / 3
    val barSize : Float = (2 * r) / steps
    val osc : Float = 1f / steps
    paint.color = Color.parseColor("#283593")
    save()
    translate(w/2, gap + i * gap)
    for (j in 0..1) {
        val sc : Float = Math.min(osc, Math.max(0f, scale - osc * j)) * steps
        val y : Float = -r + barSize * j
        save()
        translate(w/2 * sc, 0f)
        val path : Path = Path()
        path.addRect(RectF(-r, y, r, y + barSize), Path.Direction.CCW)
        clipPath(path)
        drawCircle(0f, 0f, r, paint)
        restore()
    }
    restore()
}

class CircleStepClipView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var prevScale : Float = 0f, var dir : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += (0.1f / steps) * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }
}