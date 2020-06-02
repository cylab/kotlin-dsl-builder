package net.highteq.cylab.kotlindslbuilder.target

val targetTypeMapping : Map<Class<*>, String> = mapOf()

val extraTargetTypeDSLMapping : Map<Any, String> = mapOf(
  "java.util.Map<java.lang.String, software.amazon.awssdk.services.dynamodb.model.AttributeValue>"
    to "net.highteq.cylab.awssdk.dynamodb.kotlin.dsl.ext.model.AttributeMapDSL"
)
