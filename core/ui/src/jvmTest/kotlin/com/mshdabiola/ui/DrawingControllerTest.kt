package com.mshdabiola.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect // For selectionRect and path bounds logic
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerType
import com.mshdabiola.model.note.PenProperties
import com.mshdabiola.model.note.Point
import com.mshdabiola.model.note.Path as DrawingPath // Alias for your model's Path
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DrawingControllerTest {

    private lateinit var controller: DrawingController
    private val dummyOnDragEndLambda: () -> Unit = {} // Dummy lambda for onDrag calls

    @Before
    fun setUp() {
        controller = DrawingController()
    }

    @Test
    fun `initial state is correct`() {
        assertTrue(controller.drawingPaths.isEmpty())
        assertFalse(controller.canUndo)
        assertTrue(controller.redo.isEmpty())
        assertFalse(controller.canRedo)
        assertEquals(DrawingTool.DRAW, controller.currentTool)
        assertEquals(PenProperties(), controller.currentDrawingProperties)
        assertTrue(controller.currentPath.points.isEmpty())
        assertEquals(Offset.Unspecified, controller.startDragPoint)
        assertNull(controller.selectionRect)
        assertNull(controller.collectiveSelectedPathsBounds)
    }

    @Test
    fun `setDrawingTool changes currentTool`() {
        controller.setDrawingTool(DrawingTool.ERASE)
        assertEquals(DrawingTool.ERASE, controller.currentTool)

        controller.setDrawingTool(DrawingTool.SELECT)
        assertEquals(DrawingTool.SELECT, controller.currentTool)

        controller.setDrawingTool(DrawingTool.DRAW)
        assertEquals(DrawingTool.DRAW, controller.currentTool)
    }

    @Test
    fun `setDrawingTool to ERASE clears selections and selectionRect`() {
        val selectedPath = createDummyPath(1, isSelected = true)
        controller.drawingPaths.add(selectedPath)
        controller.selectionRect = Rect(0f, 0f, 10f, 10f)
        controller.collectiveSelectedPathsBounds = Rect(1f,1f,2f,2f)
        assertNotNull(controller.collectiveSelectedPathsBounds)

        controller.setDrawingTool(DrawingTool.ERASE)

        assertEquals(DrawingTool.ERASE, controller.currentTool)
        assertFalse(controller.drawingPaths.any { it.isSelected })
        assertNull(controller.selectionRect)
        assertNull(controller.collectiveSelectedPathsBounds)
    }

    @Test
    fun `setDrawingTool to DRAW clears selections and selectionRect`() {
        val selectedPath = createDummyPath(1, isSelected = true)
        controller.drawingPaths.add(selectedPath)
        controller.selectionRect = Rect(0f, 0f, 10f, 10f)
        controller.collectiveSelectedPathsBounds = Rect(1f,1f,2f,2f)
        assertNotNull(controller.collectiveSelectedPathsBounds)

        controller.setDrawingTool(DrawingTool.DRAW)

        assertEquals(DrawingTool.DRAW, controller.currentTool)
        assertFalse(controller.drawingPaths.any { it.isSelected })
        assertNull(controller.selectionRect)
        assertNull(controller.collectiveSelectedPathsBounds)
    }

    @Test
    fun `setDrawingTool to SELECT does NOT immediately clear selections or selectionRect`() {
        val selectedPath = createDummyPath(1, isSelected = true)
        controller.drawingPaths.add(selectedPath)
        controller.selectionRect = Rect(0f, 0f, 10f, 10f)
        controller.collectiveSelectedPathsBounds = Rect(1f,1f,2f,2f)
        val initialBounds = controller.collectiveSelectedPathsBounds
        assertNotNull(initialBounds)

        controller.setDrawingTool(DrawingTool.SELECT)

        assertEquals(DrawingTool.SELECT, controller.currentTool)
        assertTrue(controller.drawingPaths.any { it.isSelected })
        assertNotNull(controller.selectionRect)
        assertEquals(initialBounds, controller.collectiveSelectedPathsBounds)
    }

    // --- Undo/Redo Tests ---
    @Test
    fun `undo moves path to redo list and updates canUndo canRedo`() {
        val path1 = createDummyPath(1)
        controller.drawingPaths.add(path1)
        controller.setRedoUndo()

        assertTrue(controller.canUndo)
        assertFalse(controller.canRedo)

        controller.undo()

        assertFalse(controller.canUndo)
        assertTrue(controller.canRedo)
        assertTrue(controller.drawingPaths.isEmpty())
        assertEquals(path1, controller.redo.first())
    }

    @Test
    fun `redo moves path to drawingPaths and updates canUndo canRedo`() {
        val path1 = createDummyPath(1)
        controller.redo.add(path1)
        controller.setRedoUndo()

        assertFalse(controller.canUndo)
        assertTrue(controller.canRedo)

        controller.redo()

        assertTrue(controller.canUndo)
        assertFalse(controller.canRedo)
        assertEquals(path1, controller.drawingPaths.first())
        assertTrue(controller.redo.isEmpty())
    }

    @Test
    fun `undo redo interaction works correctly`() {
        val path1 = createDummyPath(1)
        val path2 = createDummyPath(2)
        controller.drawingPaths.addAll(listOf(path1, path2))
        controller.setRedoUndo()

        controller.undo() // path2 to redo
        controller.undo() // path1 to redo
        assertFalse(controller.canUndo)
        assertTrue(controller.canRedo)
        assertEquals(2, controller.redo.size)
        assertEquals(path1, controller.redo[1])

        controller.redo() // path1 to drawingPaths
        assertTrue(controller.canUndo)
        assertTrue(controller.canRedo)
        assertEquals(path1, controller.drawingPaths.first())
        assertEquals(path2, controller.redo.first())
    }


    // --- Clear Canvas Tests ---
    @Test
    fun `clearCanvas clears drawingPaths, populates redo, clears selections, updates undoRedo`() {
        val path1 = createDummyPath(1)
        val path2Selected = createDummyPath(2, isSelected = true)
        controller.drawingPaths.addAll(listOf(path1, path2Selected))
        controller.selectionRect = Rect(0f,0f,10f,10f)
        controller.collectiveSelectedPathsBounds = Rect(1f,1f,2f,2f)
//        controller.setRedoUndo()
        val initialDrawingPaths = controller.drawingPaths.toList()

        controller.clearCanvas()

        assertTrue(controller.drawingPaths.isEmpty())
        assertFalse(controller.canUndo)
        assertTrue(controller.canRedo)
        assertEquals(initialDrawingPaths.size, controller.redo.size)
        assertEquals(initialDrawingPaths[0], controller.redo[0])
        assertEquals(initialDrawingPaths[1].copy(isSelected=false), controller.redo[1])
        assertNull(controller.selectionRect)
        assertNull(controller.collectiveSelectedPathsBounds)
        assertFalse(controller.redo.any { it.isSelected })
    }

    // --- Path Selections Tests ---
    @Test
    fun `clearPathSelections deselects all paths and clears collectiveBounds`() {
        val path1 = createDummyPath(1, isSelected = true)
        controller.drawingPaths.add(path1)
        controller.collectiveSelectedPathsBounds = Rect(1f,1f,10f,10f)

        controller.clearPathSelections()

        assertFalse(controller.drawingPaths[0].isSelected)
        assertNull(controller.collectiveSelectedPathsBounds)
    }

    @Test
    fun `updateCollectiveSelectedBounds is not null when paths are selected`(){
        val selectedPath = createDummyPath(1, isSelected = true)
        controller.drawingPaths.add(selectedPath)
        controller.updateCollectiveSelectedBounds()
        assertNotNull(controller.collectiveSelectedPathsBounds)
    }

    @Test
    fun `updateCollectiveSelectedBounds becomes null after deselecting all paths`(){
        val path1 = createDummyPath(1, isSelected = true)
        controller.drawingPaths.add(path1)
        controller.updateCollectiveSelectedBounds()
        assertNotNull(controller.collectiveSelectedPathsBounds)

        controller.drawingPaths[0] = controller.drawingPaths[0].copy(isSelected = false)
        controller.updateCollectiveSelectedBounds()
        assertNull(controller.collectiveSelectedPathsBounds)
    }

    // --- Drag Operation Tests ---

    // -- onDragStart --
    @Test
    fun `onDragStart with DRAW tool initializes currentPath, startDragPoint, clears selections`() {
        controller.setDrawingTool(DrawingTool.DRAW)
        val selectedPath = createDummyPath(1, isSelected = true)
        controller.drawingPaths.add(selectedPath)
        controller.collectiveSelectedPathsBounds = Rect(1f,1f,2f,2f)
        val testProperties = PenProperties(colorIndex = 2, lineWidth = 10)
        controller.currentDrawingProperties = testProperties
        val startOffset = Offset(10f, 20f)

        controller.onDragStart(startOffset)

        assertEquals(startOffset, controller.startDragPoint)
        assertEquals(testProperties, controller.currentPath.penProperties)
        assertTrue(controller.currentPath.points.isEmpty())
        assertFalse(controller.drawingPaths.any { it.isSelected })
        assertNull(controller.collectiveSelectedPathsBounds)
        assertNull(controller.selectionRect)
    }

    @Test
    fun `onDragStart with SELECT tool, click outside selection, clears previous selections, sets selectionRect`() {
        controller.setDrawingTool(DrawingTool.SELECT)
        val path1 = createDummyPath(1, isSelected = true)
        controller.drawingPaths.add(path1)
        controller.collectiveSelectedPathsBounds = Rect(1f, 1f, 2f, 2f) // Path1 bounds
        val startOffset = Offset(100f, 100f) // Click outside

        controller.onDragStart(startOffset)

        assertEquals(Rect(startOffset, startOffset), controller.selectionRect)
        assertFalse(controller.drawingPaths.any { it.isSelected })
        assertNull(controller.collectiveSelectedPathsBounds)
    }

    @Test
    fun `onDragStart with SELECT tool, click inside selection, keeps selections, sets selectionRect`() {
        controller.setDrawingTool(DrawingTool.SELECT)
        val path1 = createDummyPath(1, isSelected = true)
        controller.drawingPaths.add(path1)
        controller.collectiveSelectedPathsBounds = Rect(1f, 1f, 2f, 2f) // Path1 bounds
        val initialBounds = controller.collectiveSelectedPathsBounds
        val startOffset = Offset(1.5f, 1.5f) // Click inside

        controller.onDragStart(startOffset)

        assertEquals(Rect(startOffset, startOffset), controller.selectionRect)
        assertTrue(controller.drawingPaths.any { it.isSelected })
        assertEquals(initialBounds, controller.collectiveSelectedPathsBounds)
    }

    // -- onDrag --
    @Test
    fun `onDrag with DRAW tool adds points to currentPath`() {
        controller.setDrawingTool(DrawingTool.DRAW)
        val penProps = PenProperties(colorIndex = 1)
        controller.currentDrawingProperties = penProps
        controller.onDragStart(Offset(10f, 10f))
        val dragPoint = Offset(20f, 20f)

        controller.onDrag(dragPoint, Offset(10f,10f), dummyOnDragEndLambda)

        assertEquals(1, controller.currentPath.points.size)
        assertEquals(Point(dragPoint.x, dragPoint.y), controller.currentPath.points[0])
        assertEquals(penProps, controller.currentPath.penProperties)
    }

    @Test
    fun `onDrag with ERASE tool removes intersecting paths`() {
        controller.setDrawingTool(DrawingTool.ERASE)
        val pathToBeErased = createDummyPath(15) // Points (15,15) to (16,16)
        controller.drawingPaths.add(pathToBeErased)
        controller.onDragStart(Offset(10f, 10f))
        val dragPoint = Offset(20f, 20f) // Erase rect from (10,10) to (20,20)

        controller.onDrag(dragPoint, Offset(10f,10f), dummyOnDragEndLambda)

        assertTrue(controller.drawingPaths.isEmpty())
        assertEquals(1, controller.redo.size)
        assertEquals(pathToBeErased, controller.redo.first())
    }

    @Test
    fun `onDrag with SELECT tool updates selectionRect`() {
        controller.setDrawingTool(DrawingTool.SELECT)
        val startOffset = Offset(5f, 5f)
        controller.onDragStart(startOffset)
        val dragPoint = Offset(50f, 60f)

        controller.onDrag(dragPoint, Offset(45f,55f), dummyOnDragEndLambda)

        assertEquals(Rect(startOffset, dragPoint), controller.selectionRect)
    }

    // -- onDragEnd --
    @Test
    fun `onDragEnd with DRAW tool adds currentPath to drawingPaths and resets currentPath`() {
        controller.setDrawingTool(DrawingTool.DRAW)
        val penProps = PenProperties(colorIndex = 3)
        controller.currentDrawingProperties = penProps
        controller.onDragStart(Offset(0f,0f))
        controller.currentPath = controller.currentPath.copy(points = listOf(Point(10f,10f)))
        val pathAdded = controller.currentPath

        controller.onDragEnd()

        assertEquals(1, controller.drawingPaths.size)
        assertEquals(pathAdded, controller.drawingPaths.first())
        assertTrue(controller.currentPath.points.isEmpty())
        assertEquals(penProps, controller.currentPath.penProperties)
    }

    @Test
    fun `onDragEnd with SELECT tool selects overlapping paths, updates bounds, clears selectionRect`() {
        controller.setDrawingTool(DrawingTool.SELECT)
        // path1 has points (1,1) to (2,2). Its getBounds() will reflect this.
        val path1 = createDummyPath(1)
        controller.drawingPaths.add(path1)
        controller.onDragStart(Offset(0f,0f))
        // Selection Rect (0,0) to (5,5) - should overlap path1
        controller.selectionRect = Rect(0f,0f, 5f, 5f)

        controller.onDragEnd()

        assertTrue(controller.drawingPaths[0].isSelected)
        assertNotNull(controller.collectiveSelectedPathsBounds)
        assertNull(controller.selectionRect)
    }

    @Test
    fun `onDragEnd with SELECT tool, no selectionRect, does not change selections or bounds`(){
        controller.setDrawingTool(DrawingTool.SELECT)
        val path1 = createDummyPath(1, isSelected = true)
        controller.drawingPaths.add(path1)
        controller.updateCollectiveSelectedBounds()
        val initialSelectedState = controller.drawingPaths[0].isSelected
        val initialBounds = controller.collectiveSelectedPathsBounds
        controller.selectionRect = null

        controller.onDragEnd()

        assertEquals(initialSelectedState, controller.drawingPaths[0].isSelected)
        assertEquals(initialBounds, controller.collectiveSelectedPathsBounds)
    }


    // Helper to create a dummy path for testing
    private fun createDummyPath(id: Int, isSelected: Boolean = false): DrawingPath {
        // Creates a path with points (id,id) and (id+1,id+1)
        return DrawingPath(
            points = mutableListOf(Point(id.toFloat(), id.toFloat()), Point(id + 1f, id + 1f)),
            penProperties = PenProperties(colorIndex = id % colors.size),
            isSelected = isSelected
        )
    }
}
