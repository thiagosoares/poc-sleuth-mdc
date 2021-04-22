package br.com.dextra.poc.sleuth.api.filter

import brave.propagation.ExtraFieldPropagation
import org.slf4j.MDC;
import java.io.Closeable
import java.util.Arrays
import java.util.HashMap
import java.util.UUID

object LoggerContext {

    const val LOG_CONTEXT = "LogContext"
    const val TRACE_ID = "AleloTraceId"
    const val TRACE_ID_HEADER = "X-alelo-traceid"

    var data: ThreadLocal<DataInfo> = ThreadLocal<DataInfo>()

    fun initialize(vararg args: String?): AttributeInfoCloseable {

        require(args.size % 2 == 0) { "Wrong number os arguments" }
        val keys = arrayOfNulls<String>(args.size / 2)
        if (data.get() == null) {
            data.set(DataInfo())
        }
        var i = 0
        while (i < args.size) {
            val key = args[i]
            val value = args[i + 1]
            keys[i / 2] = key
            data.get().putAttribute(key!!, value)
            ExtraFieldPropagation.set("X"+key, value)
            i += 2
        }

//        ExtraFieldPropagation.set("baggage", "${args[0]}:${args[1]}");


        return AttributeInfoCloseable(keys)
    }


//    var data: ThreadLocal<DataInfo> = ThreadLocal<DataInfo>()


    class MPair (
        var key: String? = null,
        var value: String? = null,
        var counter: Int = 1
    )

    class DataInfo {
        var info: MutableMap<String, MPair> = HashMap()
        fun putAttribute(key: String, value: String?) {
            MDC.put(key, value)
            val mPair = info[key]
            if (mPair == null) {
                info[key] = MPair(key, value, 1)
            } else {
                mPair.value = value
                mPair.counter = mPair.counter + 1
            }
        }

        fun removeAttribute(key: String?) {
            val mPair = info[key]
            if (mPair != null) {
                mPair.counter = mPair.counter - 1
                if (mPair.counter <= 0) {
                    MDC.remove(key)
                    info.remove(key)
                }
            }
        }
    }

    class AttributeInfoCloseable(private val keys: Array<String?>) : Closeable {
        override fun close() {
            if (data.get() != null) {
                for (key in keys) {
                    data.get().removeAttribute(key)
                }
            }
        }
    }

    fun generateUUID(): String? {
        val operationId = UUID.randomUUID()
        return "{" + operationId.toString().toUpperCase() + "}"
    }

    fun getAttribute(key: String?): String? {

        return data.let { thread -> thread.get().info[key]?.value } ?: run { null }

    }



}