package com.example.cpu02351_local.firebasechatapp.utils

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ImageView

class HoldForActionButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_DELAY = 100 // ms
        private const val DEFAULT_INTERVAL = 500L // ms
    }

    private var callbackInterval = DEFAULT_INTERVAL
    private var holdDelay = DEFAULT_DELAY
    private var startTime = -1L
    private var startX = -1
    private var startY = -1
    private var listeners = mutableListOf<ActionListener>()

    fun setCallbackInterval(interval: Long) {
        if (interval > 0L)
            callbackInterval = interval
    }

    fun setHoldDelay(delay: Int) {
        if (delay > 0)
            holdDelay = delay
    }


    fun addActionListener(listener: ActionListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    fun removeActionListener(listener: ActionListener) {
        listeners.remove(listener)
    }


    private val startTask = Runnable { notifyStart() }
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val action = event?.actionMasked ?: return super.onTouchEvent(event)

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.rawX.toInt()
                startY = event.rawY.toInt()
                startTime = System.currentTimeMillis()
                mHandler.postDelayed(startTask, holdDelay.toLong())
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (isHolding) {
                    notifyMovement(startX, startY, event.rawX.toInt(), event.rawY.toInt())
                }
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL-> {
                if (isHolding) {
                    notifyEnd()
                } else {
                    notifyClick()
                }
                mHandler.removeCallbacks(startTask)
                return true
            }
        }

        return super.onTouchEvent(event)
    }

    private var isHolding = false

    private fun notifyClick() {
        listeners.forEach {
            it.onClick()
        }
    }

    private fun notifyStart() {
        isHolding = true
        startTimer()
        listeners.forEach {
            it.onActionStarted()
        }
    }

    private val mHandler = Handler()

    private val task = object : Runnable {
        override fun run() {
            notifyHold(System.currentTimeMillis() - startTime)
            mHandler.postDelayed(this, callbackInterval)
        }
    }

    private fun startTimer() {
        mHandler.post(task)
    }

    private fun notifyEnd() {
        resetState()
        stopTimer()
        listeners.forEach {
            it.onActionEnded()
        }
    }

    private fun stopTimer() {
        mHandler.removeCallbacks(task)
    }

    private fun notifyHold(totalTimeMilli: Long) {
        listeners.forEach {
            it.onHold(totalTimeMilli)
        }
    }

    private fun notifyMovement(startX: Int, startY: Int, currentX: Int, currentY: Int) {
        listeners.forEach {
            it.onMovement(startX, startY, currentX, currentY)
        }
    }

    private fun resetState() {
        isHolding = false
    }


    interface ActionListener {
        fun onActionStarted()
        fun onActionEnded()
        fun onHold(totalTimeMilli: Long)
        fun onMovement(startX: Int, startY: Int, currentX: Int, currentY: Int)
        fun onClick()
    }
}
