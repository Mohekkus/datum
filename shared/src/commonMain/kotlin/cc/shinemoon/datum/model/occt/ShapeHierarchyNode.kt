package cc.shinemoon.datum.model.occt

import cc.shinemoon.datum.types.occt.ShapeType

data class ShapeHierarchyNode(
    val treeNodeId: Int,       // Unique tree traversal index
    val parentTreeNodeId: Int,
    val shapeType: ShapeType,
    val topologyIndex: Int,    // Shared native topological index (Edge #3, Face #1, etc.)
    val children: MutableList<ShapeHierarchyNode> = mutableListOf()
)