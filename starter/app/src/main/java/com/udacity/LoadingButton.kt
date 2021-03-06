package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.withStyledAttributes
import kotlinx.android.synthetic.main.content_main.view.*
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var animatedWidth = 0.0f
    private var backgroundCustomColor = 0
    private var loadingBarColor = 0
    private var textColor = 0
    private var loadingCircleColor = 0
    private var text = ""
    private var textSize = 50.0f
    private val valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when(buttonState){
            ButtonState.Loading -> setAnimator()
            ButtonState.Completed -> {
                stopAnimator()
                custom_button.isEnabled = true
            }
            ButtonState.Clicked -> custom_button.isEnabled = false
        }
    }


    init {
        // styling
        context.withStyledAttributes(
            attrs,
            R.styleable.LoadingButton
        ) {
            textSize = 50.0f
            text = context.getString(R.string.button_download)
            loadingBarColor = context.getColor(R.color.colorPrimaryDark)
            backgroundCustomColor = context.getColor(R.color.colorPrimary)
            textColor = context.getColor(R.color.white)
            loadingCircleColor = context.getColor(R.color.colorAccent)
        }
    }

    fun setState(state: ButtonState) {
        buttonState = state
    }

    private fun setAnimator() {
        valueAnimator.duration = 3000L
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.setFloatValues(0.0f, width.toFloat())
        valueAnimator.repeatCount = ValueAnimator.INFINITE

        valueAnimator.addUpdateListener {
            animatedWidth = it.animatedValue as Float
            invalidate()
        }

        valueAnimator.start()
    }

    private fun stopAnimator() {
        valueAnimator.cancel()
        invalidate()
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 50.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        paint.color = backgroundCustomColor

        canvas?.drawRect(
            0.0f,
            0.0f,
            width.toFloat(),
            height.toFloat(),
            paint)

        when (buttonState) {
            ButtonState.Loading -> setLoadingAnimation(canvas)
            ButtonState.Completed -> setDefaultButtonState(canvas)
        }
    }


private fun setLoadingAnimation(canvas: Canvas?) {
    paint.color = loadingBarColor
    canvas?.drawRect(
        0.0f,
        0.0f,
        animatedWidth,
        height.toFloat(),
        paint
    )

    paint.color = textColor
    paint.textAlign = Paint.Align.CENTER
    canvas?.drawText(
        context.getString(R.string.button_loading),
        width / 2.0f, height / 1.7f, paint
    )

    val circleLeft: Float = width.toFloat() - .25f*width
    val circleTop: Float = height.toFloat() - .666667f*height
    val circleRight: Float = circleLeft + 60.0f
    val circleBottom: Float = circleTop + 60.0f
    val sweepAngle: Float = (animatedWidth/width) * 360

    paint.color = loadingCircleColor
    canvas?.drawArc(
        circleLeft, circleTop, circleRight, circleBottom,
        0.0f, sweepAngle, true, paint
    )

}

private fun setDefaultButtonState(canvas: Canvas?) {
    paint.color = textColor
    paint.textAlign = Paint.Align.CENTER
    canvas?.drawText(
        context.getString(R.string.button_download),
        width / 2.0f, height / 1.7f, paint
    )

}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}