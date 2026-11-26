package com.zhuzi.app.data

import android.content.Context
import org.json.JSONObject

/**
 * Tracé d'un caractère : pour chaque trait, son contour (chemin SVG) et sa ligne médiane
 * (points dans le sens d'écriture). Carré de 1024, axe y vers le haut (afficher y' = 900 − y).
 * Données : Make Me a Hanzi (hanzi-writer-data), licence publique Arphic.
 */
class CharStrokes(val outlines: List<String>, val medians: List<List<Pair<Float, Float>>>)

object StrokeData {
    @Volatile
    private var json: JSONObject? = null
    private val cache = HashMap<String, CharStrokes?>()

    fun get(context: Context, char: String): CharStrokes? = synchronized(this) {
        cache.getOrPut(char) {
            val all = json ?: context.assets.open("strokes.json").bufferedReader().use {
                JSONObject(it.readText())
            }.also { json = it }
            val c = all.optJSONObject(char) ?: return@getOrPut null
            val s = c.getJSONArray("s")
            val m = c.getJSONArray("m")
            CharStrokes(
                outlines = List(s.length()) { s.getString(it) },
                medians = List(m.length()) { i ->
                    val pts = m.getJSONArray(i)
                    List(pts.length()) { j ->
                        val p = pts.getJSONArray(j)
                        p.getDouble(0).toFloat() to p.getDouble(1).toFloat()
                    }
                },
            )
        }
    }
}
