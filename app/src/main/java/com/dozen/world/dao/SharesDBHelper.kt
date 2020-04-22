package com.dozen.world.dao

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.dozen.world.bean.TopTabItem
import org.jetbrains.anko.db.ManagedSQLiteOpenHelper

/**
 * Created by Hugo on 20-4-10.
 * Describe:
 *
 *
 *
 */
class SharesDBHelper(
    var ctx: Context,
    private var version: Int = DATABASE_VERSION
) : ManagedSQLiteOpenHelper(ctx, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        var DATABASE_NAME = "shares.db"
        var DATABASE_TABLE = "shares"
        var DATABASE_VERSION: Int = 1

        @SuppressLint("StaticFieldLeak")
        private var instance: SharesDBHelper? = null

        fun getInstance(ctx: Context, version: Int = 0): SharesDBHelper {
            if (instance == null) {
                instance = if (version > 0) SharesDBHelper(
                    ctx.applicationContext,
                    version
                ) else SharesDBHelper(ctx.applicationContext)
            }
            return instance!!
        }
    }


    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL(
            "CREATE TABLE IF NOT EXISTS $DATABASE_TABLE" +
                    "(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,code VARCHAR NOT NULL," +
                    "location VARCHAR NOT NULL,optional INTEGER NOT NULL,collection INTEGER NOT NULL,good INTEGER NOT NULL,bad INTEGER NOT NULL)"
        )
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    fun insert(item: TopTabItem): Int {
        val item = mutableListOf<TopTabItem>(item)
        return insert(item)
    }

    fun insert(itemList: MutableList<TopTabItem>): Int {
        var result: Int = -1

        for (i in itemList.indices) {
            val item = itemList[i]
            var tempList: List<TopTabItem>
            if (item.code?.isNotEmpty()!!) {
                val condition = "code='${item.code}'"
                tempList = query(condition)
                if (tempList.isNotEmpty()) {
                    update(item, condition)
                    result = itemList[0].id!!
                    continue
                }

            }

            val cv = ContentValues()
            cv.put("code", item.code)
            cv.put("location", item.location)
            cv.put("optional", item.optional)
            cv.put("collection", item.collection)
            cv.put("good", item.good)
            cv.put("bad", item.bad)


            use {
                result = insert(DATABASE_TABLE, "", cv).toInt()
            }
        }

        return result
    }

    fun delete(condition: String): Int {
        var count = 0
        use {
            count = delete(DATABASE_TABLE, condition, null)
        }
        return count
    }

    fun update(item: TopTabItem, condition: String = "_id=${item.id}"): Int {

        val cv = ContentValues()
        cv.put("code", item.code)
        cv.put("location", item.location)
        cv.put("optional", item.optional)
        cv.put("collection", item.collection)
        cv.put("good", item.good)
        cv.put("bad", item.bad)

        var count = 0
        use {
            count = update(DATABASE_TABLE, cv, condition,null)
        }
        cv.clear()

        return count

    }

    fun query(condition: String): List<TopTabItem> {

        val sql = "select _id,code,location,optional,collection,good,bad from $DATABASE_TABLE where $condition;"

        var itemList = mutableListOf<TopTabItem>()

        use {
            val cursor = rawQuery(sql, null)

            if (cursor.moveToFirst()) {
                while (true) {
                    val item: TopTabItem = TopTabItem()
                    item.id=cursor.getInt(0)
                    item.code = cursor.getString(1)
                    item.location=cursor.getString(2)
                    item.optional=cursor.getInt(3)
                    item.collection=cursor.getInt(4)
                    item.good=cursor.getInt(5)
                    item.bad=cursor.getInt(6)

                    itemList.add(item)
                    if (cursor.isLast) {
                        break
                    }
                    cursor.moveToNext()
                }
            }
            cursor.close()

        }
        return itemList
    }

    fun queryByID(location:Int,number:Int):List<TopTabItem>{
        val sql = "select _id,code,location,optional,collection,good,bad from $DATABASE_TABLE group by _id,code order by _id limit $number offset $location;"

        var itemList = mutableListOf<TopTabItem>()

        use {
            val cursor = rawQuery(sql, null)

            if (cursor.moveToFirst()) {
                while (true) {
                    val item: TopTabItem = TopTabItem()
                    item.id=cursor.getInt(0)
                    item.code = cursor.getString(1)
                    item.location=cursor.getString(2)
                    item.optional=cursor.getInt(3)
                    item.collection=cursor.getInt(4)
                    item.good=cursor.getInt(5)
                    item.bad=cursor.getInt(6)

                    itemList.add(item)
                    if (cursor.isLast) {
                        break
                    }
                    cursor.moveToNext()
                }
            }
            cursor.close()

        }
        return itemList
    }

    fun dbisNull():Boolean{
        var amount=0

        use {
            val c=rawQuery("select * from $DATABASE_TABLE", null)
            amount=c.count
        }

        return amount <= 0

    }

    fun queryByCode(code: String): TopTabItem {
        val itemList = query("code='$code'")
        return if (itemList.isNotEmpty()) itemList[0] else TopTabItem()
    }

    fun deleteAll(): Int = delete("1=1")

    fun queryAll(): List<TopTabItem> = query("1=1")

}