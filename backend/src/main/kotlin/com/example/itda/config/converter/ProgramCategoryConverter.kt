package com.example.itda.config.converter

import com.example.itda.program.persistence.enums.ProgramCategory
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class ProgramCategoryConverter : Converter<String, ProgramCategory> {
    override fun convert(source: String): ProgramCategory {
        return ProgramCategory.valueOf(source.uppercase())
    }
}
