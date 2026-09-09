package cc.shinemoon.occt.model.sanitize

import java.util.Locale

data class BoundingBox(
    val minX: Double, val minY: Double, val minZ: Double,
    val maxX: Double, val maxY: Double, val maxZ: Double
) {
    val dx: Double get() = maxX - minX
    val dy: Double get() = maxY - minY
    val dz: Double get() = maxZ - minZ
    val diagonal: Double get() = Math.sqrt(dx * dx + dy * dy + dz * dz)

    override fun toString(): String =
        String.format(Locale.US, "Min: (%.4f, %.4f, %.4f), Max: (%.4f, %.4f, %.4f) | Size: %.4f x %.4f x %.4f mm",
            minX, minY, minZ, maxX, maxY, maxZ, dx, dy, dz)
}
