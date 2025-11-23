package com.example.itda.ui.common.enums

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MaritalStatusTest {

    @Test
    fun `fromKorean returns SINGLE when given 미혼`() {
        val result = MaritalStatus.fromKorean("미혼")
        assertThat(result).isEqualTo(MaritalStatus.SINGLE)
    }

    @Test
    fun `fromKorean returns MARRIED when given 기혼`() {
        val result = MaritalStatus.fromKorean("기혼")
        assertThat(result).isEqualTo(MaritalStatus.MARRIED)
    }

    @Test
    fun `fromKorean returns DIVORCED_OR_BEREAVED when given 이혼_사별`() {
        val result = MaritalStatus.fromKorean("이혼/사별")
        assertThat(result).isEqualTo(MaritalStatus.DIVORCED_OR_BEREAVED)
    }

    @Test
    fun `fromKorean returns null when given null`() {
        val result = MaritalStatus.fromKorean(null)
        assertThat(result).isNull()
    }

    @Test
    fun `fromKorean returns null when given empty string`() {
        val result = MaritalStatus.fromKorean("")
        assertThat(result).isNull()
    }

    @Test
    fun `fromKorean returns null when given blank string`() {
        val result = MaritalStatus.fromKorean("   ")
        assertThat(result).isNull()
    }

    @Test
    fun `fromKorean returns null when given invalid value`() {
        val result = MaritalStatus.fromKorean("기타")
        assertThat(result).isNull()
    }

    @Test
    fun `fromServerValue returns SINGLE when given SINGLE`() {
        val result = MaritalStatus.fromServerValue("SINGLE")
        assertThat(result).isEqualTo(MaritalStatus.SINGLE)
    }

    @Test
    fun `fromServerValue returns MARRIED when given MARRIED`() {
        val result = MaritalStatus.fromServerValue("MARRIED")
        assertThat(result).isEqualTo(MaritalStatus.MARRIED)
    }

    @Test
    fun `fromServerValue returns DIVORCED_OR_BEREAVED when given DIVORCED_OR_BEREAVED`() {
        val result = MaritalStatus.fromServerValue("DIVORCED_OR_BEREAVED")
        assertThat(result).isEqualTo(MaritalStatus.DIVORCED_OR_BEREAVED)
    }

    @Test
    fun `fromServerValue returns null when given null`() {
        val result = MaritalStatus.fromServerValue(null)
        assertThat(result).isNull()
    }

    @Test
    fun `fromServerValue returns null when given empty string`() {
        val result = MaritalStatus.fromServerValue("")
        assertThat(result).isNull()
    }

    @Test
    fun `fromServerValue returns null when given blank string`() {
        val result = MaritalStatus.fromServerValue("   ")
        assertThat(result).isNull()
    }

    @Test
    fun `fromServerValue returns null when given invalid value`() {
        val result = MaritalStatus.fromServerValue("OTHER")
        assertThat(result).isNull()
    }

    @Test
    fun `SINGLE has correct korean and serverValue`() {
        assertThat(MaritalStatus.SINGLE.korean).isEqualTo("미혼")
        assertThat(MaritalStatus.SINGLE.serverValue).isEqualTo("SINGLE")
    }

    @Test
    fun `MARRIED has correct korean and serverValue`() {
        assertThat(MaritalStatus.MARRIED.korean).isEqualTo("기혼")
        assertThat(MaritalStatus.MARRIED.serverValue).isEqualTo("MARRIED")
    }

    @Test
    fun `DIVORCED_OR_BEREAVED has correct korean and serverValue`() {
        assertThat(MaritalStatus.DIVORCED_OR_BEREAVED.korean).isEqualTo("이혼/사별")
        assertThat(MaritalStatus.DIVORCED_OR_BEREAVED.serverValue).isEqualTo("DIVORCED_OR_BEREAVED")
    }

    @Test
    fun `MaritalStatus has exactly three values`() {
        val values = MaritalStatus.entries
        assertThat(values).hasSize(3)
        assertThat(values).containsExactly(
            MaritalStatus.SINGLE,
            MaritalStatus.MARRIED,
            MaritalStatus.DIVORCED_OR_BEREAVED
        )
    }
}
