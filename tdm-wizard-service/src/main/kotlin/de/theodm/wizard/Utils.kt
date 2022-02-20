package de.theodm.wizard

fun SHOULD_NOT_HAPPEN(): Nothing {
    throw IllegalStateException("this should never appear!")
}

fun <T> List<T>.containsUniqueItemsOnly(): Boolean {
    return this.toSet().size == this.size
}

data class ImmutableResult1<ThisType, ReturnType1>(
        val thisObject: ThisType,
        val returnValue1: ReturnType1
)