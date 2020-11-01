package main.kotlin.com.ai.labs
import main.kotlin.com.ai.labs.domain.Class
import main.kotlin.com.ai.labs.domain.Course
import kotlin.collections.HashMap

fun main(){
    val driver = Driver(Data())
    driver.backtrackingSearch()?.forEach { println("$it       ${it.course.instructors}") }
}

class Driver(private val data : Data) {
    private val csp : HashMap<String, Int?> = HashMap()
    private val domainsMap : HashMap<String, MutableList<List<String>>> = HashMap()         //key : course id    val : (("Mon", "1", "221"), ("Mon", "2", "222"), ...)
    private var cspSorted : HashMap<String, Int?>
    private var domainsMapSorted : HashMap<String, MutableList<List<String>>>

    init {

    val daysOfWeek = arrayListOf("M", "T", "W", "Th", "F")

    // Domains filling
    for(course in data.courses) {
           domainsMap[course.name] = mutableListOf()
           for(room in data.rooms) {
               if(room.seatingCapacity >= course.group.size) {
                   for(day in daysOfWeek){
                       for(i in 1..6){
                           domainsMap[course.name]?.add(listOf(day, i.toString(), room.number))
                       }
                   }
               }
           }
    }

        // CSP filling
        for(key in data.courses){
            csp[key.name] = 0
            for(course in data.courses){
                if(key.name != course.name) {
                    //same group
                    if(key.group.name == course.group.name) {
                        csp[key.name] = csp[key.name]?.plus(1)
                        break
                    }
                    //or same instructor(s)
                    for(instructor in key.instructors) {
                        if(course.instructors.contains(instructor)){
                            csp[key.name] = csp[key.name]?.plus(1)
                            break
                        }
                    }

                }
            }
        }

        //sort *values* by desc
        cspSorted = csp.toList().sortedByDescending { (_, value) -> value}.toMap() as HashMap<String, Int?>
        //sort by asc
        domainsMapSorted = domainsMap.toList().sortedBy { (_, value) -> value.size}.toMap() as HashMap<String, MutableList<List<String>>>

        //end of init
    }


    fun backtrackingSearch() : MutableList<Class>? {
        return backtrack(mutableListOf())
    }


    private fun backtrack(assignment : MutableList<Class>): MutableList<Class>? {
        if(assignment.size == data.courses.size) return assignment
        val nextCourse = selectUnassignedVar(assignment, domainsMapSorted)          //TODO if else MRV | Degree
            if (nextCourse != null) {
                for(list in domainsMap.getValue(nextCourse.name)){  //list[0]=day list[1]=1..6 list[2]=room
                    if(isConsistent(list, assignment, nextCourse)){
                        val cl = Class(nextCourse, list[0], list[1], data.getRoomByNumber(list[2]))
                        assignment.add(cl)
                        val reducedDoms = reduceDomainsOfUnassignedVars(cl)         //forward checking
                        val result = backtrack(assignment)
                        if(result != null) {
                            return result
                        } else {
                            returnReducedDomains(reducedDoms)                       //forward checking
                            assignment.dropLast(1)
                        }
                    }
                }
            }
        return null
    }

    private fun returnReducedDomains(hm : HashMap<String, MutableList<List<String>>>) {
        for ((k,v) in hm) {
            for (l in v) {
                domainsMap.get(k)?.add(l)
            }
        }
    }

    private fun reduceDomainsOfUnassignedVars(cl : Class) : HashMap<String, MutableList<List<String>>> {
        var result = HashMap<String, MutableList<List<String>>>()
        for((k,v) in domainsMap){
            if(cl.course.name != k) {
                var listOfIndices = mutableListOf<Int>()
                for(i in 0..v.size-1){
                    if(v.get(i)[0] == cl.meetingDay && v.get(i)[1] == cl.meetingTime) {

                        if((v.get(i)[2] == cl.room.number) || (data.getCourseByName(k)?.group?.name == cl.course.group.name)){
                            if(result.containsKey(k)){
                                result.get(k)?.add(listOf(v.get(i)[0], v.get(i)[1], v.get(i)[2]))
                            } else {
                                result.put(k, mutableListOf(v.get(i)))
                            }
                            listOfIndices.add(i)
                            continue
                        }

                        for(instructor in cl.course.instructors) {
                            if(data.getCourseByName(k)?.instructors?.contains(instructor)!!){
                                if(result.containsKey(k)){
                                    result.get(k)?.add(listOf(v.get(i)[0], v.get(i)[1], v.get(i)[2]))
                                } else {
                                    result.put(k, mutableListOf(v.get(i)))
                                }
                                listOfIndices.add(i)
                                break
                            }
                        }

                    }
                }
                for(idx in listOfIndices){
                    v.removeAt(idx)
                }
            }
        }
        return result
    }



    private fun isConsistent(list : List<String>, assignment: MutableList<Class>, c : Course) : Boolean {
        for (cl in assignment){
            if(cl.meetingDay == list[0] && cl.meetingTime == list[1] && cl.room.number == list[2]) return false
            if(cl.meetingDay == list[0] && cl.meetingTime == list[1]) {

                if(c.group.name == cl.course.group.name) return false

                for (instructor in c.instructors) {
                    if (cl.course.instructors.contains(instructor)) return false
                }
            }
        }
        return true
    }


    private fun <E>selectUnassignedVar(assignment: MutableList<Class>, hm : HashMap<String, E> ): Course? {
        var contains : Boolean
        if(assignment.size==0) return data.getCourseByName(hm.keys.first())

        for (courseName in hm.keys) {
            contains = false
            for (cl in assignment){
                if(courseName == cl.course.name){
                    contains = true
                    break
                }
            }
            if(!contains) return data.getCourseByName(courseName)
        }
        return null
    }




}