/*
  This file as well as the whole project is licensed under
  Apache License Version 2.0
  See LICENSE.txt for more info
 */
package net.highteq.cylab.kotlindslbuilder

import mu.KotlinLogging
import org.apache.commons.lang3.reflect.TypeUtils
import java.lang.reflect.Method
import java.lang.reflect.Type

val logger = KotlinLogging.logger {}

class Index<T>(
  private val index: Map<String, T>
) {

  val values: Collection<T> get() = index.values

  operator fun get(key: String) = index[key]
  operator fun get(type: Class<*>) = this[type.name]
  operator fun get(method: Method) = this[methodKey(method)]

  fun findFor(prefix: String) = index.entries.filter { (key, _) -> key.startsWith(prefix) }
  fun findFor(type: Class<*>) = index.entries.filter { (key, _) -> key.startsWith(type.name) }
}

fun <F, S> pairOrNull(first: F?, second: S?) =
  if (first == null || second == null) null else first to second

fun <F, S, T> tripleOrNull(first: F?, second: S?, third: T) =
  if (first == null || second == null || third == null) null else Triple(first, second, third)

fun methodKey(method: Method) =
  "${method.declaringClass.name}.${method.name}" +
    "(${method.genericParameterTypes.map { it.typeName }.joinToString(", ")})"

val Type.rawClass: Class<*> get() = TypeUtils.getRawType(this, null) ?: Object::class.java
