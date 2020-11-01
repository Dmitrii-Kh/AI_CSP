package main.kotlin.com.ai.labs.domain

import com.ai.labs.domain.Group
import com.ai.labs.domain.Instructor

data class Course(val name: String,
                  val instructors: ArrayList<Instructor>, val group : Group
) {
    override fun toString(): String {
        return name
    }
}