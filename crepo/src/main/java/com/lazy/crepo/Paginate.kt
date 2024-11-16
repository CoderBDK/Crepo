package com.lazy.crepo

import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Paginate(val kClazz: KClass<*>, val pageSize: Int = 10)
