package com.dozen.world.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.dozen.world.R
import com.dozen.world.bean.RoundItem
import kotlin.math.abs

/**
 * Created by Hugo on 19-12-31.
 * Describe:
 *
 *
 *
 */
class RoundView : View {

    //原点
    private var originX: Int = 0
    private var originY: Int = 0
    //实际宽高
    private var widthRound: Int = 0
    private var heightRound: Int = 0

    //边缘间隔
    private var interval: Int = 0

    //半径
    private var originR: Int = 0

    //属性
    var titleName: String? = null
    private var titleSize: Float = 0.0f
    private var titleColor: Int = Color.BLACK
    private var roundBackground: Drawable? = null

    //特殊大小
    private var defaultWidth: Int = 5000
    private var defaultHeight: Int = 500

    //数据
    private var data = ArrayList<RoundItem>()
    //数据当前选择项
    private var indexData: Int? = null


    //扇形大小矩形
    private lateinit var roundRect: RectF

    //三角图标
    private lateinit var pathTop: Path
    //点击按钮
    private lateinit var openRectF: RectF

    //旋转参数
    private var d: Float = 0f
    private var rotateD: Float = 0f
    private var lastX: Float = 0f

    //画笔
    private lateinit var chartPaint: Paint
    private lateinit var textPaint: Paint
    private lateinit var clickPaint: Paint

    //画板
    private lateinit var roundCanvas: Canvas
    private lateinit var detailCanvas: Canvas

    //画布
    private lateinit var roundBitmap: Bitmap
    private lateinit var detailBitmap: Bitmap

    //点击状态
    private var clickState: Boolean = false

    //单击click回调
    private lateinit var cl: RoundClickListener

    //转速调节
    private var rs: Float = 1f

    constructor(context: Context?) : super(context) {
        init(null, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs, defStyleAttr)
    }


    private fun init(attrs: AttributeSet?, defStyleAttr: Int) {

        val a = context.obtainStyledAttributes(attrs, R.styleable.RoundView, defStyleAttr, 0)

        titleName = a.getString(R.styleable.RoundView_titleName)
        titleSize = a.getDimension(R.styleable.RoundView_titleSize, titleSize)
        titleColor = a.getColor(R.styleable.RoundView_titleColor, titleColor)
        roundBackground = a.getDrawable(R.styleable.RoundView_roundBackground)

        a.recycle()


        interval = 100

        textPaint = Paint()
        textPaint.color = titleColor
        textPaint.textSize = titleSize
        textPaint.isAntiAlias = true
        textPaint.isDither = true
        textPaint.textAlign = Paint.Align.LEFT


        chartPaint = Paint()
        chartPaint.textSize = 1f
        chartPaint.color = Color.LTGRAY
        chartPaint.isAntiAlias = true
        chartPaint.isDither = true

        clickPaint = Paint()
        clickPaint.color = Color.BLUE
        clickPaint.isDither = true
        clickPaint.isAntiAlias = true
        clickPaint.textAlign = Paint.Align.CENTER

    }

    var dataInit: List<RoundItem>
        get() {
            return data
        }
        set(value) {
            data = value as ArrayList<RoundItem>
            invalidate()
        }

    var degrees: Float
        get() {
            return d
        }
        set(value) {
            d = value
            postInvalidate()
        }

    var clickListener: RoundClickListener
        get() {
            TODO()
        }
        set(value) {
            cl = value
        }
    var rotateSpeed: Float
        get() {
            return rs
        }
        set(value) {
            if (value > 0) rs = abs(value)
        }

    @SuppressLint("SwitchIntDef", "DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        //初始化宽度和高度
        widthRound = when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.UNSPECIFIED -> dip2px(defaultWidth)
            MeasureSpec.AT_MOST -> dip2px(MeasureSpec.getSize(widthMeasureSpec))
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(widthMeasureSpec)
            else -> defaultWidth
        }
        heightRound = when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.UNSPECIFIED -> dip2px(defaultHeight)
            MeasureSpec.AT_MOST -> dip2px(MeasureSpec.getSize(heightMeasureSpec))
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(heightMeasureSpec)
            else -> defaultHeight
        }


        //初始化圆心和半径
        originR = if (isHorizontal()) heightRound / 2 - interval else widthRound / 2 - interval
        originX = if (isHorizontal()) widthRound - originR - interval else widthRound / 2
        originY = if (isHorizontal()) heightRound / 2 else heightRound - originR - interval

        //圆形显示和详细信息bitmap
        roundBitmap = Bitmap.createBitmap(widthRound, heightRound, Bitmap.Config.ARGB_8888)
        detailBitmap = if (isHorizontal()) {
            Bitmap.createBitmap(
                widthRound - originR * 2 - interval * 2,
                heightRound - interval * 2,
                Bitmap.Config.ARGB_8888
            )
        } else {
            Bitmap.createBitmap(
                widthRound - interval,
                heightRound - originR * 2 - interval * 3,
                Bitmap.Config.ARGB_8888
            )
        }
        roundCanvas = Canvas(roundBitmap)
        detailCanvas = Canvas(detailBitmap)

        roundRect = RectF(
            (originX - originR).toFloat(),
            (originY - originR).toFloat(),
            (originX + originR).toFloat(),
            (originY + originR).toFloat()
        )

        //设置三角图标位置
        pathTop = Path()
        pathTop.moveTo(originX.toFloat(), (originY - originR).toFloat())
        pathTop.lineTo(originX - 15f, originY - originR - 30f)
        pathTop.lineTo(originX + 15f, originY - originR - 30f)
        pathTop.close()

        //设置点击按钮位置
        val oX =
            (detailBitmap.width + interval / 2 - if (isHorizontal()) detailBitmap.width / 30 else detailBitmap.height / 30).toFloat()
        val oY =
            (detailBitmap.height + interval - if (isHorizontal()) detailBitmap.width / 30 else detailBitmap.height / 30).toFloat()
        val widthL = if (isHorizontal()) detailBitmap.width / 3 else detailBitmap.height / 3
        openRectF = RectF(oX - widthL, oY - widthL * 2 / 5, oX, oY)

        setMeasuredDimension(widthRound, heightRound)

    }

    private fun dip2px(value: Int): Int = (value / resources.displayMetrics.density + 0.5f).toInt()

    //宽度大于高度,返回true,水平显示
    private fun isHorizontal(): Boolean = widthRound > heightRound


    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)


        canvas?.drawPath(pathTop, chartPaint)
        textPaint.color = Color.RED
        textPaint.textSize = titleSize
        titleName?.let {
            canvas?.drawText(
                it,
                interval / 2.toFloat(),
                (interval - 20).toFloat(),
                textPaint
            )
        }


        val angle = createAngle()
        if (angle.isNotEmpty()) {
            var first = angle[0] / 2 - 90f
            roundCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            for ((j, i) in angle.withIndex()) {
                chartPaint.color = data[j].color
                roundCanvas.drawArc(roundRect, first, -i, true, chartPaint)
                first += -i

            }
        }
        roundCanvas.rotate(d, originX.toFloat(), originY.toFloat())
        rotateD += d
        canvas?.drawBitmap(roundBitmap, 0f, 0f, chartPaint)

        if (angle.isNotEmpty()) {
            var first = -angle[0] / 2
            val dd = if (rotateD > 0) rotateD % 360 else 360 - (abs(rotateD) % 360)
            for ((i, v) in angle.withIndex()) {
                if (dd in first..(first + v)) {
                    drawDetailsMessage(i)
                    break
                }
                if (dd in (360 - angle[0] / 2)..360f) {
                    drawDetailsMessage(0)
                    break
                }
                first += v
            }
        }
        canvas?.drawBitmap(detailBitmap, interval / 2.toFloat(), interval.toFloat(), chartPaint)

        //is click
        if (clickState) {
            clickPaint.color = Color.RED
            canvas?.drawRoundRect(openRectF, 15f, 15f, clickPaint)
            clickState = false
        } else {
            //绘画点击按钮
            clickPaint.color = Color.BLUE
            canvas?.drawRoundRect(openRectF, 15f, 15f, clickPaint)
            clickState = false
        }
        clickPaint.textSize = openRectF.height() * 4 / 5
        clickPaint.color = Color.YELLOW
        "Click".let {
            canvas?.drawText(
                it,
                openRectF.left + openRectF.width() / 2,
                openRectF.bottom - openRectF.height() / 5,
                clickPaint
            )
        }


        //恢复初始化
        d = 0f

    }

    private fun createAngle(): List<Float> {
        val angle = ArrayList<Float>()
        var allSize = 0f
        for (i in data) allSize += i.size
        for (i in data) angle.add((i.size / allSize) * 360f)
        return angle
    }

    private fun drawDetailsMessage(index: Int) {

        //设置当前选择项
        indexData = index

        val xLeft = 30f
        val yTop = 5f

        detailCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        detailCanvas.drawColor(Color.LTGRAY)

        textPaint.textSize = titleSize
        textPaint.color = Color.BLACK
        data[index].name.let { detailCanvas.drawText(it, xLeft, titleSize + yTop, textPaint) }
        chartPaint.color = data[index].color


        textPaint.textSize = titleSize / 2
        val l = ((detailBitmap.width - xLeft * 2) / (titleSize / 2)).toInt()
        var ix = 0
        var j = 2
        while (ix < data[index].detail.length) {

            val end = if (ix + l > data[index].detail.length) data[index].detail.length else ix + l
            data[index].detail.let {
                detailCanvas.drawText(
                    it,
                    ix,
                    end,
                    xLeft,
                    (titleSize + yTop) * j,
                    textPaint
                )
            }
            if (ix + l > data[index].detail.length) ix = data[index].detail.length else ix += l
            j += 1

        }

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.x
            }
            MotionEvent.ACTION_MOVE -> {
                val moveX = event.x - lastX
                d = 0f
                if (moveX > 0) {
                    d = rs * when {
                        moveX < 10 -> 0.1f
                        moveX < 20 -> 0.3f
                        moveX < 30 -> 1f
                        moveX < 50 -> 5f
                        moveX < 80 -> 7f
                        moveX < 100 -> 10f
                        moveX < 130 -> 13f
                        else -> 15f
                    }
                    postInvalidate()

                } else if (moveX < 0) {
                    d = rs * -when {
                        moveX > -10 -> 0.1f
                        moveX > -20 -> 0.3f
                        moveX > -30 -> 1f
                        moveX > -50 -> 5f
                        moveX > -80 -> 7f
                        moveX > -100 -> 10f
                        moveX > -130 -> 13f
                        else -> 15f
                    }
                    postInvalidate()
                }
                lastX = event.x
            }
            MotionEvent.ACTION_UP -> {
                if (d == 0f && openRectF.contains(event.x, event.y)) {
                    clickState = true
                    postInvalidate()

                    if (indexData != null) {
                        cl.clickListener(data[indexData!!])

                    }
                }
            }
        }
        return true
    }


}