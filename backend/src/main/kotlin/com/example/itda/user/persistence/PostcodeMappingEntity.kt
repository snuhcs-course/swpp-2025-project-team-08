package com.example.itda.user.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "postcode_mapping")
class PostcodeMappingEntity(
    @Id
    @Column(name = "postcode_prefix", length = 3, nullable = false)
    val postcodePrefix: String,
    @Column(name = "region", nullable = false)
    val region: String,
    @Column(name = "region_major", nullable = false)
    val regionMajor: String,
)
