package de.danielscholz.kargparser.parser

import de.danielscholz.kargparser.IValueParamParser
import java.io.File

class FileValueParamParser(private val checkIsDir: Boolean = false,
                           private val checkIsFile: Boolean = false,
                           callback: ((File) -> Unit)? = null) : IValueParamParser<File> {

   override var callback: ((File) -> Unit)? = null
   private var value: File? = null

   init {
      this.callback = callback
   }

   override fun seperateValueArgs(): IntRange? {
      return 1..1
   }

   override fun matches(rawValue: String): Boolean {
      return rawValue != ""
   }

   override fun assign(rawValue: String) {
      val file = File(rawValue)
      if (checkIsFile && !file.isFile) throw RuntimeException("$file ist keine Datei!")
      if (checkIsDir && !file.isDirectory) throw RuntimeException("$file ist kein Verzeichnis!")
      value = file
   }

   override fun exec() {
      callback?.invoke(value!!) ?: throw RuntimeException("callback wurde nicht definiert!")
   }
}