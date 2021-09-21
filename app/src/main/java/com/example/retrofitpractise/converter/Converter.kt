package com.example.retrofitpractise.converter

import androidx.room.TypeConverter
import com.example.retrofitpractise.model.Source

class Converter {

    @TypeConverter
    fun convertFromSource(source: Source): String {
        return source.name
    }

    @TypeConverter
    fun convertToSource(name: String): Source {
        return Source(name, name)
    }
}