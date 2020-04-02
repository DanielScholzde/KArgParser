package de.danielscholz.kargparser

inline fun <R> List<R>.ifNotEmpty(callback: List<R>.() -> Unit) {
   if (this.isNotEmpty()) callback()
}