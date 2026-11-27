package com.zhuzi.app.data

// Fichier fabriqué par tools/chars/build_chars.py : ne pas modifier à la main.
// Décomposition, clé et type : Make Me a Hanzi (licence publique Arphic).

/**
 * Fiche d'un caractère : pinyin, nombre de traits, type (0 inconnu, 1 pictogramme, 2 idéogramme,
 * 3 idéo-phonogramme), clé, composants, sens, astuce pour le retenir, sens de la clé.
 */
data class CharInfo(
    val char: Char,
    val pinyin: String,
    val strokes: Int,
    val kind: Int,
    val radical: String,
    val components: String,
    val meaning: T3,
    val memo: T3,
    val radicalMeaning: T3,
)

/** Les 510 caractères de l'appli, dans l'ordre où ils apparaissent dans les leçons. */
val charInfos: Map<Char, CharInfo> by lazy {
    val m = LinkedHashMap<Char, CharInfo>()
    part0(m); part1(m); part2(m); part3(m); part4(m); part5(m); part6(m);
    m
}

private fun part0(m: MutableMap<Char, CharInfo>) {
    m['你'] = CharInfo('你', "nǐ", 7, 2, "亻", "亻尔",
        T3("tu, toi", "you", "ты"),
        T3("Une personne 亻 à qui l'on parle : toi.", "A person 亻 you are talking to: you.", "Человек 亻, к которому обращаются: ты."),
        T3("personne", "person", "человек"))
    m['好'] = CharInfo('好', "hǎo", 6, 2, "女", "女子",
        T3("bon, bien", "good, well", "хороший"),
        T3("Une femme 女 avec son enfant 子 : c'est bien.", "A woman 女 with her child 子: that's good.", "Женщина 女 с ребёнком 子 — это хорошо."),
        T3("femme", "woman", "женщина"))
    m['谢'] = CharInfo('谢', "xiè", 12, 3, "讠", "讠射",
        T3("remercier", "to thank", "благодарить"),
        T3("La parole 讠 pour dire merci ; 射 donne le son.", "Speech 讠 to say thanks; 射 gives the sound.", "Речь 讠, чтобы благодарить; 射 даёт звук."),
        T3("parole", "speech", "речь"))
    m['不'] = CharInfo('不', "bù", 4, 2, "一", "一",
        T3("ne… pas, non", "not, no", "не, нет"),
        T3("Un oiseau qui s'envole vers le ciel 一 sans l'atteindre : non.", "A bird flying up to the sky 一 but not reaching it: no.", "Птица летит к небу 一, но не долетает: нет."),
        T3("un", "one", "один"))
    m['客'] = CharInfo('客', "kè", 9, 2, "宀", "宀各",
        T3("invité, client", "guest", "гость"),
        T3("Quelqu'un 各 accueilli sous ton toit 宀.", "Someone 各 welcomed under your roof 宀.", "Кто-то 各, кого принимают под крышей 宀."),
        T3("toit", "roof", "крыша"))
    m['气'] = CharInfo('气', "qì", 4, 2, "气", "亻",
        T3("air, souffle", "air, breath", "воздух, дух"),
        T3("Des volutes de vapeur qui montent.", "Wisps of vapour rising.", "Поднимающиеся струйки пара."),
        T3("air", "air", "воздух"))
    m['对'] = CharInfo('对', "duì", 5, 2, "寸", "又寸",
        T3("correct, en face", "right, facing", "верно, напротив"),
        T3("Deux mains 又 寸 qui se font face.", "Two hands 又 寸 facing each other.", "Две руки 又 寸 напротив друг друга."),
        T3("pouce, main", "inch, hand", "вершок, рука"))
    m['起'] = CharInfo('起', "qǐ", 10, 3, "走", "走己",
        T3("se lever, commencer", "to rise, to start", "вставать, начинать"),
        T3("Se mettre à marcher 走 ; 己 donne le son.", "Start to walk 走; 己 gives the sound.", "Начать идти 走; 己 даёт звук."),
        T3("marcher", "walk", "идти"))
    m['没'] = CharInfo('没', "méi", 7, 3, "氵", "氵殳",
        T3("ne pas avoir", "not have", "не иметь"),
        T3("Couler sous l'eau 氵 : il n'y a plus rien.", "Sinking under water 氵: nothing left.", "Утонуть в воде 氵 — ничего не осталось."),
        T3("eau", "water", "вода"))
    m['关'] = CharInfo('关', "guān", 6, 0, "丷", "丷天",
        T3("fermer, lien", "to close, relation", "закрывать, связь"),
        T3("Une porte qu'on ferme à double barre.", "A gate shut with a double bar.", "Ворота, закрытые на двойной засов."),
        T3("huit (haut)", "eight (top)", "восемь (верх)"))
    m['系'] = CharInfo('系', "xì", 7, 3, "糸", "丿糸",
        T3("lien, système", "link, system", "связь, система"),
        T3("Des fils de soie 糸 qui relient.", "Silk threads 糸 that tie things together.", "Шёлковые нити 糸, которые связывают."),
        T3("soie, fil", "silk, thread", "шёлк, нить"))
    m['再'] = CharInfo('再', "zài", 6, 1, "冂", "一冉",
        T3("encore, de nouveau", "again", "снова"),
        T3("Une chose posée sur une autre : une seconde fois.", "One thing stacked on another: a second time.", "Одно поверх другого: ещё раз."),
        T3("enclos", "enclosure", "ограда"))
    m['见'] = CharInfo('见', "jiàn", 4, 2, "见", "冂儿",
        T3("voir", "to see", "видеть"),
        T3("Un grand œil posé sur des jambes 儿 : voir.", "A big eye on legs 儿: to see.", "Большой глаз на ногах 儿: видеть."),
        T3("voir", "see", "видеть"))
    m['请'] = CharInfo('请', "qǐng", 10, 3, "讠", "讠青",
        T3("prier, inviter", "please, to invite", "просить, приглашать"),
        T3("Des paroles 讠 polies ; 青 donne le son.", "Polite speech 讠; 青 gives the sound.", "Вежливая речь 讠; 青 даёт звук."),
        T3("parole", "speech", "речь"))
    m['喂'] = CharInfo('喂', "wèi", 12, 3, "口", "口畏",
        T3("allô", "hello (phone)", "алло"),
        T3("La bouche 口 qui appelle ; 畏 donne le son.", "The mouth 口 calling out; 畏 gives the sound.", "Рот 口 зовёт; 畏 даёт звук."),
        T3("bouche", "mouth", "рот"))
    m['高'] = CharInfo('高', "gāo", 10, 1, "高", "亠口冋",
        T3("haut, grand", "tall, high", "высокий"),
        T3("Le dessin d'un haut pavillon à étages.", "A drawing of a tall tower with floors.", "Рисунок высокой многоэтажной башни."),
        T3("haut", "tall", "высокий"))
    m['兴'] = CharInfo('兴', "xìng", 6, 0, "八", "一八",
        T3("joie, prospérer", "joy, to thrive", "радость, процветать"),
        T3("Des mains qui lèvent un objet en fête.", "Hands lifting something in celebration.", "Руки, поднимающие что-то в праздник."),
        T3("huit, partager", "eight, divide", "восемь, делить"))
    m['认'] = CharInfo('认', "rèn", 4, 3, "讠", "讠人",
        T3("reconnaître", "to recognize", "узнавать"),
        T3("La parole 讠 qui identifie une personne 人.", "Speech 讠 that identifies a person 人.", "Речь 讠, узнающая человека 人."),
        T3("parole", "speech", "речь"))
    m['识'] = CharInfo('识', "shí", 7, 3, "讠", "讠只",
        T3("savoir, connaître", "to know", "знать"),
        T3("La parole 讠 ; 只 donne le son.", "Speech 讠; 只 gives the sound.", "Речь 讠; 只 даёт звук."),
        T3("parole", "speech", "речь"))
    m['叫'] = CharInfo('叫', "jiào", 5, 3, "口", "口丩",
        T3("appeler, crier", "to call", "звать"),
        T3("La bouche 口 qui appelle ; 丩 donne le son.", "The mouth 口 calling; 丩 gives the sound.", "Рот 口 зовёт; 丩 даёт звук."),
        T3("bouche", "mouth", "рот"))
    m['名'] = CharInfo('名', "míng", 6, 2, "口", "夕口",
        T3("nom", "name", "имя"),
        T3("Le soir 夕, on crie 口 son nom pour se reconnaître.", "At night 夕 you call out 口 your name.", "Вечером 夕 называют 口 своё имя."),
        T3("bouche", "mouth", "рот"))
    m['字'] = CharInfo('字', "zì", 6, 2, "子", "宀子",
        T3("caractère, mot écrit", "character", "иероглиф"),
        T3("Un enfant 子 qui étudie à la maison 宀.", "A child 子 studying at home 宀.", "Ребёнок 子 учится дома 宀."),
        T3("enfant", "child", "ребёнок"))
    m['我'] = CharInfo('我', "wǒ", 7, 2, "戈", "扌戈",
        T3("je, moi", "I, me", "я"),
        T3("Une main 扌 qui tient une hallebarde 戈 : moi.", "A hand 扌 holding a halberd 戈: me.", "Рука 扌 держит алебарду 戈: я."),
        T3("hallebarde", "halberd", "алебарда"))
    m['他'] = CharInfo('他', "tā", 5, 2, "亻", "亻也",
        T3("il, lui", "he, him", "он"),
        T3("Une autre personne 亻.", "Another person 亻.", "Другой человек 亻."),
        T3("personne", "person", "человек"))
    m['她'] = CharInfo('她', "tā", 6, 2, "女", "女也",
        T3("elle", "she, her", "она"),
        T3("Une femme 女 : elle.", "A woman 女: she.", "Женщина 女: она."),
        T3("femme", "woman", "женщина"))
    m['们'] = CharInfo('们', "men", 5, 3, "亻", "亻门",
        T3("(pluriel)", "(plural)", "(мн. число)"),
        T3("Des personnes 亻 ; 门 donne le son.", "People 亻; 门 gives the sound.", "Люди 亻; 门 даёт звук."),
        T3("personne", "person", "человек"))
    m['人'] = CharInfo('人', "rén", 2, 1, "人", "",
        T3("personne", "person", "человек"),
        T3("Deux jambes qui marchent.", "Two legs walking.", "Две шагающие ноги."),
        T3("personne", "person", "человек"))
    m['谁'] = CharInfo('谁', "shuí", 10, 3, "讠", "讠隹",
        T3("qui ?", "who?", "кто?"),
        T3("La parole 讠 qui demande qui ; 隹 donne le son.", "Speech 讠 asking who; 隹 gives the sound.", "Речь 讠 спрашивает «кто»; 隹 даёт звук."),
        T3("parole", "speech", "речь"))
    m['先'] = CharInfo('先', "xiān", 6, 2, "儿", "土儿",
        T3("d'abord, avant", "first", "сначала"),
        T3("Un pied qui avance devant les autres 儿.", "A foot stepping ahead of others 儿.", "Нога, шагающая впереди других 儿."),
        T3("jambes, enfant", "legs, child", "ноги, ребёнок"))
    m['生'] = CharInfo('生', "shēng", 5, 2, "生", "一土",
        T3("naître, vie", "life, to be born", "жизнь, рождаться"),
        T3("Une pousse qui sort de la terre 土.", "A sprout coming out of the soil 土.", "Росток выходит из земли 土."),
        T3("vie", "life", "жизнь"))
    m['小'] = CharInfo('小', "xiǎo", 3, 2, "小", "亅八",
        T3("petit", "small", "маленький"),
        T3("Un trait 亅 qui partage en deux 八 : petit.", "A stroke 亅 splitting in two 八: small.", "Черта 亅 делит пополам 八: маленький."),
        T3("petit", "small", "маленький"))
    m['姐'] = CharInfo('姐', "jiě", 8, 3, "女", "女且",
        T3("grande sœur", "older sister", "старшая сестра"),
        T3("Une femme 女 ; 且 donne le son.", "A woman 女; 且 gives the sound.", "Женщина 女; 且 даёт звук."),
        T3("femme", "woman", "женщина"))
    m['老'] = CharInfo('老', "lǎo", 6, 1, "老", "老匕",
        T3("vieux", "old", "старый"),
        T3("Une personne courbée aux longs cheveux.", "A bent person with long hair.", "Сгорбленный человек с длинными волосами."),
        T3("vieux", "old", "старый"))
    m['师'] = CharInfo('师', "shī", 6, 2, "巾", "刂帀",
        T3("maître, professeur", "teacher", "учитель"),
        T3("Celui qui manie l'art avec habileté.", "One who masters a craft.", "Тот, кто владеет мастерством."),
        T3("tissu", "cloth", "ткань"))
    m['学'] = CharInfo('学', "xué", 8, 2, "子", "冖子",
        T3("étudier", "to study", "учиться"),
        T3("Un enfant 子 sous un toit 冖 où l'on apprend.", "A child 子 under a roof 冖 where one learns.", "Ребёнок 子 под крышей 冖, где учатся."),
        T3("enfant", "child", "ребёнок"))
    m['同'] = CharInfo('同', "tóng", 6, 2, "口", "凡口",
        T3("même, ensemble", "same, together", "одинаковый, вместе"),
        T3("Tout le monde parle 口 d'une même voix.", "Everyone speaks 口 with one voice.", "Все говорят 口 одним голосом."),
        T3("bouche", "mouth", "рот"))
    m['朋'] = CharInfo('朋', "péng", 8, 2, "月", "月月",
        T3("ami", "friend", "друг"),
        T3("Deux 月 côte à côte, comme deux amis.", "Two 月 side by side, like two friends.", "Два 月 рядом, как два друга."),
        T3("lune, chair", "moon, flesh", "луна, плоть"))
    m['友'] = CharInfo('友', "yǒu", 4, 2, "又", "又",
        T3("ami", "friend", "друг"),
        T3("Deux mains 又 qui se serrent.", "Two hands 又 joined together.", "Две руки 又 сжаты вместе."),
        T3("main", "hand", "рука"))
    m['医'] = CharInfo('医', "yī", 7, 3, "匸", "匸矢",
        T3("soigner, médecin", "to heal, doctor", "лечить, врач"),
        T3("Une flèche 矢 dans une boîte 匸 : on soigne la blessure.", "An arrow 矢 in a box 匸: treating the wound.", "Стрела 矢 в коробке 匸: лечат рану."),
        T3("boîte", "box", "ящик"))
    m['爸'] = CharInfo('爸', "bà", 8, 3, "父", "父巴",
        T3("papa", "dad", "папа"),
        T3("Le père 父 ; 巴 donne le son.", "Father 父; 巴 gives the sound.", "Отец 父; 巴 даёт звук."),
        T3("père", "father", "отец"))
    m['妈'] = CharInfo('妈', "mā", 6, 3, "女", "女马",
        T3("maman", "mum", "мама"),
        T3("Une femme 女 ; 马 donne le son (mǎ).", "A woman 女; 马 gives the sound (mǎ).", "Женщина 女; 马 даёт звук (mǎ)."),
        T3("femme", "woman", "женщина"))
    m['儿'] = CharInfo('儿', "ér", 2, 1, "儿", "丿乚",
        T3("enfant, fils", "child, son", "ребёнок, сын"),
        T3("Les jambes d'un petit enfant.", "The legs of a small child.", "Ножки маленького ребёнка."),
        T3("jambes, enfant", "legs, child", "ноги, ребёнок"))
    m['子'] = CharInfo('子', "zǐ", 3, 1, "子", "了一",
        T3("enfant, fils", "child, son", "ребёнок, сын"),
        T3("Un bébé emmailloté, les bras tendus.", "A swaddled baby with arms out.", "Спелёнутый малыш с раскинутыми руками."),
        T3("enfant", "child", "ребёнок"))
    m['女'] = CharInfo('女', "nǚ", 3, 1, "女", "",
        T3("femme", "woman", "женщина"),
        T3("Une femme agenouillée, vue de côté.", "A kneeling woman, seen from the side.", "Женщина на коленях, вид сбоку."),
        T3("femme", "woman", "женщина"))
    m['家'] = CharInfo('家', "jiā", 10, 3, "宀", "宀豕",
        T3("maison, famille", "home, family", "дом, семья"),
        T3("Un cochon 豕 sous un toit 宀 : la maison.", "A pig 豕 under a roof 宀: home.", "Свинья 豕 под крышей 宀: дом."),
        T3("toit", "roof", "крыша"))
    m['狗'] = CharInfo('狗', "gǒu", 8, 3, "犭", "犭句",
        T3("chien", "dog", "собака"),
        T3("L'animal 犭 ; 句 donne le son.", "Animal 犭; 句 gives the sound.", "Животное 犭; 句 даёт звук."),
        T3("animal", "animal", "зверь"))
    m['猫'] = CharInfo('猫', "māo", 11, 3, "犭", "犭苗",
        T3("chat", "cat", "кошка"),
        T3("L'animal 犭 ; 苗 (miáo) imite le miaou.", "Animal 犭; 苗 (miáo) sounds like a meow.", "Животное 犭; 苗 (miáo) звучит как «мяу»."),
        T3("animal", "animal", "зверь"))
    m['爱'] = CharInfo('爱', "ài", 10, 2, "爫", "爫冖友",
        T3("aimer", "to love", "любить"),
        T3("Une main 爫 qui accueille un ami 友 sous son toit 冖.", "A hand 爫 bringing a friend 友 under one's roof 冖.", "Рука 爫 приводит друга 友 под крышу 冖."),
        T3("griffe, main", "claw, hand", "коготь, рука"))
    m['喜'] = CharInfo('喜', "xǐ", 12, 2, "口", "口",
        T3("joie", "joy", "радость"),
        T3("Chanter 口 au son du tambour, posé en haut.", "Singing 口 to the beat of a drum, drawn on top.", "Петь 口 под барабан, нарисованный сверху."),
        T3("bouche", "mouth", "рот"))
    m['欢'] = CharInfo('欢', "huān", 6, 0, "欠", "又欠",
        T3("joyeux", "happy", "радостный"),
        T3("Une bouche grande ouverte 欠 de joie.", "A mouth opened wide 欠 with joy.", "Рот, широко открытый 欠 от радости."),
        T3("bâiller, bouche ouverte", "yawn, open mouth", "зевать, открытый рот"))
    m['住'] = CharInfo('住', "zhù", 7, 2, "亻", "亻主",
        T3("habiter", "to live", "жить"),
        T3("Une personne 亻 qui reste chez son hôte 主.", "A person 亻 staying at a host's 主.", "Человек 亻 живёт у хозяина 主."),
        T3("personne", "person", "человек"))
    m['桌'] = CharInfo('桌', "zhuō", 10, 0, "木", "卓木",
        T3("table", "table", "стол"),
        T3("Un plateau haut 卓 en bois 木.", "A raised top 卓 made of wood 木.", "Высокая доска 卓 из дерева 木."),
        T3("arbre, bois", "tree, wood", "дерево"))
    m['椅'] = CharInfo('椅', "yǐ", 12, 3, "木", "木奇",
        T3("chaise", "chair", "стул"),
        T3("En bois 木 ; 奇 donne le son.", "Made of wood 木; 奇 gives the sound.", "Из дерева 木; 奇 даёт звук."),
        T3("arbre, bois", "tree, wood", "дерево"))
    m['东'] = CharInfo('东', "dōng", 5, 1, "一", "七小",
        T3("est", "east", "восток"),
        T3("Le soleil qui se lève derrière un arbre.", "The sun rising behind a tree.", "Солнце встаёт из-за дерева."),
        T3("un", "one", "один"))
    m['西'] = CharInfo('西', "xī", 6, 1, "西", "兀囗",
        T3("ouest", "west", "запад"),
        T3("Un oiseau qui rentre au nid au coucher du soleil.", "A bird settling in its nest at sunset.", "Птица садится в гнездо на закате."),
        T3("ouest", "west", "запад"))
    m['一'] = CharInfo('一', "yī", 1, 2, "一", "",
        T3("un", "one", "один"),
        T3("Un seul trait.", "A single stroke.", "Одна черта."),
        T3("un", "one", "один"))
    m['二'] = CharInfo('二', "èr", 2, 2, "二", "一一",
        T3("deux", "two", "два"),
        T3("Deux traits.", "Two strokes.", "Две черты."),
        T3("deux", "two", "два"))
    m['三'] = CharInfo('三', "sān", 3, 2, "一", "一二",
        T3("trois", "three", "три"),
        T3("Trois traits.", "Three strokes.", "Три черты."),
        T3("un", "one", "один"))
    m['四'] = CharInfo('四', "sì", 5, 2, "囗", "囗儿",
        T3("quatre", "four", "четыре"),
        T3("Une pièce à quatre murs 囗.", "A room with four walls 囗.", "Комната с четырьмя стенами 囗."),
        T3("enceinte", "enclosure", "ограда"))
    m['五'] = CharInfo('五', "wǔ", 4, 2, "二", "二",
        T3("cinq", "five", "пять"),
        T3("Le ciel et la terre 二 croisés : les cinq éléments.", "Heaven and earth 二 crossed: the five elements.", "Небо и земля 二 крест-накрест: пять стихий."),
        T3("deux", "two", "два"))
    m['六'] = CharInfo('六', "liù", 4, 0, "八", "亠八",
        T3("six", "six", "шесть"),
        T3("Un toit 亠 posé sur deux pieds 八.", "A roof 亠 on two legs 八.", "Крыша 亠 на двух ножках 八."),
        T3("huit, partager", "eight, divide", "восемь, делить"))
    m['七'] = CharInfo('七', "qī", 2, 0, "一", "一乚",
        T3("sept", "seven", "семь"),
        T3("Un trait coupé : on tranche.", "A stroke cut across.", "Перерезанная черта."),
        T3("un", "one", "один"))
    m['八'] = CharInfo('八', "bā", 2, 2, "八", "",
        T3("huit", "eight", "восемь"),
        T3("Deux traits qui s'écartent : partager.", "Two strokes parting: to divide.", "Две расходящиеся черты: делить."),
        T3("huit, partager", "eight, divide", "восемь, делить"))
    m['九'] = CharInfo('九', "jiǔ", 2, 1, "乙", "丿乙",
        T3("neuf", "nine", "девять"),
        T3("Un coude plié.", "A bent elbow.", "Согнутый локоть."),
        T3("second, crochet", "second, hook", "второй, крюк"))
    m['十'] = CharInfo('十', "shí", 2, 0, "十", "一丨",
        T3("dix", "ten", "десять"),
        T3("Une croix : tout est complet.", "A cross: everything is complete.", "Крест: всё полно."),
        T3("dix", "ten", "десять"))
    m['几'] = CharInfo('几', "jǐ", 2, 1, "几", "丿乙",
        T3("combien, quelques", "how many, a few", "сколько, несколько"),
        T3("Le dessin d'une petite table.", "The drawing of a small table.", "Рисунок маленького столика."),
        T3("petite table", "small table", "столик"))
    m['多'] = CharInfo('多', "duō", 6, 2, "夕", "夕夕",
        T3("beaucoup", "many, much", "много"),
        T3("Deux soirs 夕 empilés : beaucoup.", "Two evenings 夕 stacked: many.", "Два вечера 夕 друг на друге: много."),
        T3("soir", "evening", "вечер"))
    m['少'] = CharInfo('少', "shǎo", 4, 1, "小", "小丿",
        T3("peu", "few, little", "мало"),
        T3("Des grains de sable 小 : peu.", "Grains of sand 小: little.", "Песчинки 小: мало."),
        T3("petit", "small", "маленький"))
    m['岁'] = CharInfo('岁', "suì", 6, 0, "山", "山夕",
        T3("an (âge)", "year of age", "год (возраст)"),
        T3("Les montagnes 山 et les soirs 夕 qui passent.", "Mountains 山 and evenings 夕 going by.", "Горы 山 и проходящие вечера 夕."),
        T3("montagne", "mountain", "гора"))
    m['个'] = CharInfo('个', "gè", 3, 2, "丨", "人丨",
        T3("(classificateur)", "(measure word)", "(счётное слово)"),
        T3("Une personne 人 comptée d'un trait 丨.", "A person 人 counted with a tally 丨.", "Человек 人, отмеченный чертой 丨."),
        T3("trait vertical", "vertical stroke", "вертикальная черта"))
    m['本'] = CharInfo('本', "běn", 5, 2, "木", "木一",
        T3("racine ; (livres)", "root; (books)", "корень; (книги)"),
        T3("Un arbre 木 dont on marque la racine.", "A tree 木 with its root marked.", "Дерево 木 с отмеченным корнем."),
        T3("arbre, bois", "tree, wood", "дерево"))
    m['块'] = CharInfo('块', "kuài", 7, 3, "土", "土夬",
        T3("morceau ; yuan", "piece; yuan", "кусок; юань"),
        T3("Un morceau de terre 土 ; 夬 donne le son.", "A lump of earth 土; 夬 gives the sound.", "Кусок земли 土; 夬 даёт звук."),
        T3("terre", "earth", "земля"))
    m['些'] = CharInfo('些', "xiē", 8, 3, "二", "此二",
        T3("quelques", "some", "несколько"),
        T3("Un peu de ceci 此 ; 二 complète.", "A little of this 此; 二 completes it.", "Немного этого 此; 二 дополняет."),
        T3("deux", "two", "два"))
    m['今'] = CharInfo('今', "jīn", 4, 2, "人", "人",
        T3("maintenant", "now", "сейчас"),
        T3("Un toit qui couvre le moment présent.", "A roof over the present moment.", "Крыша над настоящим моментом."),
        T3("personne", "person", "человек"))
    m['天'] = CharInfo('天', "tiān", 4, 2, "大", "一大",
        T3("ciel, jour", "sky, day", "небо, день"),
        T3("Le ciel 一 au-dessus d'un homme 大.", "The sky 一 above a man 大.", "Небо 一 над человеком 大."),
        T3("grand", "big", "большой"))
    m['明'] = CharInfo('明', "míng", 8, 2, "日", "日月",
        T3("clair, demain", "bright, tomorrow", "светлый, завтра"),
        T3("Le soleil 日 et la lune 月 : la lumière.", "Sun 日 and moon 月: light.", "Солнце 日 и луна 月: свет."),
        T3("soleil, jour", "sun, day", "солнце, день"))
    m['昨'] = CharInfo('昨', "zuó", 9, 3, "日", "日乍",
        T3("hier", "yesterday", "вчера"),
        T3("Un jour 日 déjà passé ; 乍 donne le son.", "A day 日 gone by; 乍 gives the sound.", "Прошедший день 日; 乍 даёт звук."),
        T3("soleil, jour", "sun, day", "солнце, день"))
    m['年'] = CharInfo('年', "nián", 6, 2, "干", "干",
        T3("année", "year", "год"),
        T3("Un homme qui porte la récolte : une année.", "A man carrying the harvest: a year.", "Человек несёт урожай: год."),
        T3("bouclier, sec", "shield, dry", "щит, сухой"))
    m['月'] = CharInfo('月', "yuè", 4, 1, "月", "冂二",
        T3("lune, mois", "moon, month", "луна, месяц"),
        T3("Un croissant de lune.", "A crescent moon.", "Полумесяц."),
        T3("lune, chair", "moon, flesh", "луна, плоть"))
    m['号'] = CharInfo('号', "hào", 5, 3, "口", "口丂",
        T3("numéro, date", "number, date", "номер, число"),
        T3("La bouche 口 qui annonce un numéro.", "The mouth 口 calling a number.", "Рот 口 называет номер."),
        T3("bouche", "mouth", "рот"))
}

private fun part1(m: MutableMap<Char, CharInfo>) {
    m['星'] = CharInfo('星', "xīng", 9, 3, "日", "日生",
        T3("étoile", "star", "звезда"),
        T3("Un soleil 日 qui naît 生 : une étoile.", "A sun 日 being born 生: a star.", "Рождающееся 生 солнце 日: звезда."),
        T3("soleil, jour", "sun, day", "солнце, день"))
    m['期'] = CharInfo('期', "qī", 12, 3, "月", "其月",
        T3("période", "period", "период"),
        T3("La lune 月 marque le temps ; 其 donne le son.", "The moon 月 marks time; 其 gives the sound.", "Луна 月 отмечает время; 其 даёт звук."),
        T3("lune, chair", "moon, flesh", "луна, плоть"))
    m['上'] = CharInfo('上', "shàng", 3, 2, "一", "一",
        T3("dessus, monter", "up, on", "верх, на"),
        T3("Un trait au-dessus de la ligne.", "A mark above the line.", "Черта над линией."),
        T3("un", "one", "один"))
    m['午'] = CharInfo('午', "wǔ", 4, 0, "十", "丿干",
        T3("midi", "noon", "полдень"),
        T3("Un pilon dressé : le soleil au plus haut.", "An upright pestle: the sun at its peak.", "Стоящий пест: солнце в зените."),
        T3("dix", "ten", "десять"))
    m['中'] = CharInfo('中', "zhōng", 4, 2, "丨", "口丨",
        T3("milieu, Chine", "middle, China", "середина, Китай"),
        T3("Un trait 丨 au centre d'une cible 口.", "A line 丨 through the middle of a target 口.", "Черта 丨 через центр мишени 口."),
        T3("trait vertical", "vertical stroke", "вертикальная черта"))
    m['下'] = CharInfo('下', "xià", 3, 2, "一", "一卜",
        T3("dessous, descendre", "down, under", "низ, под"),
        T3("Un trait sous la ligne.", "A mark below the line.", "Черта под линией."),
        T3("un", "one", "один"))
    m['现'] = CharInfo('现', "xiàn", 8, 2, "王", "王见",
        T3("apparaître, maintenant", "appear, now", "появляться, сейчас"),
        T3("Voir 见 le jade 王 briller.", "To see 见 jade 王 shine.", "Видеть 见, как сияет нефрит 王."),
        T3("roi, jade", "king, jade", "царь, нефрит"))
    m['在'] = CharInfo('在', "zài", 6, 3, "土", "才土",
        T3("être à, en train de", "to be at", "находиться"),
        T3("La terre 土 où l'on se trouve ; 才 donne le son.", "The ground 土 where you are; 才 gives the sound.", "Земля 土, где находишься; 才 даёт звук."),
        T3("terre", "earth", "земля"))
    m['时'] = CharInfo('时', "shí", 7, 3, "日", "日寸",
        T3("heure, temps", "time", "время"),
        T3("Le soleil 日 qui avance pas à pas 寸.", "The sun 日 moving inch by inch 寸.", "Солнце 日 движется понемногу 寸."),
        T3("soleil, jour", "sun, day", "солнце, день"))
    m['候'] = CharInfo('候', "hòu", 10, 3, "亻", "亻",
        T3("attendre, moment", "to wait, time", "ждать, время"),
        T3("Une personne 亻 qui attend.", "A person 亻 waiting.", "Человек 亻 ждёт."),
        T3("personne", "person", "человек"))
    m['点'] = CharInfo('点', "diǎn", 9, 3, "灬", "占灬",
        T3("point ; heure", "dot; o'clock", "точка; час"),
        T3("Des points de feu 灬 ; 占 donne le son.", "Dots of fire 灬; 占 gives the sound.", "Точки огня 灬; 占 даёт звук."),
        T3("feu", "fire", "огонь"))
    m['分'] = CharInfo('分', "fēn", 4, 2, "刀", "八刀",
        T3("partager ; minute", "to divide; minute", "делить; минута"),
        T3("Un couteau 刀 qui coupe en deux 八.", "A knife 刀 cutting in two 八.", "Нож 刀 режет пополам 八."),
        T3("couteau", "knife", "нож"))
    m['钟'] = CharInfo('钟', "zhōng", 9, 3, "钅", "钅中",
        T3("horloge, cloche", "clock, bell", "часы, колокол"),
        T3("Le métal 钅 au milieu 中 sonne.", "Metal 钅 ringing in the middle 中.", "Металл 钅 звенит посередине 中."),
        T3("métal", "metal", "металл"))
    m['吃'] = CharInfo('吃', "chī", 6, 3, "口", "口乞",
        T3("manger", "to eat", "есть"),
        T3("La bouche 口 ; 乞 donne le son.", "The mouth 口; 乞 gives the sound.", "Рот 口; 乞 даёт звук."),
        T3("bouche", "mouth", "рот"))
    m['喝'] = CharInfo('喝', "hē", 12, 3, "口", "口曷",
        T3("boire", "to drink", "пить"),
        T3("La bouche 口 ; 曷 donne le son.", "The mouth 口; 曷 gives the sound.", "Рот 口; 曷 даёт звук."),
        T3("bouche", "mouth", "рот"))
    m['茶'] = CharInfo('茶', "chá", 9, 3, "艹", "艹人木",
        T3("thé", "tea", "чай"),
        T3("Une plante 艹 cueillie par l'homme 人 sur l'arbre 木.", "A plant 艹 picked by a person 人 from a tree 木.", "Растение 艹, которое человек 人 собирает с дерева 木."),
        T3("herbe, plante", "grass, plant", "трава, растение"))
    m['水'] = CharInfo('水', "shuǐ", 4, 1, "水", "亅",
        T3("eau", "water", "вода"),
        T3("Une rivière qui coule entre ses rives.", "A river flowing between its banks.", "Река течёт между берегами."),
        T3("eau", "water", "вода"))
    m['菜'] = CharInfo('菜', "cài", 11, 3, "艹", "艹采",
        T3("légume, plat", "vegetable, dish", "овощ, блюдо"),
        T3("Une plante 艹 qu'on cueille 采.", "A plant 艹 you pick 采.", "Растение 艹, которое срывают 采."),
        T3("herbe, plante", "grass, plant", "трава, растение"))
    m['米'] = CharInfo('米', "mǐ", 6, 1, "米", "丷木",
        T3("riz", "rice", "рис"),
        T3("Des grains de riz éparpillés.", "Scattered grains of rice.", "Рассыпанные зёрна риса."),
        T3("riz", "rice", "рис"))
    m['饭'] = CharInfo('饭', "fàn", 7, 3, "饣", "饣反",
        T3("repas, riz cuit", "meal, rice", "еда, рис"),
        T3("La nourriture 饣 ; 反 donne le son.", "Food 饣; 反 gives the sound.", "Еда 饣; 反 даёт звук."),
        T3("nourriture", "food", "еда"))
    m['果'] = CharInfo('果', "guǒ", 8, 2, "木", "田木",
        T3("fruit", "fruit", "плод"),
        T3("Un fruit 田 sur un arbre 木.", "A fruit 田 on a tree 木.", "Плод 田 на дереве 木."),
        T3("arbre, bois", "tree, wood", "дерево"))
    m['苹'] = CharInfo('苹', "píng", 8, 3, "艹", "艹平",
        T3("pomme (苹果)", "apple (苹果)", "яблоко (苹果)"),
        T3("Une plante 艹 ; 平 donne le son.", "A plant 艹; 平 gives the sound.", "Растение 艹; 平 даёт звук."),
        T3("herbe, plante", "grass, plant", "трава, растение"))
    m['杯'] = CharInfo('杯', "bēi", 8, 3, "木", "木不",
        T3("tasse, verre", "cup, glass", "чашка, стакан"),
        T3("En bois 木 ; 不 donne le son.", "Made of wood 木; 不 gives the sound.", "Из дерева 木; 不 даёт звук."),
        T3("arbre, bois", "tree, wood", "дерево"))
    m['店'] = CharInfo('店', "diàn", 8, 3, "广", "广占",
        T3("magasin", "shop", "магазин"),
        T3("Un abri 广 ; 占 donne le son.", "A shelter 广; 占 gives the sound.", "Навес 广; 占 даёт звук."),
        T3("abri", "shelter", "навес"))
    m['钱'] = CharInfo('钱', "qián", 10, 3, "钅", "钅戋",
        T3("argent", "money", "деньги"),
        T3("Le métal 钅 des pièces.", "The metal 钅 of coins.", "Металл 钅 монет."),
        T3("métal", "metal", "металл"))
    m['买'] = CharInfo('买', "mǎi", 6, 0, "大", "乛头",
        T3("acheter", "to buy", "покупать"),
        T3("On sort l'argent pour avoir quelque chose.", "Taking out money to get something.", "Достать деньги, чтобы что-то получить."),
        T3("grand", "big", "большой"))
    m['商'] = CharInfo('商', "shāng", 11, 3, "口", "亠丷冏",
        T3("commerce", "trade", "торговля"),
        T3("Un marchand qui parle 口 sous son auvent.", "A merchant talking 口 under his awning.", "Торговец говорит 口 под навесом."),
        T3("bouche", "mouth", "рот"))
    m['看'] = CharInfo('看', "kàn", 9, 2, "目", "手目",
        T3("regarder", "to look", "смотреть"),
        T3("Une main 手 au-dessus de l'œil 目 pour regarder loin.", "A hand 手 over the eye 目 to look far.", "Рука 手 над глазом 目, чтобы смотреть вдаль."),
        T3("œil", "eye", "глаз"))
    m['听'] = CharInfo('听', "tīng", 7, 2, "口", "口斤",
        T3("écouter", "to listen", "слушать"),
        T3("Des paroles 口 qui arrivent à l'oreille.", "Words 口 reaching the ear.", "Слова 口 доходят до уха."),
        T3("bouche", "mouth", "рот"))
    m['说'] = CharInfo('说', "shuō", 9, 3, "讠", "讠兑",
        T3("parler, dire", "to speak", "говорить"),
        T3("La parole 讠 ; 兑 donne le son.", "Speech 讠; 兑 gives the sound.", "Речь 讠; 兑 даёт звук."),
        T3("parole", "speech", "речь"))
    m['读'] = CharInfo('读', "dú", 10, 2, "讠", "讠卖",
        T3("lire, étudier", "to read", "читать"),
        T3("La parole 讠 : lire à voix haute.", "Speech 讠: reading aloud.", "Речь 讠: читать вслух."),
        T3("parole", "speech", "речь"))
    m['写'] = CharInfo('写', "xiě", 5, 0, "冖", "冖与",
        T3("écrire", "to write", "писать"),
        T3("Un couvercle 冖 posé sur ce qu'on écrit.", "A cover 冖 over what you write.", "Крышка 冖 над написанным."),
        T3("couvercle", "cover", "крышка"))
    m['做'] = CharInfo('做', "zuò", 11, 2, "亻", "亻故",
        T3("faire", "to do, to make", "делать"),
        T3("Une personne 亻 qui agit 故.", "A person 亻 taking action 故.", "Человек 亻 действует 故."),
        T3("personne", "person", "человек"))
    m['工'] = CharInfo('工', "gōng", 3, 1, "工", "一丄",
        T3("travail", "work", "работа"),
        T3("Un outil d'artisan.", "A craftsman's tool.", "Инструмент ремесленника."),
        T3("travail", "work", "работа"))
    m['作'] = CharInfo('作', "zuò", 7, 2, "亻", "亻乍",
        T3("faire, créer", "to make, to do", "делать, создавать"),
        T3("Une personne 亻 qui fabrique pour la première fois.", "A person 亻 making something new.", "Человек 亻 создаёт что-то новое."),
        T3("personne", "person", "человек"))
    m['习'] = CharInfo('习', "xí", 3, 0, "冫", "冫",
        T3("pratiquer", "to practise", "упражняться"),
        T3("Un oiseau qui bat des ailes pour apprendre à voler.", "A bird flapping its wings to learn to fly.", "Птица машет крыльями, учась летать."),
        T3("glace", "ice", "лёд"))
    m['睡'] = CharInfo('睡', "shuì", 13, 3, "目", "目垂",
        T3("dormir", "to sleep", "спать"),
        T3("L'œil 目 qui tombe 垂 de sommeil.", "The eye 目 drooping 垂 with sleep.", "Глаз 目 закрывается 垂 от сна."),
        T3("œil", "eye", "глаз"))
    m['觉'] = CharInfo('觉', "jué", 9, 3, "见", "冖见",
        T3("sommeil ; sentir", "sleep; to feel", "сон; чувствовать"),
        T3("Voir 见 clair en s'éveillant.", "Seeing 见 clearly on waking.", "Ясно видеть 见 при пробуждении."),
        T3("voir", "see", "видеть"))
    m['打'] = CharInfo('打', "dǎ", 5, 3, "扌", "扌丁",
        T3("frapper ; faire", "to hit; to do", "бить; делать"),
        T3("La main 扌 qui frappe ; 丁 donne le son.", "The hand 扌 striking; 丁 gives the sound.", "Рука 扌 бьёт; 丁 даёт звук."),
        T3("main", "hand", "рука"))
    m['电'] = CharInfo('电', "diàn", 5, 2, "曰", "曰乚",
        T3("électricité", "electricity", "электричество"),
        T3("Un éclair qui jaillit d'un nuage.", "Lightning bursting from a cloud.", "Молния вырывается из тучи."),
        T3("dire", "say", "говорить"))
    m['话'] = CharInfo('话', "huà", 8, 2, "讠", "讠舌",
        T3("parole, langue", "speech, words", "речь, слова"),
        T3("La parole 讠 et la langue 舌.", "Speech 讠 and the tongue 舌.", "Речь 讠 и язык 舌."),
        T3("parole", "speech", "речь"))
    m['开'] = CharInfo('开', "kāi", 4, 2, "廾", "一廾",
        T3("ouvrir", "to open", "открывать"),
        T3("Deux mains 廾 qui soulèvent la barre d'une porte.", "Two hands 廾 lifting a door bar.", "Две руки 廾 поднимают засов."),
        T3("deux mains", "two hands", "две руки"))
    m['坐'] = CharInfo('坐', "zuò", 7, 2, "土", "从土",
        T3("s'asseoir", "to sit", "сидеть"),
        T3("Deux personnes 从 assises par terre 土.", "Two people 从 sitting on the ground 土.", "Два человека 从 сидят на земле 土."),
        T3("terre", "earth", "земля"))
    m['来'] = CharInfo('来', "lái", 7, 2, "木", "未丷",
        T3("venir", "to come", "приходить"),
        T3("Un épi de blé qui arrive à maturité.", "An ear of wheat coming to ripeness.", "Колос пшеницы созревает."),
        T3("arbre, bois", "tree, wood", "дерево"))
    m['去'] = CharInfo('去', "qù", 5, 0, "厶", "土厶",
        T3("aller, partir", "to go", "идти, уходить"),
        T3("Quelqu'un qui quitte sa terre 土.", "Someone leaving their land 土.", "Кто-то покидает свою землю 土."),
        T3("privé", "private", "частный"))
    m['回'] = CharInfo('回', "huí", 6, 2, "囗", "囗口",
        T3("revenir", "to return", "возвращаться"),
        T3("Une spirale qui revient à son centre.", "A spiral coming back to its centre.", "Спираль возвращается к центру."),
        T3("enceinte", "enclosure", "ограда"))
    m['国'] = CharInfo('国', "guó", 8, 2, "囗", "囗玉",
        T3("pays", "country", "страна"),
        T3("Un trésor 玉 à l'intérieur des frontières 囗.", "A treasure 玉 inside the borders 囗.", "Сокровище 玉 внутри границ 囗."),
        T3("enceinte", "enclosure", "ограда"))
    m['北'] = CharInfo('北', "běi", 5, 1, "匕", "匕",
        T3("nord", "north", "север"),
        T3("Deux personnes dos à dos.", "Two people back to back.", "Два человека спиной к спине."),
        T3("cuillère", "spoon", "ложка"))
    m['京'] = CharInfo('京', "jīng", 8, 1, "亠", "亠口小",
        T3("capitale", "capital", "столица"),
        T3("Un grand édifice sur une colline.", "A big building on a hill.", "Большое здание на холме."),
        T3("couvercle, toit", "lid, roof", "крышка"))
    m['校'] = CharInfo('校', "xiào", 10, 3, "木", "木交",
        T3("école", "school", "школа"),
        T3("En bois 木 ; 交 donne le son.", "Made of wood 木; 交 gives the sound.", "Из дерева 木; 交 даёт звук."),
        T3("arbre, bois", "tree, wood", "дерево"))
    m['院'] = CharInfo('院', "yuàn", 9, 3, "阝", "阝完",
        T3("cour, institut", "courtyard, institute", "двор, институт"),
        T3("Un lieu 阝 entouré de murs ; 完 donne le son.", "A walled place 阝; 完 gives the sound.", "Огороженное место 阝; 完 даёт звук."),
        T3("ville, colline", "town, hill", "город, холм"))
    m['这'] = CharInfo('这', "zhè", 7, 0, "辶", "辶文",
        T3("ceci, ce", "this", "этот"),
        T3("Marcher 辶 vers ce qui est proche.", "Walking 辶 toward what is near.", "Идти 辶 к тому, что рядом."),
        T3("marcher", "walk", "идти"))
    m['那'] = CharInfo('那', "nà", 6, 0, "阝", "阝",
        T3("cela, ce…-là", "that", "тот"),
        T3("Un lieu 阝 au loin.", "A place 阝 far away.", "Далёкое место 阝."),
        T3("ville, colline", "town, hill", "город, холм"))
    m['哪'] = CharInfo('哪', "nǎ", 9, 3, "口", "口那",
        T3("lequel ? où ?", "which? where?", "какой? где?"),
        T3("La bouche 口 qui demande « 那 » lequel ?", "The mouth 口 asking which 那?", "Рот 口 спрашивает: который 那?"),
        T3("bouche", "mouth", "рот"))
    m['里'] = CharInfo('里', "lǐ", 7, 2, "里", "田土",
        T3("dans, intérieur", "inside", "внутри"),
        T3("Des champs 田 sur la terre 土 : le village.", "Fields 田 on the land 土: a village.", "Поля 田 на земле 土: деревня."),
        T3("village", "village", "деревня"))
    m['前'] = CharInfo('前', "qián", 9, 0, "刂", "丷一刖",
        T3("devant, avant", "front, before", "перед"),
        T3("Un bateau qui avance, proue en avant.", "A boat moving forward, bow first.", "Лодка плывёт носом вперёд."),
        T3("couteau", "knife", "нож"))
    m['面'] = CharInfo('面', "miàn", 9, 1, "面", "",
        T3("visage, côté", "face, side", "лицо, сторона"),
        T3("Le contour d'un visage.", "The outline of a face.", "Контур лица."),
        T3("visage", "face", "лицо"))
    m['后'] = CharInfo('后', "hòu", 6, 0, "口", "口",
        T3("derrière, après", "behind, after", "сзади, после"),
        T3("Celui qui parle 口 en dernier.", "The one who speaks 口 last.", "Тот, кто говорит 口 последним."),
        T3("bouche", "mouth", "рот"))
    m['大'] = CharInfo('大', "dà", 3, 2, "大", "一人",
        T3("grand", "big", "большой"),
        T3("Un homme 人 qui écarte les bras.", "A man 人 with arms spread wide.", "Человек 人 с раскинутыми руками."),
        T3("grand", "big", "большой"))
    m['很'] = CharInfo('很', "hěn", 9, 3, "彳", "彳艮",
        T3("très", "very", "очень"),
        T3("Un pas 彳 ferme ; 艮 donne le son.", "A firm step 彳; 艮 gives the sound.", "Твёрдый шаг 彳; 艮 даёт звук."),
        T3("pas", "step", "шаг"))
    m['太'] = CharInfo('太', "tài", 4, 2, "大", "大丶",
        T3("trop, très", "too, very", "слишком"),
        T3("Un grand 大 avec un point de plus : trop.", "Big 大 with one more dot: too much.", "Большой 大 с лишней точкой: слишком."),
        T3("grand", "big", "большой"))
    m['冷'] = CharInfo('冷', "lěng", 7, 3, "冫", "冫令",
        T3("froid", "cold", "холодный"),
        T3("La glace 冫 ; 令 donne le son.", "Ice 冫; 令 gives the sound.", "Лёд 冫; 令 даёт звук."),
        T3("glace", "ice", "лёд"))
    m['热'] = CharInfo('热', "rè", 10, 3, "灬", "执灬",
        T3("chaud", "hot", "жаркий"),
        T3("Le feu 灬 en dessous ; 执 donne le son.", "Fire 灬 underneath; 执 gives the sound.", "Огонь 灬 снизу; 执 даёт звук."),
        T3("feu", "fire", "огонь"))
    m['漂'] = CharInfo('漂', "piào", 14, 3, "氵", "氵票",
        T3("flotter ; joli (漂亮)", "to float; pretty (漂亮)", "плыть; красивый (漂亮)"),
        T3("L'eau 氵 qui porte ; 票 donne le son.", "Water 氵 carrying; 票 gives the sound.", "Вода 氵 несёт; 票 даёт звук."),
        T3("eau", "water", "вода"))
    m['亮'] = CharInfo('亮', "liàng", 9, 1, "亠", "亠口冖几",
        T3("brillant", "bright", "яркий"),
        T3("Une lampe à huile allumée.", "A lit oil lamp.", "Зажжённая масляная лампа."),
        T3("couvercle, toit", "lid, roof", "крышка"))
    m['雨'] = CharInfo('雨', "yǔ", 8, 1, "雨", "帀",
        T3("pluie", "rain", "дождь"),
        T3("Des gouttes qui tombent d'un nuage.", "Drops falling from a cloud.", "Капли падают из тучи."),
        T3("pluie", "rain", "дождь"))
    m['是'] = CharInfo('是', "shì", 9, 2, "日", "日疋",
        T3("être, oui", "to be, yes", "быть, да"),
        T3("Le soleil 日 bien droit 疋 : c'est juste.", "The sun 日 standing straight 疋: that's right.", "Солнце 日 прямо 疋: это верно."),
        T3("soleil, jour", "sun, day", "солнце, день"))
    m['有'] = CharInfo('有', "yǒu", 6, 3, "月", "月",
        T3("avoir, il y a", "to have", "иметь, есть"),
        T3("Une main qui tient de la viande 月 : avoir.", "A hand holding meat 月: to have.", "Рука держит мясо 月: иметь."),
        T3("lune, chair", "moon, flesh", "луна, плоть"))
    m['吗'] = CharInfo('吗', "ma", 6, 3, "口", "口马",
        T3("(question oui/non)", "(yes/no question)", "(вопрос да/нет)"),
        T3("La bouche 口 ; 马 (mǎ) donne le son.", "The mouth 口; 马 (mǎ) gives the sound.", "Рот 口; 马 (mǎ) даёт звук."),
        T3("bouche", "mouth", "рот"))
    m['呢'] = CharInfo('呢', "ne", 8, 3, "口", "口尼",
        T3("(et… ?)", "(and…?)", "(а…?)"),
        T3("La bouche 口 ; 尼 donne le son.", "The mouth 口; 尼 gives the sound.", "Рот 口; 尼 даёт звук."),
        T3("bouche", "mouth", "рот"))
    m['什'] = CharInfo('什', "shén", 4, 2, "亻", "亻十",
        T3("quoi (什么)", "what (什么)", "что (什么)"),
        T3("Une file de dix 十 personnes 亻 : un mélange.", "A row of ten 十 people 亻: a mix.", "Ряд из десяти 十 людей 亻: смесь."),
        T3("personne", "person", "человек"))
    m['么'] = CharInfo('么', "me", 3, 0, "丿", "丿厶",
        T3("(dans 什么, 怎么)", "(in 什么, 怎么)", "(в 什么, 怎么)"),
        T3("Un petit fil tordu : sert à former les questions.", "A small twisted thread: used to form questions.", "Маленькая скрученная нить: служит для вопросов."),
        T3("trait tombant", "falling stroke", "откидная черта"))
    m['怎'] = CharInfo('怎', "zěn", 9, 3, "心", "乍心",
        T3("comment ?", "how?", "как?"),
        T3("Le cœur 心 qui se demande ; 乍 donne le son.", "The heart 心 wondering; 乍 gives the sound.", "Сердце 心 задаётся вопросом; 乍 даёт звук."),
        T3("cœur", "heart", "сердце"))
    m['样'] = CharInfo('样', "yàng", 10, 3, "木", "木羊",
        T3("forme, manière", "shape, way", "вид, образ"),
        T3("En bois 木 ; 羊 donne le son.", "Made of wood 木; 羊 gives the sound.", "Из дерева 木; 羊 даёт звук."),
        T3("arbre, bois", "tree, wood", "дерево"))
    m['的'] = CharInfo('的', "de", 8, 0, "白", "白勺",
        T3("(de, possession)", "(of, 's)", "(притяжательная частица)"),
        T3("Le blanc 白 de la cible : relie deux mots.", "The white 白 bullseye: links two words.", "Белое 白 яблочко мишени: связывает слова."),
        T3("blanc", "white", "белый"))
    m['了'] = CharInfo('了', "le", 2, 1, "亅", "乛亅",
        T3("(action finie)", "(completed action)", "(завершённость)"),
        T3("Un enfant 子 sans bras : c'est fini.", "A child 子 without arms: it's done.", "Ребёнок 子 без рук: всё закончено."),
        T3("crochet", "hook", "крюк"))
    m['都'] = CharInfo('都', "dōu", 10, 3, "阝", "者阝",
        T3("tous, tout", "all", "все"),
        T3("La grande ville 阝 où sont tous ; 者 donne le son.", "The city 阝 where all are; 者 gives the sound.", "Город 阝, где все; 者 даёт звук."),
        T3("ville, colline", "town, hill", "город, холм"))
    m['和'] = CharInfo('和', "hé", 8, 3, "口", "禾口",
        T3("et, avec ; paix", "and, with; peace", "и, с; мир"),
        T3("Le grain 禾 partagé de bouche 口 en bouche.", "Grain 禾 shared mouth 口 to mouth.", "Зерно 禾, разделённое между ртами 口."),
        T3("bouche", "mouth", "рот"))
    m['会'] = CharInfo('会', "huì", 6, 2, "人", "人云",
        T3("savoir faire ; réunion", "can; meeting", "уметь; собрание"),
        T3("Des gens 人 qui se réunissent pour parler 云.", "People 人 meeting to talk 云.", "Люди 人 собираются поговорить 云."),
        T3("personne", "person", "человек"))
    m['能'] = CharInfo('能', "néng", 10, 1, "月", "厶月匕匕",
        T3("pouvoir", "can, able", "мочь"),
        T3("Un ours fort : il en est capable.", "A strong bear: it can do it.", "Сильный медведь: он может."),
        T3("lune, chair", "moon, flesh", "луна, плоть"))
}

private fun part2(m: MutableMap<Char, CharInfo>) {
    m['想'] = CharInfo('想', "xiǎng", 13, 3, "心", "相心",
        T3("penser, vouloir", "to think, to want", "думать, хотеть"),
        T3("Le cœur 心 qui pense ; 相 donne le son.", "The heart 心 thinking; 相 gives the sound.", "Сердце 心 думает; 相 даёт звук."),
        T3("cœur", "heart", "сердце"))
    m['书'] = CharInfo('书', "shū", 4, 2, "丨", "丨",
        T3("livre", "book", "книга"),
        T3("Un pinceau qui trace des lignes.", "A brush drawing lines.", "Кисть рисует линии."),
        T3("trait vertical", "vertical stroke", "вертикальная черта"))
    m['汉'] = CharInfo('汉', "hàn", 5, 2, "氵", "氵又",
        T3("chinois (Han)", "Chinese (Han)", "китайский (хань)"),
        T3("L'eau 氵 de la rivière Han.", "The water 氵 of the Han river.", "Вода 氵 реки Хань."),
        T3("eau", "water", "вода"))
    m['语'] = CharInfo('语', "yǔ", 9, 3, "讠", "讠吾",
        T3("langue", "language", "язык"),
        T3("La parole 讠 ; 吾 donne le son.", "Speech 讠; 吾 gives the sound.", "Речь 讠; 吾 даёт звук."),
        T3("parole", "speech", "речь"))
    m['衣'] = CharInfo('衣', "yī", 6, 1, "衣", "亠",
        T3("vêtement", "clothes", "одежда"),
        T3("Le col et les manches d'une robe.", "The collar and sleeves of a robe.", "Ворот и рукава халата."),
        T3("vêtement", "clothes", "одежда"))
    m['服'] = CharInfo('服', "fú", 8, 2, "月", "月卩又",
        T3("vêtement ; servir", "clothes; to serve", "одежда; служить"),
        T3("Quelqu'un qui enfile un habit.", "Someone putting on a garment.", "Кто-то надевает одежду."),
        T3("lune, chair", "moon, flesh", "луна, плоть"))
    m['脑'] = CharInfo('脑', "nǎo", 10, 3, "月", "月亠凶",
        T3("cerveau", "brain", "мозг"),
        T3("La chair 月 de la tête.", "The flesh 月 of the head.", "Плоть 月 головы."),
        T3("lune, chair", "moon, flesh", "луна, плоть"))
    m['视'] = CharInfo('视', "shì", 8, 3, "见", "礻见",
        T3("regarder", "to look at", "смотреть"),
        T3("Voir 见 attentivement ; 礻 donne le son.", "Seeing 见 closely; 礻 gives the sound.", "Внимательно видеть 见; 礻 даёт звук."),
        T3("voir", "see", "видеть"))
    m['影'] = CharInfo('影', "yǐng", 15, 3, "彡", "景彡",
        T3("ombre, image", "shadow, image", "тень, образ"),
        T3("Les rayons 彡 du soleil 景 qui font une ombre.", "Rays 彡 of sunlight 景 casting a shadow.", "Лучи 彡 солнца 景 отбрасывают тень."),
        T3("poils, rayons", "hair, rays", "волосы, лучи"))
    m['飞'] = CharInfo('飞', "fēi", 3, 1, "飞", "",
        T3("voler", "to fly", "летать"),
        T3("Un oiseau qui bat des ailes.", "A bird flapping its wings.", "Птица машет крыльями."),
        T3("voler", "fly", "летать"))
    m['机'] = CharInfo('机', "jī", 6, 2, "木", "木几",
        T3("machine", "machine", "машина"),
        T3("Une table 几 en bois 木 : un mécanisme.", "A wooden 木 table 几: a mechanism.", "Деревянный 木 столик 几: механизм."),
        T3("arbre, bois", "tree, wood", "дерево"))
    m['出'] = CharInfo('出', "chū", 5, 2, "凵", "屮凵",
        T3("sortir", "to go out", "выходить"),
        T3("Une pousse 屮 qui sort d'un pot 凵.", "A sprout 屮 coming out of a pot 凵.", "Росток 屮 выходит из горшка 凵."),
        T3("récipient", "container", "сосуд"))
    m['租'] = CharInfo('租', "zū", 10, 2, "禾", "禾且",
        T3("louer", "to rent", "арендовать"),
        T3("Le grain 禾 payé régulièrement 且 : un loyer.", "Grain 禾 paid regularly 且: rent.", "Зерно 禾, платимое регулярно 且: аренда."),
        T3("céréale", "grain", "злак"))
    m['车'] = CharInfo('车', "chē", 4, 1, "车", "七十",
        T3("voiture", "vehicle", "машина"),
        T3("Une charrette vue d'en haut, avec ses roues.", "A cart seen from above, with its wheels.", "Повозка сверху, с колёсами."),
        T3("véhicule", "vehicle", "повозка"))
    m['哥'] = CharInfo('哥', "gē", 10, 2, "口", "可可",
        T3("grand frère", "older brother", "старший брат"),
        T3("Deux 可 empilés : le grand frère qui parle par-dessus.", "Two 可 stacked: the big brother talking over you.", "Два 可 друг на друге: старший брат говорит поверх тебя."),
        T3("bouche", "mouth", "рот"))
    m['弟'] = CharInfo('弟', "dì", 7, 0, "弓", "丷弓",
        T3("petit frère", "younger brother", "младший брат"),
        T3("Une corde enroulée en ordre : le cadet vient après.", "A cord wound in order: the younger one comes after.", "Верёвка, намотанная по порядку: младший идёт после."),
        T3("arc", "bow", "лук"))
    m['妹'] = CharInfo('妹', "mèi", 8, 3, "女", "女未",
        T3("petite sœur", "younger sister", "младшая сестра"),
        T3("Une femme 女 encore jeune, « pas encore » 未 grande.", "A woman 女 not yet 未 grown.", "Женщина 女, ещё не 未 взрослая."),
        T3("femme", "woman", "женщина"))
    m['丈'] = CharInfo('丈', "zhàng", 3, 0, "一", "一又",
        T3("homme, mari ; 3 mètres", "husband; unit of length", "муж; мера длины"),
        T3("Une main 又 qui mesure avec une règle 一.", "A hand 又 measuring with a rule 一.", "Рука 又 измеряет линейкой 一."),
        T3("un", "one", "один"))
    m['夫'] = CharInfo('夫', "fū", 4, 2, "大", "一大",
        T3("homme, mari", "man, husband", "мужчина, муж"),
        T3("Un homme 大 avec l'épingle 一 des adultes dans les cheveux.", "A man 大 wearing the adult hairpin 一.", "Мужчина 大 со шпилькой 一 взрослого в волосах."),
        T3("grand", "big", "большой"))
    m['妻'] = CharInfo('妻', "qī", 8, 2, "女", "十彐女",
        T3("épouse", "wife", "жена"),
        T3("Une femme 女 qui tient la maison en main.", "A woman 女 running the household by hand.", "Женщина 女, которая ведёт дом."),
        T3("femme", "woman", "женщина"))
    m['孩'] = CharInfo('孩', "hái", 9, 3, "子", "子亥",
        T3("enfant", "child", "ребёнок"),
        T3("Un enfant 子 ; 亥 donne le son.", "A child 子; 亥 gives the sound.", "Ребёнок 子; 亥 даёт звук."),
        T3("enfant", "child", "ребёнок"))
    m['男'] = CharInfo('男', "nán", 7, 2, "田", "田力",
        T3("homme", "man, male", "мужчина"),
        T3("La force 力 qui travaille le champ 田.", "Strength 力 working the field 田.", "Сила 力, работающая в поле 田."),
        T3("champ", "field", "поле"))
    m['您'] = CharInfo('您', "nín", 11, 2, "心", "你心",
        T3("vous (poli)", "you (polite)", "Вы"),
        T3("Toi 你 avec le cœur 心 : le « vous » respectueux.", "You 你 with the heart 心: the respectful “you”.", "Ты 你 с сердцем 心: уважительное «Вы»."),
        T3("cœur", "heart", "сердце"))
    m['它'] = CharInfo('它', "tā", 5, 0, "宀", "宀匕",
        T3("il, elle (chose)", "it", "оно"),
        T3("Un serpent sous un toit 宀 : « ça », une chose.", "A snake under a roof 宀: “it”, a thing.", "Змея под крышей 宀: «оно», вещь."),
        T3("toit", "roof", "крыша"))
    m['姓'] = CharInfo('姓', "xìng", 8, 3, "女", "女生",
        T3("nom de famille", "surname", "фамилия"),
        T3("Né 生 d'une femme 女 : le nom vient de la mère.", "Born 生 of a woman 女: the name came from the mother.", "Рождённый 生 женщиной 女: фамилия от матери."),
        T3("femme", "woman", "женщина"))
    m['介'] = CharInfo('介', "jiè", 4, 2, "人", "人",
        T3("entre, intermédiaire", "between, go-between", "между, посредник"),
        T3("Une personne 人 entre deux bords 八.", "A person 人 between two edges 八.", "Человек 人 между двумя краями 八."),
        T3("personne", "person", "человек"))
    m['绍'] = CharInfo('绍', "shào", 8, 3, "纟", "纟召",
        T3("relier, présenter", "to connect, introduce", "связывать, знакомить"),
        T3("Un fil 纟 qui relie ; 召 donne le son.", "A thread 纟 that links; 召 gives the sound.", "Нить 纟, которая связывает; 召 даёт звук."),
        T3("soie, fil", "silk, thread", "шёлк, нить"))
    m['迎'] = CharInfo('迎', "yíng", 7, 3, "辶", "辶",
        T3("accueillir", "to welcome", "встречать"),
        T3("Marcher 辶 à la rencontre de quelqu'un.", "Walking 辶 out to meet someone.", "Идти 辶 навстречу кому-то."),
        T3("marcher", "walk", "идти"))
    m['身'] = CharInfo('身', "shēn", 7, 1, "身", "",
        T3("corps", "body", "тело"),
        T3("Le dessin d'un corps de profil, le ventre rond.", "A body seen from the side, belly round.", "Тело в профиль, с круглым животом."),
        T3("corps", "body", "тело"))
    m['体'] = CharInfo('体', "tǐ", 7, 0, "亻", "亻本",
        T3("corps", "body", "тело"),
        T3("La personne 亻 et sa racine 本 : le corps.", "A person 亻 and their root 本: the body.", "Человек 亻 и его основа 本: тело."),
        T3("personne", "person", "человек"))
    m['眼'] = CharInfo('眼', "yǎn", 11, 3, "目", "目艮",
        T3("œil", "eye", "глаз"),
        T3("L'œil 目 ; 艮 donne le son.", "The eye 目; 艮 gives the sound.", "Глаз 目; 艮 даёт звук."),
        T3("œil", "eye", "глаз"))
    m['睛'] = CharInfo('睛', "jīng", 13, 3, "目", "目青",
        T3("prunelle", "eyeball", "зрачок"),
        T3("L'œil 目 ; 青 donne le son.", "The eye 目; 青 gives the sound.", "Глаз 目; 青 даёт звук."),
        T3("œil", "eye", "глаз"))
    m['病'] = CharInfo('病', "bìng", 10, 3, "疒", "疒丙",
        T3("maladie", "illness", "болезнь"),
        T3("Un lit de malade 疒 ; 丙 donne le son.", "A sickbed 疒; 丙 gives the sound.", "Больничная кровать 疒; 丙 даёт звук."),
        T3("maladie", "sickness", "болезнь"))
    m['药'] = CharInfo('药', "yào", 9, 3, "艹", "艹约",
        T3("médicament", "medicine", "лекарство"),
        T3("Une plante 艹 qui soigne ; 约 donne le son.", "A healing plant 艹; 约 gives the sound.", "Лечебное растение 艹; 约 даёт звук."),
        T3("herbe, plante", "grass, plant", "трава, растение"))
    m['累'] = CharInfo('累', "lèi", 11, 2, "糸", "田糸",
        T3("fatigué", "tired", "усталый"),
        T3("Le champ 田 et la soie 糸 : un travail qui épuise.", "Field 田 and silk 糸: work that wears you out.", "Поле 田 и шёлк 糸: изнурительный труд."),
        T3("soie, fil", "silk, thread", "шёлк, нить"))
    m['休'] = CharInfo('休', "xiū", 6, 2, "亻", "亻木",
        T3("se reposer", "to rest", "отдыхать"),
        T3("Une personne 亻 adossée à un arbre 木.", "A person 亻 leaning against a tree 木.", "Человек 亻 прислонился к дереву 木."),
        T3("personne", "person", "человек"))
    m['息'] = CharInfo('息', "xī", 10, 2, "心", "自心",
        T3("souffle, repos", "breath, rest", "дыхание, отдых"),
        T3("Le nez 自 et le cœur 心 : respirer.", "The nose 自 and the heart 心: breathing.", "Нос 自 и сердце 心: дышать."),
        T3("cœur", "heart", "сердце"))
    m['床'] = CharInfo('床', "chuáng", 7, 2, "广", "广木",
        T3("lit", "bed", "кровать"),
        T3("Du bois 木 sous un abri 广 : le lit.", "Wood 木 under a shelter 广: a bed.", "Дерево 木 под навесом 广: кровать."),
        T3("abri", "shelter", "навес"))
    m['洗'] = CharInfo('洗', "xǐ", 9, 3, "氵", "氵先",
        T3("laver", "to wash", "мыть"),
        T3("L'eau 氵 ; 先 donne le son.", "Water 氵; 先 gives the sound.", "Вода 氵; 先 даёт звук."),
        T3("eau", "water", "вода"))
    m['运'] = CharInfo('运', "yùn", 7, 3, "辶", "辶云",
        T3("transporter ; chance", "to transport; luck", "перевозить; удача"),
        T3("Marcher 辶 avec un nuage 云 : ça bouge.", "Walking 辶 with a cloud 云: things move.", "Идти 辶 с облаком 云: всё движется."),
        T3("marcher", "walk", "идти"))
    m['动'] = CharInfo('动', "dòng", 6, 3, "力", "云力",
        T3("bouger", "to move", "двигаться"),
        T3("La force 力 qui met en mouvement.", "Strength 力 that sets things moving.", "Сила 力, приводящая в движение."),
        T3("force", "strength", "сила"))
    m['跑'] = CharInfo('跑', "pǎo", 12, 3, "足", "足包",
        T3("courir", "to run", "бегать"),
        T3("Le pied 足 ; 包 donne le son.", "The foot 足; 包 gives the sound.", "Нога 足; 包 даёт звук."),
        T3("pied", "foot", "нога"))
    m['步'] = CharInfo('步', "bù", 7, 2, "止", "止",
        T3("pas", "step", "шаг"),
        T3("Un pied 止 devant l'autre.", "One foot 止 in front of the other.", "Одна нога 止 перед другой."),
        T3("pied, s'arrêter", "foot, stop", "нога, стоять"))
    m['游'] = CharInfo('游', "yóu", 12, 2, "氵", "氵",
        T3("nager, voyager", "to swim, to travel", "плавать, путешествовать"),
        T3("Flotter librement dans l'eau 氵.", "Floating freely in the water 氵.", "Свободно плыть по воде 氵."),
        T3("eau", "water", "вода"))
    m['泳'] = CharInfo('泳', "yǒng", 8, 3, "氵", "氵永",
        T3("nager", "to swim", "плавать"),
        T3("L'eau 氵 ; 永 donne le son.", "Water 氵; 永 gives the sound.", "Вода 氵; 永 даёт звук."),
        T3("eau", "water", "вода"))
    m['踢'] = CharInfo('踢', "tī", 15, 3, "足", "足易",
        T3("donner un coup de pied", "to kick", "пинать"),
        T3("Le pied 足 ; 易 donne le son.", "The foot 足; 易 gives the sound.", "Нога 足; 易 даёт звук."),
        T3("pied", "foot", "нога"))
    m['足'] = CharInfo('足', "zú", 7, 1, "足", "口",
        T3("pied ; assez", "foot; enough", "нога; достаточно"),
        T3("La jambe et le pied, debout.", "A leg and a foot, standing.", "Нога и ступня."),
        T3("pied", "foot", "нога"))
    m['球'] = CharInfo('球', "qiú", 11, 3, "王", "王求",
        T3("balle, ballon", "ball", "мяч"),
        T3("Le jade 王 bien rond ; 求 donne le son.", "Round jade 王; 求 gives the sound.", "Круглый нефрит 王; 求 даёт звук."),
        T3("roi, jade", "king, jade", "царь, нефрит"))
    m['篮'] = CharInfo('篮', "lán", 16, 3, "竹", "竹监",
        T3("panier", "basket", "корзина"),
        T3("Du bambou 竹 tressé ; 监 donne le son.", "Woven bamboo 竹; 监 gives the sound.", "Плетёный бамбук 竹; 监 даёт звук."),
        T3("bambou", "bamboo", "бамбук"))
    m['跳'] = CharInfo('跳', "tiào", 13, 3, "足", "足兆",
        T3("sauter", "to jump", "прыгать"),
        T3("Le pied 足 ; 兆 donne le son.", "The foot 足; 兆 gives the sound.", "Нога 足; 兆 даёт звук."),
        T3("pied", "foot", "нога"))
    m['舞'] = CharInfo('舞', "wǔ", 14, 2, "舛", "舛",
        T3("danser", "to dance", "танцевать"),
        T3("Des pieds 舛 qui tournent en dansant.", "Feet 舛 turning in a dance.", "Ноги 舛, кружащиеся в танце."),
        T3("pieds opposés", "opposite feet", "ноги врозь"))
    m['唱'] = CharInfo('唱', "chàng", 11, 3, "口", "口昌",
        T3("chanter", "to sing", "петь"),
        T3("La bouche 口 ; 昌 donne le son.", "The mouth 口; 昌 gives the sound.", "Рот 口; 昌 даёт звук."),
        T3("bouche", "mouth", "рот"))
    m['歌'] = CharInfo('歌', "gē", 14, 3, "欠", "哥欠",
        T3("chanson", "song", "песня"),
        T3("Ouvrir grand la bouche 欠 ; 哥 donne le son.", "Opening the mouth wide 欠; 哥 gives the sound.", "Широко открыть рот 欠; 哥 даёт звук."),
        T3("bâiller, bouche ouverte", "yawn, open mouth", "зевать, открытый рот"))
    m['早'] = CharInfo('早', "zǎo", 6, 2, "日", "日十",
        T3("tôt, matin", "early, morning", "рано, утро"),
        T3("Le soleil 日 qui se lève au-dessus de l'horizon.", "The sun 日 rising over the horizon.", "Солнце 日 встаёт над горизонтом."),
        T3("soleil, jour", "sun, day", "солнце, день"))
    m['晚'] = CharInfo('晚', "wǎn", 11, 2, "日", "日免",
        T3("soir, tard", "evening, late", "вечер, поздно"),
        T3("Le soleil 日 qui s'en va ; 免 donne le son.", "The sun 日 leaving; 免 gives the sound.", "Солнце 日 уходит; 免 даёт звук."),
        T3("soleil, jour", "sun, day", "солнце, день"))
    m['日'] = CharInfo('日', "rì", 4, 1, "日", "口一",
        T3("soleil, jour", "sun, day", "солнце, день"),
        T3("Le soleil, tracé en carré.", "The sun, drawn as a square.", "Солнце, нарисованное квадратом."),
        T3("soleil, jour", "sun, day", "солнце, день"))
    m['间'] = CharInfo('间', "jiān", 7, 2, "门", "门日",
        T3("entre, espace", "between, room", "между, помещение"),
        T3("Le soleil 日 vu par une porte 门 entrouverte.", "The sun 日 seen through a door 门.", "Солнце 日 видно сквозь дверь 门."),
        T3("porte", "door", "дверь"))
    m['已'] = CharInfo('已', "yǐ", 3, 0, "己", "",
        T3("déjà", "already", "уже"),
        T3("Une forme enroulée, comme 己 : c'est fait.", "A coiled shape, like 己: it's done.", "Свёрнутая форма, как 己: уже сделано."),
        T3("soi", "self", "сам"))
    m['经'] = CharInfo('经', "jīng", 8, 3, "纟", "纟工",
        T3("passer par ; classique", "to undergo; classic", "проходить; канон"),
        T3("Le fil 纟 tendu sur le métier : ce qui traverse.", "The thread 纟 on the loom: what runs through.", "Нить 纟 на станке: то, что проходит насквозь."),
        T3("soie, fil", "silk, thread", "шёлк, нить"))
    m['始'] = CharInfo('始', "shǐ", 8, 3, "女", "女台",
        T3("commencer", "to begin", "начинать"),
        T3("Une femme 女 : tout commence par la mère ; 台 donne le son.", "A woman 女: all begins with the mother; 台 gives the sound.", "Женщина 女: всё начинается с матери; 台 даёт звук."),
        T3("femme", "woman", "женщина"))
    m['完'] = CharInfo('完', "wán", 7, 3, "宀", "宀元",
        T3("finir", "to finish", "заканчивать"),
        T3("Sous un toit 宀 ; 元 donne le son.", "Under a roof 宀; 元 gives the sound.", "Под крышей 宀; 元 даёт звук."),
        T3("toit", "roof", "крыша"))
    m['正'] = CharInfo('正', "zhèng", 5, 2, "止", "一止",
        T3("droit, juste", "straight, right", "прямой, правильный"),
        T3("Un pied 止 qui s'arrête pile sur la ligne 一.", "A foot 止 stopping right on the line 一.", "Нога 止 останавливается точно на линии 一."),
        T3("pied, s'arrêter", "foot, stop", "нога, стоять"))
    m['着'] = CharInfo('着', "zhe", 11, 0, "目", "羊目",
        T3("(en train de)", "(ongoing)", "(длительность)"),
        T3("Un mouton 羊 qui garde les yeux 目 ouverts.", "A sheep 羊 keeping its eyes 目 open.", "Овца 羊 держит глаза 目 открытыми."),
        T3("œil", "eye", "глаз"))
    m['过'] = CharInfo('过', "guò", 6, 3, "辶", "辶寸",
        T3("passer", "to pass", "проходить"),
        T3("Marcher 辶 au-delà ; 寸 complète.", "Walking 辶 past; 寸 completes it.", "Идти 辶 мимо; 寸 дополняет."),
        T3("marcher", "walk", "идти"))
    m['次'] = CharInfo('次', "cì", 6, 2, "欠", "冫欠",
        T3("fois, suivant", "time, next", "раз, следующий"),
        T3("Une personne qui bâille 欠 encore et encore.", "Someone yawning 欠 again and again.", "Человек зевает 欠 снова и снова."),
        T3("bâiller, bouche ouverte", "yawn, open mouth", "зевать, открытый рот"))
    m['第'] = CharInfo('第', "dì", 11, 2, "竹", "竹弟",
        T3("rang, numéro", "rank, ordinal", "порядковый номер"),
        T3("Des lattes de bambou 竹 rangées en ordre ; 弟 donne le son.", "Bamboo slips 竹 lined up in order; 弟 gives the sound.", "Бамбуковые планки 竹 по порядку; 弟 даёт звук."),
        T3("bambou", "bamboo", "бамбук"))
    m['鸡'] = CharInfo('鸡', "jī", 7, 2, "鸟", "又鸟",
        T3("poulet", "chicken", "курица"),
        T3("Un oiseau 鸟 ; 又 l'abrège.", "A bird 鸟; 又 shortens it.", "Птица 鸟; 又 — сокращение."),
        T3("oiseau", "bird", "птица"))
    m['蛋'] = CharInfo('蛋', "dàn", 11, 3, "虫", "疋虫",
        T3("œuf", "egg", "яйцо"),
        T3("Ce que pondent les petites bêtes 虫.", "What small creatures 虫 lay.", "То, что откладывают зверьки 虫."),
        T3("insecte", "insect", "насекомое"))
    m['条'] = CharInfo('条', "tiáo", 7, 0, "木", "夂木",
        T3("bande, branche", "strip, branch", "полоса, ветка"),
        T3("Une longue branche d'arbre 木.", "A long branch of a tree 木.", "Длинная ветка дерева 木."),
        T3("arbre, bois", "tree, wood", "дерево"))
    m['牛'] = CharInfo('牛', "niú", 4, 1, "牛", "",
        T3("bœuf, vache", "ox, cow", "бык, корова"),
        T3("La tête d'un bœuf avec ses cornes.", "An ox's head with its horns.", "Голова быка с рогами."),
        T3("bœuf", "ox", "бык"))
    m['奶'] = CharInfo('奶', "nǎi", 5, 3, "女", "女乃",
        T3("lait", "milk", "молоко"),
        T3("La femme 女 qui nourrit ; 乃 donne le son.", "The woman 女 who nurses; 乃 gives the sound.", "Женщина 女 кормит; 乃 даёт звук."),
        T3("femme", "woman", "женщина"))
    m['羊'] = CharInfo('羊', "yáng", 6, 1, "羊", "丷",
        T3("mouton", "sheep", "баран"),
        T3("La tête d'un mouton et ses cornes.", "A sheep's head and horns.", "Голова барана с рогами."),
        T3("mouton", "sheep", "баран"))
    m['肉'] = CharInfo('肉', "ròu", 6, 1, "肉", "冂",
        T3("viande", "meat", "мясо"),
        T3("Un morceau de viande avec ses fibres.", "A piece of meat with its grain.", "Кусок мяса с волокнами."),
        T3("viande", "meat", "мясо"))
    m['鱼'] = CharInfo('鱼', "yú", 8, 1, "鱼", "田一",
        T3("poisson", "fish", "рыба"),
        T3("Un poisson : la tête, le corps, la queue.", "A fish: head, body, tail.", "Рыба: голова, тело, хвост."),
        T3("poisson", "fish", "рыба"))
    m['瓜'] = CharInfo('瓜', "guā", 5, 1, "瓜", "厶",
        T3("melon", "melon", "дыня"),
        T3("Un melon qui pend à sa tige.", "A melon hanging from its vine.", "Дыня свисает с плети."),
        T3("melon", "melon", "дыня"))
    m['咖'] = CharInfo('咖', "kā", 8, 3, "口", "口加",
        T3("(café)", "(coffee)", "(кофе)"),
        T3("La bouche 口 ; 加 donne le son : kā.", "The mouth 口; 加 gives the sound: kā.", "Рот 口; 加 даёт звук: kā."),
        T3("bouche", "mouth", "рот"))
    m['啡'] = CharInfo('啡', "fēi", 11, 3, "口", "口非",
        T3("(café)", "(coffee)", "(кофе)"),
        T3("La bouche 口 ; 非 donne le son : fēi.", "The mouth 口; 非 gives the sound: fēi.", "Рот 口; 非 даёт звук: fēi."),
        T3("bouche", "mouth", "рот"))
    m['卖'] = CharInfo('卖', "mài", 8, 3, "十", "十买",
        T3("vendre", "to sell", "продавать"),
        T3("« Acheter » 买 avec un trait 十 de plus : l'inverse.", "“Buy” 买 plus one more stroke 十: the opposite.", "«Купить» 买 и ещё черта 十: наоборот."),
        T3("dix", "ten", "десять"))
    m['便'] = CharInfo('便', "biàn", 9, 0, "亻", "亻更",
        T3("commode", "convenient", "удобный"),
        T3("Une personne 亻 qui change 更 les choses en plus simple.", "A person 亻 changing 更 things to make them easier.", "Человек 亻 меняет 更 всё к удобству."),
        T3("personne", "person", "человек"))
    m['宜'] = CharInfo('宜', "yí", 8, 2, "宀", "宀且",
        T3("convenable", "suitable", "подходящий"),
        T3("Sous un toit 宀, tout est à sa place.", "Under a roof 宀, all is in its place.", "Под крышей 宀 всё на месте."),
        T3("toit", "roof", "крыша"))
}

private fun part3(m: MutableMap<Char, CharInfo>) {
    m['贵'] = CharInfo('贵', "guì", 9, 3, "贝", "中一贝",
        T3("cher", "expensive", "дорогой"),
        T3("Un coquillage 贝, la monnaie d'autrefois.", "A shell 贝, money of old.", "Ракушка 贝 — древние деньги."),
        T3("coquillage, argent", "shell, money", "раковина, деньги"))
    m['千'] = CharInfo('千', "qiān", 3, 0, "十", "丿十",
        T3("mille", "thousand", "тысяча"),
        T3("Dix 十 avec un trait penché en plus.", "Ten 十 with an extra slanting stroke.", "Десять 十 с косой чертой сверху."),
        T3("dix", "ten", "десять"))
    m['百'] = CharInfo('百', "bǎi", 6, 3, "白", "一白",
        T3("cent", "hundred", "сто"),
        T3("Un 一 au-dessus de blanc 白 : cent.", "One 一 over white 白: a hundred.", "Один 一 над белым 白: сто."),
        T3("blanc", "white", "белый"))
    m['零'] = CharInfo('零', "líng", 13, 3, "雨", "雨令",
        T3("zéro", "zero", "ноль"),
        T3("Des gouttes de pluie 雨 qui tombent ; 令 donne le son.", "Raindrops 雨 falling; 令 gives the sound.", "Падают капли дождя 雨; 令 даёт звук."),
        T3("pluie", "rain", "дождь"))
    m['两'] = CharInfo('两', "liǎng", 7, 2, "一", "一冂从",
        T3("deux", "two", "два"),
        T3("Deux personnes côte à côte dans un cadre.", "Two people side by side in a frame.", "Двое рядом в рамке."),
        T3("un", "one", "один"))
    m['场'] = CharInfo('场', "chǎng", 6, 3, "土", "土勿",
        T3("place, terrain", "field, place", "площадка"),
        T3("La terre 土 dégagée pour un lieu.", "Earth 土 cleared for a place.", "Расчищенная земля 土."),
        T3("terre", "earth", "земля"))
    m['宾'] = CharInfo('宾', "bīn", 10, 2, "宀", "宀兵",
        T3("invité", "guest", "гость"),
        T3("Un soldat 兵 logé sous ton toit 宀.", "A soldier 兵 lodged under your roof 宀.", "Солдат 兵 под твоей крышей 宀."),
        T3("toit", "roof", "крыша"))
    m['馆'] = CharInfo('馆', "guǎn", 11, 2, "饣", "饣官",
        T3("maison, établissement", "building, house", "здание, заведение"),
        T3("Un bâtiment public 官 où l'on sert à manger 饣.", "A public 官 building serving food 饣.", "Общественное здание 官, где кормят 饣."),
        T3("nourriture", "food", "еда"))
    m['教'] = CharInfo('教', "jiào", 11, 3, "攵", "孝攵",
        T3("enseigner", "to teach", "учить"),
        T3("Une main qui guide 攵 l'enfant 孝.", "A guiding hand 攵 for the child 孝.", "Рука 攵 направляет ребёнка 孝."),
        T3("frapper, agir", "strike, act", "удар, действие"))
    m['室'] = CharInfo('室', "shì", 9, 3, "宀", "宀至",
        T3("pièce, salle", "room", "комната"),
        T3("Sous un toit 宀, là où l'on arrive 至.", "Under a roof 宀, where you arrive 至.", "Под крышей 宀, куда приходишь 至."),
        T3("toit", "roof", "крыша"))
    m['房'] = CharInfo('房', "fáng", 8, 3, "户", "户方",
        T3("maison, pièce", "house, room", "дом, комната"),
        T3("Une porte 户 ; 方 donne le son.", "A door 户; 方 gives the sound.", "Дверь 户; 方 даёт звук."),
        T3("porte", "door", "дверь"))
    m['门'] = CharInfo('门', "mén", 3, 1, "门", "丶丨",
        T3("porte", "door", "дверь"),
        T3("Une porte à deux battants.", "A two-leaf door.", "Двустворчатая дверь."),
        T3("porte", "door", "дверь"))
    m['路'] = CharInfo('路', "lù", 13, 3, "足", "足各",
        T3("route", "road", "дорога"),
        T3("Le pied 足 qui va dans chaque 各 direction.", "The foot 足 going each 各 way.", "Нога 足 идёт в каждую 各 сторону."),
        T3("pied", "foot", "нога"))
    m['旁'] = CharInfo('旁', "páng", 10, 3, "方", "立方",
        T3("côté", "side", "сторона"),
        T3("Se tenir 立 sur le côté ; 方 donne le son.", "Standing 立 aside; 方 gives the sound.", "Стоять 立 сбоку; 方 даёт звук."),
        T3("carré, direction", "square, direction", "квадрат, сторона"))
    m['边'] = CharInfo('边', "biān", 5, 3, "辶", "辶力",
        T3("bord, côté", "side, edge", "край, сторона"),
        T3("Marcher 辶 le long du bord.", "Walking 辶 along the edge.", "Идти 辶 вдоль края."),
        T3("marcher", "walk", "идти"))
    m['左'] = CharInfo('左', "zuǒ", 5, 1, "工", "工",
        T3("gauche", "left", "левый"),
        T3("La main gauche qui tient l'équerre 工.", "The left hand holding a square 工.", "Левая рука держит угольник 工."),
        T3("travail", "work", "работа"))
    m['右'] = CharInfo('右', "yòu", 5, 0, "口", "口",
        T3("droite", "right", "правый"),
        T3("La main droite qui porte à la bouche 口.", "The right hand bringing food to the mouth 口.", "Правая рука подносит еду ко рту 口."),
        T3("bouche", "mouth", "рот"))
    m['外'] = CharInfo('外', "wài", 5, 2, "夕", "夕卜",
        T3("dehors", "outside", "снаружи"),
        T3("Le soir 夕 on consulte les oracles 卜 dehors.", "In the evening 夕 oracles 卜 are read outside.", "Вечером 夕 гадают 卜 снаружи."),
        T3("soir", "evening", "вечер"))
    m['远'] = CharInfo('远', "yuǎn", 7, 3, "辶", "辶元",
        T3("loin", "far", "далеко"),
        T3("Marcher 辶 longtemps ; 元 donne le son.", "Walking 辶 a long way; 元 gives the sound.", "Долго идти 辶; 元 даёт звук."),
        T3("marcher", "walk", "идти"))
    m['近'] = CharInfo('近', "jìn", 7, 3, "辶", "辶斤",
        T3("près", "near", "близко"),
        T3("Marcher 辶 peu ; 斤 donne le son.", "Walking 辶 a little; 斤 gives the sound.", "Пройти 辶 немного; 斤 даёт звук."),
        T3("marcher", "walk", "идти"))
    m['离'] = CharInfo('离', "lí", 10, 1, "禸", "亠凶禸",
        T3("s'éloigner, de", "away from", "от, отделяться"),
        T3("Un animal debout qui s'en va.", "An animal standing up and leaving.", "Зверь встаёт и уходит."),
        T3("empreinte", "track", "след"))
    m['往'] = CharInfo('往', "wǎng", 8, 3, "彳", "彳王",
        T3("vers", "towards", "к, в сторону"),
        T3("Un pas 彳 en avant, vers quelque part.", "A step 彳 forward, towards somewhere.", "Шаг 彳 вперёд, куда-то."),
        T3("pas", "step", "шаг"))
    m['公'] = CharInfo('公', "gōng", 4, 2, "八", "八厶",
        T3("public", "public", "общественный"),
        T3("Partager 八 ce qui est privé 厶.", "Sharing 八 what is private 厶.", "Делить 八 личное 厶."),
        T3("huit, partager", "eight, divide", "восемь, делить"))
    m['共'] = CharInfo('共', "gòng", 6, 2, "八", "廾一八",
        T3("ensemble", "together", "вместе"),
        T3("Deux mains qui tiennent un même objet.", "Two hands holding one object.", "Две руки держат одну вещь."),
        T3("huit, partager", "eight, divide", "восемь, делить"))
    m['汽'] = CharInfo('汽', "qì", 7, 2, "氵", "氵气",
        T3("vapeur", "steam", "пар"),
        T3("L'eau 氵 devenue air 气.", "Water 氵 turned into air 气.", "Вода 氵, ставшая паром 气."),
        T3("eau", "water", "вода"))
    m['司'] = CharInfo('司', "sī", 5, 2, "口", "口",
        T3("diriger", "to manage", "управлять"),
        T3("Une bouche 口 qui donne les ordres.", "A mouth 口 giving orders.", "Рот 口 отдаёт приказы."),
        T3("bouche", "mouth", "рот"))
    m['班'] = CharInfo('班', "bān", 10, 2, "王", "王刂王",
        T3("classe, équipe", "class, shift", "класс, смена"),
        T3("Un couteau 刂 coupe le jade 王 en deux groupes.", "A knife 刂 splits jade 王 into two groups.", "Нож 刂 делит нефрит 王 на две группы."),
        T3("roi, jade", "king, jade", "царь, нефрит"))
    m['务'] = CharInfo('务', "wù", 5, 0, "夂", "夂力",
        T3("affaire, tâche", "task, affair", "дело"),
        T3("La force 力 qu'on met à la tâche.", "The strength 力 put into a task.", "Сила 力, вложенная в дело."),
        T3("marcher lentement", "go slowly", "идти медленно"))
    m['员'] = CharInfo('员', "yuán", 7, 3, "口", "口贝",
        T3("membre, employé", "member, staff", "сотрудник"),
        T3("Une bouche 口 et de l'argent 贝 : celui qu'on paie.", "A mouth 口 and money 贝: someone on the payroll.", "Рот 口 и деньги 贝: тот, кому платят."),
        T3("bouche", "mouth", "рот"))
    m['报'] = CharInfo('报', "bào", 7, 2, "扌", "扌卩又",
        T3("annoncer ; journal", "to report; newspaper", "сообщать; газета"),
        T3("Une main 扌 qui rapporte les nouvelles.", "A hand 扌 reporting the news.", "Рука 扌 приносит новости."),
        T3("main", "hand", "рука"))
    m['纸'] = CharInfo('纸', "zhǐ", 7, 3, "纟", "纟氏",
        T3("papier", "paper", "бумага"),
        T3("Des fibres 纟 ; 氏 donne le son.", "Fibres 纟; 氏 gives the sound.", "Волокна 纟; 氏 даёт звук."),
        T3("soie, fil", "silk, thread", "шёлк, нить"))
    m['铅'] = CharInfo('铅', "qiān", 10, 3, "钅", "钅几口",
        T3("plomb", "lead (metal)", "свинец"),
        T3("Un métal 钅 ; la mine du crayon.", "A metal 钅; the pencil's lead.", "Металл 钅; грифель карандаша."),
        T3("métal", "metal", "металл"))
    m['笔'] = CharInfo('笔', "bǐ", 10, 2, "竹", "竹毛",
        T3("pinceau, stylo", "pen, brush", "кисть, ручка"),
        T3("Du bambou 竹 avec des poils 毛 : un pinceau.", "Bamboo 竹 with hair 毛: a brush.", "Бамбук 竹 с волосом 毛: кисть."),
        T3("bambou", "bamboo", "бамбук"))
    m['课'] = CharInfo('课', "kè", 10, 3, "讠", "讠果",
        T3("cours", "lesson", "урок"),
        T3("La parole 讠 du maître ; 果 donne le son.", "The teacher's speech 讠; 果 gives the sound.", "Речь 讠 учителя; 果 даёт звук."),
        T3("parole", "speech", "речь"))
    m['考'] = CharInfo('考', "kǎo", 6, 3, "老", "老丂",
        T3("examiner", "to test", "экзаменовать"),
        T3("Le vieil homme 老 qui fait passer l'épreuve.", "The old man 老 setting the test.", "Старик 老 устраивает испытание."),
        T3("vieux", "old", "старый"))
    m['试'] = CharInfo('试', "shì", 8, 3, "讠", "讠式",
        T3("essayer", "to try", "пробовать"),
        T3("La parole 讠 ; 式 donne le son.", "Speech 讠; 式 gives the sound.", "Речь 讠; 式 даёт звук."),
        T3("parole", "speech", "речь"))
    m['题'] = CharInfo('题', "tí", 15, 3, "页", "是页",
        T3("sujet, question", "topic, question", "тема, задание"),
        T3("La tête 页 de la page : le titre.", "The head 页 of the page: the title.", "Голова 页 страницы: заголовок."),
        T3("tête, page", "head, page", "голова, страница"))
    m['问'] = CharInfo('问', "wèn", 6, 3, "门", "门口",
        T3("demander", "to ask", "спрашивать"),
        T3("Une bouche 口 à la porte 门 : on demande.", "A mouth 口 at the door 门: asking.", "Рот 口 у двери 门: спросить."),
        T3("porte", "door", "дверь"))
    m['答'] = CharInfo('答', "dá", 12, 0, "竹", "竹合",
        T3("répondre", "to answer", "отвечать"),
        T3("Des lattes de bambou 竹 qui s'accordent 合.", "Bamboo slips 竹 that fit together 合.", "Бамбуковые планки 竹, которые сходятся 合."),
        T3("bambou", "bamboo", "бамбук"))
    m['懂'] = CharInfo('懂', "dǒng", 15, 3, "忄", "忄董",
        T3("comprendre", "to understand", "понимать"),
        T3("Le cœur 忄 qui saisit ; 董 donne le son.", "The heart 忄 grasping it; 董 gives the sound.", "Сердце 忄 постигает; 董 даёт звук."),
        T3("cœur", "heart", "сердце"))
    m['意'] = CharInfo('意', "yì", 13, 3, "心", "音心",
        T3("idée, sens", "idea, meaning", "мысль, смысл"),
        T3("Le son 音 du cœur 心 : l'intention.", "The sound 音 of the heart 心: intention.", "Звук 音 сердца 心: намерение."),
        T3("cœur", "heart", "сердце"))
    m['思'] = CharInfo('思', "sī", 9, 2, "心", "田心",
        T3("penser", "to think", "думать"),
        T3("La tête (田) et le cœur 心 qui réfléchissent.", "The head (田) and the heart 心 thinking.", "Голова (田) и сердце 心 думают."),
        T3("cœur", "heart", "сердце"))
    m['告'] = CharInfo('告', "gào", 7, 2, "口", "牛口",
        T3("dire, annoncer", "to tell", "сообщать"),
        T3("Parler 口 avec la force d'un bœuf 牛.", "Speaking 口 with the strength of an ox 牛.", "Говорить 口 с силой быка 牛."),
        T3("bouche", "mouth", "рот"))
    m['诉'] = CharInfo('诉', "sù", 7, 2, "讠", "讠斥",
        T3("raconter, se plaindre", "to tell, to complain", "рассказывать, жаловаться"),
        T3("Des paroles 讠 ; 斥 donne le son.", "Words 讠; 斥 gives the sound.", "Слова 讠; 斥 даёт звук."),
        T3("parole", "speech", "речь"))
    m['准'] = CharInfo('准', "zhǔn", 10, 3, "冫", "冫隹",
        T3("précis, permettre", "accurate, allow", "точный, разрешать"),
        T3("L'eau 冫 d'un niveau, bien à plat.", "Water 冫 in a spirit level, perfectly flat.", "Вода 冫 в уровне, ровно."),
        T3("glace", "ice", "лёд"))
    m['备'] = CharInfo('备', "bèi", 8, 2, "夂", "夂田",
        T3("préparer", "to prepare", "готовить"),
        T3("Aller 夂 au champ 田 préparer la récolte.", "Going 夂 to the field 田 to prepare the harvest.", "Идти 夂 в поле 田 готовить урожай."),
        T3("marcher lentement", "go slowly", "идти медленно"))
    m['白'] = CharInfo('白', "bái", 5, 2, "白", "日",
        T3("blanc", "white", "белый"),
        T3("Une flamme qui éclaire : la clarté.", "A flame giving light: brightness.", "Пламя, дающее свет: ясность."),
        T3("blanc", "white", "белый"))
    m['黑'] = CharInfo('黑', "hēi", 12, 2, "黑", "灬",
        T3("noir", "black", "чёрный"),
        T3("Un visage noirci par la suie du feu 灬.", "A face blackened by soot from the fire 灬.", "Лицо, чёрное от копоти огня 灬."),
        T3("noir", "black", "чёрный"))
    m['红'] = CharInfo('红', "hóng", 6, 3, "纟", "纟工",
        T3("rouge", "red", "красный"),
        T3("Un fil de soie 纟 teint ; 工 donne le son.", "A dyed silk thread 纟; 工 gives the sound.", "Окрашенная шёлковая нить 纟; 工 даёт звук."),
        T3("soie, fil", "silk, thread", "шёлк, нить"))
    m['颜'] = CharInfo('颜', "yán", 15, 3, "页", "彦页",
        T3("visage, couleur", "face, colour", "лицо, цвет"),
        T3("La tête 页 ; 彦 donne le son.", "The head 页; 彦 gives the sound.", "Голова 页; 彦 даёт звук."),
        T3("tête, page", "head, page", "голова, страница"))
    m['色'] = CharInfo('色', "sè", 6, 2, "色", "巴",
        T3("couleur", "colour", "цвет"),
        T3("Une personne penchée sur une autre : l'émotion qui colore.", "One person over another: the feeling that colours.", "Человек над другим: чувство, которое окрашивает."),
        T3("couleur", "colour", "цвет"))
    m['新'] = CharInfo('新', "xīn", 13, 2, "斤", "斤",
        T3("nouveau", "new", "новый"),
        T3("Un arbre fraîchement coupé à la hache 斤.", "A tree freshly cut with an axe 斤.", "Дерево, только что срубленное топором 斤."),
        T3("hache", "axe", "топор"))
    m['长'] = CharInfo('长', "cháng", 4, 2, "长", "一",
        T3("long ; grandir", "long; to grow", "длинный; расти"),
        T3("Un vieil homme aux très longs cheveux.", "An old man with very long hair.", "Старик с очень длинными волосами."),
        T3("long", "long", "длинный"))
    m['快'] = CharInfo('快', "kuài", 7, 2, "忄", "忄夬",
        T3("rapide", "fast", "быстрый"),
        T3("Un cœur 忄 décidé 夬 : on va vite.", "A decided 夬 heart 忄: you go fast.", "Решительное 夬 сердце 忄: быстро."),
        T3("cœur", "heart", "сердце"))
    m['慢'] = CharInfo('慢', "màn", 14, 3, "忄", "忄曼",
        T3("lent", "slow", "медленный"),
        T3("Un cœur 忄 tranquille ; 曼 donne le son.", "A calm heart 忄; 曼 gives the sound.", "Спокойное сердце 忄; 曼 даёт звук."),
        T3("cœur", "heart", "сердце"))
    m['忙'] = CharInfo('忙', "máng", 6, 2, "忄", "忄亡",
        T3("occupé", "busy", "занятой"),
        T3("Un cœur 忄 qui se perd 亡 dans le travail.", "A heart 忄 lost 亡 in work.", "Сердце 忄, потерявшееся 亡 в работе."),
        T3("cœur", "heart", "сердце"))
    m['真'] = CharInfo('真', "zhēn", 10, 2, "目", "几",
        T3("vrai", "true, real", "настоящий"),
        T3("Un regard 目 bien droit : ce qui est vrai.", "A straight gaze 目: what is true.", "Прямой взгляд 目: правда."),
        T3("œil", "eye", "глаз"))
    m['非'] = CharInfo('非', "fēi", 8, 2, "非", "",
        T3("non, pas", "not", "не"),
        T3("Deux ailes qui s'opposent.", "Two wings facing away.", "Два крыла, обращённые в разные стороны."),
        T3("non", "not", "не"))
    m['常'] = CharInfo('常', "cháng", 11, 3, "巾", "巾",
        T3("souvent", "often", "часто"),
        T3("Un tissu 巾 qu'on porte tous les jours.", "A cloth 巾 worn every day.", "Ткань 巾, которую носят каждый день."),
        T3("tissu", "cloth", "ткань"))
    m['最'] = CharInfo('最', "zuì", 12, 2, "曰", "日取",
        T3("le plus", "most", "самый"),
        T3("Prendre 取 sous le soleil 日 : le meilleur.", "Taking 取 under the sun 日: the very best.", "Брать 取 под солнцем 日: самое лучшее."),
        T3("dire", "say", "говорить"))
    m['错'] = CharInfo('错', "cuò", 13, 2, "钅", "钅昔",
        T3("faux, erreur", "wrong", "ошибка"),
        T3("Un métal 钅 mal travaillé ; 昔 donne le son.", "Badly worked metal 钅; 昔 gives the sound.", "Плохо обработанный металл 钅; 昔 даёт звук."),
        T3("métal", "metal", "металл"))
    m['走'] = CharInfo('走', "zǒu", 7, 2, "走", "土止",
        T3("marcher", "to walk", "идти"),
        T3("Une personne qui lève le pied 止 pour marcher.", "A person lifting a foot 止 to walk.", "Человек поднимает ногу 止, чтобы идти."),
        T3("marcher", "walk", "идти"))
    m['进'] = CharInfo('进', "jìn", 7, 3, "辶", "辶井",
        T3("entrer, avancer", "to enter", "входить"),
        T3("Marcher 辶 vers l'avant ; 井 donne le son.", "Walking 辶 forward; 井 gives the sound.", "Идти 辶 вперёд; 井 даёт звук."),
        T3("marcher", "walk", "идти"))
    m['到'] = CharInfo('到', "dào", 8, 3, "刂", "至刂",
        T3("arriver", "to arrive", "прибывать"),
        T3("Arriver 至 au but ; 刂 donne le son.", "Reaching 至 the goal; 刂 gives the sound.", "Достичь 至 цели; 刂 даёт звук."),
        T3("couteau", "knife", "нож"))
    m['送'] = CharInfo('送', "sòng", 9, 2, "辶", "辶关",
        T3("offrir, raccompagner", "to give, see off", "дарить, провожать"),
        T3("Marcher 辶 avec quelqu'un pour le raccompagner.", "Walking 辶 with someone to see them off.", "Идти 辶 рядом, провожая."),
        T3("marcher", "walk", "идти"))
    m['给'] = CharInfo('给', "gěi", 9, 3, "纟", "纟合",
        T3("donner", "to give", "давать"),
        T3("Un fil 纟 qui unit 合 deux personnes.", "A thread 纟 joining 合 two people.", "Нить 纟, соединяющая 合 двоих."),
        T3("soie, fil", "silk, thread", "шёлк, нить"))
    m['找'] = CharInfo('找', "zhǎo", 7, 2, "扌", "扌戈",
        T3("chercher", "to look for", "искать"),
        T3("Une main 扌 armée d'une lance 戈 part chercher.", "A hand 扌 with a spear 戈 goes searching.", "Рука 扌 с копьём 戈 идёт искать."),
        T3("main", "hand", "рука"))
    m['等'] = CharInfo('等', "děng", 12, 2, "竹", "竹",
        T3("attendre ; rang", "to wait; rank", "ждать; ранг"),
        T3("Des lattes de bambou 竹 rangées : attendre son tour.", "Bamboo slips 竹 in rows: waiting your turn.", "Бамбуковые планки 竹 рядами: ждать очереди."),
        T3("bambou", "bamboo", "бамбук"))
    m['让'] = CharInfo('让', "ràng", 5, 3, "讠", "讠上",
        T3("laisser, céder", "to let, yield", "позволять, уступать"),
        T3("Des paroles 讠 qui laissent passer au-dessus 上.", "Words 讠 letting someone go above 上.", "Слова 讠, пропускающие вперёд 上."),
        T3("parole", "speech", "речь"))
    m['玩'] = CharInfo('玩', "wán", 8, 3, "王", "王元",
        T3("jouer", "to play", "играть"),
        T3("Jouer avec un jade 王 ; 元 donne le son.", "Playing with jade 王; 元 gives the sound.", "Играть с нефритом 王; 元 даёт звук."),
        T3("roi, jade", "king, jade", "царь, нефрит"))
    m['穿'] = CharInfo('穿', "chuān", 9, 3, "穴", "穴牙",
        T3("porter, percer", "to wear, pierce", "носить, пронзать"),
        T3("Une dent 牙 qui perce un trou 穴.", "A tooth 牙 boring a hole 穴.", "Зуб 牙 прокалывает дыру 穴."),
        T3("trou, grotte", "hole, cave", "дыра, пещера"))
    m['旅'] = CharInfo('旅', "lǚ", 10, 2, "方", "方氏",
        T3("voyage", "journey", "путешествие"),
        T3("Une troupe en marche sous sa bannière 方.", "A band marching under its banner 方.", "Отряд в пути под знаменем 方."),
        T3("carré, direction", "square, direction", "квадрат, сторона"))
    m['手'] = CharInfo('手', "shǒu", 4, 1, "手", "",
        T3("main", "hand", "рука"),
        T3("Une main aux doigts écartés.", "A hand with spread fingers.", "Рука с растопыренными пальцами."),
        T3("main", "hand", "рука"))
    m['表'] = CharInfo('表', "biǎo", 8, 2, "衣", "毛衣",
        T3("montrer ; montre", "to show; watch", "показывать; часы"),
        T3("Un vêtement 衣 de fourrure qu'on montre dehors.", "A fur garment 衣 shown on the outside.", "Меховая одежда 衣 напоказ."),
        T3("vêtement", "clothes", "одежда"))
    m['票'] = CharInfo('票', "piào", 11, 2, "示", "覀示",
        T3("billet", "ticket", "билет"),
        T3("Des flammes au-dessus d'un autel 示 : un papier officiel.", "Flames over an altar 示: an official slip.", "Пламя над алтарём 示: официальная бумага."),
        T3("autel, montrer", "altar, show", "алтарь, показывать"))
    m['晴'] = CharInfo('晴', "qíng", 12, 3, "日", "日青",
        T3("beau temps", "sunny", "ясно"),
        T3("Le soleil 日 ; 青 donne le son.", "The sun 日; 青 gives the sound.", "Солнце 日; 青 даёт звук."),
        T3("soleil, jour", "sun, day", "солнце, день"))
    m['阴'] = CharInfo('阴', "yīn", 6, 2, "阝", "阝月",
        T3("ombre, couvert", "shade, cloudy", "тень, пасмурно"),
        T3("Le lieu 阝 de la lune 月 : le côté sombre.", "The place 阝 of the moon 月: the dark side.", "Место 阝 луны 月: тёмная сторона."),
        T3("ville, colline", "town, hill", "город, холм"))
    m['雪'] = CharInfo('雪', "xuě", 11, 3, "雨", "雨彐",
        T3("neige", "snow", "снег"),
        T3("La pluie 雨 qu'on balaie à la main.", "Rain 雨 you sweep up by hand.", "Дождь 雨, который сметают рукой."),
        T3("pluie", "rain", "дождь"))
    m['笑'] = CharInfo('笑', "xiào", 10, 2, "竹", "竹夭",
        T3("rire", "to laugh", "смеяться"),
        T3("Une personne 夭 qui se plie de rire, comme le bambou 竹.", "A person 夭 bending with laughter, like bamboo 竹.", "Человек 夭 гнётся от смеха, как бамбук 竹."),
        T3("bambou", "bamboo", "бамбук"))
    m['乐'] = CharInfo('乐', "lè", 5, 2, "丿", "丿小",
        T3("joie ; musique", "joy; music", "радость; музыка"),
        T3("Un instrument de musique en bois.", "A wooden musical instrument.", "Деревянный музыкальный инструмент."),
        T3("trait tombant", "falling stroke", "откидная черта"))
}

private fun part4(m: MutableMap<Char, CharInfo>) {
    m['希'] = CharInfo('希', "xī", 7, 0, "巾", "",
        T3("espérer ; rare", "to hope; rare", "надеяться; редкий"),
        T3("Un tissu 巾 brodé, rare et désiré.", "An embroidered cloth 巾, rare and longed for.", "Вышитая ткань 巾, редкая и желанная."),
        T3("tissu", "cloth", "ткань"))
    m['望'] = CharInfo('望', "wàng", 11, 2, "月", "亡月王",
        T3("regarder au loin", "to gaze, hope", "смотреть вдаль"),
        T3("Un roi 王 qui contemple la lune 月.", "A king 王 gazing at the moon 月.", "Царь 王 смотрит на луну 月."),
        T3("lune, chair", "moon, flesh", "луна, плоть"))
    m['得'] = CharInfo('得', "de", 11, 2, "彳", "彳旦寸",
        T3("obtenir ; (manière)", "to get; (degree)", "получать; (степень)"),
        T3("Une main 寸 qui saisit un coquillage précieux.", "A hand 寸 grabbing a precious shell.", "Рука 寸 хватает ценную ракушку."),
        T3("pas", "step", "шаг"))
    m['可'] = CharInfo('可', "kě", 5, 3, "口", "丁口",
        T3("pouvoir", "can", "мочь"),
        T3("Une bouche 口 qui dit oui.", "A mouth 口 saying yes.", "Рот 口 говорит «да»."),
        T3("bouche", "mouth", "рот"))
    m['以'] = CharInfo('以', "yǐ", 4, 0, "人", "人",
        T3("avec, afin de", "with, so as to", "с помощью, чтобы"),
        T3("Une personne 人 qui se sert d'un outil.", "A person 人 using a tool.", "Человек 人 пользуется орудием."),
        T3("personne", "person", "человек"))
    m['要'] = CharInfo('要', "yào", 9, 2, "覀", "覀女",
        T3("vouloir", "to want", "хотеть"),
        T3("Une femme 女, les mains sur la taille 覀 : elle exige.", "A woman 女, hands on hips 覀: she insists.", "Женщина 女 руки в боки 覀: требует."),
        T3("couvercle", "cover", "крышка"))
    m['别'] = CharInfo('别', "bié", 7, 2, "刂", "刂",
        T3("ne… pas ; autre", "don't; other", "не; другой"),
        T3("Un couteau 刂 qui sépare : chacun à part.", "A knife 刂 that separates: each apart.", "Нож 刂 разделяет: каждый отдельно."),
        T3("couteau", "knife", "нож"))
    m['件'] = CharInfo('件', "jiàn", 6, 2, "亻", "亻牛",
        T3("(chose, vêtement)", "(item, clothing)", "(вещь, одежда)"),
        T3("Une personne 亻 qui partage un bœuf 牛 en morceaux.", "A person 亻 cutting an ox 牛 into pieces.", "Человек 亻 делит быка 牛 на части."),
        T3("personne", "person", "человек"))
    m['每'] = CharInfo('每', "měi", 7, 3, "母", "母",
        T3("chaque", "every", "каждый"),
        T3("Une mère 母 qui veille sur chacun.", "A mother 母 watching over each one.", "Мать 母 заботится о каждом."),
        T3("mère", "mother", "мать"))
    m['吧'] = CharInfo('吧', "ba", 7, 3, "口", "口巴",
        T3("(suggestion)", "(suggestion)", "(предложение)"),
        T3("La bouche 口 ; 巴 donne le son : ba.", "The mouth 口; 巴 gives the sound: ba.", "Рот 口; 巴 даёт звук: ba."),
        T3("bouche", "mouth", "рот"))
    m['也'] = CharInfo('也', "yě", 3, 0, "乚", "乚",
        T3("aussi", "also", "тоже"),
        T3("Un trait qui s'ajoute : « moi aussi ».", "A stroke added on: “me too”.", "Добавленная черта: «я тоже»."),
        T3("crochet", "hook", "крюк"))
    m['还'] = CharInfo('还', "hái", 7, 2, "辶", "辶不",
        T3("encore ; rendre", "still; to return", "ещё; возвращать"),
        T3("Marcher 辶 en revenant sur ses pas.", "Walking 辶 back the way you came.", "Идти 辶 обратно."),
        T3("marcher", "walk", "идти"))
    m['就'] = CharInfo('就', "jiù", 12, 3, "尢", "京",
        T3("tout de suite", "right away", "сразу"),
        T3("Aller vers la capitale 京 : on y est déjà.", "Heading to the capital 京: you're nearly there.", "Идти в столицу 京: уже почти там."),
        T3("boiteux", "lame", "хромой"))
    m['从'] = CharInfo('从', "cóng", 4, 2, "人", "人人",
        T3("depuis ; suivre", "from; to follow", "от; следовать"),
        T3("Une personne 人 qui en suit une autre 人.", "One person 人 following another 人.", "Человек 人 идёт за другим 人."),
        T3("personne", "person", "человек"))
    m['比'] = CharInfo('比', "bǐ", 4, 2, "比", "匕匕",
        T3("comparer", "to compare", "сравнивать"),
        T3("Deux cuillères 匕 côte à côte qu'on compare.", "Two spoons 匕 side by side, compared.", "Две ложки 匕 рядом для сравнения."),
        T3("comparer", "compare", "сравнивать"))
    m['因'] = CharInfo('因', "yīn", 6, 0, "囗", "囗大",
        T3("cause", "cause", "причина"),
        T3("Une personne 大 sur une natte 囗 : la raison d'être là.", "A person 大 on a mat 囗: the reason they're there.", "Человек 大 на циновке 囗: причина быть здесь."),
        T3("enceinte", "enclosure", "ограда"))
    m['为'] = CharInfo('为', "wèi", 4, 0, "丶", "丶力丶",
        T3("pour ; faire", "for; to do", "для; делать"),
        T3("Une main qui travaille avec force 力.", "A hand working with strength 力.", "Рука, работающая с силой 力."),
        T3("point", "dot", "точка"))
    m['所'] = CharInfo('所', "suǒ", 8, 2, "户", "户斤",
        T3("lieu, ce que", "place, that which", "место, то, что"),
        T3("Une hache 斤 à la porte 户 : la maison, le lieu.", "An axe 斤 by the door 户: the house, the place.", "Топор 斤 у двери 户: дом, место."),
        T3("porte", "door", "дверь"))
    m['虽'] = CharInfo('虽', "suī", 9, 0, "虫", "口虫",
        T3("bien que", "although", "хотя"),
        T3("Une bouche 口 et un insecte 虫 ; c'est le son qui compte.", "A mouth 口 and an insect 虫; it's the sound that counts.", "Рот 口 и насекомое 虫; важен звук."),
        T3("insecte", "insect", "насекомое"))
    m['然'] = CharInfo('然', "rán", 12, 3, "灬", "灬",
        T3("ainsi, en effet", "so, indeed", "так, действительно"),
        T3("De la viande sur le feu 灬 : bien sûr, ça cuit.", "Meat over the fire 灬: naturally, it cooks.", "Мясо на огне 灬: конечно, жарится."),
        T3("feu", "fire", "огонь"))
    m['但'] = CharInfo('但', "dàn", 7, 3, "亻", "亻旦",
        T3("mais", "but", "но"),
        T3("Une personne 亻 ; 旦 donne le son.", "A person 亻; 旦 gives the sound.", "Человек 亻; 旦 даёт звук."),
        T3("personne", "person", "человек"))
    m['阿'] = CharInfo('阿', "ā", 7, 3, "阝", "阝可",
        T3("(préfixe familier)", "(familiar prefix)", "(ласковая приставка)"),
        T3("Un lieu 阝 ; 可 donne le son : ā.", "A place 阝; 可 gives the sound: ā.", "Место 阝; 可 даёт звук: ā."),
        T3("ville, colline", "town, hill", "город, холм"))
    m['姨'] = CharInfo('姨', "yí", 9, 3, "女", "女夷",
        T3("tante maternelle", "aunt (mother's sister)", "тётя по матери"),
        T3("Une femme 女 ; 夷 donne le son.", "A woman 女; 夷 gives the sound.", "Женщина 女; 夷 даёт звук."),
        T3("femme", "woman", "женщина"))
    m['叔'] = CharInfo('叔', "shū", 8, 2, "又", "上小又",
        T3("oncle (cadet du père)", "uncle (father's younger brother)", "дядя (младший брат отца)"),
        T3("Le petit 小 frère, la main 又 tendue.", "The little 小 brother, hand 又 held out.", "Младший 小 брат с протянутой рукой 又."),
        T3("main", "hand", "рука"))
    m['爷'] = CharInfo('爷', "yé", 6, 3, "父", "父卩",
        T3("grand-père", "grandfather", "дедушка"),
        T3("Le père 父 du père ; 卩 complète.", "The father 父 of the father; 卩 completes it.", "Отец 父 отца; 卩 дополняет."),
        T3("père", "father", "отец"))
    m['邻'] = CharInfo('邻', "lín", 7, 3, "阝", "令阝",
        T3("voisin", "neighbour", "сосед"),
        T3("La ville 阝 où l'on vit côte à côte ; 令 donne le son.", "The town 阝 where people live side by side; 令 gives the sound.", "Город 阝, где живут рядом; 令 даёт звук."),
        T3("ville, colline", "town, hill", "город, холм"))
    m['居'] = CharInfo('居', "jū", 8, 3, "户", "户古",
        T3("habiter", "to live", "жить"),
        T3("Derrière la porte 户 ; 古 donne le son.", "Behind the door 户; 古 gives the sound.", "За дверью 户; 古 даёт звук."),
        T3("porte", "door", "дверь"))
    m['事'] = CharInfo('事', "shì", 8, 0, "亅", "一口彐亅",
        T3("affaire, chose", "matter, thing", "дело"),
        T3("Une main qui tient un pinceau pour noter les affaires.", "A hand holding a brush to record affairs.", "Рука с кистью записывает дела."),
        T3("crochet", "hook", "крюк"))
    m['理'] = CharInfo('理', "lǐ", 11, 3, "王", "王里",
        T3("raison, logique", "reason, logic", "разум, логика"),
        T3("Le jade 王 dont on suit les veines ; 里 donne le son.", "Jade 王 cut along its grain; 里 gives the sound.", "Нефрит 王, обработанный по прожилкам; 里 даёт звук."),
        T3("roi, jade", "king, jade", "царь, нефрит"))
    m['自'] = CharInfo('自', "zì", 6, 1, "自", "目",
        T3("soi ; depuis", "self; from", "сам; от"),
        T3("Un nez : en Chine on se montre le nez pour dire « moi ».", "A nose: in China you point to your nose to say “me”.", "Нос: в Китае, говоря «я», показывают на нос."),
        T3("soi, nez", "self, nose", "сам, нос"))
    m['己'] = CharInfo('己', "jǐ", 3, 1, "己", "乚",
        T3("soi-même", "oneself", "сам"),
        T3("Un fil enroulé sur le métier.", "A thread coiled on the loom.", "Нить, намотанная на станок."),
        T3("soi", "self", "сам"))
    m['轻'] = CharInfo('轻', "qīng", 9, 3, "车", "车又工",
        T3("léger", "light", "лёгкий"),
        T3("Une charrette 车 légère ; 圣 donne le son.", "A light cart 车; 圣 gives the sound.", "Лёгкая повозка 车; 圣 даёт звук."),
        T3("véhicule", "vehicle", "повозка"))
    m['聪'] = CharInfo('聪', "cōng", 15, 3, "耳", "耳总",
        T3("vif d'esprit", "bright, sharp", "сообразительный"),
        T3("Une oreille 耳 fine ; 总 donne le son.", "A sharp ear 耳; 总 gives the sound.", "Чуткое ухо 耳; 总 даёт звук."),
        T3("oreille", "ear", "ухо"))
    m['情'] = CharInfo('情', "qíng", 11, 3, "忄", "忄青",
        T3("sentiment", "feeling", "чувство"),
        T3("Le cœur 忄 ; 青 donne le son.", "The heart 忄; 青 gives the sound.", "Сердце 忄; 青 даёт звук."),
        T3("cœur", "heart", "сердце"))
    m['鼻'] = CharInfo('鼻', "bí", 14, 3, "鼻", "自畀",
        T3("nez", "nose", "нос"),
        T3("Le nez 自 ; 畀 donne le son.", "The nose 自; 畀 gives the sound.", "Нос 自; 畀 даёт звук."),
        T3("nez", "nose", "нос"))
    m['耳'] = CharInfo('耳', "ěr", 6, 1, "耳", "二",
        T3("oreille", "ear", "ухо"),
        T3("Le dessin d'une oreille.", "The drawing of an ear.", "Рисунок уха."),
        T3("oreille", "ear", "ухо"))
    m['朵'] = CharInfo('朵', "duǒ", 6, 2, "木", "几木",
        T3("(fleurs) ; lobe", "(flowers); earlobe", "(цветы); мочка"),
        T3("Des fleurs 几 qui pendent d'un arbre 木.", "Blossoms 几 hanging from a tree 木.", "Цветы 几 свисают с дерева 木."),
        T3("arbre, bois", "tree, wood", "дерево"))
    m['脸'] = CharInfo('脸', "liǎn", 11, 3, "月", "月佥",
        T3("visage", "face", "лицо"),
        T3("La chair 月 du visage ; 佥 donne le son.", "The flesh 月 of the face; 佥 gives the sound.", "Плоть 月 лица; 佥 даёт звук."),
        T3("lune, chair", "moon, flesh", "луна, плоть"))
    m['脚'] = CharInfo('脚', "jiǎo", 11, 3, "月", "月却",
        T3("pied", "foot", "ступня"),
        T3("La chair 月 ; 却 donne le son.", "Flesh 月; 却 gives the sound.", "Плоть 月; 却 даёт звук."),
        T3("lune, chair", "moon, flesh", "луна, плоть"))
    m['腿'] = CharInfo('腿', "tuǐ", 13, 2, "月", "月退",
        T3("jambe", "leg", "нога"),
        T3("La chair 月 qui sert à marcher 退.", "The flesh 月 used for walking 退.", "Плоть 月, которой ходят 退."),
        T3("lune, chair", "moon, flesh", "луна, плоть"))
    m['头'] = CharInfo('头', "tóu", 5, 3, "大", "冫大",
        T3("tête", "head", "голова"),
        T3("Une personne 大 et deux points : la tête.", "A person 大 and two dots: the head.", "Человек 大 и две точки: голова."),
        T3("grand", "big", "большой"))
    m['发'] = CharInfo('发', "fā", 5, 0, "又", "又",
        T3("envoyer ; cheveux", "to send; hair", "посылать; волосы"),
        T3("Une main 又 qui lance : envoyer, se développer.", "A hand 又 throwing: to send, to grow.", "Рука 又 бросает: посылать, развиваться."),
        T3("main", "hand", "рука"))
    m['感'] = CharInfo('感', "gǎn", 13, 2, "心", "咸心",
        T3("ressentir", "to feel", "чувствовать"),
        T3("Le cœur 心 touché de partout 咸.", "The heart 心 touched all over 咸.", "Сердце 心, тронутое со всех сторон 咸."),
        T3("cœur", "heart", "сердце"))
    m['冒'] = CharInfo('冒', "mào", 9, 3, "冂", "日目",
        T3("braver, couvrir", "to risk, cover", "рисковать, покрывать"),
        T3("Un capuchon posé sur les yeux 目.", "A hood pulled over the eyes 目.", "Капюшон, надвинутый на глаза 目."),
        T3("enclos", "enclosure", "ограда"))
    m['烧'] = CharInfo('烧', "shāo", 10, 3, "火", "火尧",
        T3("brûler", "to burn", "жечь"),
        T3("Le feu 火 ; 尧 donne le son.", "Fire 火; 尧 gives the sound.", "Огонь 火; 尧 даёт звук."),
        T3("feu", "fire", "огонь"))
    m['疼'] = CharInfo('疼', "téng", 10, 3, "疒", "疒冬",
        T3("avoir mal", "to ache", "болеть"),
        T3("Le lit du malade 疒 ; 冬 donne le son.", "The sickbed 疒; 冬 gives the sound.", "Больничная кровать 疒; 冬 даёт звук."),
        T3("maladie", "sickness", "болезнь"))
    m['健'] = CharInfo('健', "jiàn", 10, 2, "亻", "亻建",
        T3("vigoureux", "strong, healthy", "крепкий"),
        T3("Une personne 亻 qui bâtit 建 sa force.", "A person 亻 building 建 their strength.", "Человек 亻 строит 建 свою силу."),
        T3("personne", "person", "человек"))
    m['康'] = CharInfo('康', "kāng", 11, 2, "广", "广",
        T3("paix, santé", "peace, health", "спокойствие, здоровье"),
        T3("Une maison 广 où tout va bien.", "A home 广 where all is well.", "Дом 广, где всё хорошо."),
        T3("abri", "shelter", "навес"))
    m['锻'] = CharInfo('锻', "duàn", 14, 3, "钅", "钅段",
        T3("forger", "to forge", "ковать"),
        T3("Le métal 钅 qu'on frappe ; 段 donne le son.", "Metal 钅 being struck; 段 gives the sound.", "Металл 钅 под молотом; 段 даёт звук."),
        T3("métal", "metal", "металл"))
    m['炼'] = CharInfo('炼', "liàn", 9, 3, "火", "火柬",
        T3("affiner au feu", "to refine", "закалять"),
        T3("Le feu 火 qui purifie ; 柬 donne le son.", "Fire 火 that purifies; 柬 gives the sound.", "Огонь 火 очищает; 柬 даёт звук."),
        T3("feu", "fire", "огонь"))
    m['检'] = CharInfo('检', "jiǎn", 11, 3, "木", "木佥",
        T3("vérifier", "to check", "проверять"),
        T3("Du bois 木 qu'on inspecte ; 佥 donne le son.", "Wood 木 being inspected; 佥 gives the sound.", "Дерево 木 под проверкой; 佥 даёт звук."),
        T3("arbre, bois", "tree, wood", "дерево"))
    m['查'] = CharInfo('查', "chá", 9, 0, "木", "木旦",
        T3("enquêter", "to investigate", "расследовать"),
        T3("Le soleil 旦 derrière l'arbre 木 : on regarde de près.", "The sun 旦 behind the tree 木: looking closely.", "Солнце 旦 за деревом 木: присмотреться."),
        T3("arbre, bois", "tree, wood", "дерево"))
    m['胖'] = CharInfo('胖', "pàng", 9, 3, "月", "月半",
        T3("gros", "fat", "толстый"),
        T3("La chair 月 ; 半 donne le son.", "Flesh 月; 半 gives the sound.", "Плоть 月; 半 даёт звук."),
        T3("lune, chair", "moon, flesh", "луна, плоть"))
    m['瘦'] = CharInfo('瘦', "shòu", 14, 3, "疒", "疒叟",
        T3("maigre", "thin", "худой"),
        T3("Maigri par la maladie 疒 ; 叟 donne le son.", "Thinned by illness 疒; 叟 gives the sound.", "Исхудавший от болезни 疒; 叟 даёт звук."),
        T3("maladie", "sickness", "болезнь"))
    m['舒'] = CharInfo('舒', "shū", 12, 3, "舌", "舍予",
        T3("à l'aise", "at ease", "удобно"),
        T3("Chez soi 舍, on se détend 予.", "At home 舍, you relax 予.", "Дома 舍 расслабляешься 予."),
        T3("langue", "tongue", "язык"))
    m['衬'] = CharInfo('衬', "chèn", 8, 3, "衤", "衤寸",
        T3("doublure", "lining", "подкладка"),
        T3("Un vêtement 衤 ; 寸 donne le son.", "A garment 衤; 寸 gives the sound.", "Одежда 衤; 寸 даёт звук."),
        T3("vêtement", "clothes", "одежда"))
    m['衫'] = CharInfo('衫', "shān", 8, 3, "衤", "衤彡",
        T3("chemise", "shirt", "рубашка"),
        T3("Un vêtement 衤 léger 彡.", "A light garment 衤 彡.", "Лёгкая одежда 衤 彡."),
        T3("vêtement", "clothes", "одежда"))
    m['裤'] = CharInfo('裤', "kù", 12, 3, "衤", "衤库",
        T3("pantalon", "trousers", "брюки"),
        T3("Un vêtement 衤 ; 库 donne le son.", "A garment 衤; 库 gives the sound.", "Одежда 衤; 库 даёт звук."),
        T3("vêtement", "clothes", "одежда"))
    m['裙'] = CharInfo('裙', "qún", 12, 3, "衤", "衤君",
        T3("jupe", "skirt", "юбка"),
        T3("Un vêtement 衤 ; 君 donne le son.", "A garment 衤; 君 gives the sound.", "Одежда 衤; 君 даёт звук."),
        T3("vêtement", "clothes", "одежда"))
    m['帽'] = CharInfo('帽', "mào", 12, 3, "巾", "巾冒",
        T3("chapeau", "hat", "шапка"),
        T3("Du tissu 巾 qui couvre 冒 la tête.", "Cloth 巾 that covers 冒 the head.", "Ткань 巾, покрывающая 冒 голову."),
        T3("tissu", "cloth", "ткань"))
    m['皮'] = CharInfo('皮', "pí", 5, 1, "皮", "",
        T3("peau, cuir", "skin, leather", "кожа"),
        T3("Une main qui détache la peau d'une bête.", "A hand stripping an animal's hide.", "Рука снимает шкуру."),
        T3("peau", "skin", "кожа"))
    m['鞋'] = CharInfo('鞋', "xié", 15, 3, "革", "革圭",
        T3("chaussure", "shoe", "обувь"),
        T3("Du cuir 革 ; 圭 donne le son.", "Leather 革; 圭 gives the sound.", "Кожа 革; 圭 даёт звук."),
        T3("cuir", "leather", "кожа (выделанная)"))
    m['双'] = CharInfo('双', "shuāng", 4, 2, "又", "又又",
        T3("paire", "pair", "пара"),
        T3("Deux mains 又 côte à côte.", "Two hands 又 side by side.", "Две руки 又 рядом."),
        T3("main", "hand", "рука"))
    m['换'] = CharInfo('换', "huàn", 10, 3, "扌", "扌奂",
        T3("échanger", "to exchange", "менять"),
        T3("La main 扌 qui échange ; 奂 donne le son.", "The hand 扌 that swaps; 奂 gives the sound.", "Рука 扌 меняет; 奂 даёт звук."),
        T3("main", "hand", "рука"))
    m['蓝'] = CharInfo('蓝', "lán", 13, 3, "艹", "艹监",
        T3("bleu", "blue", "синий"),
        T3("La plante 艹 qui donne la teinture indigo ; 监 donne le son.", "The plant 艹 giving indigo dye; 监 gives the sound.", "Растение 艹, дающее индиго; 监 даёт звук."),
        T3("herbe, plante", "grass, plant", "трава, растение"))
    m['绿'] = CharInfo('绿', "lǜ", 11, 3, "纟", "纟录",
        T3("vert", "green", "зелёный"),
        T3("Un fil 纟 teint ; 录 donne le son.", "A dyed thread 纟; 录 gives the sound.", "Окрашенная нить 纟; 录 даёт звук."),
        T3("soie, fil", "silk, thread", "шёлк, нить"))
    m['旧'] = CharInfo('旧', "jiù", 5, 2, "日", "丨日",
        T3("ancien, usé", "old, worn", "старый"),
        T3("Un trait 丨 plus ancien que le soleil 日.", "A stroke 丨 older than the sun 日.", "Черта 丨 старше солнца 日."),
        T3("soleil, jour", "sun, day", "солнце, день"))
    m['干'] = CharInfo('干', "gàn", 3, 1, "十", "一十",
        T3("sec ; faire", "dry; to do", "сухой; делать"),
        T3("Une massue fourchue, outil de travail.", "A forked club, a work tool.", "Раздвоенная дубина, орудие труда."),
        T3("dix", "ten", "десять"))
    m['净'] = CharInfo('净', "jìng", 8, 3, "冫", "冫争",
        T3("propre", "clean", "чистый"),
        T3("L'eau 冫 qui lave ; 争 donne le son.", "Water 冫 that washes; 争 gives the sound.", "Вода 冫 моет; 争 даёт звук."),
        T3("glace", "ice", "лёд"))
    m['伞'] = CharInfo('伞', "sǎn", 6, 1, "人", "人",
        T3("parapluie", "umbrella", "зонт"),
        T3("Le dessin d'un parapluie ouvert.", "The drawing of an open umbrella.", "Рисунок раскрытого зонта."),
        T3("personne", "person", "человек"))
    m['短'] = CharInfo('短', "duǎn", 12, 3, "矢", "矢豆",
        T3("court", "short", "короткий"),
        T3("Une flèche 矢 : petite mesure ; 豆 donne le son.", "An arrow 矢: a short measure; 豆 gives the sound.", "Стрела 矢 — короткая мера; 豆 даёт звук."),
        T3("flèche", "arrow", "стрела"))
    m['矮'] = CharInfo('矮', "ǎi", 13, 3, "矢", "矢委",
        T3("petit, bas", "short, low", "низкий"),
        T3("Une flèche 矢 courte ; 委 donne le son.", "A short arrow 矢; 委 gives the sound.", "Короткая стрела 矢; 委 даёт звук."),
        T3("flèche", "arrow", "стрела"))
    m['像'] = CharInfo('像', "xiàng", 13, 3, "亻", "亻象",
        T3("ressembler", "to resemble", "быть похожим"),
        T3("Une personne 亻 ; 象 (l'éléphant) donne le son.", "A person 亻; 象 (elephant) gives the sound.", "Человек 亻; 象 (слон) даёт звук."),
        T3("personne", "person", "человек"))
    m['单'] = CharInfo('单', "dān", 8, 0, "丷", "丷",
        T3("seul ; liste", "single; list", "один; список"),
        T3("Une arme simple à deux pointes.", "A simple two-pronged weapon.", "Простое двузубое оружие."),
        T3("huit (haut)", "eight (top)", "восемь (верх)"))
    m['糕'] = CharInfo('糕', "gāo", 16, 3, "米", "米羔",
        T3("gâteau", "cake", "пирожное"),
        T3("Du riz 米 ; 羔 donne le son.", "Rice 米; 羔 gives the sound.", "Рис 米; 羔 даёт звук."),
        T3("riz", "rice", "рис"))
    m['包'] = CharInfo('包', "bāo", 5, 2, "勹", "勹巳",
        T3("envelopper, sac", "to wrap, bag", "заворачивать, сумка"),
        T3("Un bébé 巳 enveloppé 勹.", "A baby 巳 wrapped up 勹.", "Младенец 巳, завёрнутый 勹."),
        T3("envelopper", "wrap", "обёртка"))
    m['香'] = CharInfo('香', "xiāng", 9, 2, "香", "禾日",
        T3("parfumé", "fragrant", "ароматный"),
        T3("Une céréale 禾 qui sent bon au soleil 日.", "Grain 禾 smelling sweet in the sun 日.", "Злак 禾 ароматный на солнце 日."),
        T3("parfum", "fragrance", "аромат"))
    m['蕉'] = CharInfo('蕉', "jiāo", 15, 3, "艹", "艹焦",
        T3("banane", "banana", "банан"),
        T3("Une plante 艹 ; 焦 donne le son.", "A plant 艹; 焦 gives the sound.", "Растение 艹; 焦 даёт звук."),
        T3("herbe, plante", "grass, plant", "трава, растение"))
    m['饮'] = CharInfo('饮', "yǐn", 7, 3, "饣", "饣欠",
        T3("boire", "to drink", "пить"),
        T3("La nourriture 饣 qu'on avale, bouche ouverte 欠.", "Food 饣 swallowed, mouth open 欠.", "Еда 饣, проглатываемая с открытым ртом 欠."),
        T3("nourriture", "food", "еда"))
    m['料'] = CharInfo('料', "liào", 10, 2, "斗", "米斗",
        T3("matière", "material", "материал"),
        T3("Mesurer au boisseau 斗 le riz 米.", "Measuring rice 米 with a scoop 斗.", "Отмерять рис 米 ковшом 斗."),
        T3("boisseau", "dipper", "ковш"))
}

private fun part5(m: MutableMap<Char, CharInfo>) {
    m['汁'] = CharInfo('汁', "zhī", 5, 3, "氵", "氵十",
        T3("jus", "juice", "сок"),
        T3("Le liquide 氵 ; 十 donne le son.", "Liquid 氵; 十 gives the sound.", "Жидкость 氵; 十 даёт звук."),
        T3("eau", "water", "вода"))
    m['啤'] = CharInfo('啤', "pí", 11, 3, "口", "口卑",
        T3("(bière)", "(beer)", "(пиво)"),
        T3("La bouche 口 ; 卑 donne le son : pí.", "The mouth 口; 卑 gives the sound: pí.", "Рот 口; 卑 даёт звук: pí."),
        T3("bouche", "mouth", "рот"))
    m['酒'] = CharInfo('酒', "jiǔ", 10, 2, "酉", "氵酉",
        T3("alcool", "alcohol", "алкоголь"),
        T3("Le liquide 氵 dans la jarre 酉.", "Liquid 氵 in the jar 酉.", "Жидкость 氵 в кувшине 酉."),
        T3("jarre de vin", "wine jar", "кувшин"))
    m['筷'] = CharInfo('筷', "kuài", 13, 3, "竹", "竹快",
        T3("baguettes", "chopsticks", "палочки"),
        T3("En bambou 竹 ; 快 donne le son (et la vitesse !).", "Bamboo 竹; 快 gives the sound (and the speed!).", "Бамбук 竹; 快 даёт звук (и скорость!)."),
        T3("bambou", "bamboo", "бамбук"))
    m['碗'] = CharInfo('碗', "wǎn", 13, 3, "石", "石宛",
        T3("bol", "bowl", "миска"),
        T3("En pierre 石 ; 宛 donne le son.", "Stone 石; 宛 gives the sound.", "Камень 石; 宛 даёт звук."),
        T3("pierre", "stone", "камень"))
    m['盘'] = CharInfo('盘', "pán", 11, 3, "皿", "舟皿",
        T3("assiette", "plate", "тарелка"),
        T3("Un plat 皿 ; 舟 donne la forme.", "A dish 皿; 舟 gives the shape.", "Блюдо 皿; 舟 даёт форму."),
        T3("récipient", "dish", "посуда"))
    m['瓶'] = CharInfo('瓶', "píng", 10, 3, "瓦", "并瓦",
        T3("bouteille", "bottle", "бутылка"),
        T3("Une poterie 瓦 ; 并 donne le son.", "Pottery 瓦; 并 gives the sound.", "Керамика 瓦; 并 даёт звук."),
        T3("tuile, poterie", "tile, pottery", "черепица"))
    m['甜'] = CharInfo('甜', "tián", 11, 2, "甘", "舌甘",
        T3("sucré", "sweet", "сладкий"),
        T3("Ce qui est bon 甘 sur la langue 舌.", "What's tasty 甘 on the tongue 舌.", "То, что вкусно 甘 на языке 舌."),
        T3("sucré", "sweet", "сладкий"))
    m['饿'] = CharInfo('饿', "è", 10, 2, "饣", "饣我",
        T3("avoir faim", "hungry", "голодный"),
        T3("Moi 我 qui veux de la nourriture 饣.", "Me 我 wanting food 饣.", "Я 我 хочу еды 饣."),
        T3("nourriture", "food", "еда"))
    m['饱'] = CharInfo('饱', "bǎo", 8, 3, "饣", "饣包",
        T3("rassasié", "full", "сытый"),
        T3("La nourriture 饣 qui remplit 包 le ventre.", "Food 饣 filling 包 the belly.", "Еда 饣 наполняет 包 живот."),
        T3("nourriture", "food", "еда"))
    m['渴'] = CharInfo('渴', "kě", 12, 3, "氵", "氵曷",
        T3("avoir soif", "thirsty", "испытывать жажду"),
        T3("L'eau 氵 qui manque ; 曷 donne le son.", "The water 氵 you lack; 曷 gives the sound.", "Вода 氵, которой не хватает; 曷 даёт звук."),
        T3("eau", "water", "вода"))
    m['超'] = CharInfo('超', "chāo", 12, 3, "走", "走召",
        T3("dépasser", "to exceed", "превосходить"),
        T3("Courir 走 au-delà ; 召 donne le son.", "Running 走 beyond; 召 gives the sound.", "Бежать 走 дальше; 召 даёт звук."),
        T3("marcher", "walk", "идти"))
    m['市'] = CharInfo('市', "shì", 5, 0, "巾", "亠巾",
        T3("marché, ville", "market, city", "рынок, город"),
        T3("Un étal sous un auvent.", "A stall under an awning.", "Прилавок под навесом."),
        T3("tissu", "cloth", "ткань"))
    m['银'] = CharInfo('银', "yín", 11, 3, "钅", "钅艮",
        T3("argent (métal)", "silver", "серебро"),
        T3("Le métal 钅 ; 艮 donne le son.", "Metal 钅; 艮 gives the sound.", "Металл 钅; 艮 даёт звук."),
        T3("métal", "metal", "металл"))
    m['行'] = CharInfo('行', "xíng", 6, 2, "行", "彳",
        T3("aller ; rangée", "to go; row", "идти; ряд"),
        T3("Un carrefour : on marche à petits pas.", "A crossroads: walking step by step.", "Перекрёсток: идти шаг за шагом."),
        T3("marcher", "walk", "идти"))
    m['园'] = CharInfo('园', "yuán", 7, 3, "囗", "囗元",
        T3("jardin, parc", "garden, park", "сад, парк"),
        T3("Un enclos 囗 ; 元 donne le son.", "An enclosure 囗; 元 gives the sound.", "Ограда 囗; 元 даёт звук."),
        T3("enceinte", "enclosure", "ограда"))
    m['图'] = CharInfo('图', "tú", 8, 2, "囗", "囗冬",
        T3("image, plan", "picture, map", "рисунок, карта"),
        T3("Une carte dessinée dans un cadre 囗.", "A map drawn inside a frame 囗.", "Карта в рамке 囗."),
        T3("enceinte", "enclosure", "ограда"))
    m['地'] = CharInfo('地', "de", 6, 3, "土", "土也",
        T3("terre, sol", "earth, ground", "земля"),
        T3("La terre 土 ; 也 donne le son.", "Earth 土; 也 gives the sound.", "Земля 土; 也 даёт звук."),
        T3("terre", "earth", "земля"))
    m['铁'] = CharInfo('铁', "tiě", 10, 3, "钅", "钅失",
        T3("fer", "iron", "железо"),
        T3("Le métal 钅 ; 失 donne le son.", "Metal 钅; 失 gives the sound.", "Металл 钅; 失 даёт звук."),
        T3("métal", "metal", "металл"))
    m['街'] = CharInfo('街', "jiē", 12, 3, "行", "行圭",
        T3("rue", "street", "улица"),
        T3("Un carrefour 行 bordé de maisons 圭.", "A crossroads 行 lined with houses 圭.", "Перекрёсток 行 с домами 圭."),
        T3("marcher", "walk", "идти"))
    m['道'] = CharInfo('道', "dào", 12, 3, "辶", "辶首",
        T3("chemin, voie", "way, road", "путь, дорога"),
        T3("Marcher 辶 avec sa tête 首 : le chemin.", "Walking 辶 with your head 首: the way.", "Идти 辶 головой 首 вперёд: путь."),
        T3("marcher", "walk", "идти"))
    m['楼'] = CharInfo('楼', "lóu", 13, 3, "木", "木娄",
        T3("immeuble, étage", "building, floor", "здание, этаж"),
        T3("Du bois 木 empilé ; 娄 donne le son.", "Wood 木 stacked up; 娄 gives the sound.", "Дерево 木 ярусами; 娄 даёт звук."),
        T3("arbre, bois", "tree, wood", "дерево"))
    m['层'] = CharInfo('层', "céng", 7, 2, "尸", "尸云",
        T3("couche, étage", "layer, floor", "слой, этаж"),
        T3("Un toit 尸 et des nuages 云 empilés.", "A roof 尸 and stacked clouds 云.", "Крыша 尸 и облака 云 ярусами."),
        T3("corps, toit", "body, roof", "тело, крыша"))
    m['梯'] = CharInfo('梯', "tī", 11, 3, "木", "木弟",
        T3("échelle", "ladder", "лестница"),
        T3("Du bois 木 aux barreaux en ordre 弟.", "Wood 木 with rungs in order 弟.", "Дерево 木 с перекладинами по порядку 弟."),
        T3("arbre, bois", "tree, wood", "дерево"))
    m['附'] = CharInfo('附', "fù", 7, 3, "阝", "阝付",
        T3("attaché, près", "attached, near", "прилагать, рядом"),
        T3("Un lieu 阝 qu'on donne 付 en plus.", "A place 阝 given 付 in addition.", "Место 阝, данное 付 в придачу."),
        T3("ville, colline", "town, hill", "город, холм"))
    m['城'] = CharInfo('城', "chéng", 9, 3, "土", "土成",
        T3("ville fortifiée", "city, wall", "город, стена"),
        T3("La terre 土 des remparts ; 成 donne le son.", "The earth 土 of the walls; 成 gives the sound.", "Земля 土 крепостных стен; 成 даёт звук."),
        T3("terre", "earth", "земля"))
    m['方'] = CharInfo('方', "fāng", 4, 0, "方", "亠",
        T3("carré ; côté", "square; side", "квадрат; сторона"),
        T3("Une charrue à deux bras : une direction.", "A two-handled plough: a direction.", "Плуг с двумя рукоятками: направление."),
        T3("carré, direction", "square, direction", "квадрат, сторона"))
    m['环'] = CharInfo('环', "huán", 8, 3, "王", "王不",
        T3("anneau, entourer", "ring, to surround", "кольцо, окружать"),
        T3("Un anneau de jade 王.", "A ring of jade 王.", "Кольцо из нефрита 王."),
        T3("roi, jade", "king, jade", "царь, нефрит"))
    m['境'] = CharInfo('境', "jìng", 14, 3, "土", "土竟",
        T3("frontière, lieu", "border, place", "граница, место"),
        T3("La terre 土 où l'on arrive au bout 竟.", "The land 土 where you reach the end 竟.", "Земля 土, где доходишь до края 竟."),
        T3("terre", "earth", "земля"))
    m['护'] = CharInfo('护', "hù", 7, 2, "扌", "扌户",
        T3("protéger", "to protect", "защищать"),
        T3("Une main 扌 qui garde la porte 户.", "A hand 扌 guarding the door 户.", "Рука 扌 охраняет дверь 户."),
        T3("main", "hand", "рука"))
    m['照'] = CharInfo('照', "zhào", 13, 3, "灬", "昭灬",
        T3("éclairer, photo", "to shine, photo", "светить, фото"),
        T3("Le feu 灬 qui éclaire ; 昭 donne le son.", "Fire 灬 that lights up; 昭 gives the sound.", "Огонь 灬 освещает; 昭 даёт звук."),
        T3("feu", "fire", "огонь"))
    m['李'] = CharInfo('李', "lǐ", 7, 3, "木", "木子",
        T3("prune ; bagages", "plum; luggage", "слива; багаж"),
        T3("Un arbre 木 qui donne des fruits 子.", "A tree 木 that bears fruit 子.", "Дерево 木, дающее плоды 子."),
        T3("arbre, bois", "tree, wood", "дерево"))
    m['箱'] = CharInfo('箱', "xiāng", 15, 3, "竹", "竹相",
        T3("caisse, malle", "box, trunk", "ящик, сундук"),
        T3("Du bambou 竹 tressé ; 相 donne le son.", "Woven bamboo 竹; 相 gives the sound.", "Плетёный бамбук 竹; 相 даёт звук."),
        T3("bambou", "bamboo", "бамбук"))
    m['船'] = CharInfo('船', "chuán", 11, 3, "舟", "舟几口",
        T3("bateau", "boat", "корабль"),
        T3("Une barque 舟 ; 㕣 donne le son.", "A boat 舟; 㕣 gives the sound.", "Лодка 舟; 㕣 даёт звук."),
        T3("barque", "boat", "лодка"))
    m['辆'] = CharInfo('辆', "liàng", 11, 2, "车", "车两",
        T3("(véhicules)", "(vehicles)", "(машины)"),
        T3("Deux 两 roues de charrette 车.", "Two 两 cart 车 wheels.", "Два 两 колеса повозки 车."),
        T3("véhicule", "vehicle", "повозка"))
    m['骑'] = CharInfo('骑', "qí", 11, 3, "马", "马奇",
        T3("chevaucher", "to ride", "ездить верхом"),
        T3("Le cheval 马 ; 奇 donne le son.", "The horse 马; 奇 gives the sound.", "Лошадь 马; 奇 даёт звук."),
        T3("cheval", "horse", "лошадь"))
    m['留'] = CharInfo('留', "liú", 10, 0, "田", "卯田",
        T3("rester, garder", "to stay, keep", "оставаться, хранить"),
        T3("Un champ 田 où l'on s'arrête.", "A field 田 where you stay.", "Поле 田, где остаются."),
        T3("champ", "field", "поле"))
    m['带'] = CharInfo('带', "dài", 9, 2, "巾", "冖巾",
        T3("ceinture ; emporter", "belt; to take", "пояс; брать"),
        T3("Une ceinture qui plisse la robe 巾.", "A belt creasing the robe 巾.", "Пояс, собирающий одежду 巾."),
        T3("tissu", "cloth", "ткань"))
    m['接'] = CharInfo('接', "jiē", 11, 3, "扌", "扌妾",
        T3("recevoir, relier", "to receive, connect", "принимать, соединять"),
        T3("La main 扌 qui accueille ; 妾 donne le son.", "The hand 扌 that receives; 妾 gives the sound.", "Рука 扌 принимает; 妾 даёт звук."),
        T3("main", "hand", "рука"))
    m['搬'] = CharInfo('搬', "bān", 13, 3, "扌", "扌般",
        T3("déplacer", "to move", "переносить"),
        T3("La main 扌 qui porte ; 般 donne le son.", "The hand 扌 that carries; 般 gives the sound.", "Рука 扌 несёт; 般 даёт звук."),
        T3("main", "hand", "рука"))
    m['信'] = CharInfo('信', "xìn", 9, 0, "亻", "亻言",
        T3("croire ; lettre", "to believe; letter", "верить; письмо"),
        T3("Une personne 亻 fidèle à sa parole 言.", "A person 亻 true to their word 言.", "Человек 亻, верный слову 言."),
        T3("personne", "person", "человек"))
    m['用'] = CharInfo('用', "yòng", 5, 0, "用", "冂",
        T3("utiliser", "to use", "использовать"),
        T3("Un seau ou un ustensile dont on se sert.", "A bucket or tool you use.", "Ведро или утварь, которой пользуются."),
        T3("utiliser", "use", "использовать"))
    m['卡'] = CharInfo('卡', "kǎ", 5, 2, "卜", "上卜",
        T3("carte", "card", "карта"),
        T3("Une carte coincée entre le haut 上 et le bas 下.", "A card stuck between up 上 and down 下.", "Карта, зажатая между верхом 上 и низом 下."),
        T3("divination", "divination", "гадание"))
    m['相'] = CharInfo('相', "xiāng", 9, 2, "目", "木目",
        T3("mutuel ; regarder", "mutual; to look", "взаимный; смотреть"),
        T3("Un œil 目 qui observe un arbre 木.", "An eye 目 watching a tree 木.", "Глаз 目 смотрит на дерево 木."),
        T3("œil", "eye", "глаз"))
    m['片'] = CharInfo('片', "piàn", 4, 1, "片", "",
        T3("morceau, tranche", "piece, slice", "кусок, ломтик"),
        T3("La moitié d'un tronc fendu.", "Half of a split trunk.", "Половина расколотого ствола."),
        T3("tranche", "slice", "ломтик"))
    m['春'] = CharInfo('春', "chūn", 9, 3, "日", "日",
        T3("printemps", "spring", "весна"),
        T3("Le soleil 日 qui fait pousser les plantes.", "The sun 日 making plants grow.", "Солнце 日, от которого растут растения."),
        T3("soleil, jour", "sun, day", "солнце, день"))
    m['夏'] = CharInfo('夏', "xià", 10, 0, "夊", "一自夂",
        T3("été", "summer", "лето"),
        T3("Une personne 自 qui marche 夊 sous la chaleur.", "A person 自 walking 夊 in the heat.", "Человек 自 идёт 夊 по жаре."),
        T3("marcher", "walk slowly", "идти"))
    m['秋'] = CharInfo('秋', "qiū", 9, 2, "禾", "禾火",
        T3("automne", "autumn", "осень"),
        T3("Le grain 禾 et le feu 火 des moissons.", "Grain 禾 and the fire 火 of harvest.", "Зерно 禾 и огонь 火 жатвы."),
        T3("céréale", "grain", "злак"))
    m['冬'] = CharInfo('冬', "dōng", 5, 2, "冫", "夂冫",
        T3("hiver", "winter", "зима"),
        T3("Marcher 夂 sur la glace.", "Walking 夂 on ice.", "Идти 夂 по льду."),
        T3("glace", "ice", "лёд"))
    m['季'] = CharInfo('季', "jì", 8, 2, "子", "禾子",
        T3("saison", "season", "сезон"),
        T3("Le temps de semer 子 les céréales 禾.", "The time to sow 子 grain 禾.", "Время сеять 子 злаки 禾."),
        T3("enfant", "child", "ребёнок"))
    m['节'] = CharInfo('节', "jié", 5, 2, "艹", "艹",
        T3("fête ; nœud", "festival; knot", "праздник; узел"),
        T3("Les nœuds d'une tige de bambou : les étapes de l'année.", "The nodes of a bamboo stem: the year's milestones.", "Узлы стебля бамбука: вехи года."),
        T3("herbe, plante", "grass, plant", "трава, растение"))
    m['刮'] = CharInfo('刮', "guā", 8, 3, "刂", "舌刂",
        T3("racler ; souffler", "to scrape; to blow", "скоблить; дуть"),
        T3("Le couteau 刂 qui racle ; 舌 donne le son.", "The knife 刂 scraping; 舌 gives the sound.", "Нож 刂 скоблит; 舌 даёт звук."),
        T3("couteau", "knife", "нож"))
    m['风'] = CharInfo('风', "fēng", 4, 0, "风", "几",
        T3("vent", "wind", "ветер"),
        T3("Une voile 几 qui gonfle.", "A sail 几 billowing.", "Парус 几 надувается."),
        T3("vent", "wind", "ветер"))
    m['阳'] = CharInfo('阳', "yáng", 6, 2, "阝", "阝日",
        T3("soleil, yang", "sun, yang", "солнце, ян"),
        T3("Le lieu 阝 du soleil 日 : le côté lumineux.", "The place 阝 of the sun 日: the bright side.", "Место 阝 солнца 日: светлая сторона."),
        T3("ville, colline", "town, hill", "город, холм"))
    m['树'] = CharInfo('树', "shù", 9, 3, "木", "木对",
        T3("arbre", "tree", "дерево"),
        T3("Le bois 木 ; 对 donne le son.", "Wood 木; 对 gives the sound.", "Дерево 木; 对 даёт звук."),
        T3("arbre, bois", "tree, wood", "дерево"))
    m['草'] = CharInfo('草', "cǎo", 9, 3, "艹", "艹早",
        T3("herbe", "grass", "трава"),
        T3("Des pousses 艹 qui sortent tôt 早.", "Shoots 艹 coming up early 早.", "Ростки 艹 появляются рано 早."),
        T3("herbe, plante", "grass, plant", "трава, растение"))
    m['花'] = CharInfo('花', "huā", 7, 2, "艹", "艹化",
        T3("fleur ; dépenser", "flower; to spend", "цветок; тратить"),
        T3("Une plante 艹 qui se transforme 化 en fleur.", "A plant 艹 turning 化 into a flower.", "Растение 艹 превращается 化 в цветок."),
        T3("herbe, plante", "grass, plant", "трава, растение"))
    m['鸟'] = CharInfo('鸟', "niǎo", 5, 1, "鸟", "",
        T3("oiseau", "bird", "птица"),
        T3("Le dessin d'un oiseau perché.", "The drawing of a perched bird.", "Рисунок сидящей птицы."),
        T3("oiseau", "bird", "птица"))
    m['物'] = CharInfo('物', "wù", 8, 3, "牛", "牛勿",
        T3("chose", "thing", "вещь"),
        T3("Un bœuf 牛 : le bien le plus précieux ; 勿 donne le son.", "An ox 牛: the most precious thing; 勿 gives the sound.", "Бык 牛 — самая ценная вещь; 勿 даёт звук."),
        T3("bœuf", "ox", "бык"))
    m['马'] = CharInfo('马', "mǎ", 3, 1, "马", "一",
        T3("cheval", "horse", "лошадь"),
        T3("Un cheval au galop, crinière au vent.", "A galloping horse, mane flying.", "Скачущая лошадь с развевающейся гривой."),
        T3("cheval", "horse", "лошадь"))
    m['爬'] = CharInfo('爬', "pá", 8, 3, "爪", "爪巴",
        T3("grimper", "to climb", "карабкаться"),
        T3("Des griffes 爪 qui s'accrochent ; 巴 donne le son.", "Claws 爪 gripping; 巴 gives the sound.", "Когти 爪 цепляются; 巴 даёт звук."),
        T3("griffe", "claw", "коготь"))
    m['山'] = CharInfo('山', "shān", 3, 1, "山", "凵丨",
        T3("montagne", "mountain", "гора"),
        T3("Trois sommets.", "Three peaks.", "Три вершины."),
        T3("montagne", "mountain", "гора"))
    m['级'] = CharInfo('级', "jí", 6, 3, "纟", "纟及",
        T3("niveau, classe", "level, grade", "уровень, класс"),
        T3("Un fil 纟 aux nœuds en étages ; 及 donne le son.", "A thread 纟 with knots in tiers; 及 gives the sound.", "Нить 纟 с узлами ярусами; 及 даёт звук."),
        T3("soie, fil", "silk, thread", "шёлк, нить"))
    m['板'] = CharInfo('板', "bǎn", 8, 3, "木", "木反",
        T3("planche", "board", "доска"),
        T3("Du bois 木 ; 反 donne le son.", "Wood 木; 反 gives the sound.", "Дерево 木; 反 даёт звук."),
        T3("arbre, bois", "tree, wood", "дерево"))
    m['记'] = CharInfo('记', "jì", 5, 2, "讠", "讠己",
        T3("noter, retenir", "to note, remember", "записывать, помнить"),
        T3("La parole 讠 qu'on garde pour soi 己.", "Speech 讠 kept for yourself 己.", "Речь 讠, сохранённая для себя 己."),
        T3("parole", "speech", "речь"))
    m['词'] = CharInfo('词', "cí", 7, 3, "讠", "讠司",
        T3("mot", "word", "слово"),
        T3("La parole 讠 ; 司 donne le son.", "Speech 讠; 司 gives the sound.", "Речь 讠; 司 даёт звук."),
        T3("parole", "speech", "речь"))
    m['典'] = CharInfo('典', "diǎn", 8, 2, "八", "八",
        T3("ouvrage de référence", "classic, canon", "канон, словарь"),
        T3("Deux mains 八 qui tiennent un livre.", "Two hands 八 holding a book.", "Две руки 八 держат книгу."),
        T3("huit, partager", "eight, divide", "восемь, делить"))
    m['业'] = CharInfo('业', "yè", 5, 0, "一", "一",
        T3("métier, études", "trade, studies", "дело, учёба"),
        T3("Un support à instruments de musique.", "A rack for musical instruments.", "Подставка для музыкальных инструментов."),
        T3("un", "one", "один"))
    m['复'] = CharInfo('复', "fù", 9, 2, "夊", "亻日夂",
        T3("répéter", "to repeat", "повторять"),
        T3("Aller 夊 au même endroit tous les jours 日.", "Going 夊 to the same place every day 日.", "Ходить 夊 в одно место каждый день 日."),
        T3("marcher", "walk slowly", "идти"))
    m['练'] = CharInfo('练', "liàn", 8, 3, "纟", "纟柬",
        T3("s'exercer", "to practise", "упражняться"),
        T3("Le fil 纟 qu'on travaille encore ; 柬 donne le son.", "Thread 纟 worked again and again; 柬 gives the sound.", "Нить 纟, которую обрабатывают снова; 柬 даёт звук."),
        T3("soie, fil", "silk, thread", "шёлк, нить"))
    m['成'] = CharInfo('成', "chéng", 6, 0, "戈", "丁戈",
        T3("accomplir, devenir", "to accomplish, become", "совершать, становиться"),
        T3("Une hallebarde 戈 : la tâche est accomplie.", "A halberd 戈: the job is done.", "Алебарда 戈: дело сделано."),
        T3("hallebarde", "halberd", "алебарда"))
    m['绩'] = CharInfo('绩', "jī", 11, 2, "纟", "纟责",
        T3("résultats", "achievements", "достижения"),
        T3("Le fil 纟 du travail accompli 责.", "The thread 纟 of duty done 责.", "Нить 纟 исполненного долга 责."),
        T3("soie, fil", "silk, thread", "шёлк, нить"))
    m['数'] = CharInfo('数', "shù", 13, 3, "攵", "娄攵",
        T3("nombre, compter", "number, to count", "число, считать"),
        T3("Une main 攵 qui compte ; 娄 donne le son.", "A hand 攵 counting; 娄 gives the sound.", "Рука 攵 считает; 娄 даёт звук."),
        T3("frapper, agir", "strike, act", "удар, действие"))
    m['历'] = CharInfo('历', "lì", 4, 3, "厂", "厂力",
        T3("passer ; calendrier", "to go through; calendar", "проходить; календарь"),
        T3("La force 力 du temps sous un abri 厂.", "The force 力 of time under a shelter 厂.", "Сила 力 времени под навесом 厂."),
        T3("abri, falaise", "shelter, cliff", "навес, утёс"))
    m['史'] = CharInfo('史', "shǐ", 5, 2, "口", "口",
        T3("histoire", "history", "история"),
        T3("Une main qui tient le pinceau du chroniqueur.", "A hand holding the chronicler's brush.", "Рука с кистью летописца."),
        T3("bouche", "mouth", "рот"))
    m['育'] = CharInfo('育', "yù", 8, 2, "月", "亠厶月",
        T3("élever, éduquer", "to raise, educate", "растить, воспитывать"),
        T3("Un enfant qui naît et qu'on nourrit 月.", "A child born and fed 月.", "Рождённый ребёнок, которого кормят 月."),
        T3("lune, chair", "moon, flesh", "луна, плоть"))
    m['平'] = CharInfo('平', "píng", 5, 1, "广", "干丷",
        T3("plat, calme", "flat, calm", "ровный, спокойный"),
        T3("Une balance bien à l'équilibre.", "A scale perfectly balanced.", "Весы в полном равновесии."),
        T3("abri", "shelter", "навес"))
    m['提'] = CharInfo('提', "tí", 12, 3, "扌", "扌是",
        T3("soulever", "to lift", "поднимать"),
        T3("La main 扌 qui lève ; 是 donne le son.", "The hand 扌 lifting; 是 gives the sound.", "Рука 扌 поднимает; 是 даёт звук."),
        T3("main", "hand", "рука"))
    m['努'] = CharInfo('努', "nǔ", 7, 3, "力", "奴力",
        T3("s'efforcer", "to strive", "стараться"),
        T3("La force 力 du serviteur 奴 qui travaille dur.", "The strength 力 of a hard-working servant 奴.", "Сила 力 усердного слуги 奴."),
        T3("force", "strength", "сила"))
    m['力'] = CharInfo('力', "lì", 2, 2, "力", "丿",
        T3("force", "strength", "сила"),
        T3("Un soc de charrue : la force.", "A plough blade: strength.", "Лемех плуга: сила."),
        T3("force", "strength", "сила"))
}

private fun part6(m: MutableMap<Char, CharInfo>) {
    m['音'] = CharInfo('音', "yīn", 9, 2, "音", "立日",
        T3("son", "sound", "звук"),
        T3("La langue 立 qui forme un son dans la bouche 日.", "The tongue 立 shaping a sound in the mouth 日.", "Язык 立 образует звук во рту 日."),
        T3("son", "sound", "звук"))
    m['戏'] = CharInfo('戏', "xì", 6, 2, "戈", "又戈",
        T3("jeu, théâtre", "play, show", "игра, спектакль"),
        T3("Une main 又 qui jongle avec une lance 戈.", "A hand 又 juggling a spear 戈.", "Рука 又 жонглирует копьём 戈."),
        T3("hallebarde", "halberd", "алебарда"))
    m['目'] = CharInfo('目', "mù", 5, 1, "目", "口二",
        T3("œil", "eye", "глаз"),
        T3("Un œil dessiné debout.", "An eye drawn upright.", "Глаз, нарисованный вертикально."),
        T3("œil", "eye", "глаз"))
    m['闻'] = CharInfo('闻', "wén", 9, 3, "门", "门耳",
        T3("entendre, nouvelle", "to hear, news", "слышать, новость"),
        T3("Une oreille 耳 à la porte 门.", "An ear 耳 at the door 门.", "Ухо 耳 у двери 门."),
        T3("porte", "door", "дверь"))
    m['网'] = CharInfo('网', "wǎng", 6, 1, "网", "冂",
        T3("filet, réseau", "net, network", "сеть"),
        T3("Un filet de pêche.", "A fishing net.", "Рыболовная сеть."),
        T3("filet", "net", "сеть"))
    m['聊'] = CharInfo('聊', "liáo", 11, 3, "耳", "耳卯",
        T3("bavarder", "to chat", "болтать"),
        T3("L'oreille 耳 qui écoute ; 卯 donne le son.", "The ear 耳 listening; 卯 gives the sound.", "Ухо 耳 слушает; 卯 даёт звук."),
        T3("oreille", "ear", "ухо"))
    m['画'] = CharInfo('画', "huà", 8, 2, "田", "一凵田",
        T3("dessiner, tableau", "to draw, painting", "рисовать, картина"),
        T3("Un tableau 田 sur son chevalet.", "A painting 田 on its easel.", "Картина 田 на мольберте."),
        T3("champ", "field", "поле"))
    m['故'] = CharInfo('故', "gù", 9, 3, "攵", "古攵",
        T3("ancien ; cause", "old; reason", "старый; причина"),
        T3("Une main 攵 qui touche le passé 古.", "A hand 攵 touching the past 古.", "Рука 攵 касается прошлого 古."),
        T3("frapper, agir", "strike, act", "удар, действие"))
    m['冰'] = CharInfo('冰', "bīng", 6, 2, "冫", "冫水",
        T3("glace", "ice", "лёд"),
        T3("L'eau 水 devenue froide 冫.", "Water 水 turned cold 冫.", "Вода 水, ставшая холодной 冫."),
        T3("glace", "ice", "лёд"))
    m['空'] = CharInfo('空', "kōng", 8, 3, "穴", "穴工",
        T3("vide ; ciel", "empty; sky", "пустой; небо"),
        T3("Le creux d'une grotte 穴 ; 工 donne le son.", "The hollow of a cave 穴; 工 gives the sound.", "Пустота пещеры 穴; 工 даёт звук."),
        T3("trou, grotte", "hole, cave", "дыра, пещера"))
    m['调'] = CharInfo('调', "diào", 10, 3, "讠", "讠周",
        T3("régler ; air", "to adjust; tune", "настраивать; мелодия"),
        T3("La parole 讠 qui accorde ; 周 donne le son.", "Speech 讠 that harmonises; 周 gives the sound.", "Речь 讠, которая настраивает; 周 даёт звук."),
        T3("parole", "speech", "речь"))
    m['灯'] = CharInfo('灯', "dēng", 6, 3, "火", "火丁",
        T3("lampe", "lamp", "лампа"),
        T3("Le feu 火 ; 丁 donne le son.", "Fire 火; 丁 gives the sound.", "Огонь 火; 丁 даёт звук."),
        T3("feu", "fire", "огонь"))
    m['扫'] = CharInfo('扫', "sǎo", 6, 2, "扌", "扌彐",
        T3("balayer", "to sweep", "подметать"),
        T3("La main 扌 qui tient le balai 彐.", "The hand 扌 holding the broom 彐.", "Рука 扌 держит метлу 彐."),
        T3("main", "hand", "рука"))
    m['安'] = CharInfo('安', "ān", 6, 2, "宀", "宀女",
        T3("paix", "peace", "спокойствие"),
        T3("Une femme 女 à l'abri sous le toit 宀.", "A woman 女 safe under the roof 宀.", "Женщина 女 в безопасности под крышей 宀."),
        T3("toit", "roof", "крыша"))
    m['静'] = CharInfo('静', "jìng", 14, 3, "青", "青争",
        T3("calme", "quiet", "тихий"),
        T3("La nature 青 au repos ; 争 donne le son.", "Nature 青 at rest; 争 gives the sound.", "Природа 青 в покое; 争 даёт звук."),
        T3("vert-bleu", "blue-green", "сине-зелёный"))
    m['邮'] = CharInfo('邮', "yóu", 7, 3, "阝", "由阝",
        T3("poste", "mail", "почта"),
        T3("Le relais 阝 ; 由 donne le son.", "The post station 阝; 由 gives the sound.", "Почтовая станция 阝; 由 даёт звук."),
        T3("ville, colline", "town, hill", "город, холм"))
    m['担'] = CharInfo('担', "dān", 8, 3, "扌", "扌旦",
        T3("porter (charge)", "to carry", "нести (груз)"),
        T3("La main 扌 qui porte ; 旦 donne le son.", "The hand 扌 carrying; 旦 gives the sound.", "Рука 扌 несёт; 旦 даёт звук."),
        T3("main", "hand", "рука"))
    m['心'] = CharInfo('心', "xīn", 4, 1, "心", "",
        T3("cœur", "heart", "сердце"),
        T3("Le dessin d'un cœur.", "The drawing of a heart.", "Рисунок сердца."),
        T3("cœur", "heart", "сердце"))
    m['害'] = CharInfo('害', "hài", 10, 0, "宀", "宀口",
        T3("nuire", "to harm", "вредить"),
        T3("Le mal qui entre dans la maison 宀.", "Harm entering the house 宀.", "Беда, вошедшая в дом 宀."),
        T3("toit", "roof", "крыша"))
    m['怕'] = CharInfo('怕', "pà", 8, 3, "忄", "忄白",
        T3("avoir peur", "to fear", "бояться"),
        T3("Le cœur 忄 qui pâlit 白.", "The heart 忄 turning white 白.", "Сердце 忄 бледнеет 白."),
        T3("cœur", "heart", "сердце"))
    m['难'] = CharInfo('难', "nán", 10, 2, "又", "又隹",
        T3("difficile", "difficult", "трудный"),
        T3("Une main 又 qui essaie d'attraper un oiseau 隹.", "A hand 又 trying to catch a bird 隹.", "Рука 又 пытается поймать птицу 隹."),
        T3("main", "hand", "рука"))
    m['满'] = CharInfo('满', "mǎn", 13, 3, "氵", "氵艹两",
        T3("plein", "full", "полный"),
        T3("L'eau 氵 jusqu'au bord ; 两 sous l'herbe.", "Water 氵 to the brim.", "Вода 氵 до краёв."),
        T3("eau", "water", "вода"))
    m['放'] = CharInfo('放', "fàng", 8, 3, "攵", "方攵",
        T3("lâcher, poser", "to release, put", "отпускать, класть"),
        T3("Une main 攵 qui laisse partir ; 方 donne le son.", "A hand 攵 letting go; 方 gives the sound.", "Рука 攵 отпускает; 方 даёт звук."),
        T3("frapper, agir", "strike, act", "удар, действие"))
    m['哭'] = CharInfo('哭', "kū", 10, 2, "口", "口口犬",
        T3("pleurer", "to cry", "плакать"),
        T3("Deux yeux 口口 qui pleurent sur un chien 犬.", "Two eyes 口口 weeping over a dog 犬.", "Два глаза 口口 плачут над собакой 犬."),
        T3("bouche", "mouth", "рот"))
    m['奇'] = CharInfo('奇', "qí", 8, 2, "大", "大可",
        T3("étrange", "strange", "странный"),
        T3("Une personne 大 sur un cheval 可 : drôle de vue.", "A person 大 on a horse 可: an odd sight.", "Человек 大 на лошади 可: странное зрелище."),
        T3("grand", "big", "большой"))
    m['怪'] = CharInfo('怪', "guài", 8, 0, "忄", "忄圣",
        T3("bizarre", "odd", "странный"),
        T3("Le cœur 忄 surpris ; 圣 donne le son.", "The heart 忄 surprised; 圣 gives the sound.", "Удивлённое сердце 忄; 圣 даёт звук."),
        T3("cœur", "heart", "сердце"))
    m['愿'] = CharInfo('愿', "yuàn", 14, 3, "心", "原心",
        T3("souhaiter", "to wish", "желать"),
        T3("Le cœur 心 ; 原 donne le son.", "The heart 心; 原 gives the sound.", "Сердце 心; 原 даёт звук."),
        T3("cœur", "heart", "сердце"))
    m['决'] = CharInfo('决', "jué", 6, 0, "冫", "冫夬",
        T3("trancher, décider", "to decide", "решать"),
        T3("L'eau 冫 qui rompt la digue 夬.", "Water 冫 breaking the dyke 夬.", "Вода 冫 прорывает плотину 夬."),
        T3("glace", "ice", "лёд"))
    m['定'] = CharInfo('定', "dìng", 8, 2, "宀", "宀疋",
        T3("fixer, décider", "to fix, settle", "устанавливать"),
        T3("Quelqu'un 疋 qui s'installe sous un toit 宀.", "Someone 疋 settling under a roof 宀.", "Кто-то 疋 поселился под крышей 宀."),
        T3("toit", "roof", "крыша"))
    m['惯'] = CharInfo('惯', "guàn", 11, 3, "忄", "忄贯",
        T3("habitude", "habit", "привычка"),
        T3("Le cœur 忄 enfilé 贯 dans la routine.", "The heart 忄 threaded 贯 into routine.", "Сердце 忄, нанизанное 贯 на рутину."),
        T3("cœur", "heart", "сердце"))
}
