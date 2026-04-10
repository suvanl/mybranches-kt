import com.suvanl.mybranches.git.Branch
import com.suvanl.mybranches.ui.AppState
import com.suvanl.mybranches.ui.moveDown
import com.suvanl.mybranches.ui.moveUp
import com.suvanl.mybranches.ui.visiblePageStart
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class NavigationTest {

    @Test
    fun shouldNotChangePageStartWhenSelectedIsInWindow() {
        // Given / When
        val result = visiblePageStart(selected = 3, pageStart = 0, pageSize = 10)

        // Then
        result shouldBe 0
    }

    @Test
    fun shouldSnapPageStartToSelectedWhenAboveWindow() {
        // Given / When
        val result = visiblePageStart(selected = 2, pageStart = 5, pageSize = 10)

        // Then
        result shouldBe 2
    }

    @Test
    fun shouldSnapSelectedToBottomOfWindowWhenBelowWindow() {
        // Given / When
        val result = visiblePageStart(selected = 15, pageStart = 0, pageSize = 10)

        // Then
        result shouldBe 6
    }

    @Test
    fun shouldNeverReturnNegativePageStart() {
        // Given / When
        val result = visiblePageStart(selected = 0, pageStart = 0, pageSize = 10)

        // Then
        result shouldBe 0
    }

    @Test
    fun shouldDecrementSelectedWhenMovingUpFromMiddle() {
        // Given
        val state = AppState.Ready(branches(10), selected = 5, pageStart = 0)

        // When
        val next = state.moveUp()

        // Then
        next.selected shouldBe 4
    }

    @Test
    fun shouldNotDecrementSelectedBelowZeroWhenAtTop() {
        // Given
        val state = AppState.Ready(branches(10), selected = 0, pageStart = 0)

        // When
        val next = state.moveUp()

        // Then
        next.selected shouldBe 0
    }

    @Test
    fun shouldScrollPageStartWhenMovingUpFromTopOfPage() {
        // Given
        val state = AppState.Ready(branches(20), selected = 5, pageStart = 5)

        // When
        val next = state.moveUp()

        // Then
        next.selected shouldBe 4
        next.pageStart shouldBe 4
    }

    @Test
    fun shouldNotScrollPageStartWhenMovingUpWithinPage() {
        // Given
        val state = AppState.Ready(branches(20), selected = 6, pageStart = 5)

        // When
        val next = state.moveUp()

        // Then
        next.selected shouldBe 5
        next.pageStart shouldBe 5
    }

    @Test
    fun shouldIncrementSelectedWhenMovingDownFromMiddle() {
        // Given
        val state = AppState.Ready(branches(10), selected = 3, pageStart = 0)

        // When
        val next = state.moveDown(pageSize = 10)

        // Then
        next.selected shouldBe 4
    }

    @Test
    fun shouldNotIncrementSelectedPastLastItemWhenAtBottom() {
        // Given
        val state = AppState.Ready(branches(5), selected = 4, pageStart = 0)

        // When
        val next = state.moveDown(pageSize = 10)

        // Then
        next.selected shouldBe 4
    }

    @Test
    fun shouldScrollPageStartWhenMovingDownFromBottomOfPage() {
        // Given
        val state = AppState.Ready(branches(20), selected = 9, pageStart = 0)

        // When
        val next = state.moveDown(pageSize = 10)

        // Then
        next.selected shouldBe 10
        next.pageStart shouldBe 1
    }

    @Test
    fun shouldNotScrollPageStartWhenMovingDownWithinPage() {
        // Given
        val state = AppState.Ready(branches(20), selected = 5, pageStart = 0)

        // When
        val next = state.moveDown(pageSize = 10)

        // Then
        next.selected shouldBe 6
        next.pageStart shouldBe 0
    }

    private fun branches(n: Int) = List(n) { Branch("user/branch-$it", current = it == 0) }
}
