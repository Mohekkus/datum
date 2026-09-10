package cc.shinemoon.datum.utility.preset

import cc.shinemoon.datum.ui.launcher.OnDragListener
import java.io.File

interface DragListener: OnDragListener {
    override fun onStart() {}
    override fun onEnded() {}
    override fun onValidFile(file: File) {}
    override fun onInvalidFile() {}
}