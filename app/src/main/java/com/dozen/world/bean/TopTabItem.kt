package com.dozen.world.bean

import java.io.Serializable

/**
 * Created by Hugo on 20-4-9.
 * Describe:
 *
 *
 *
 */
class TopTabItem(
    var id: Int?,
    var code: String?,
    var location: String?,
    var optional: Int?,
    var collection: Int?,
    var good: Int?,
    var bad: Int?
) : Serializable {
    constructor():this(0,null,null,0,0,0,0)
}