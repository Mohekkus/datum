package cc.shinemoon.occt.classifier

enum class ShapeType(val value: Int) {
    COMPOUND(0), COMPSOLID(1), SOLID(2), SHELL(3),
    FACE(4), WIRE(5), EDGE(6), VERTEX(7), SHAPE(8);

    companion object {
        fun fromValue(value: Int): ShapeType = entries.firstOrNull { it.value == value } ?: SHAPE
    }
}