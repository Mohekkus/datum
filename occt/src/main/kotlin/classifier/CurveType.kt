package cc.shinemoon.occt.classifier

enum class CurveType(val value: Int) {
    LINE(0), CIRCLE(1), ELLIPSE(2), HYPERBOLA(3), PARABOLA(4),
    BEZIER_CURVE(5), BSPLINE_CURVE(6), OTHER_CURVE(7);

    companion object {
        fun fromValue(value: Int): CurveType = entries.firstOrNull { it.value == value } ?: OTHER_CURVE
    }
}