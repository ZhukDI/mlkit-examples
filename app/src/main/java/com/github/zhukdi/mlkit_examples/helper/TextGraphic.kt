package com.github.zhukdi.mlkit_examples.helper

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.google.firebase.ml.vision.text.FirebaseVisionText
import java.lang.IllegalStateException

class TextGraphic internal constructor(overlay: GraphicOverlay,
                                       private val text: FirebaseVisionText.Element?) : GraphicOverlay.Graphic(overlay) {

    private val rectPaint: Paint
    private val textPaint: Paint

    companion object {
        private val TEXT_COLOR = Color.BLUE
        private val TEXT_SIZE = 54.0f
        private val STROKE_WIDTH = 4.0f
    }

    init {
        rectPaint = Paint()
        rectPaint.color = TEXT_COLOR
        rectPaint.style = Paint.Style.STROKE
        rectPaint.strokeWidth = STROKE_WIDTH

        textPaint = Paint()
        textPaint.color = TEXT_COLOR
        textPaint.textSize = TEXT_SIZE
    }


    override fun draw(canvas: Canvas) {
        if (text == null) {
            throw IllegalStateException("Attempting to draw a null text");
        }

        val rect = RectF(text.boundingBox)
        rect.left = translateX(rect.left)
        rect.right = translateX(rect.right)
        rect.top = translateX(rect.top)
        rect.bottom = translateX(rect.bottom)

        canvas.drawText(text.text, rect.left, rect.bottom, textPaint)

    }
}