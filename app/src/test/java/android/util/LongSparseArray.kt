package android.util

/**
 * Fake LongSparseArray implementation that is backed by a [MutableMap].
 *
 * Used only in unit tests.
 */
class LongSparseArray<E> {
    private val map = mutableMapOf<Long, E?>()

    operator fun get(key: Long): E? = map[key]

    fun put(key: Long, value: E?) {
        map[key] = value
    }
}