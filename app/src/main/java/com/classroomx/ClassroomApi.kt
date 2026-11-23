package com.classroomx

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

data class ClassroomCourse(
    val id: String,
    val name: String,
    val section: String?,
    val description: String?,
    val room: String?,
    val url: String?
)

data class ClassroomAttachment(
    val title: String,
    val url: String?,
    val type: String
)

data class ClassroomWork(
    val id: String,
    val title: String,
    val description: String?,
    val due: String?,
    val state: String?,
    val materials: List<ClassroomAttachment>
)

data class ClassroomStreamItem(
    val title: String,
    val body: String?,
    val type: String,
    val materials: List<ClassroomAttachment>
)

class ClassroomApi(private val accessToken: String) {
    suspend fun courses(): List<ClassroomCourse> = withContext(Dispatchers.IO) {
        list("courses", "courses", "courseStates=ACTIVE&pageSize=100").map { item ->
            ClassroomCourse(
                id = item.optString("id"),
                name = item.optString("name", "Без названия"),
                section = item.optString("section").takeIf(String::isNotBlank),
                description = item.optString("description").takeIf(String::isNotBlank),
                room = item.optString("room").takeIf(String::isNotBlank),
                url = item.optString("alternateLink").takeIf(String::isNotBlank)
            )
        }
    }

    suspend fun coursework(courseId: String): List<ClassroomWork> = withContext(Dispatchers.IO) {
        list("courses/$courseId/courseWork", "courseWork", "pageSize=100").map { item ->
            ClassroomWork(
                id = item.optString("id"),
                title = item.optString("title", "Задание"),
                description = item.optString("description").takeIf(String::isNotBlank),
                due = if (item.has("dueDate")) {
                    val date = item.getJSONObject("dueDate")
                    "%04d-%02d-%02d".format(date.optInt("year"), date.optInt("month"), date.optInt("day"))
                } else null,
                state = item.optString("state").takeIf(String::isNotBlank),
                materials = attachments(item.optJSONArray("materials"))
            )
        }
    }

    suspend fun materials(courseId: String): List<ClassroomStreamItem> = withContext(Dispatchers.IO) {
        list("courses/$courseId/courseWorkMaterials", "courseWorkMaterial", "pageSize=100").map { item ->
            ClassroomStreamItem(
                title = item.optString("title", "Материал"),
                body = item.optString("description").takeIf(String::isNotBlank),
                type = "Материал",
                materials = attachments(item.optJSONArray("materials"))
            )
        }
    }

    suspend fun announcements(courseId: String): List<ClassroomStreamItem> = withContext(Dispatchers.IO) {
        list("courses/$courseId/announcements", "announcements", "pageSize=100").map { item ->
            ClassroomStreamItem(
                title = "Объявление",
                body = item.optString("text").takeIf(String::isNotBlank),
                type = "Объявление",
                materials = attachments(item.optJSONArray("materials"))
            )
        }
    }

    private fun attachments(array: org.json.JSONArray?): List<ClassroomAttachment> {
        if (array == null) return emptyList()
        return (0 until array.length()).mapNotNull { index ->
            val material = array.optJSONObject(index) ?: return@mapNotNull null
            when {
                material.has("driveFile") -> {
                    val file = material.getJSONObject("driveFile").optJSONObject("driveFile") ?: JSONObject()
                    ClassroomAttachment(file.optString("title", "Файл"), file.optString("alternateLink").takeIf(String::isNotBlank), "Google Drive")
                }
                material.has("link") -> {
                    val link = material.getJSONObject("link")
                    ClassroomAttachment(link.optString("title", link.optString("url", "Ссылка")), link.optString("url").takeIf(String::isNotBlank), "Ссылка")
                }
                material.has("youtubeVideo") -> {
                    val video = material.getJSONObject("youtubeVideo")
                    ClassroomAttachment(video.optString("title", "YouTube"), video.optString("alternateLink").takeIf(String::isNotBlank), "YouTube")
                }
                else -> null
            }
        }
    }

    private fun list(path: String, key: String, query: String): List<JSONObject> {
        val result = mutableListOf<JSONObject>()
        var pageToken: String? = null
        do {
            val suffix = buildString {
                append(query)
                if (pageToken != null) append("&pageToken=").append(encode(pageToken!!))
            }
            val json = get("https://classroom.googleapis.com/v1/$path?$suffix")
            val array = json.optJSONArray(key) ?: org.json.JSONArray()
            for (i in 0 until array.length()) array.optJSONObject(i)?.let(result::add)
            pageToken = json.optString("nextPageToken").takeIf(String::isNotBlank)
        } while (pageToken != null)
        result
    }

    private fun get(url: String): JSONObject {
        val connection = URL(url).openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 15000
            connection.readTimeout = 20000
            connection.setRequestProperty("Authorization", "Bearer $accessToken")
            connection.setRequestProperty("Accept", "application/json")
            val status = connection.responseCode
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
            val body = stream.bufferedReader().use { it.readText() }
            if (status !in 200..299) {
                val message = runCatching { JSONObject(body).optJSONObject("error")?.optString("message") }.getOrNull()
                throw IllegalStateException(message?.takeIf(String::isNotBlank) ?: "Google Classroom API вернул HTTP $status")
            }
            return JSONObject(body)
        } finally {
            connection.disconnect()
        }
    }

    private fun encode(value: String): String = URLEncoder.encode(value, Charsets.UTF_8.name())
}
