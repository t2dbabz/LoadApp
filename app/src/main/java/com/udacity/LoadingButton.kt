package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_BUTTON_TEXT = "Download"
        private const val ANIMATION_TEXT = "We are loading"
    }
    private var widthSize = 0
    private var heightSize = 0
    private var textWidth = 0f


    private var buttonBackgroundColor = ContextCompat.getColor(context, R.color.colorPrimary)
    private var buttonAnimationColor = ContextCompat.getColor(context, R.color.colorPrimaryDark)
    private var circleAnimationColor = ContextCompat.getColor(context, R.color.colorAccent)
    private var textColor = ContextCompat.getColor(context, R.color.white)
    private var defaultButtonText = DEFAULT_BUTTON_TEXT
    private var buttonText = DEFAULT_BUTTON_TEXT
    private var animatedButtonText = ANIMATION_TEXT


    private var buttonBackgroundRect = RectF()
    private var buttonAnimationRect = RectF()
    private var textBound = Rect()

    private val animationDuration = 3000L
    private var progress = 0f


    private val buttonTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.LEFT
        textSize = 55f
        typeface = Typeface.DEFAULT
        color = textColor
    }

    private val buttonBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = buttonBackgroundColor
        style = Paint.Style.FILL
    }

    private val buttonAnimationPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = buttonAnimationColor
        style = Paint.Style.FILL
    }

    private val circleAnimationPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = circleAnimationColor
        style = Paint.Style.FILL
    }


    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonAnimationColor = getColor(R.styleable.LoadingButton_animationColor, Color.BLACK)
            buttonBackgroundColor =
                getColor(R.styleable.LoadingButton_backgroundColor, Color.DKGRAY)
            circleAnimationColor =
                getColor(R.styleable.LoadingButton_circleColor, Color.GRAY)

            textColor = getColor(R.styleable.LoadingButton_textColor, Color.WHITE)
        }


    }


    private val valueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = animationDuration
        interpolator = LinearInterpolator()
        addUpdateListener {
            progress = it.animatedValue as Float
            invalidate()
        }
        addListener(object: AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                buttonState = ButtonState.Loading
                this@LoadingButton.isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator?) {
                buttonState = ButtonState.Completed
                this@LoadingButton.isEnabled = true
            }
        })
    }


     var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, newState ->

        when (newState) {
            ButtonState.Loading -> {
                buttonText = animatedButtonText
                invalidate()

            }
            ButtonState.Clicked -> {

                valueAnimator.start()
                invalidate()
            }

            ButtonState.Completed -> {
                valueAnimator.cancel()
                buttonText = defaultButtonText
                invalidate()
            }
        }

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawBackground(canvas)
        drawButtonText(canvas)
        drawCircle(canvas)
    }

    private fun drawCircle(canvas: Canvas) {
        if (buttonState == ButtonState.Loading) {
            buttonTextPaint.getTextBounds(buttonText, 0, buttonText.length, textBound)
            val radius = textBound.height().toFloat()
            canvas.translate((widthSize + textWidth + radius) / 2f, heightSize / 2f - radius / 2)
            canvas.drawArc(0f, 0f, radius, radius, 0f, 360f * progress, true, circleAnimationPaint)
        }

    }

    private fun drawButtonText(canvas: Canvas) {
         textWidth = buttonTextPaint.measureText(buttonText)

        canvas.drawText(
            buttonText,
            (widthSize - textWidth) / 2f,
            (heightSize - (buttonTextPaint.ascent() + buttonTextPaint.descent())) / 2f,
            buttonTextPaint
        )

    }

    private fun drawBackground(canvas: Canvas) {
        if(buttonState == ButtonState.Loading){
            buttonBackgroundRect.set(0f, 0f, widthSize.toFloat(), heightSize.toFloat())
            canvas.drawRect(buttonBackgroundRect, buttonBackgroundPaint)

            buttonAnimationRect.set(0f, 0f, widthSize * progress, heightSize.toFloat())
            canvas.drawRect(buttonAnimationRect, buttonAnimationPaint)
        } else {
            buttonBackgroundRect.set(0f, 0f, widthSize.toFloat(), heightSize.toFloat())
            canvas.drawRect(buttonBackgroundRect, buttonBackgroundPaint)
        }
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