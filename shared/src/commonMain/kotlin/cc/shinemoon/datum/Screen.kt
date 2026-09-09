package cc.shinemoon.datum

sealed interface Screen {
    data object DropZone : Screen
    data object Results : Screen
}