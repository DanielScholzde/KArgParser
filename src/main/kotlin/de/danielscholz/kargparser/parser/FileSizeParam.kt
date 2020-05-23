package de.danielscholz.kargparser.parser

import de.danielscholz.kargparser.ArgParseException

class FileSizeParam : ParamParserBase<Long, Long?>() {

   private val regex = Regex("[0-9]+(B|KB|MB|GB|TB)?")

   override var callback: ((Long) -> Unit)? = null
   private var value: Long? = null

   override fun matches(rawValue: String): Boolean {
      return rawValue.toUpperCase().matches(regex)
   }

   override fun assign(rawValue: String) {
      val num = Regex("[0-9]+").find(rawValue)?.value
      val unit = Regex("[A-Z]+").find(rawValue.toUpperCase())?.value

      val factor = when (unit) {
         "KB" -> 1024L
         "MB" -> 1024L * 1024
         "GB" -> 1024L * 1024 * 1024
         "TB" -> 1024L * 1024 * 1024 * 1024
         else -> 1
      }

      value = Integer.parseInt(num) * factor
   }

   override fun exec() {
      callback?.invoke(value!!) ?: throw ArgParseException("callback must be specified!", argParser!!)
   }

   override fun printout(): String {
      return "file size"
   }
}