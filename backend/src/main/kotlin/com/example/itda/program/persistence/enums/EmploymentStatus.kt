package com.example.itda.program.persistence.enums

enum class EmploymentStatus(val dbValue: String) {
    EMPLOYED("재직자"),
    UNEMPLOYED("미취업자"),
    SELF_EMPLOYED("자영업자"),
}
