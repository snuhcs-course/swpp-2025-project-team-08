package com.example.itda.ui.common.enums

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class EducationLevelTest {

    @Test
    fun `fromKorean returns correct value for all student levels`() {
        assertThat(EducationLevel.fromKorean("초등학생")).isEqualTo(EducationLevel.ELEMENTARY_SCHOOL_STUDENT)
        assertThat(EducationLevel.fromKorean("중학생")).isEqualTo(EducationLevel.MIDDLE_SCHOOL_STUDENT)
        assertThat(EducationLevel.fromKorean("고등학생")).isEqualTo(EducationLevel.HIGH_SCHOOL_STUDENT)
        assertThat(EducationLevel.fromKorean("대학생")).isEqualTo(EducationLevel.COLLEGE_STUDENT)
    }

    @Test
    fun `fromKorean returns correct value for all graduate levels`() {
        assertThat(EducationLevel.fromKorean("초졸")).isEqualTo(EducationLevel.ELEMENTARY_SCHOOL)
        assertThat(EducationLevel.fromKorean("중졸")).isEqualTo(EducationLevel.MIDDLE_SCHOOL)
        assertThat(EducationLevel.fromKorean("고졸")).isEqualTo(EducationLevel.HIGH_SCHOOL)
        assertThat(EducationLevel.fromKorean("전문대졸")).isEqualTo(EducationLevel.ASSOCIATE)
        assertThat(EducationLevel.fromKorean("대졸")).isEqualTo(EducationLevel.BACHELOR)
    }

    @Test
    fun `fromKorean returns null when given null`() {
        val result = EducationLevel.fromKorean(null)
        assertThat(result).isNull()
    }

    @Test
    fun `fromKorean returns null when given empty string`() {
        val result = EducationLevel.fromKorean("")
        assertThat(result).isNull()
    }

    @Test
    fun `fromKorean returns null when given blank string`() {
        val result = EducationLevel.fromKorean("   ")
        assertThat(result).isNull()
    }

    @Test
    fun `fromKorean returns null when given invalid value`() {
        val result = EducationLevel.fromKorean("박사")
        assertThat(result).isNull()
    }

    @Test
    fun `fromServerValue returns correct value for all student levels`() {
        assertThat(EducationLevel.fromServerValue("ELEMENTARY_SCHOOL_STUDENT")).isEqualTo(EducationLevel.ELEMENTARY_SCHOOL_STUDENT)
        assertThat(EducationLevel.fromServerValue("MIDDLE_SCHOOL_STUDENT")).isEqualTo(EducationLevel.MIDDLE_SCHOOL_STUDENT)
        assertThat(EducationLevel.fromServerValue("HIGH_SCHOOL_STUDENT")).isEqualTo(EducationLevel.HIGH_SCHOOL_STUDENT)
        assertThat(EducationLevel.fromServerValue("COLLEGE_STUDENT")).isEqualTo(EducationLevel.COLLEGE_STUDENT)
    }

    @Test
    fun `fromServerValue returns correct value for all graduate levels`() {
        assertThat(EducationLevel.fromServerValue("ELEMENTARY_SCHOOL")).isEqualTo(EducationLevel.ELEMENTARY_SCHOOL)
        assertThat(EducationLevel.fromServerValue("MIDDLE_SCHOOL")).isEqualTo(EducationLevel.MIDDLE_SCHOOL)
        assertThat(EducationLevel.fromServerValue("HIGH_SCHOOL")).isEqualTo(EducationLevel.HIGH_SCHOOL)
        assertThat(EducationLevel.fromServerValue("ASSOCIATE")).isEqualTo(EducationLevel.ASSOCIATE)
        assertThat(EducationLevel.fromServerValue("BACHELOR")).isEqualTo(EducationLevel.BACHELOR)
    }

    @Test
    fun `fromServerValue returns null when given null`() {
        val result = EducationLevel.fromServerValue(null)
        assertThat(result).isNull()
    }

    @Test
    fun `fromServerValue returns null when given empty string`() {
        val result = EducationLevel.fromServerValue("")
        assertThat(result).isNull()
    }

    @Test
    fun `fromServerValue returns null when given blank string`() {
        val result = EducationLevel.fromServerValue("   ")
        assertThat(result).isNull()
    }

    @Test
    fun `fromServerValue returns null when given invalid value`() {
        val result = EducationLevel.fromServerValue("DOCTORATE")
        assertThat(result).isNull()
    }

    @Test
    fun `ELEMENTARY_SCHOOL_STUDENT has correct korean and serverValue`() {
        assertThat(EducationLevel.ELEMENTARY_SCHOOL_STUDENT.korean).isEqualTo("초등학생")
        assertThat(EducationLevel.ELEMENTARY_SCHOOL_STUDENT.serverValue).isEqualTo("ELEMENTARY_SCHOOL_STUDENT")
    }

    @Test
    fun `BACHELOR has correct korean and serverValue`() {
        assertThat(EducationLevel.BACHELOR.korean).isEqualTo("대졸")
        assertThat(EducationLevel.BACHELOR.serverValue).isEqualTo("BACHELOR")
    }

    @Test
    fun `EducationLevel has exactly nine values`() {
        val values = EducationLevel.entries
        assertThat(values).hasSize(9)
        assertThat(values).containsExactly(
            EducationLevel.ELEMENTARY_SCHOOL_STUDENT,
            EducationLevel.MIDDLE_SCHOOL_STUDENT,
            EducationLevel.HIGH_SCHOOL_STUDENT,
            EducationLevel.COLLEGE_STUDENT,
            EducationLevel.ELEMENTARY_SCHOOL,
            EducationLevel.MIDDLE_SCHOOL,
            EducationLevel.HIGH_SCHOOL,
            EducationLevel.ASSOCIATE,
            EducationLevel.BACHELOR
        )
    }
}
