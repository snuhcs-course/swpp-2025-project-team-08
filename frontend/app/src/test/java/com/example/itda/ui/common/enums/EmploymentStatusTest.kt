package com.example.itda.ui.common.enums

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class EmploymentStatusTest {

    @Test
    fun `fromKorean returns EMPLOYED when given 재직자`() {
        val result = EmploymentStatus.fromKorean("재직자")
        assertThat(result).isEqualTo(EmploymentStatus.EMPLOYED)
    }

    @Test
    fun `fromKorean returns UNEMPLOYED when given 미취업자`() {
        val result = EmploymentStatus.fromKorean("미취업자")
        assertThat(result).isEqualTo(EmploymentStatus.UNEMPLOYED)
    }

    @Test
    fun `fromKorean returns SELF_EMPLOYED when given 자영업자`() {
        val result = EmploymentStatus.fromKorean("자영업자")
        assertThat(result).isEqualTo(EmploymentStatus.SELF_EMPLOYED)
    }

    @Test
    fun `fromKorean returns null when given null`() {
        val result = EmploymentStatus.fromKorean(null)
        assertThat(result).isNull()
    }

    @Test
    fun `fromKorean returns null when given empty string`() {
        val result = EmploymentStatus.fromKorean("")
        assertThat(result).isNull()
    }

    @Test
    fun `fromKorean returns null when given blank string`() {
        val result = EmploymentStatus.fromKorean("   ")
        assertThat(result).isNull()
    }

    @Test
    fun `fromKorean returns null when given invalid value`() {
        val result = EmploymentStatus.fromKorean("학생")
        assertThat(result).isNull()
    }

    @Test
    fun `fromServerValue returns EMPLOYED when given EMPLOYED`() {
        val result = EmploymentStatus.fromServerValue("EMPLOYED")
        assertThat(result).isEqualTo(EmploymentStatus.EMPLOYED)
    }

    @Test
    fun `fromServerValue returns UNEMPLOYED when given UNEMPLOYED`() {
        val result = EmploymentStatus.fromServerValue("UNEMPLOYED")
        assertThat(result).isEqualTo(EmploymentStatus.UNEMPLOYED)
    }

    @Test
    fun `fromServerValue returns SELF_EMPLOYED when given SELF_EMPLOYED`() {
        val result = EmploymentStatus.fromServerValue("SELF_EMPLOYED")
        assertThat(result).isEqualTo(EmploymentStatus.SELF_EMPLOYED)
    }

    @Test
    fun `fromServerValue returns null when given null`() {
        val result = EmploymentStatus.fromServerValue(null)
        assertThat(result).isNull()
    }

    @Test
    fun `fromServerValue returns null when given empty string`() {
        val result = EmploymentStatus.fromServerValue("")
        assertThat(result).isNull()
    }

    @Test
    fun `fromServerValue returns null when given blank string`() {
        val result = EmploymentStatus.fromServerValue("   ")
        assertThat(result).isNull()
    }

    @Test
    fun `fromServerValue returns null when given invalid value`() {
        val result = EmploymentStatus.fromServerValue("STUDENT")
        assertThat(result).isNull()
    }

    @Test
    fun `EMPLOYED has correct korean and serverValue`() {
        assertThat(EmploymentStatus.EMPLOYED.korean).isEqualTo("재직자")
        assertThat(EmploymentStatus.EMPLOYED.serverValue).isEqualTo("EMPLOYED")
    }

    @Test
    fun `UNEMPLOYED has correct korean and serverValue`() {
        assertThat(EmploymentStatus.UNEMPLOYED.korean).isEqualTo("미취업자")
        assertThat(EmploymentStatus.UNEMPLOYED.serverValue).isEqualTo("UNEMPLOYED")
    }

    @Test
    fun `SELF_EMPLOYED has correct korean and serverValue`() {
        assertThat(EmploymentStatus.SELF_EMPLOYED.korean).isEqualTo("자영업자")
        assertThat(EmploymentStatus.SELF_EMPLOYED.serverValue).isEqualTo("SELF_EMPLOYED")
    }

    @Test
    fun `EmploymentStatus has exactly three values`() {
        val values = EmploymentStatus.entries
        assertThat(values).hasSize(3)
        assertThat(values).containsExactly(
            EmploymentStatus.EMPLOYED,
            EmploymentStatus.UNEMPLOYED,
            EmploymentStatus.SELF_EMPLOYED
        )
    }
}
