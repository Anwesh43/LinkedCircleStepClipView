package com.anwesh.uiprojects.circlestepclipview

/**
 * Created by anweshmishra on 22/10/18.
 */

import android.app.Activity
import android.view.View
import android.view.MotionEvent
import android.content.Context
import android.graphics.*

val nodes : Int = 5

val steps : Int = 10

fun Canvas.drawCSCNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = h / (nodes + 1)
    val sFactor = 1f - 2 * (i % 2)
    val r : Float = gap / 3
    val barSize : Float = (2 * r) / steps
    val osc : Float = 1f / steps
    paint.color = Color.parseColor("#283593")
    save()
    translate(w/2, gap + i * gap)
    for (j in 0..steps-1) {
        val sc : Float = Math.min(osc, Math.max(0f, scale - osc * j)) * steps
        val y : Float = -r + barSize * j
        save()
        translate((w/2 - r) * sc * sFactor, 0f)
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

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
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
                    Thread.sleep(30)
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

    data class CSCNode(var i : Int, val state : State = State()) {

        private var prev : CSCNode? = null

        private var next : CSCNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = CSCNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawCSCNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            state.update {
                cb(i, it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : CSCNode {
            var curr : CSCNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class CircleStepClip (var i : Int) {

        private val root : CSCNode = CSCNode(0)

        private var curr : CSCNode = root

        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            curr.update {i, scl ->
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(i, scl)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : CircleStepClipView) {

        private val animator : Animator = Animator(view)

        private val csc : CircleStepClip = CircleStepClip(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#BDBDBD"))
            csc.draw(canvas, paint)
            animator.animate {
                csc.update {i, scl ->
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            csc.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : CircleStepClipView {
            val view : CircleStepClipView = CircleStepClipView(activity)
            activity.setContentView(view)
            return view
        }
    }
}