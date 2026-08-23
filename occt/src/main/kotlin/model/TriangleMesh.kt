package cc.shinemoon.occt.model

import java.io.File
import java.util.Locale

data class TriangleMesh(
    val vertices: FloatArray,
    val normals: FloatArray,
    val triangles: IntArray
) {
    val vertexCount: Int get() = vertices.size / 3
    val triangleCount: Int get() = triangles.size / 3

    fun saveWavefrontObj(targetFile: File) {
        targetFile.bufferedWriter().use { writer ->
            writer.write("# Exported from OcctBridge\n")
            writer.write("# Vertices: $vertexCount, Triangles: $triangleCount\n")
            for (i in 0 until vertexCount) {
                writer.write(String.format(Locale.US, "v %.6f %.6f %.6f\n", vertices[i * 3], vertices[i * 3 + 1], vertices[i * 3 + 2]))
            }
            for (i in 0 until vertexCount) {
                writer.write(String.format(Locale.US, "vn %.6f %.6f %.6f\n", normals[i * 3], normals[i * 3 + 1], normals[i * 3 + 2]))
            }
            for (i in 0 until triangleCount) {
                val i1 = triangles[i * 3] + 1
                val i2 = triangles[i * 3 + 1] + 1
                val i3 = triangles[i * 3 + 2] + 1
                writer.write("f $i1//$i1 $i2//$i2 $i3//$i3\n")
            }
        }
    }
}