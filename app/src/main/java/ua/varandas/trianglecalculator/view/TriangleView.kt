package ua.varandas.trianglecalculator.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import ua.varandas.trianglecalculator.R
import ua.varandas.trianglecalculator.model.Triangle

class TriangleView @JvmOverloads constructor(context: Context,

                                             attrs: AttributeSet? = null,

                                             defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {


    private val SIZE_ABC = 20
    private val SIZEE_ABC = 16
    private val TOP_MARGIN = 15
    private val MARGIN = 6
    private val STROKE_WIDTH = 3
    private val sizeABC = SIZE_ABC * resources.displayMetrics.scaledDensity
    private val sizeeabc = SIZEE_ABC * resources.displayMetrics.scaledDensity
    private val topMargin = TOP_MARGIN * resources.displayMetrics.scaledDensity
    private val margin = MARGIN * resources.displayMetrics.scaledDensity
    private val stroke = STROKE_WIDTH * resources.displayMetrics.scaledDensity
    private var scale = 10f

    private val paint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.colorAccent)
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = stroke
    }

    private val paintText = Paint().apply {
        color = ContextCompat.getColor(context, R.color.colorABC)
        isAntiAlias = true
        style = Paint.Style.FILL
        textSize = sizeABC
        strokeWidth = 1f
    }
    var triangle = Triangle().calculate()
    var mat = Matrix()
    private val rectTriangle = RectF()
    private val rectMatrix = RectF()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        setMeasuredDimension(widthMeasureSpec, widthMeasureSpec / 2)
    }


    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {

        val xReal = height.toFloat()
        val yReal = width.toFloat()

        val path = Path()

        val pathA = Path()
        val pathB = Path()
        val pathC = Path()

        val pathUA = Path()
        val pathUB = Path()
        val pathUC = Path()

        canvas.translate(0f, xReal)
        canvas.rotate(-90f, 0f, 0f)
        scale = if (triangle.A < 800 || triangle.B < 800 || triangle.C < 800) 1f else 1000f

        pathA.moveTo(triangle.pointB.x / 2 / scale, triangle.pointB.y / 2 / scale)
        pathA.lineTo(triangle.pointB.x / 2 / scale, triangle.pointB.y / 2 / scale - 0.001f)
        pathA.close()

        pathB.moveTo(triangle.pointA.x / scale, triangle.pointA.y / 2 / scale)
        pathB.lineTo(triangle.pointA.x / scale, triangle.pointA.y / 2 / scale - 0.001f)
        pathB.close()

        pathC.moveTo(triangle.pointB.x / 2 / scale, drawC() / scale)
        pathC.lineTo(triangle.pointB.x / 2 / scale, drawC() / scale - 0.001f)
        pathC.close()

        pathUB.moveTo(triangle.pointB.x / scale, triangle.pointB.y / scale)
        pathUB.lineTo(triangle.pointB.x / scale, triangle.pointB.y / scale - 0.001f)
        pathUB.close()

        pathUC.moveTo(triangle.pointC.x / scale, triangle.pointC.y / scale)
        pathUC.lineTo(triangle.pointC.x / scale, triangle.pointC.y / scale - 0.001f)
        pathUC.close()

        pathUA.moveTo(triangle.pointA.x / scale, triangle.pointA.y / scale)
        pathUA.lineTo(triangle.pointA.x / scale, triangle.pointA.y / scale - 0.001f)
        pathUA.close()

        path.moveTo(triangle.pointC.x / scale, triangle.pointC.y / scale)
        path.lineTo(triangle.pointB.x / scale, triangle.pointB.y / scale)
        path.lineTo(triangle.pointA.x / scale, triangle.pointA.y / scale)
        path.lineTo(triangle.pointC.x / scale, triangle.pointC.y / scale)
        path.close()

        path.computeBounds(rectTriangle, true)
        rectMatrix.set(margin, margin, xReal - topMargin, yReal - margin)
        mat.reset()
        mat.setRectToRect(rectTriangle, rectMatrix, Matrix.ScaleToFit.CENTER)

        path.transform(mat)

        pathA.transform(mat)
        pathB.transform(mat)
        pathC.transform(mat)

        pathUA.transform(mat)
        pathUB.transform(mat)
        pathUC.transform(mat)

        path.addPath(pathA)
        path.addPath(pathB)
        path.addPath(pathC)

        path.addPath(pathUA)
        path.addPath(pathUB)
        path.addPath(pathUC)

        canvas.drawPath(path, paint)

        paintText.textSize = sizeABC
        paintText.color = ContextCompat.getColor(context, R.color.colorABC)
        canvas.drawTextOnPath("A", pathA, 0f, 0f, paintText)
        canvas.drawTextOnPath("B", pathB, 0f, 0f, paintText)
        canvas.drawTextOnPath("C", pathC, 0f, 0f, paintText)

        paintText.textSize = sizeeabc
        paintText.color = ContextCompat.getColor(context, R.color.colorabc)
        canvas.drawTextOnPath("α", pathUA, 0f, 0f, paintText)
        canvas.drawTextOnPath("β", pathUB, 0f, 0f, paintText)
        canvas.drawTextOnPath("γ", pathUC, 0f, 0f, paintText)


        super.onDraw(canvas)

    }

    fun updateCanvas() {
        invalidate()
    }

    private fun drawC(): Float {
        return when {
            triangle.uA > 90 -> {
                triangle.B + (triangle.katetPiphagora(triangle.C, triangle.visota())) / 2
            }
            triangle.uA == 90f -> triangle.B
            else -> triangle.B - (triangle.katetPiphagora(triangle.C, triangle.visota())) / 2
        }
    }
}