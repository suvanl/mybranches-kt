package com.suvanl.mybranches.ui

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class FuzzyMatchTest {

    @Test
    fun shouldMatchExactSubstring() {
        // Given
        val text = "feature/bugfix"

        // When / Then
        text.fuzzyContains("bug") shouldBe true
    }

    @Test
    fun shouldMatchSubsequenceAcrossSegments() {
        // Given
        val text = "feature/bugfix"

        // When / Then
        text.fuzzyContains("fb") shouldBe true
    }

    @Test
    fun shouldMatchCaseInsensitively() {
        // Given
        val text = "JIRA-123/fix-thing"

        // When / Then
        text.fuzzyContains("jira") shouldBe true
    }

    @Test
    fun shouldMatchEmptyQuery() {
        // Given
        val text = "anything"

        // When / Then
        text.fuzzyContains("") shouldBe true
    }

    @Test
    fun shouldNotMatchWhenCharacterOrderIsWrong() {
        // Given
        val text = "abc"

        // When / Then
        text.fuzzyContains("ba") shouldBe false
    }

    @Test
    fun shouldNotMatchWhenQueryHasExtraCharacters() {
        // Given
        val text = "main"

        // When / Then
        text.fuzzyContains("mainx") shouldBe false
    }

    @Test
    fun shouldMatchEmptyQueryAgainstEmptyText() {
        // When / Then
        "".fuzzyContains("") shouldBe true
    }

    @Test
    fun shouldNotMatchNonEmptyQueryAgainstEmptyText() {
        // When / Then
        "".fuzzyContains("a") shouldBe false
    }
}
