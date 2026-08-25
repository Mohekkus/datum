package cc.shinemoon.datum

import cc.shinemoon.occt.model.OcctInspectionData

sealed interface Screen {
    data object DropZone : Screen
    data object Results : Screen
}