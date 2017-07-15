package com.calintat.explorer.utils

import android.content.Context
import android.content.CursorLoader
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.format.DateFormat
import android.text.format.Formatter
import android.webkit.MimeTypeMap

import com.calintat.explorer.R

import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel
import java.util.ArrayList
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

object FileUtils {

    @Throws(Exception::class)
    fun copyFile(src: File, path: File): File {

        try {

            if (src.isDirectory) {

                if (src.path == path.path) throw Exception()

                val directory = createDirectory(path, src.name)

                for (file in src.listFiles()) copyFile(file, directory)

                return directory
            } else {

                val file = File(path, src.name)

                val channel = FileInputStream(src).channel

                channel.transferTo(0, channel.size(), FileOutputStream(file).channel)

                return file
            }
        } catch (e: Exception) {

            throw Exception(String.format("Error copying %s", src.name))
        }

    }

    //----------------------------------------------------------------------------------------------

    @Throws(Exception::class)
    fun createDirectory(path: File, name: String): File {

        val directory = File(path, name)

        if (directory.mkdirs()) return directory

        if (directory.exists()) throw Exception(String.format("%s already exists", name))

        throw Exception(String.format("Error creating %s", name))
    }

    @Throws(Exception::class)
    fun deleteFile(file: File): File {

        if (file.isDirectory) {

            for (child in file.listFiles()) {

                deleteFile(child)
            }
        }

        if (file.delete()) return file

        throw Exception(String.format("Error deleting %s", file.name))
    }

    @Throws(Exception::class)
    fun renameFile(file: File, name: String): File {

        var name = name

        val extension = getExtension(file.name)

        if (!extension.isEmpty()) name += "." + extension

        val newFile = File(file.parent, name)

        if (file.renameTo(newFile)) return newFile

        throw Exception(String.format("Error renaming %s", file.name))
    }

//    @Throws(Exception::class)
//    fun unzip(zip: File): File {
//
//        val directory = createDirectory(zip.parentFile, removeExtension(zip.name))
//
//        val fileInputStream = FileInputStream(zip)
//
//        val bufferedInputStream = BufferedInputStream(fileInputStream)
//
//        ZipInputStream(bufferedInputStream).use { zipInputStream ->
//
//            var zipEntry: ZipEntry
//
//            while ((zipEntry = zipInputStream.nextEntry) != null) {
//
//                val buffer = ByteArray(1024)
//
//                val file = File(directory, zipEntry.name)
//
//                if (zipEntry.isDirectory) {
//
//                    if (!file.mkdirs()) throw Exception("Error uncompressing")
//                } else {
//
//                    var count: Int
//
//                    FileOutputStream(file).use { fileOutputStream ->
//
//                        while ((count = zipInputStream.read(buffer)) != -1) {
//
//                            fileOutputStream.write(buffer, 0, count)
//                        }
//                    }
//                }
//            }
//        }
//
//        return directory
//    }


    //returns the path to the internal storage
    val internalStorage: File
        get() = Environment.getExternalStorageDirectory()

    //----------------------------------------------------------------------------------------------

    //returns the path to the external storage or null if it doesn't exist
    val externalStorage: File?
        get() {

            val path = System.getenv("SECONDARY_STORAGE")

            return if (path != null) File(path) else null
        }

    fun getPublicDirectory(type: String): File {

        //returns the path to the public directory of the given type

        return Environment.getExternalStoragePublicDirectory(type)
    }

    fun getAlbum(file: File): String? {

        try {

            val retriever = MediaMetadataRetriever()

            retriever.setDataSource(file.path)

            return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
        } catch (e: Exception) {

            return null
        }

    }

    //----------------------------------------------------------------------------------------------

    fun getArtist(file: File): String? {

        try {

            val retriever = MediaMetadataRetriever()

            retriever.setDataSource(file.path)

            return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
        } catch (e: Exception) {

            return null
        }

    }

    fun getDuration(file: File): String? {

        try {

            val retriever = MediaMetadataRetriever()

            retriever.setDataSource(file.path)

            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)

            val milliseconds = java.lang.Long.parseLong(duration)

            val s = milliseconds / 1000 % 60

            val m = milliseconds / 1000 / 60 % 60

            val h = milliseconds / 1000 / 60 / 60 % 24

            if (h == 0L) return String.format(Locale.getDefault(), "%02d:%02d", m, s)

            return String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s)
        } catch (e: Exception) {

            return null
        }

    }

    fun getLastModified(file: File): String {

        //returns the last modified date of the given file as a formatted string

        return DateFormat.format("dd MMM yyy", Date(file.lastModified())).toString()
    }

    fun getMimeType(file: File): String? {

        //returns the mime type for the given file or null iff there is none

        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(getExtension(file.name))
    }

    fun getName(file: File): String {

        //returns the name of the file hiding extensions of known file types

        when (FileType.getFileType(file)) {

            FileUtils.FileType.DIRECTORY -> return file.name

            FileUtils.FileType.MISC_FILE -> return file.name

            else -> return removeExtension(file.name)
        }
    }

    fun getPath(file: File?): String? {

        //returns the path of the given file or null if the file is null

        return file?.path
    }

    fun getSize(context: Context, file: File): String? {

        if (file.isDirectory) {

            val children = getChildren(file) ?: return null

            return String.format("%s items", children.size)
        } else {

            return Formatter.formatShortFileSize(context, file.length())
        }
    }

    fun getStorageUsage(context: Context): String {

        val internal = internalStorage

        val external = externalStorage

        var f = internal.freeSpace

        var t = internal.totalSpace

        if (external != null) {

            f += external.freeSpace

            t += external.totalSpace
        }

        val use = Formatter.formatShortFileSize(context, t - f)

        val tot = Formatter.formatShortFileSize(context, t)

        return String.format("%s used of %s", use, tot)
    }

    fun getTitle(file: File): String? {

        try {

            val retriever = MediaMetadataRetriever()

            retriever.setDataSource(file.path)

            return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        } catch (e: Exception) {

            return null
        }

    }

    private fun getExtension(filename: String): String {

        //returns the file extension or an empty string iff there is no extension

        return if (filename.contains(".")) filename.substring(filename.lastIndexOf(".") + 1) else ""
    }

    //----------------------------------------------------------------------------------------------

    fun removeExtension(filename: String): String {

        val index = filename.lastIndexOf(".")

        return if (index != -1) filename.substring(0, index) else filename
    }

    fun compareDate(file1: File, file2: File): Int {

        val lastModified1 = file1.lastModified()

        val lastModified2 = file2.lastModified()

        return java.lang.Long.compare(lastModified2, lastModified1)
    }

    //----------------------------------------------------------------------------------------------

    fun compareName(file1: File, file2: File): Int {

        val name1 = file1.name

        val name2 = file2.name

        return name1.compareTo(name2, ignoreCase = true)
    }

    fun compareSize(file1: File, file2: File): Int {

        val length1 = file1.length()

        val length2 = file2.length()

        return java.lang.Long.compare(length2, length1)
    }

    fun getColorResource(file: File): Int {

        when (FileType.getFileType(file)) {

            FileUtils.FileType.DIRECTORY -> return R.color.directory

            FileUtils.FileType.MISC_FILE -> return R.color.misc_file

            FileUtils.FileType.AUDIO -> return R.color.audio

            FileUtils.FileType.IMAGE -> return R.color.image

            FileUtils.FileType.VIDEO -> return R.color.video

            FileUtils.FileType.DOC -> return R.color.doc

            FileUtils.FileType.PPT -> return R.color.ppt

            FileUtils.FileType.XLS -> return R.color.xls

            FileUtils.FileType.PDF -> return R.color.pdf

            FileUtils.FileType.TXT -> return R.color.txt

            FileUtils.FileType.ZIP -> return R.color.zip

            else -> return 0
        }
    }

    //----------------------------------------------------------------------------------------------

    fun getImageResource(file: File): Int {

        when (FileType.getFileType(file)) {

            FileUtils.FileType.DIRECTORY -> return R.drawable.ic_directory

            FileUtils.FileType.MISC_FILE -> return R.drawable.ic_misc_file

            FileUtils.FileType.AUDIO -> return R.drawable.ic_audio

            FileUtils.FileType.IMAGE -> return R.drawable.ic_image

            FileUtils.FileType.VIDEO -> return R.drawable.ic_video

            FileUtils.FileType.DOC -> return R.drawable.ic_doc

            FileUtils.FileType.PPT -> return R.drawable.ic_ppt

            FileUtils.FileType.XLS -> return R.drawable.ic_xls

            FileUtils.FileType.PDF -> return R.drawable.ic_pdf

            FileUtils.FileType.TXT -> return R.drawable.ic_txt

            FileUtils.FileType.ZIP -> return R.drawable.ic_zip

            else -> return 0
        }
    }

    fun isStorage(dir: File?): Boolean {

        return dir == null || dir == internalStorage || dir == externalStorage
    }

    //----------------------------------------------------------------------------------------------

    fun getChildren(directory: File): Array<File>? {

        if (!directory.canRead()) return null

        return directory.listFiles { pathname -> pathname.exists() && !pathname.isHidden }
    }

    //----------------------------------------------------------------------------------------------

    fun getAudioLibrary(context: Context): ArrayList<File> {

        val list = ArrayList<File>()

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val data = arrayOf(MediaStore.Audio.Media.DATA)

        val selection = MediaStore.Audio.Media.IS_MUSIC

        val cursor = CursorLoader(context, uri, data, selection, null, null).loadInBackground()

        if (cursor != null) {

            while (cursor.moveToNext()) {

                val file = File(cursor.getString(cursor.getColumnIndex(data[0])))

                if (file.exists()) list.add(file)
            }

            cursor.close()
        }

        return list
    }

    //----------------------------------------------------------------------------------------------

    fun getImageLibrary(context: Context): ArrayList<File> {

        val list = ArrayList<File>()

        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val data = arrayOf(MediaStore.Images.Media.DATA)

        val cursor = CursorLoader(context, uri, data, null, null, null).loadInBackground()

        if (cursor != null) {

            while (cursor.moveToNext()) {

                val file = File(cursor.getString(cursor.getColumnIndex(data[0])))

                if (file.exists()) list.add(file)
            }

            cursor.close()
        }

        return list
    }

    fun getVideoLibrary(context: Context): ArrayList<File> {

        val list = ArrayList<File>()

        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

        val data = arrayOf(MediaStore.Video.Media.DATA)

        val cursor = CursorLoader(context, uri, data, null, null, null).loadInBackground()

        if (cursor != null) {

            while (cursor.moveToNext()) {

                val file = File(cursor.getString(cursor.getColumnIndex(data[0])))

                if (file.exists()) list.add(file)
            }

            cursor.close()
        }

        return list
    }

    fun searchFilesName(context: Context, name: String): ArrayList<File> {

        val list = ArrayList<File>()

        val uri = MediaStore.Files.getContentUri("external")

        val data = arrayOf(MediaStore.Files.FileColumns.DATA)

        val cursor = CursorLoader(context, uri, data, null, null, null).loadInBackground()

        if (cursor != null) {

            while (cursor.moveToNext()) {

                val file = File(cursor.getString(cursor.getColumnIndex(data[0])))

                if (file.exists() && file.name.startsWith(name)) list.add(file)
            }

            cursor.close()
        }

        return list
    }

    enum class FileType {

        DIRECTORY, MISC_FILE, AUDIO, IMAGE, VIDEO, DOC, PPT, XLS, PDF, TXT, ZIP;


        companion object {

            fun getFileType(file: File): FileType {

                if (file.isDirectory)
                    return FileType.DIRECTORY

                val mime = FileUtils.getMimeType(file) ?: return FileType.MISC_FILE

                if (mime.startsWith("audio"))
                    return FileType.AUDIO

                if (mime.startsWith("image"))
                    return FileType.IMAGE

                if (mime.startsWith("video"))
                    return FileType.VIDEO

                if (mime.startsWith("application/ogg"))
                    return FileType.AUDIO

                if (mime.startsWith("application/msword"))
                    return FileType.DOC

                if (mime.startsWith("application/vnd.ms-word"))
                    return FileType.DOC

                if (mime.startsWith("application/vnd.ms-powerpoint"))
                    return FileType.PPT

                if (mime.startsWith("application/vnd.ms-excel"))
                    return FileType.XLS

                if (mime.startsWith("application/vnd.openxmlformats-officedocument.wordprocessingml"))
                    return FileType.DOC

                if (mime.startsWith("application/vnd.openxmlformats-officedocument.presentationml"))
                    return FileType.PPT

                if (mime.startsWith("application/vnd.openxmlformats-officedocument.spreadsheetml"))
                    return FileType.XLS

                if (mime.startsWith("application/pdf"))
                    return FileType.PDF

                if (mime.startsWith("text"))
                    return FileType.TXT

                if (mime.startsWith("application/zip"))
                    return FileType.ZIP

                return FileType.MISC_FILE
            }
        }
    }
}