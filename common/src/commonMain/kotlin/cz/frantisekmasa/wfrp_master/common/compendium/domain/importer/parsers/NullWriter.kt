package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers

import java.io.IOException
import java.io.Writer
import java.util.Objects

class NullWriter : Writer() {
    @Volatile
    private var closed = false

    @Throws(IOException::class)
    private fun ensureOpen() {
        if (closed) {
            throw IOException("Stream closed")
        }
    }

    @Throws(IOException::class)
    override fun append(c: Char): Writer {
        ensureOpen()

        return this
    }

    @Throws(IOException::class)
    override fun append(csq: CharSequence?): Writer {
        ensureOpen()

        return this
    }

    @Throws(IOException::class)
    override fun append(csq: CharSequence?, start: Int, end: Int): Writer {
        ensureOpen()
        if (csq != null) {
            Objects.checkFromToIndex(start, end, csq.length)
        }

        return this
    }

    @Throws(IOException::class)
    override fun write(c: Int) {
        ensureOpen()
    }

    @Throws(IOException::class)
    override fun write(cbuf: CharArray, off: Int, len: Int) {
        Objects.checkFromIndexSize(off, len, cbuf.size)
        ensureOpen()
    }

    @Throws(IOException::class)
    override fun write(str: String) {
        ensureOpen()
    }

    @Throws(IOException::class)
    override fun write(str: String, off: Int, len: Int) {
        Objects.checkFromIndexSize(off, len, str.length)
        ensureOpen()
    }

    @Throws(IOException::class)
    override fun flush() {
        ensureOpen()
    }

    @Throws(IOException::class)
    override fun close() {
        closed = true
    }
}
