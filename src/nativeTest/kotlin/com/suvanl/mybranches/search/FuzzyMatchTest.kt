package com.suvanl.mybranches.search

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

    @Test
    fun shouldMatchCaseSensitivelyWhenQueryHasUppercase() {
        // Given
        val text = "user/ABC-1223-hello/WorldNextTest"

        // When / Then
        text.fuzzyContains("ABC1WorldNeTes") shouldBe true
    }

    @Test
    fun shouldFailCaseSensitiveMatchWhenQueryIsMixedCase() {
        // Given
        val text = "user/ABC-1223-hello/world"

        // When the query does contain part of the text, but the casing doesn't line up / Then
        text.fuzzyContains("Abc1") shouldBe false
    }

    @Test
    fun shouldNotMatchUppercaseQueryAgainstLowercaseText() {
        // Given the text is all lowercase
        val text = "feature/foobar"

        // When the search query contains uppercase chars / Then
        text.fuzzyContains("FOO") shouldBe false
    }

    @Test
    fun shouldMatchLowercaseQueryAgainstUppercaseText() {
        // Given
        val text = "feature/FOOBAR"

        // When / Then
        text.fuzzyContains("foo") shouldBe true
    }
}
