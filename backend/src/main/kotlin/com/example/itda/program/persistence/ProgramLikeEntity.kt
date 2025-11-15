package com.example.itda.program.persistence

import com.example.itda.user.persistence.UserEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.OffsetDateTime

@Entity
@Table(
    name = "program_like",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["user_id", "program_id"]),
    ],
)
class ProgramLikeEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long? = null,
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    var user: UserEntity,
    @ManyToOne(optional = false)
    @JoinColumn(name = "program_id", nullable = false)
    var program: ProgramEntity,
    @Column(name = "is_like", nullable = false)
    var isLike: Boolean,
    @Column(
        name = "created_at",
        insertable = false,
        updatable = false,
        columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP",
    )
    var createdAt: OffsetDateTime? = null,
)
