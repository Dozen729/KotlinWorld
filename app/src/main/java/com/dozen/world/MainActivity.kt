package com.dozen.world

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dozen.world.bean.RoundItem
import com.dozen.world.custom.RoundClickListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), RoundClickListener {

    private var rotateState: Boolean = true
    private lateinit var autoRunnable:Runnable

    companion object{
        val USERPERMISSION=10001
    }

    override fun clickListener(data: RoundItem) {

        rotateState = false

        if (data.className!!.isNotEmpty()) {
            startActivity(Intent(baseContext, Class.forName("${data.className}")))
        } else {
            Toast.makeText(baseContext, "className is empty", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val data = ArrayList<RoundItem>()
//        for (i in 1..30) {
//            val co: Color = Color.valueOf(
//                (Math.random() * 255).toFloat(),
//                (Math.random() * 255).toFloat(), (Math.random() * 255).toFloat()
//            )
//            val item = RoundItem(
//                i,
//                "name $i",
//                (Math.random() * 20 + 1).toFloat(),
//                co.toArgb(),
//                "",
//                "重写构造方法。一般是重写前三个构造方法，让前两个构造方法最终调用三个参数的构造方法，然后在第三个构造方法中进行一些初始化操作。"
//            )
//            data.add(item)
//        }
//        round_test.dataInit=data


        round_test.dataInit = Constant.getDataList()
        round_test.clickListener = this
        round_test.rotateSpeed = 3f

        //无聊旋转中无聊旋转中无聊旋转中无聊旋转中无聊旋转中
        startRotate()

        checkPermission()

    }

    private fun checkPermission(){
        Constant.user_permission.forEach {
            val hasPermission=ContextCompat.checkSelfPermission(application,it)
            if (hasPermission!=PackageManager.PERMISSION_GRANTED){
                //没有权限
                ActivityCompat.requestPermissions(this, arrayOf(it),USERPERMISSION)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode== USERPERMISSION){
            if (grantResults.isNotEmpty() &&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                //用户同意
                Log.d("testtest","success")
                checkPermission()
            }else{
                //用户不同意
                Log.d("testtest","fail")
            }
        }
    }

    private fun startRotate() {
        autoRunnable= Runnable {
            autoRotate.sendEmptyMessage(1)
            if (rotateState){
                autoRotate.postDelayed(autoRunnable,100)
            }
        }
        autoRunnable.run()
    }

    private val autoRotate = Handler{
        if (it.what==1){
            round_test.degrees=1f
        }
        false
    }

    override fun onRestart() {
        super.onRestart()
        rotateState = true
        startRotate()
    }


}
