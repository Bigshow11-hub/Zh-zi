package com.zhuzi.app.data

import android.content.Context
import org.json.JSONObject

/**
 * Le bengali : un dictionnaire anglais → bengali (assets/i18n/bn.json, fabriqué par tools/i18n/).
 * Chaque texte de l'appli existe en anglais ; en bengali on cherche sa traduction, sinon on garde l'anglais.
 * Les nombres sont remplacés par {0}, {1}… pour retrouver les phrases qui en contiennent.
 */
object Bn {
    @Volatile private var dict: Map<String, String> = emptyMap()
    private val number = Regex("\\d+")

    fun init(context: Context) {
        if (dict.isNotEmpty()) return
        dict = runCatching {
            val json = JSONObject(context.assets.open("i18n/bn.json").bufferedReader().use { it.readText() })
            buildMap { json.keys().forEach { k -> put(k, json.getString(k)) } }
        }.getOrDefault(emptyMap())
    }

    fun t(en: String): String {
        if (en.isEmpty()) return en
        dict[en]?.let { return it }
        val nums = number.findAll(en).map { it.value }.toList()
        if (nums.isNotEmpty()) {
            var i = 0
            val key = number.replace(en) { "{${i++}}" }
            dict[key]?.let { tpl ->
                var out = tpl
                nums.forEachIndexed { j, n -> out = out.replace("{$j}", n) }
                return out
            }
        }
        return en
    }
}
