package cc.shinemoon.datum

import cc.shinemoon.occt.OcctModel

sealed interface Screen {
    data object DropZone : Screen
    data class Results(val model: OcctModel) : Screen
}