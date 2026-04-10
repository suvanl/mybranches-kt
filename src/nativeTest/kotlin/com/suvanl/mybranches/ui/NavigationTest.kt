package com.suvanl.mybranches.ui

import com.suvanl.mybranches.git.Branch
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class NavigationTest {

    @Test
    fun shouldNotChangePageStartWhenSelectedIsInWindow() {
        // Given / When
        val result = visiblePageStart(selectedIndex = 3, pageStartIndex = 0, pageSize = 10)

        // Then
        result shouldBe 0
    }

    @Test
    fun shouldSnapPageStartToSelectedWhenAboveWindow() {
        // Given / When
        val result = visiblePageStart(selectedIndex = 2, pageStartIndex = 5, pageSize = 10)

        // Then
        result shouldBe 2
    }

    @Test
    fun shouldSnapSelectedToBottomOfWindowWhenBelowWindow() {
        // Given / When
        val result = visiblePageStart(selectedIndex = 15, pageStartIndex = 0, pageSize = 10)

        // Then
        result shouldBe 6
    }

    @Test
    fun shouldNeverReturnNegativePageStart() {
        // Given / When
        val result = visiblePageStart(selectedIndex = 0, pageStartIndex = 0, pageSize = 10)

        // Then
        result shouldBe 0
    }

    @Test
    fun shouldDecrementSelectedWhenMovingUpFromMiddle() {
        // Given
        val state = AppState.Ready(branches = givenBranches(10), selectedItemIndex = 5, pageStartIndex = 0)

        // When
        val next = state.moveUp()

        // Then
        next.selectedItemIndex shouldBe 4
    }

    @Test
    fun shouldNotDecrementSelectedBelowZeroWhenAtTop() {
        // Given
        val state = AppState.Ready(branches = givenBranches(10), selectedItemIndex = 0, pageStartIndex = 0)

        // When
        val next = state.moveUp()

        // Then
        next.selectedItemIndex shouldBe 0
    }

    @Test
    fun shouldScrollPageStartWhenMovingUpFromTopOfPage() {
        // Given
        val state = AppState.Ready(branches = givenBranches(20), selectedItemIndex = 5, pageStartIndex = 5)

        // When
        val next = state.moveUp()

        // Then
        next.selectedItemIndex shouldBe 4
        next.pageStartIndex shouldBe 4
    }

    @Test
    fun shouldNotScrollPageStartWhenMovingUpWithinPage() {
        // Given
        val state = AppState.Ready(branches = givenBranches(20), selectedItemIndex = 6, pageStartIndex = 5)

        // When
        val next = state.moveUp()

        // Then
        next.selectedItemIndex shouldBe 5
        next.pageStartIndex shouldBe 5
    }

    @Test
    fun shouldIncrementSelectedWhenMovingDownFromMiddle() {
        // Given
        val state = AppState.Ready(branches = givenBranches(10), selectedItemIndex = 3, pageStartIndex = 0)

        // When
        val next = state.moveDown(currentPageSize = 10)

        // Then
        next.selectedItemIndex shouldBe 4
    }

    @Test
    fun shouldNotIncrementSelectedPastLastItemWhenAtBottom() {
        // Given
        val state = AppState.Ready(branches = givenBranches(5), selectedItemIndex = 4, pageStartIndex = 0)

        // When
        val next = state.moveDown(currentPageSize = 10)

        // Then
        next.selectedItemIndex shouldBe 4
    }

    @Test
    fun shouldScrollPageStartWhenMovingDownFromBottomOfPage() {
        // Given
        val state = AppState.Ready(branches = givenBranches(20), selectedItemIndex = 9, pageStartIndex = 0)

        // When
        val next = state.moveDown(currentPageSize = 10)

        // Then
        next.selectedItemIndex shouldBe 10
        next.pageStartIndex shouldBe 1
    }

    @Test
    fun shouldNotScrollPageStartWhenMovingDownWithinPage() {
        // Given
        val state = AppState.Ready(branches = givenBranches(20), selectedItemIndex = 5, pageStartIndex = 0)

        // When
        val next = state.moveDown(currentPageSize = 10)

        // Then
        next.selectedItemIndex shouldBe 6
        next.pageStartIndex shouldBe 0
    }

    private fun givenBranches(n: Int) = List(n) {
        Branch(
            name = "user/branch-$it",
            isCurrent = it == 0,
        )
    }
}
