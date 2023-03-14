package com.mobile.videocutter.domain.model

import com.mobile.videocutter.R

data class ToolVideo(
    val image: Int,
    val name: String
)

fun mockToolVideo(): List<ToolVideo> {
    val itemList = listOf<ToolVideo>(
        ToolVideo(R.drawable.ic_tool_1, "Crop"),
        ToolVideo(R.drawable.ic_tool_2, "Cut"),
        ToolVideo(R.drawable.ic_tool_3, "Speed"),
        ToolVideo(R.drawable.ic_tool_4, "Filter"),
        ToolVideo(R.drawable.ic_tool_5, "Music"),
        ToolVideo(R.drawable.ic_tool_6, "Rotate"),
    )

    return itemList
}
