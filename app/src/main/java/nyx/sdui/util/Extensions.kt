package nyx.sdui.util

import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import nyx.sdui.components.base.CStyle

// is this even needed??
fun List<Int>.paddingValues() = when (size) {
    1 -> PaddingValues(
        get(1).dp,
        get(2).dp,
        get(3).dp,
        get(4).dp
    )
    2 -> PaddingValues(
        get(0).dp,
        get(1).dp,
        get(0).dp,
        get(1).dp
    )
    4 -> PaddingValues(
        get(0).dp,
        get(1).dp,
        get(2).dp,
        get(3).dp
    )
    else -> throw Exception("Padding can only be for 1, 2 or 4 directions.")
}

//add more here
fun Modifier.applyStyle(style:CStyle?) = style?.let{ s ->
        s.padding?.paddingValues()?.let { then(Modifier.padding(it)) }
        s.width?.let { if(it==-1) fillMaxWidth() else then(width(it.dp)) }
        s.height?.let { if(it==-1) fillMaxHeight() else then(height(it.dp)) }
}?:Modifier



