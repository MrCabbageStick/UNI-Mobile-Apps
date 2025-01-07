package com.example.broadcasts

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

class BookDownloadService: Service() {

    private val handler = Handler()

    private var books = mutableListOf<String>()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MyService ----> ", "onStartCommand()")
        download()
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun download() {

        handler.postDelayed({

            Log.d("MyService ----> ", "downloading")

            val book = hardCodedBooks.random()

            val bookData = BookData(
                book.first,
                countLetters(book.second),
                countWords(book.second),
                findMostCommonWord(book.second) ?: "n/a"
            )

            val broadcastIntent = Intent("com.example.DATA_DOWNLOADED")
            bookDataToIntent(bookData, broadcastIntent)
            sendBroadcast(Intent(broadcastIntent))

            if(!checkNotificationPermission(this)){
                Toast.makeText(this, "⛔⛔⛔", Toast.LENGTH_SHORT).show()
            }
            else {
                postNotification(this)
            }

            stopSelf()

        }, 4000)
    }

    override fun onBind(p0: Intent?): IBinder? {
        Log.d("MyService ----> ", "onBind()")
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        Log.d("MyService ----> ", "onDestroy()")
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            return
        }

        val channelId = "default_channel"
        val channelName = "uni_app_notification_channel"
        val channelDescription = "Notification channel for uni apps"
        val channelImportance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(channelId, channelName, channelImportance).apply {
            description = channelDescription
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun checkNotificationPermission(context: Context): Boolean{
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            val permissionState = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )

            return permissionState == PackageManager.PERMISSION_GRANTED
        }

        return true
    }

    private fun countWords(text: String): Int{
        return text
            .filterNot { skippedSymbols.plus('\'').contains(it) }
            .split(" ")
            .size
    }

    private fun countLetters(text: String): Int{
        return text.filterNot { skippedSymbols.plus('\'').contains(it) }.length
    }

    private fun findMostCommonWord(text: String): String?{
        val words = text
            .filterNot { skippedSymbols.contains(it) }
            .split(" ")
            // Removes apostrophes
            .map { it.replace("'[a-z]".toRegex(), "") }
            .map { it.lowercase() }
            .filterNot { skippedWords.contains(it) }

        return words
            .groupingBy { it }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key
    }

    companion object{
        val hardCodedBooks = listOf(
            Pair("Great Expectations", "\"Great Expectations\" by Charles Dickens is a novel written in the mid-19th century (Victorian era). The story follows the life of a young orphan named Philip \"Pip\" Pirrip as he navigates social classes, personal aspirations, and the complexities of human relationships. The narrative begins with Pip's fateful encounter with an escaped convict, setting the stage for themes of ambition, morality, and transformation. The opening of the novel introduces Pip as he wanders through a churchyard, reflecting on his family history derived from tombstones. His innocent musings are interrupted by a terrifying confrontation with a convict who demands food and a file, instilling fear in Pip. As Pip grapples with the fear of being discovered stealing food for the convict and the horror of his surroundings, we are drawn into the bleak marshes that shape much of his childhood. This intense encounter not only establishes a sense of danger but also foreshadows Pip's future entanglements with crime and class disparity, as he later must navigate his relationships with figures from both the convict's world and his own lower-class upbringing. (This is an automatically generated summary.)"),
            Pair("The King in Yellow", "\"The King in Yellow\" by Robert W. Chambers is a collection of short stories written in the late 19th century. This work weaves together themes of madness, decay, and the supernatural, often revolving around a mysterious play that drives its readers to madness. The stories explore the lives of various characters, including the ambitious Hildred Castaigne, who becomes entangled with the dark influence of the titular King in Yellow, a character symbolizing despair and horror. At the start of \"The King in Yellow,\" we are introduced to a disquieting atmosphere set in a future America, where the government has established a 'Lethal Chamber' for those seeking voluntary death. The protagonist, Hildred Castaigne, reflects on his recent convalescence from a head injury and his obsession with a particular play, \"The King in Yellow.\" His fixation leads him to visit a deranged character named Mr. Wilde, a so-called \"Repairer of Reputations,\" who signifies the blurring line between sanity and madness. As Hildred interacts with the characters around him, including the romantic tension involving his cousin Louis and Constance, the story hints at a larger, ominous force that looms over their lives, foreshadowing the psychological and tragic consequences of their obsessions. (This is an automatically generated summary.)"),
            Pair("The Odyssey", "\"The Odyssey\" by Homer is an epic poem attributed to the ancient Greek poet, believed to have been composed in the late 8th century BC. This foundational work of Western literature chronicles the adventures of Odysseus, a clever hero whose journey home following the Trojan War is fraught with peril, delays, and divine intervention. The central narrative follows Odysseus' attempts to return to his wife, Penelope, and son, Telemachus, while grappling with the challenges posed by suitors in his absence. The opening portion of \"The Odyssey\" sets the stage for the epic tale by introducing the plight of its hero, Odysseus, who is trapped on the island of Ogygia by the goddess Calypso as he longs to return to Ithaca. The narrative begins with a divine council at Olympus, where the gods discuss Odysseus's fate, revealing their sympathy for him, especially from Athena. It quickly shifts to Ithaca, where Telemachus grapples with his father's absence and the disrespectful suitors devouring his household. Prompted by Athena, he resolves to seek news of Odysseus, embarking on a quest that propels him into a broader world of heroism, fate, and familial loyalty. (This is an automatically generated summary.)"),
            Pair("Some Christmas Stories", "\"Some Christmas Stories\" by Charles Dickens is a collection of short stories written during the mid-19th century. The book captures the spirit of Christmas through various narratives that reflect on childhood, nostalgia, family, and the meaning of the holiday season. The stories delve into themes of joy, sorrow, and the passage of time, often featuring characters that embody the essence of Christmas. The beginning of the book introduces readers to the first story, \"A Christmas Tree,\" where the narrator reflects on a delightful Christmas gathering with children around a beautifully decorated tree. The narrative depicts the enchantment of childhood, evoking vivid memories of toys and festivities that spark the imagination. As the narrator reminisces about their own Christmas tree and the toys that adorned it, we see an exploration of the transition from the innocence of youth to the complexities of adulthood, interspersed with elements of nostalgia and whimsy. The opening sets the tone for a rich emotional journey through the various stories that follow, encapsulating the warmth and reflections associated with the holiday season. (This is an automatically generated summary.)"),
            Pair("The Tragical History of Doctor Faustus", "\"The Tragical History of Doctor Faustus\" by Christopher Marlowe is a play that was likely written during the late 16th century. This dramatic work explores themes of ambition, desire, and the consequences of pursuing forbidden knowledge through the tragic story of its main character, Dr. Faustus, a scholar who seeks to gain unlimited knowledge and power by making a pact with the devil. The opening of the play introduces us to Dr. Faustus, who is disillusioned with traditional forms of academia. Despite his considerable knowledge in various fields, Faustus craves more and turns to necromancy in his quest for ultimate power. In his study, he debates the merits of different disciplines before ultimately deciding to delve into magic. He is soon joined by companions who encourage his pursuits, and we witness his internal conflict between good and evil as he is tempted by both a Good Angel and an Evil Angel. As Faustus embarks on his fateful journey, he prepares to conjure Mephistophilis, a demon who will fulfill his desires but at a dire cost. This complex interplay of ambition and moral choice sets the stage for Faustus's tragic fall. (This is an automatically generated summary.)"),
        );

        val skippedSymbols = listOf(',', '"', '(', ')')
        val skippedWords = listOf<String>("able","about","above","abroad","according","accordingly","across","actually","adj","after","afterwards","again","against","ago","ahead","ain't","all","allow","allows","almost","alone","along","alongside","already","also","although","always","am","amid","amidst","among","amongst","an","and","another","any","anybody","anyhow","anyone","anything","anyway","anyways","anywhere","apart","appear","appreciate","appropriate","are","aren't","around","as","a's","aside","ask","asking","associated","at","available","away","awfully","back","backward","backwards","be","became","because","become","becomes","becoming","been","before","beforehand","begin","behind","being","believe","below","beside","besides","best","better","between","beyond","both","brief","but","by","came","can","cannot","cant","can't","caption","cause","causes","certain","certainly","changes","clearly","c'mon","co","co.","com","come","comes","concerning","consequently","consider","considering","contain","containing","contains","corresponding","could","couldn't","course","c's","currently","dare","daren't","definitely","described","despite","did","didn't","different","directly","do","does","doesn't","doing","done","don't","down","downwards","during","each","edu","eg","eight","eighty","either","else","elsewhere","end","ending","enough","entirely","especially","et","etc","even","ever","evermore","every","everybody","everyone","everything","everywhere","ex","exactly","example","except","fairly","far","farther","few","fewer","fifth","first","five","followed","following","follows","for","forever","former","formerly","forth","forward","found","four","from","further","furthermore","get","gets","getting","given","gives","go","goes","going","gone","got","gotten","greetings","had","hadn't","half","happens","hardly","has","hasn't","have","haven't","having","he","he'd","he'll","hello","help","hence","her","here","hereafter","hereby","herein","here's","hereupon","hers","herself","he's","hi","him","himself","his","hither","hopefully","how","howbeit","however","hundred","i'd","ie","if","ignored","i'll","i'm","immediate","in","inasmuch","inc","inc.","indeed","indicate","indicated","indicates","inner","inside","insofar","instead","into","inward","is","isn't","it","it'd","it'll","its","it's","itself","i've","just","k","keep","keeps","kept","know","known","knows","last","lately","later","latter","latterly","least","less","lest","let","let's","like","liked","likely","likewise","little","look","looking","looks","low","lower","ltd","made","mainly","make","makes","many","may","maybe","mayn't","me","mean","meantime","meanwhile","merely","might","mightn't","mine","minus","miss","more","moreover","most","mostly","mr","mrs","much","must","mustn't","my","myself","name","namely","nd","near","nearly","necessary","need","needn't","needs","neither","never","neverf","neverless","nevertheless","new","next","nine","ninety","no","nobody","non","none","nonetheless","noone","no-one","nor","normally","not","nothing","notwithstanding","novel","now","nowhere","obviously","of","off","often","oh","ok","okay","old","on","once","one","ones","one's","only","onto","opposite","or","other","others","otherwise","ought","oughtn't","our","ours","ourselves","out","outside","over","overall","own","particular","particularly","past","per","perhaps","placed","please","plus","possible","presumably","probably","provided","provides","que","quite","qv","rather","rd","re","really","reasonably","recent","recently","regarding","regardless","regards","relatively","respectively","right","round","said","same","saw","say","saying","says","second","secondly","see","seeing","seem","seemed","seeming","seems","seen","self","selves","sensible","sent","serious","seriously","seven","several","shall","shan't","she","she'd","she'll","she's","should","shouldn't","since","six","so","some","somebody","someday","somehow","someone","something","sometime","sometimes","somewhat","somewhere","soon","sorry","specified","specify","specifying","still","sub","such","sup","sure","take","taken","taking","tell","tends","th","than","thank","thanks","thanx","that","that'll","thats","that's","that've","the","their","theirs","them","themselves","then","thence","there","thereafter","thereby","there'd","therefore","therein","there'll","there're","theres","there's","thereupon","there've","these","they","they'd","they'll","they're","they've","thing","things","think","third","thirty","this","thorough","thoroughly","those","though","three","through","throughout","thru","thus","till","to","together","too","took","toward","towards","tried","tries","truly","try","trying","t's","twice","two","un","under","underneath","undoing","unfortunately","unless","unlike","unlikely","until","unto","up","upon","upwards","us","use","used","useful","uses","using","usually","v","value","various","versus","very","via","viz","vs","want","wants","was","wasn't","way","we","we'd","welcome","well","we'll","went","were","we're","weren't","we've","what","whatever","what'll","what's","what've","when","whence","whenever","where","whereafter","whereas","whereby","wherein","where's","whereupon","wherever","whether","which","whichever","while","whilst","whither","who","who'd","whoever","whole","who'll","whom","whomever","who's","whose","why","will","willing","wish","with","within","without","wonder","won't","would","wouldn't","yes","yet","you","you'd","you'll","your","you're","yours","yourself","yourselves","you've","zero","a","how's","i","when's","why's","b","c","d","e","f","g","h","j","l","m","n","o","p","q","r","s","t","u","uucp","w","x","y","z","I","www","amount","bill","bottom","call","computer","con","couldnt","cry","de","describe","detail","due","eleven","empty","fifteen","fifty","fill","find","fire","forty","front","full","give","hasnt","herse","himse","interest","itse”","mill","move","myse”","part","put","show","side","sincere","sixty","system","ten","thick","thin","top","twelve","twenty","abst","accordance","act","added","adopted","affected","affecting","affects","ah","announce","anymore","apparently","approximately","aren","arent","arise","auth","beginning","beginnings","begins","biol","briefly","ca","date","ed","effect","et-al","ff","fix","gave","giving","heres","hes","hid","home","id","im","immediately","importance","important","index","information","invention","itd","keys","kg","km","largely","lets","line","'ll","means","mg","million","ml","mug","na","nay","necessarily","nos","noted","obtain","obtained","omitted","ord","owing","page","pages","poorly","possibly","potentially","pp","predominantly","present","previously","primarily","promptly","proud","quickly","ran","readily","ref","refs","related","research","resulted","resulting","results","run","sec","section","shed","shes","showed","shown","showns","shows","significant","significantly","similar","similarly","slightly","somethan","specifically","state","states","stop","strongly","substantially","successfully","sufficiently","suggest","thered","thereof","therere","thereto","theyd","theyre","thou","thoughh","thousand","throug","til","tip","ts","ups","usefully","usefulness","'ve","vol","vols","wed","whats","wheres","whim","whod","whos","widely","words","world","youd","youre")
    }
}