package main.kotlin.com.ai.labs

import com.ai.labs.domain.*
import main.kotlin.com.ai.labs.domain.Course

class Data {

    val rooms: ArrayList<Room>
    val courses: ArrayList<Course>

    init {
        val room1 = Room("R1", 25)
        val room2 = Room("R2", 45)
        val room3 = Room("R3", 50)
        rooms = arrayListOf(room1, room2, room3)

        val instructor1 = Instructor("Ivan Petrov")
        val instructor2 = Instructor("Alex Zen")
        val instructor3 = Instructor("Bob Lee")
        val instructor4 = Instructor("Sam White")

        val gr1 = Group("gr1", 20)
        val gr2 = Group("gr2", 40)
        val gr3 = Group("gr3", 50)

        val course1 = Course("MLlec", arrayListOf(instructor1, instructor2), gr1)
        val course2 = Course("MLpr", arrayListOf(instructor1, instructor2, instructor3), gr2)
        val course3 = Course("ADSlec", arrayListOf(instructor1, instructor2), gr3)
        val course4 = Course("ADSpr", arrayListOf(instructor3, instructor4), gr1)
        val course5 = Course("CliServlec", arrayListOf(instructor4), gr3)
        val course6 = Course("CliSerpr", arrayListOf(instructor1, instructor3), gr3)
        courses = arrayListOf(course1, course2, course3, course4, course5, course6)

    }

    fun getCourseByName(courseName: String) : Course? {
        for (c in courses){
            if(c.name == courseName) return c
        }
        return null
    }

    fun getRoomByNumber(roomNumber: String) : Room {
        for (r in rooms){
            if(r.number == roomNumber) return r
        }
        return Room("default", -1)
    }

}