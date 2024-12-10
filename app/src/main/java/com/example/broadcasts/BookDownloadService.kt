package com.example.broadcasts

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log

class BookDownloadService: Service() {

    companion object{
        val hardCodedBooks = listOf(
            Pair("Great Expectations", "\"Great Expectations\" by Charles Dickens is a novel written in the mid-19th century (Victorian era). The story follows the life of a young orphan named Philip \"Pip\" Pirrip as he navigates social classes, personal aspirations, and the complexities of human relationships. The narrative begins with Pip's fateful encounter with an escaped convict, setting the stage for themes of ambition, morality, and transformation. The opening of the novel introduces Pip as he wanders through a churchyard, reflecting on his family history derived from tombstones. His innocent musings are interrupted by a terrifying confrontation with a convict who demands food and a file, instilling fear in Pip. As Pip grapples with the fear of being discovered stealing food for the convict and the horror of his surroundings, we are drawn into the bleak marshes that shape much of his childhood. This intense encounter not only establishes a sense of danger but also foreshadows Pip's future entanglements with crime and class disparity, as he later must navigate his relationships with figures from both the convict's world and his own lower-class upbringing. (This is an automatically generated summary.)"),
            Pair("The King in Yellow", "\"The King in Yellow\" by Robert W. Chambers is a collection of short stories written in the late 19th century. This work weaves together themes of madness, decay, and the supernatural, often revolving around a mysterious play that drives its readers to madness. The stories explore the lives of various characters, including the ambitious Hildred Castaigne, who becomes entangled with the dark influence of the titular King in Yellow, a character symbolizing despair and horror. At the start of \"The King in Yellow,\" we are introduced to a disquieting atmosphere set in a future America, where the government has established a 'Lethal Chamber' for those seeking voluntary death. The protagonist, Hildred Castaigne, reflects on his recent convalescence from a head injury and his obsession with a particular play, \"The King in Yellow.\" His fixation leads him to visit a deranged character named Mr. Wilde, a so-called \"Repairer of Reputations,\" who signifies the blurring line between sanity and madness. As Hildred interacts with the characters around him, including the romantic tension involving his cousin Louis and Constance, the story hints at a larger, ominous force that looms over their lives, foreshadowing the psychological and tragic consequences of their obsessions. (This is an automatically generated summary.)"),
            Pair("The Odyssey", "\"The Odyssey\" by Homer is an epic poem attributed to the ancient Greek poet, believed to have been composed in the late 8th century BC. This foundational work of Western literature chronicles the adventures of Odysseus, a clever hero whose journey home following the Trojan War is fraught with peril, delays, and divine intervention. The central narrative follows Odysseus' attempts to return to his wife, Penelope, and son, Telemachus, while grappling with the challenges posed by suitors in his absence. The opening portion of \"The Odyssey\" sets the stage for the epic tale by introducing the plight of its hero, Odysseus, who is trapped on the island of Ogygia by the goddess Calypso as he longs to return to Ithaca. The narrative begins with a divine council at Olympus, where the gods discuss Odysseus's fate, revealing their sympathy for him, especially from Athena. It quickly shifts to Ithaca, where Telemachus grapples with his father's absence and the disrespectful suitors devouring his household. Prompted by Athena, he resolves to seek news of Odysseus, embarking on a quest that propels him into a broader world of heroism, fate, and familial loyalty. (This is an automatically generated summary.)"),
            Pair("Some Christmas Stories", "\"Some Christmas Stories\" by Charles Dickens is a collection of short stories written during the mid-19th century. The book captures the spirit of Christmas through various narratives that reflect on childhood, nostalgia, family, and the meaning of the holiday season. The stories delve into themes of joy, sorrow, and the passage of time, often featuring characters that embody the essence of Christmas. The beginning of the book introduces readers to the first story, \"A Christmas Tree,\" where the narrator reflects on a delightful Christmas gathering with children around a beautifully decorated tree. The narrative depicts the enchantment of childhood, evoking vivid memories of toys and festivities that spark the imagination. As the narrator reminisces about their own Christmas tree and the toys that adorned it, we see an exploration of the transition from the innocence of youth to the complexities of adulthood, interspersed with elements of nostalgia and whimsy. The opening sets the tone for a rich emotional journey through the various stories that follow, encapsulating the warmth and reflections associated with the holiday season. (This is an automatically generated summary.)"),
            Pair("The Tragical History of Doctor Faustus", "\"The Tragical History of Doctor Faustus\" by Christopher Marlowe is a play that was likely written during the late 16th century. This dramatic work explores themes of ambition, desire, and the consequences of pursuing forbidden knowledge through the tragic story of its main character, Dr. Faustus, a scholar who seeks to gain unlimited knowledge and power by making a pact with the devil. The opening of the play introduces us to Dr. Faustus, who is disillusioned with traditional forms of academia. Despite his considerable knowledge in various fields, Faustus craves more and turns to necromancy in his quest for ultimate power. In his study, he debates the merits of different disciplines before ultimately deciding to delve into magic. He is soon joined by companions who encourage his pursuits, and we witness his internal conflict between good and evil as he is tempted by both a Good Angel and an Evil Angel. As Faustus embarks on his fateful journey, he prepares to conjure Mephistophilis, a demon who will fulfill his desires but at a dire cost. This complex interplay of ambition and moral choice sets the stage for Faustus's tragic fall. (This is an automatically generated summary.)"),
        );
    }

    private val handler = Handler()
    private var currentBook = 0
    private val total = hardCodedBooks.size

    private var books = mutableListOf<String>()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MyService ----> ", "onStartCommand()")
        download()
        return START_STICKY
    }

    private fun download(){
        if(currentBook < total) {
            Log.d("MyService ----> ", "downloading: $currentBook")
            handler.postDelayed({download()}, 1000)

            books.add(hardCodedBooks[currentBook].first)
            books.add(hardCodedBooks[currentBook].second)
            currentBook++;
        }
        else{
            Log.d("MyService ----> ", "Downloaded all")
            val broadcastIntent = Intent("com.example.DATA_DOWNLOADED")
            broadcastIntent.putStringArrayListExtra("BOOKS", ArrayList(books))
            sendBroadcast(Intent(broadcastIntent))
            stopSelf()
        }
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

}