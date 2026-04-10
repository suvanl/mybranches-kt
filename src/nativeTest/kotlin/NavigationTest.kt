import com.suvanl.mybranches.git.Branch
import com.suvanl.mybranches.ui.AppState
import com.suvanl.mybranches.ui.moveDown
import com.suvanl.mybranches.ui.moveUp
import com.suvanl.mybranches.ui.visiblePageStart
import kotlin.test.Test
import kotlin.test.assertEquals

class NavigationTest {

    private fun branches(n: Int) = List(n) { Branch("user/branch-$it", current = it == 0) }

    // visiblePageStart

    @Test
    fun visiblePageStart_selectedInWindow_unchanged() {
        assertEquals(0, visiblePageStart(selected = 3, pageStart = 0, pageSize = 10))
    }

    @Test
    fun visiblePageStart_selectedAboveWindow_snapsToSelected() {
        assertEquals(2, visiblePageStart(selected = 2, pageStart = 5, pageSize = 10))
    }

    @Test
    fun visiblePageStart_selectedBelowWindow_snapsSelectedToBottom() {
        assertEquals(6, visiblePageStart(selected = 15, pageStart = 0, pageSize = 10))
    }

    @Test
    fun visiblePageStart_neverNegative() {
        assertEquals(0, visiblePageStart(selected = 0, pageStart = 0, pageSize = 10))
    }

    // moveUp

    @Test
    fun moveUp_fromMiddle_decrementsSelected() {
        val state = AppState.Ready(branches(10), selected = 5, pageStart = 0)
        assertEquals(4, state.moveUp().selected)
    }

    @Test
    fun moveUp_atTop_staysAt0() {
        val state = AppState.Ready(branches(10), selected = 0, pageStart = 0)
        assertEquals(0, state.moveUp().selected)
    }

    @Test
    fun moveUp_atTopOfPage_scrollsPageStart() {
        val state = AppState.Ready(branches(20), selected = 5, pageStart = 5)
        val next = state.moveUp()
        assertEquals(4, next.selected)
        assertEquals(4, next.pageStart)
    }

    @Test
    fun moveUp_withinPage_doesNotScrollPageStart() {
        val state = AppState.Ready(branches(20), selected = 6, pageStart = 5)
        val next = state.moveUp()
        assertEquals(5, next.selected)
        assertEquals(5, next.pageStart)
    }

    // moveDown

    @Test
    fun moveDown_fromMiddle_incrementsSelected() {
        val state = AppState.Ready(branches(10), selected = 3, pageStart = 0)
        assertEquals(4, state.moveDown(pageSize = 10).selected)
    }

    @Test
    fun moveDown_atBottom_staysAtLast() {
        val state = AppState.Ready(branches(5), selected = 4, pageStart = 0)
        assertEquals(4, state.moveDown(pageSize = 10).selected)
    }

    @Test
    fun moveDown_atBottomOfPage_scrollsPageStart() {
        val state = AppState.Ready(branches(20), selected = 9, pageStart = 0)
        val next = state.moveDown(pageSize = 10)
        assertEquals(10, next.selected)
        assertEquals(1, next.pageStart)
    }

    @Test
    fun moveDown_withinPage_doesNotScrollPageStart() {
        val state = AppState.Ready(branches(20), selected = 5, pageStart = 0)
        val next = state.moveDown(pageSize = 10)
        assertEquals(6, next.selected)
        assertEquals(0, next.pageStart)
    }
}
