package cc.shinemoon.datum.types.occt

enum class SurfaceType(val value: Int) {
    PLANE(0), CYLINDER(1), CONE(2), SPHERE(3), TORUS(4),
    BEZIER_SURFACE(5), BSPLINE_SURFACE(6), SURFACE_OF_REVOLUTION(7),
    SURFACE_OF_EXTRUSION(8), OTHER_SURFACE(9);

    companion object {
        fun fromValue(value: Int): SurfaceType = entries.firstOrNull { it.value == value } ?: OTHER_SURFACE
    }
}