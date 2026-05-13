package com.suvanl.mybranches.search

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class FuzzyMatchTest {

    @Test
    fun shouldMatchExactSubstring() {
        // Given
        val text = "feature/bugfix"

        // When
        val result = text.fuzzyContains("bug")

        // Then
        result shouldBe true
    }

    @Test
    fun shouldMatchSubsequenceAcrossSegments() {
        // Given
        val text = "feature/bugfix"

        // When
        val result = text.fuzzyContains("fb")

        // Then
        result shouldBe true
    }

    @Test
    fun shouldMatchCaseInsensitively() {
        // Given
        val text = "JIRA-123/fix-thing"

        // When
        val result = text.fuzzyContains("jira")

        // Then
        result shouldBe true
    }

    @Test
    fun shouldMatchEmptyQuery() {
        // Given
        val text = "anything"

        // When
        val result = text.fuzzyContains("")

        // Then
        result shouldBe true
    }

    @Test
    fun shouldNotMatchWhenCharacterOrderIsWrong() {
        // Given
        val text = "abc"

        // When
        val result = text.fuzzyContains("ba")

        // Then
        result shouldBe false
    }

    @Test
    fun shouldNotMatchWhenQueryHasExtraCharacters() {
        // Given
        val text = "main"

        // When
        val result = text.fuzzyContains("mainx")

        // Then
        result shouldBe false
    }

    @Test
    fun shouldMatchEmptyQueryAgainstEmptyText() {
        // Given
        val text = ""

        // When
        val result = text.fuzzyContains("")

        // Then
        result shouldBe true
    }

    @Test
    fun shouldNotMatchNonEmptyQueryAgainstEmptyText() {
        // Given
        val text = ""

        // When
        val result = text.fuzzyContains("a")

        // Then
        result shouldBe false
    }

    @Test
    fun shouldMatchCaseSensitivelyWhenQueryHasUppercase() {
        // Given
        val text = "user/ABC-1223-hello/WorldNextTest"

        // When
        val result = text.fuzzyContains("ABC1WorldNeTes")

        // Then
        result shouldBe true
    }

    @Test
    fun shouldFailCaseSensitiveMatchWhenQueryIsMixedCase() {
        // Given
        val text = "user/ABC-1223-hello/world"

        // When the query does contain part of the text, but the casing doesn't line up
        val result = text.fuzzyContains("Abc1")

        // Then
        result shouldBe false
    }

    @Test
    fun shouldNotMatchUppercaseQueryAgainstLowercaseText() {
        // Given the text is all lowercase
        val text = "user/feature/foobar"

        // When the search query contains uppercase chars
        val result = text.fuzzyContains("FOO")

        // Then
        result shouldBe false
    }

    @Test
    fun shouldMatchLowercaseQueryAgainstUppercaseText() {
        // Given
        val text = "user/feature/FOOBAR"

        // When
        val result = text.fuzzyContains("foo")

        // Then
        result shouldBe true
    }
}
