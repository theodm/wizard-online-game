package extensions

fun <T> List<T>.lastOrThrow() =
        this.lastOrNull() ?: throw IllegalStateException("In der Liste gibt es kein letztes Element.")