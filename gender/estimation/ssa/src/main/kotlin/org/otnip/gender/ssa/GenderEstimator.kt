package org.otnip.gender.ssa

import java.io.IOException
import java.util.HashMap
import java.util.logging.Level
import java.util.logging.Logger

private const val PATH_NAME_COUNTS = "/org/otnip/gender/ssa/names.csv"

/**
 * estimates gender based on first-names
 *
 * @author David Pinto
 */
object GenderEstimator {

    private val map_name_to_gender: Map<String, Gender>

    /* *************************************************************************
     * CLASS INIT
     * ************************************************************************/
    init {
        map_name_to_gender = try {
            GenderEstimator::class.java.getResourceAsStream(PATH_NAME_COUNTS).use { inputStream ->
                val counters = HashMap<String, LongArray>()
                // load data
                val lines = inputStream.reader(Charsets.UTF_8).readLines()
                for (line in lines) {
                    val tokens = line.split(',')
                    val name = tokens[1].trim { it <= ' ' } .toLowerCase()
                    val gender = if (tokens[2] == "M") Gender.M else Gender.F
                    val count = java.lang.Long.parseLong(tokens[3])
                    var counter = counters[name]
                    if (counter == null) {
                        counter = LongArray(2)
                        counters[name] = counter
                    }
                    counter[gender.ordinal] += count
                }

                val output = HashMap<String, Gender>()
                for (entry in counters.entries) {
                    val counter = entry.value
                    val total = (counter[0] + counter[1]).toDouble()
                    if (counter[Gender.F.ordinal] / total > 0.9) {
                        output[entry.key] = Gender.F
                    } else if (counter[Gender.M.ordinal] / total > 0.9) {
                        output[entry.key] = Gender.M
                    }
                }
                output
            }
        } catch (ex: IOException) {
            Logger.getLogger(GenderEstimator::class.java.name).log(Level.SEVERE, "initializing class", ex)
            throw ex
        }

        println(map_name_to_gender.size)
    }

    /**
     * estimate gender for a given name
     *
     * @param input a name ( case insensitive )
     * @return gender
     */
    fun apply(input: String): Gender {
        return if (input.isNotEmpty()) {
            val tokens = input.split(' ')
            if (tokens.isNotEmpty()) {
                val firstName = tokens[0].trim { it <= ' ' }.toLowerCase()
                map_name_to_gender[firstName] ?: Gender.U
            } else Gender.U
        } else Gender.U
    }

    /**
     * Gender Enumeration
     */
    enum class Gender {

        M, // male
        F, // female
        U // unknown
    }
}

fun main(args: Array<String>) {
    println(GenderEstimator.apply("david"))
    println(GenderEstimator.apply("phoenix"))

}
