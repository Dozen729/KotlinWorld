package com.dozen.world.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.size
import com.dozen.world.R

/**
 * Created by Hugo on 20-3-23.
 * Describe:
 *
 *
 *
 */
class TopTabView : LinearLayout {

    private val dividerColor = 0xffcccccc
    private val textSelectColor = Color.rgb(0, 69, 255)
    private val textUnSelectColor = Color.rgb(164, 218, 233)

    private var menuTextSize = 15f
    private var menuSelectedIcon = 0
    private var menuUnSelectedIcon = 0

    private var moveOpen = 0f
    private var move = 0f
    private var next = 0f

    private var tabWidth = 30f
    private var ttWidth = 0f

    private var displayWidth = 0
    private var listNumber=0

    lateinit var ttcl:TopTabClickListener


    private lateinit var tabMenuView: LinearLayout

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        initView(context, attributeSet)
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        initView(context, attributeSet)
    }

    private fun initView(context: Context, attributeSet: AttributeSet) {
        val a = context.obtainStyledAttributes(attributeSet, R.styleable.TopTabView)
        menuTextSize = a.getDimension(R.styleable.TopTabView_menuTextSize, menuTextSize)
        menuSelectedIcon = a.getResourceId(R.styleable.TopTabView_menuSelectIcon, menuSelectedIcon)
        menuUnSelectedIcon =
            a.getResourceId(R.styleable.TopTabView_menuUnSelectIcon, menuUnSelectedIcon)
        tabWidth = a.getDimension(R.styleable.TopTabView_tabWidth, tabWidth)

        a.recycle()

        val wm: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val p = Point()
        wm.defaultDisplay.getRealSize(p)
        displayWidth = p.x

    }

    private fun dp2px(value: Float): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        value,
        resources.displayMetrics
    ).toInt()

    private fun addTab(s: String,i: Int, n: Boolean) {
        val tab = TextView(context)
        tab.text = s
        tab.setTextColor(Color.BLACK)
        tab.gravity = Gravity.CENTER
        tab.isSingleLine = true
        tab.ellipsize = TextUtils.TruncateAt.END
        tab.textSize = menuTextSize
        tab.layoutParams = LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f)
        tab.setPadding(dp2px(5f), dp2px(12f), dp2px(5f), dp2px(10f))
        tab.tag = tab

        if (i==0){
            tab.setCompoundDrawablesWithIntrinsicBounds(null,null,resources.getDrawable(menuSelectedIcon),null)
            tab.setBackgroundColor(textSelectColor)
        }else{
            tab.setCompoundDrawablesWithIntrinsicBounds(null,null,resources.getDrawable(menuUnSelectedIcon),null)
            tab.setBackgroundColor(textUnSelectColor)
        }

        tabMenuView.addView(tab)

        if (n) {
            val view = View(context)
            view.layoutParams = LayoutParams(dp2px(3f), ViewGroup.LayoutParams.MATCH_PARENT)
            view.setBackgroundColor(dividerColor.toInt())
            tabMenuView.addView(view)

        }
    }

    private fun switchMenu(tab: TextView) {
        for (i in 0..tabMenuView.size step 2) {
            val t=tabMenuView.getChildAt(i) as TextView
            if (tabMenuView.getChildAt(i).tag  == tab) {
                //选中
//                Log.d("test", "${tab.text} $i")
                t.setBackgroundColor(textSelectColor)
                t.setCompoundDrawablesWithIntrinsicBounds(null,null,resources.getDrawable(menuSelectedIcon),null)
                ttcl.clickListener(i/2)
            } else {
                //未选中
//                Log.d("test", "....................... ${tab.text} $i")
                t.setBackgroundColor(textUnSelectColor)
                t.setCompoundDrawablesWithIntrinsicBounds(null,null,resources.getDrawable(menuUnSelectedIcon),null)

            }
        }

    }

    fun initSwitchData(s: List<String>) {

        ttWidth = tabWidth * s.size
        listNumber=s.size
        if (ttWidth<displayWidth){
            ttWidth= displayWidth.toFloat()
        }

        tabMenuView = LinearLayout(context)
        tabMenuView.orientation = HORIZONTAL
        tabMenuView.layoutParams = LayoutParams(ttWidth.toInt(), LayoutParams.WRAP_CONTENT)
        tabMenuView.setBackgroundColor(Color.RED)
        addView(tabMenuView, 0)

        val view = View(context)
        view.layoutParams = LayoutParams(ttWidth.toInt(), dp2px(2f))
        view.setBackgroundColor(dividerColor.toInt())
        addView(view, 1)

//        Log.d("data", "ttwidth:$ttWidth dp2px:${dp2px(ttWidth)} tabwidth:$tabWidth size:${s.size}")

        for (i in s.indices) {
            addTab(s[i], i,i != (s.size - 1))
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                next = event.x
            }
            MotionEvent.ACTION_MOVE -> {
                move = next - event.x
                if (ttWidth > displayWidth) {
//                    Log.d("move", "move:$move    moveopen:$moveOpen     ttwidth:$ttWidth")
                    if (move.toInt() != 0) {
                        when {
                            moveOpen + move > ttWidth - displayWidth -> scrollTo(
                                (ttWidth - displayWidth).toInt(),
                                0
                            )
                            moveOpen + move in 0f..ttWidth - displayWidth -> scrollTo(
                                move.toInt() + moveOpen.toInt(),
                                0
                            )
                            else -> scrollTo(0, 0)
                        }
                        postInvalidate()

                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                if (ttWidth > displayWidth) {
                    when {
                        moveOpen + move in 0f..ttWidth - displayWidth -> moveOpen += move
                        moveOpen + move > ttWidth - displayWidth -> moveOpen = ttWidth - displayWidth
                        else -> moveOpen = 0f
                    }

                }

                if (move.toInt()==0){
//                    Log.d("hear","hear:${((moveOpen+next)/(ttWidth/listNumber)).toInt()}   ${moveOpen+next}   ${ttWidth/listNumber}")
                    val i=((moveOpen+next)/(ttWidth/listNumber)).toInt()
                    switchMenu(tabMenuView.getChildAt(i*2) as TextView)
                }

                postInvalidate()
            }
        }

        return true
    }


}