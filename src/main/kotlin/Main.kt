import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.nugsky.Config
import org.json.JSONArray
import org.json.JSONObject
import java.io.DataOutputStream
import java.io.FileReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

/**
 * should provide 1 argument which is the name of file contains config represents {@link com.nugsky.Config}
 */
fun main(args: Array<String>) {
    if(args.size<1){
        println("should provide 1 argument which is the name of file contains config represents {@link com.nugsky.Config}")
        System.exit(1)
    }
    val gson = Gson()
    val jsonReader = JsonReader(FileReader(args[0]))
    val config:Config = gson.fromJson(jsonReader,Config::class.java)
    var res = URL(config.newsApiUrl).readText()
    var jsonRes = JSONObject(res)
    var jsonArticles = jsonRes.getJSONArray("articles")

    var embedsJson = JSONArray()

    var c=0
    for (article in jsonArticles) {
        c++
        article as JSONObject
        var embedJson = JSONObject()
        embedJson.put(
            "author",
            JSONObject().put("name", if (article.isNull("author")) "n/a" else article.getString("author"))
        )
        embedJson.put("title", article.getString("title"))
        embedJson.put("url", article.getString("url"))
        embedJson.put("description", article.getString("description"))
        embedJson.put("thumbnail", JSONObject().put("url", article.getString("urlToImage")))
        embedsJson.put(embedJson)
        if(c%10==0){
            for(webhookUrl in config.webhookUrls){
                pushToChat(embedsJson,webhookUrl)
            }
            embedsJson = JSONArray()
        }
    }
    if(!embedsJson.isEmpty){
        for(webhookUrl in config.webhookUrls){
            pushToChat(embedsJson,webhookUrl)
        }
    }
    println("done at ${Date()}")
}

fun pushToChat(embeds:JSONArray, webhookUrl:String) {
    println(embeds)
    val jsonReq = JSONObject().put("embeds", embeds)
    val url = URL(webhookUrl)
    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "POST"
    connection.connectTimeout = 300000
    connection.doOutput = true


    val postData: ByteArray = jsonReq.toString().toByteArray(Charsets.UTF_8)

    connection.setRequestProperty("charset", "utf-8")
    connection.setRequestProperty("Content-lenght", postData.size.toString())
    connection.setRequestProperty("Content-Type", "application/json")

    try {
        val outputStream = DataOutputStream(connection.outputStream)
        outputStream.write(postData)
        outputStream.flush()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    println("push to chat returns ${connection.responseCode}")
    connection.disconnect()
}