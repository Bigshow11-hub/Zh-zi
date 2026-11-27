package com.zhuzi.app.data

import android.content.Context
import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.time.LocalDate

/**
 * Sauvegarde des progrès dans un fichier (révisions, série, scores, mes mots, réglages, avatar…),
 * pour changer de téléphone ou réinstaller l'appli sans rien perdre.
 */
object Backup {
    private const val APP = "zhuzi"

    /** Les fichiers de réglages de l'appli (pas ceux des bibliothèques). */
    private fun prefNames(context: Context): List<String> =
        File(context.applicationInfo.dataDir, "shared_prefs").listFiles().orEmpty()
            .map { it.name.removeSuffix(".xml") }
            .filter { !it.startsWith("androidx") && !it.startsWith("com.google") && !it.startsWith("WebView") }
            .sorted()

    fun fileName(): String = "zhuzi-sauvegarde-${LocalDate.now()}.json"

    fun export(context: Context, uri: Uri): Boolean = runCatching {
        val prefs = JSONObject()
        prefNames(context).forEach { name ->
            val values = JSONObject()
            context.getSharedPreferences(name, Context.MODE_PRIVATE).all.forEach { (k, v) ->
                val entry = when (v) {
                    is Boolean -> JSONArray().put("b").put(v)
                    is Int -> JSONArray().put("i").put(v)
                    is Long -> JSONArray().put("l").put(v)
                    is Float -> JSONArray().put("f").put(v.toDouble())
                    is String -> JSONArray().put("s").put(v)
                    is Set<*> -> JSONArray().put("set").put(JSONArray(v.map { it.toString() }))
                    else -> null
                }
                if (entry != null) values.put(k, entry)
            }
            prefs.put(name, values)
        }
        val root = JSONObject().put("app", APP).put("version", 1).put("date", LocalDate.now().toString()).put("prefs", prefs)
        context.contentResolver.openOutputStream(uri, "wt")!!.bufferedWriter().use { it.write(root.toString()) }
        true
    }.getOrDefault(false)

    /** Remplace les progrès actuels par ceux du fichier. Rend false si le fichier n'est pas une sauvegarde Zhúzi. */
    fun import(context: Context, uri: Uri): Boolean = runCatching {
        val text = context.contentResolver.openInputStream(uri)!!.bufferedReader().use { it.readText() }
        val root = JSONObject(text)
        if (root.optString("app") != APP) return@runCatching false
        val prefs = root.getJSONObject("prefs")
        prefs.keys().forEach { name ->
            val values = prefs.getJSONObject(name)
            val edit = context.getSharedPreferences(name, Context.MODE_PRIVATE).edit().clear()
            values.keys().forEach { k ->
                val e = values.getJSONArray(k)
                when (e.getString(0)) {
                    "b" -> edit.putBoolean(k, e.getBoolean(1))
                    "i" -> edit.putInt(k, e.getInt(1))
                    "l" -> edit.putLong(k, e.getLong(1))
                    "f" -> edit.putFloat(k, e.getDouble(1).toFloat())
                    "s" -> edit.putString(k, e.getString(1))
                    "set" -> {
                        val arr = e.getJSONArray(1)
                        edit.putStringSet(k, (0 until arr.length()).map { arr.getString(it) }.toSet())
                    }
                }
            }
            edit.commit()
        }
        true
    }.getOrDefault(false)
}
