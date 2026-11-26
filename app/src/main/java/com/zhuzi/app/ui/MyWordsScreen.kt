package com.zhuzi.app.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.zhuzi.app.data.MyWord
import com.zhuzi.app.data.PinyinAuto
import com.zhuzi.app.ui.theme.Accent
import com.zhuzi.app.ui.theme.Ink
import com.zhuzi.app.ui.theme.InkSoft
import com.zhuzi.app.ui.theme.Kai
import com.zhuzi.app.ui.theme.Mist
import com.zhuzi.app.ui.theme.Paper
import com.zhuzi.app.ui.theme.PillShape
import com.zhuzi.app.ui.theme.PinyinFont
import com.zhuzi.app.ui.theme.Rule
import com.zhuzi.app.ui.theme.Wrong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.resume

/** Un mot en préparation (formulaire ou photo) : caractères, pinyin, sens, coché ou non. */
class Draft(hanzi: String, pinyin: String, meaning: String, selected: Boolean = true) {
    var hanzi by mutableStateOf(hanzi)
    var pinyin by mutableStateOf(pinyin)
    var meaning by mutableStateOf(meaning)
    var selected by mutableStateOf(selected)
}

/**
 * « Mes mots » : les mots de l'élève, ajoutés à la main ou depuis une photo de son cours.
 * Ils deviennent des cartes et entrent dans la révision espacée.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyWordsScreen(
    words: List<MyWord>,
    dueCount: Int,
    speak: (String) -> Unit,
    onAdd: (List<Triple<String, String, String>>, String) -> Unit,
    onRemove: (MyWord) -> Unit,
    onReview: () -> Unit,
    onPhoto: () -> Unit,
    onBack: () -> Unit,
    contentPadding: PaddingValues,
) {
    var adding by remember { mutableStateOf(false) }
    var toRemove by remember { mutableStateOf<MyWord?>(null) }
    val seen = remember { mutableSetOf<Any>() }
    Column(Modifier.fillMaxSize().padding(top = contentPadding.calculateTopPadding())) {
        TopBar(tr("Retour", "Back", "Назад"), "", onBack)
        LazyColumn(
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp,
                bottom = contentPadding.calculateBottomPadding() + 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                ScreenTitle(tr("Mes mots", "My words", "Мои слова"),
                    tr("Les mots de tes cours : écris-les ou prends-les en photo, ils deviennent des cartes à réviser.",
                        "Words from your classes: type them or snap a photo, they become cards to review.",
                        "Слова с твоих занятий: впиши их или сфотографируй — они станут карточками."))
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    InkButton(tr("Ajouter", "Add", "Добавить"), { adding = true }, Modifier.weight(1f))
                    InkOutlinedButton(tr("Photo", "Photo", "Фото"), onPhoto, Modifier.weight(1f))
                }
            }
            if (words.isNotEmpty()) {
                item {
                    ReviewMine(words.size, dueCount, onReview)
                }
            }
            if (words.isEmpty()) {
                item { EmptyMine() }
            }
            itemsIndexed(words.reversed(), key = { _, w -> w.id }) { i, w ->
                MyWordRow(w, speak, { toRemove = w }, Modifier.animateItem().staggerIn("my${w.id}", i, seen))
            }
        }
    }
    if (adding) {
        ModalBottomSheet(onDismissRequest = { adding = false }, containerColor = Paper,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
            AddWordForm { h, p, m ->
                onAdd(listOf(Triple(h, p, m)), "main")
                adding = false
            }
        }
    }
    toRemove?.let { w ->
        AlertDialog(
            onDismissRequest = { toRemove = null },
            title = { Text(tr("Supprimer ce mot ?", "Delete this word?", "Удалить это слово?")) },
            text = { Text(w.hanzi + " · " + w.meaning) },
            confirmButton = {
                TextButton(onClick = { onRemove(w); toRemove = null }) {
                    Text(tr("Supprimer", "Delete", "Удалить"), color = Wrong, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { toRemove = null }) { Text(tr("Garder", "Keep", "Оставить"), color = Ink) } },
            containerColor = Paper,
        )
    }
}

@Composable
private fun ReviewMine(total: Int, due: Int, onReview: () -> Unit) {
    val lang = currentLang()
    Row(
        Modifier.fillMaxWidth().softCard().clickable(onClick = onReview).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(tr("Réviser mes mots", "Review my words", "Повторить мои слова"), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(
                if (due > 0) wordsCount(lang, due) + tr(" à revoir aujourd'hui", " due today", " на сегодня")
                else wordsCount(lang, total) + tr(" · tout est à jour, entraîne-toi quand tu veux",
                    " · all caught up, practise any time", " · всё повторено, тренируйся когда хочешь"),
                fontSize = 13.sp, color = InkSoft,
            )
        }
        Text("›", fontSize = 22.sp, color = InkSoft)
    }
}

@Composable
private fun EmptyMine() {
    Column(
        Modifier.fillMaxWidth().padding(top = 28.dp, bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("词", fontFamily = Kai, fontSize = 56.sp, color = Rule)
        Text(tr("Pas encore de mot à toi", "No words of your own yet", "Пока нет своих слов"),
            fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(top = 8.dp))
        Text(tr("Photographie une page de ton manuel ou du tableau : Zhúzi trouve les caractères et prépare les cartes.",
            "Photograph a page of your textbook or the board: Zhúzi finds the characters and prepares the cards.",
            "Сфотографируй страницу учебника или доску: Zhúzi найдёт иероглифы и подготовит карточки."),
            fontSize = 14.sp, color = InkSoft, modifier = Modifier.padding(top = 4.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}

@Composable
private fun MyWordRow(w: MyWord, speak: (String) -> Unit, onDelete: () -> Unit, modifier: Modifier) {
    Row(
        modifier.fillMaxWidth().softCard().clickable { speak(w.hanzi) }.padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(w.hanzi, fontFamily = Kai, fontSize = 28.sp, color = Ink, maxLines = 1)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            PinyinText(w.pinyin.ifBlank { " " }, 15.sp)
            Text(w.meaning, fontSize = 13.sp, color = InkSoft, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
        if (w.source == "photo") {
            Text(tr("photo", "photo", "фото"), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = InkSoft,
                modifier = Modifier.clip(PillShape).background(Mist).padding(horizontal = 8.dp, vertical = 2.dp))
        }
        val label = tr("Supprimer", "Delete", "Удалить")
        Box(
            Modifier.size(48.dp).clickable(onClick = onDelete).semantics { contentDescription = label },
            contentAlignment = Alignment.Center,
        ) { CrossIcon(InkSoft) }
    }
}

@Composable
private fun CrossIcon(c: Color) {
    Canvas(Modifier.size(12.dp)) {
        val w = size.width
        drawLine(c, Offset(0f, 0f), Offset(w, w), 1.8.dp.toPx(), cap = StrokeCap.Round)
        drawLine(c, Offset(w, 0f), Offset(0f, w), 1.8.dp.toPx(), cap = StrokeCap.Round)
    }
}

/** Champ de saisie sur fond doux, avec son étiquette au-dessus. */
@Composable
fun ZField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    placeholder: String = "",
    big: Boolean = false,
    font: FontFamily? = null,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxWidth()) {
        if (label.isNotEmpty()) {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = InkSoft, modifier = Modifier.padding(bottom = 6.dp))
        }
        BasicTextField(
            value = value,
            onValueChange = onChange,
            singleLine = true,
            textStyle = TextStyle(color = Ink, fontSize = if (big) 26.sp else 16.sp, fontFamily = font),
            cursorBrush = SolidColor(Accent),
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Mist)
                .padding(horizontal = 16.dp, vertical = if (big) 12.dp else 14.dp),
            decorationBox = { inner ->
                Box {
                    if (value.isEmpty()) Text(placeholder, color = InkSoft.copy(alpha = 0.7f),
                        fontSize = if (big) 22.sp else 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    inner()
                }
            },
        )
    }
}

/** Formulaire « ajouter un mot » : le pinyin se remplit tout seul, le sens aussi quand l'appli le connaît. */
@Composable
private fun AddWordForm(onSave: (String, String, String) -> Unit) {
    val lang = currentLang()
    var hanzi by remember { mutableStateOf("") }
    var pinyin by remember { mutableStateOf("") }
    var meaning by remember { mutableStateOf("") }
    var pinyinEdited by remember { mutableStateOf(false) }
    val hint = remember(hanzi, lang) { PinyinAuto.meaningHint(hanzi, lang) }
    Column(
        Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).navigationBarsPadding()
            .padding(start = 20.dp, end = 20.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(tr("Nouveau mot", "New word", "Новое слово"), style = MaterialTheme.typography.titleLarge)
        ZField(tr("Caractères", "Characters", "Иероглифы"), hanzi, {
            hanzi = it
            if (!pinyinEdited) pinyin = PinyinAuto.of(it)
            if (meaning.isEmpty()) {
                val known = com.zhuzi.app.data.hsk1Lessons.flatMap { l -> l.words }.firstOrNull { w -> w.hanzi == it.trim() }
                if (known != null) meaning = known.meaning(lang)
            }
        }, placeholder = "你好", big = true, font = Kai)
        ZField(tr("Pinyin (rempli tout seul, modifiable)", "Pinyin (filled in for you, editable)", "Пиньинь (заполняется сам)"),
            pinyin, { pinyin = it; pinyinEdited = true }, placeholder = "nǐ hǎo", font = PinyinFont)
        ZField(tr("Sens", "Meaning", "Значение"), meaning, { meaning = it },
            placeholder = hint.ifEmpty { tr("bonjour", "hello", "привет") })
        InkButton(tr("Ajouter à mes mots", "Add to my words", "Добавить в мои слова"),
            { onSave(hanzi, pinyin, meaning) }, Modifier.fillMaxWidth(),
            enabled = hanzi.isNotBlank() && meaning.isNotBlank())
    }
}

// ——— Photo → cartes ———

/** Lit le texte chinois d'une photo, sur le téléphone (ML Kit) : rend les suites de caractères trouvées. */
suspend fun readChinese(context: Context, uri: Uri): List<String> = suspendCancellableCoroutine { cont ->
    val image = try {
        InputImage.fromFilePath(context, uri)
    } catch (e: Exception) {
        cont.resume(emptyList()); return@suspendCancellableCoroutine
    }
    val reader = TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())
    reader.process(image)
        .addOnSuccessListener { text ->
            val runs = linkedSetOf<String>()
            text.textBlocks.flatMap { it.lines }.forEach { line ->
                // On coupe la ligne à chaque signe qui n'est pas un caractère chinois
                Regex("[\\u3400-\\u4dbf\\u4e00-\\u9fff]+").findAll(line.text).forEach { m ->
                    if (m.value.length in 1..16) runs += m.value
                }
            }
            reader.close()
            cont.resume(runs.toList())
        }
        .addOnFailureListener {
            reader.close()
            cont.resume(emptyList())
        }
}

private fun loadPreview(context: Context, uri: Uri): Bitmap? = try {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
    var sample = 1
    while (bounds.outWidth / sample > 1200) sample *= 2
    context.contentResolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = sample })
    }
} catch (e: Exception) {
    null
}

/**
 * Photo d'un cours → cartes : on prend une photo (ou on en choisit une), l'appli lit les caractères,
 * on coche ceux qu'on veut, on complète le sens, et les cartes sont créées.
 */
@Composable
fun PhotoImportScreen(
    onCreate: (List<Triple<String, String, String>>) -> Unit,
    onBack: () -> Unit,
    contentPadding: PaddingValues,
    previewDrafts: List<Draft>? = null,
) {
    val context = LocalContext.current
    val lang = currentLang()
    val scope = rememberCoroutineScope()
    // 0 : choisir · 1 : lecture en cours · 2 : résultats · 3 : rien trouvé
    var step by remember { mutableStateOf(if (previewDrafts != null) 2 else 0) }
    var preview by remember { mutableStateOf<Bitmap?>(null) }
    val drafts = remember { mutableStateListOf<Draft>().apply { previewDrafts?.let { addAll(it) } } }
    var pending by remember { mutableStateOf<Uri?>(null) }

    val handle: (Uri) -> Unit = { uri ->
        step = 1
        scope.launch {
            preview = withContext(Dispatchers.IO) { loadPreview(context, uri) }
            val runs = readChinese(context, uri)
            drafts.clear()
            runs.forEach { r ->
                val known = com.zhuzi.app.data.hsk1Lessons.flatMap { it.words }.firstOrNull { it.hanzi == r }
                drafts += Draft(r, PinyinAuto.of(r), known?.meaning(lang) ?: "", selected = r.length <= 6)
            }
            step = if (drafts.isEmpty()) 3 else 2
        }
    }
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        val uri = pending
        if (ok && uri != null) handle(uri)
    }
    val gallery = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) handle(uri)
    }
    val takePhoto: () -> Unit = {
        val dir = File(context.cacheDir, "photos").apply { mkdirs() }
        val file = File(dir, "cours_${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.photos", file)
        pending = uri
        try {
            camera.launch(uri)
        } catch (e: Exception) {
            gallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }
    val pick: () -> Unit = { gallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }

    Column(Modifier.fillMaxSize().padding(top = contentPadding.calculateTopPadding())) {
        TopBar(tr("Mes mots", "My words", "Мои слова"), "", onBack)
        AnimatedContent(step, transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(120)) },
            label = "photoStep", modifier = Modifier.weight(1f)) { s ->
            when (s) {
                0, 3 -> PhotoStart(s == 3, takePhoto, pick, contentPadding)
                1 -> Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center) {
                    preview?.let {
                        Image(it.asImageBitmap(), null, contentScale = ContentScale.Crop,
                            modifier = Modifier.size(180.dp).clip(RoundedCornerShape(16.dp)))
                        Spacer(Modifier.height(20.dp))
                    }
                    CircularProgressIndicator(color = Accent, strokeWidth = 3.dp, modifier = Modifier.size(32.dp))
                    Text(tr("Lecture des caractères…", "Reading the characters…", "Читаю иероглифы…"),
                        color = InkSoft, modifier = Modifier.padding(top = 14.dp))
                }
                else -> PhotoResults(preview, drafts, contentPadding, onCreate, pick = takePhoto)
            }
        }
    }
}

@Composable
private fun PhotoStart(nothing: Boolean, takePhoto: () -> Unit, pick: () -> Unit, contentPadding: PaddingValues) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, bottom = contentPadding.calculateBottomPadding() + 24.dp),
    ) {
        ScreenTitle(tr("Photo → cartes", "Photo → cards", "Фото → карточки"),
            tr("Prends en photo ton manuel, une fiche ou le tableau.", "Snap your textbook, a handout or the board.",
                "Сфотографируй учебник, листок или доску."))
        if (nothing) {
            Text(tr("Aucun caractère chinois trouvé sur cette photo. Essaie de plus près, bien droit et avec plus de lumière.",
                "No Chinese characters found on this photo. Try closer, straight on, with more light.",
                "На фото не найдено иероглифов. Попробуй ближе, ровнее и при лучшем свете."),
                color = Wrong, fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Wrong.copy(alpha = 0.08f)).padding(14.dp))
            Spacer(Modifier.height(14.dp))
        }
        // Les 3 étapes, simplement
        listOf(
            tr("Photographie la page, bien à plat et nette.", "Photograph the page, flat and sharp.", "Сфотографируй страницу ровно и чётко."),
            tr("Coche les mots que tu veux apprendre.", "Tick the words you want to learn.", "Отметь слова, которые хочешь выучить."),
            tr("Complète le sens : les cartes sont prêtes.", "Fill in the meaning: your cards are ready.", "Допиши значение — карточки готовы."),
        ).forEachIndexed { i, t ->
            Row(Modifier.padding(bottom = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(28.dp).clip(CircleShape).background(Mist), contentAlignment = Alignment.Center) {
                    Text("${i + 1}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Ink)
                }
                Spacer(Modifier.width(12.dp))
                Text(t, fontSize = 15.sp, color = Ink)
            }
        }
        Spacer(Modifier.height(12.dp))
        InkButton(tr("Prendre une photo", "Take a photo", "Сделать фото"), takePhoto, Modifier.fillMaxWidth())
        Spacer(Modifier.height(10.dp))
        InkOutlinedButton(tr("Choisir dans la galerie", "Choose from gallery", "Выбрать из галереи"), pick, Modifier.fillMaxWidth())
        Text(tr("La lecture se fait sur ton téléphone : la photo ne quitte pas l'appareil.",
            "Reading happens on your phone: the photo never leaves the device.",
            "Распознавание идёт на телефоне: фото никуда не отправляется."),
            fontSize = 12.sp, color = InkSoft, modifier = Modifier.padding(top = 14.dp))
    }
}

@Composable
private fun PhotoResults(
    preview: Bitmap?,
    drafts: MutableList<Draft>,
    contentPadding: PaddingValues,
    onCreate: (List<Triple<String, String, String>>) -> Unit,
    pick: () -> Unit,
) {
    val lang = currentLang()
    val chosen = drafts.filter { it.selected && it.hanzi.isNotBlank() }
    val ready = chosen.isNotEmpty() && chosen.all { it.meaning.isNotBlank() }
    Column(Modifier.fillMaxSize()) {
        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 4.dp)) {
                    preview?.let {
                        Image(it.asImageBitmap(), null, contentScale = ContentScale.Crop,
                            modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)))
                        Spacer(Modifier.width(14.dp))
                    }
                    Column(Modifier.weight(1f)) {
                        Text(countOf(lang, drafts.size, "mot trouvé" to "mots trouvés", "word found" to "words found",
                            Triple("слово найдено", "слова найдено", "слов найдено")), style = MaterialTheme.typography.titleLarge)
                        Text(tr("Coche ceux à garder et complète leur sens.", "Tick the ones to keep and add their meaning.",
                            "Отметь нужные и допиши значение."), fontSize = 13.sp, color = InkSoft)
                    }
                }
            }
            itemsIndexed(drafts) { _, d -> DraftRow(d) }
            item {
                TextButton(onClick = pick) {
                    Text(tr("Prendre une autre photo", "Take another photo", "Сделать другое фото"), color = Accent, fontWeight = FontWeight.Bold)
                }
            }
        }
        BottomActions(contentPadding) {
            InkButton(
                when {
                    chosen.isEmpty() -> tr("Coche au moins un mot", "Tick at least one word", "Отметь хотя бы одно слово")
                    !ready -> tr("Complète le sens", "Add the meanings", "Допиши значения")
                    else -> tr("Créer ", "Create ", "Создать: ") + countOf(lang, chosen.size, "carte" to "cartes", "card" to "cards",
                        Triple("карточку", "карточки", "карточек"))
                },
                { onCreate(chosen.map { Triple(it.hanzi, it.pinyin, it.meaning) }) },
                Modifier.weight(1f), enabled = ready,
            )
        }
    }
}

@Composable
private fun DraftRow(d: Draft) {
    val lang = currentLang()
    val hint = remember(d.hanzi, lang) { PinyinAuto.meaningHint(d.hanzi, lang) }
    Row(
        Modifier.fillMaxWidth().softCard().padding(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        // Case à cocher dessinée, 48 dp de zone tactile
        val label = d.hanzi
        Box(
            Modifier.size(48.dp).clickable { d.selected = !d.selected }.semantics { contentDescription = label },
            contentAlignment = Alignment.Center,
        ) {
            Box(
                Modifier.size(24.dp).clip(RoundedCornerShape(7.dp))
                    .background(if (d.selected) Accent else Paper)
                    .border(1.5.dp, if (d.selected) Accent else Rule, RoundedCornerShape(7.dp)),
                contentAlignment = Alignment.Center,
            ) {
                if (d.selected) {
                    Canvas(Modifier.size(12.dp)) {
                        val w = size.width
                        val p = androidx.compose.ui.graphics.Path().apply {
                            moveTo(w * 0.05f, w * 0.55f); lineTo(w * 0.38f, w * 0.85f); lineTo(w * 0.95f, w * 0.15f)
                        }
                        drawPath(p, Color.White, style = Stroke(2.dp.toPx(), cap = StrokeCap.Round))
                    }
                }
            }
        }
        Column(Modifier.weight(1f).padding(start = 4.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ZField("", d.hanzi, { d.hanzi = it; d.pinyin = PinyinAuto.of(it) }, big = true, font = Kai,
                    modifier = Modifier.weight(1f))
            }
            Text(d.pinyin, fontFamily = PinyinFont, fontSize = 14.sp, color = InkSoft, maxLines = 1,
                overflow = TextOverflow.Ellipsis, modifier = Modifier.heightIn(min = 18.dp))
            if (d.selected) {
                ZField("", d.meaning, { d.meaning = it },
                    placeholder = hint.ifEmpty { tr("Sens", "Meaning", "Значение") })
            }
        }
    }
}
