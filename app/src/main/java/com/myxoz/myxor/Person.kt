package com.myxoz.myxor

import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import kotlin.math.sign

class Person(
    val name: String,
    val elements: List<Element>,
    val uuid: UUID = UUID.randomUUID()
): JSONAble() {
    fun copy(
        name: String=this.name,
        elements: List<Element> = this.elements
    ): Person{
        return Person(name,elements,uuid)
    }
    companion object: JSONAbleCompanion<Person>() {
        const val NAME = "name"
        const val ELEMENTS = "bills"

        fun parseJSONasPersonList(json: String): List<Person> =
            JSONArray(json).listOfJSONObject().map { obj ->
                fromJSON(obj)
            }

        fun json(people: List<Person>): JSONArray = people.map { it.asJSON() }.asJSONArray()
        override fun fromJSON(obj: JSONObject): Person = Person(obj.getString(NAME), obj.getJSONArray(ELEMENTS).listOfJSONObject().map { Element.fromJSON(it) })
    }

    override fun asJSON(): JSONObject = JSONObject()
        .put(NAME, name)
        .put(ELEMENTS, elements.map { it.asJSON() }.asJSONArray())
    override fun toString(): String {
        return "Name: $name - Elems:\n${elements.sortedByDescending { it.date }.joinToString("") { "   -> $it\n" }}\n"
    }

    fun lastSignFlip(): Long{
        var sum = elements.total()
        val sorted = elements.sortedByDescending { it.date }
        val sign = sum.sign
        sorted.forEach {
            sum-=it.priceInCent
            if(sum.sign!=sign) return it.date
        }
        return sorted.lastOrNull()?.date?:System.currentTimeMillis()
    }
    fun isUrgent(): Boolean = System.currentTimeMillis()-lastSignFlip()>1000L*60*60*24*100
}
class Element(val name: String, val priceInCent: Int, val date: Long, val uuid: UUID=UUID.randomUUID()): JSONAble() {
    fun copy(name: String=this.name, priceInCent: Int=this.priceInCent, date: Long=this.date): Element{
        return Element(name, priceInCent, date, uuid)
    }
    companion object: JSONAbleCompanion<Element>() {
        const val NAME = "description"
        const val PRICE = "price"
        const val DATE = "time"

        override fun fromJSON(obj: JSONObject): Element = Element(
            obj.getString(NAME),
            obj.getInt(PRICE),
            obj.getLong(DATE)
        )
    }
    override fun asJSON(): JSONObject = JSONObject()
        .put(NAME, name)
        .put(PRICE, priceInCent)
        .put(DATE, date)

    override fun toString(): String {
        return "Name: $name | Price: ${priceInCent/100f} | Date: $date"
    }
}
abstract class JSONAble {
    abstract fun asJSON(): JSONObject
}
abstract class JSONAbleCompanion<T> {
    abstract fun fromJSON(obj: JSONObject): T
}
fun JSONArray.listOfJSONObject(): List<JSONObject> {
    val list = mutableListOf<JSONObject>()
    repeat(length()){
        list.add(getJSONObject(it))
    }
    return list
}
fun List<JSONObject>.asJSONArray() = JSONArray(this)