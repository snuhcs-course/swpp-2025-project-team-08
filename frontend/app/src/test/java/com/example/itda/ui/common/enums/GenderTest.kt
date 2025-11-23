package com.example.itda.ui.common.enums

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class GenderTest {

    @Test
    fun `fromKorean returns MALE when given 남성`() {
        val result = Gender.fromKorean("남성")
        assertThat(result).isEqualTo(Gender.MALE)
    }

    @Test
    fun `fromKorean returns FEMALE when given 여성`() {
        val result = Gender.fromKorean("여성")
        assertThat(result).isEqualTo(Gender.FEMALE)
    }

    @Test
    fun `fromKorean returns null when given null`() {
        val result = Gender.fromKorean(null)
        assertThat(result).isNull()
    }

    @Test
    fun `fromKorean returns null when given empty string`() {
        val result = Gender.fromKorean("")
        assertThat(result).isNull()
    }

    @Test
    fun `fromKorean returns null when given blank string`() {
        val result = Gender.fromKorean("   ")
        assertThat(result).isNull()
    }

    @Test
    fun `fromKorean returns null when given invalid value`() {
        val result = Gender.fromKorean("기타")
        assertThat(result).isNull()
    }

    @Test
    fun `fromServerValue returns MALE when given MALE`() {
        val result = Gender.fromServerValue("MALE")
        assertThat(result).isEqualTo(Gender.MALE)
    }

    @Test
    fun `fromServerValue returns FEMALE when given FEMALE`() {
        val result = Gender.fromServerValue("FEMALE")
        assertThat(result).isEqualTo(Gender.FEMALE)
    }

    @Test
    fun `fromServerValue returns null when given null`() {
        val result = Gender.fromServerValue(null)
        assertThat(result).isNull()
    }

    @Test
    fun `fromServerValue returns null when given empty string`() {
        val result = Gender.fromServerValue("")
        assertThat(result).isNull()
    }

    @Test
    fun `fromServerValue returns null when given blank string`() {
        val result = Gender.fromServerValue("   ")
        assertThat(result).isNull()
    }

    @Test
    fun `fromServerValue returns null when given invalid value`() {
        val result = Gender.fromServerValue("OTHER")
        assertThat(result).isNull()
    }

    @Test
    fun `MALE has correct korean and serverValue`() {
        assertThat(Gender.MALE.korean).isEqualTo("남성")
        assertThat(Gender.MALE.serverValue).isEqualTo("MALE")
    }

    @Test
    fun `FEMALE has correct korean and serverValue`() {
        assertThat(Gender.FEMALE.korean).isEqualTo("여성")
        assertThat(Gender.FEMALE.serverValue).isEqualTo("FEMALE")
    }

    @Test
    fun `Gender has exactly two values`() {
        val values = Gender.entries
        assertThat(values).hasSize(2)
        assertThat(values).containsExactly(Gender.MALE, Gender.FEMALE)
    }
}
