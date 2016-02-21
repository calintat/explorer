package com.calintat.explorer;

import java.io.File;

public enum FileType
{
    DIRECTORY,MISC_FILE,

    AUDIO,IMAGE,VIDEO,

    DOC,PPT,XLS,

    PDF,TXT,ZIP;

    public static FileType getFileType(File file)
    {
        if(file.isDirectory()) return FileType.DIRECTORY;

        String mime=FileUtils.getMimeType(file);

        if(mime==null) return FileType.MISC_FILE;

        if(mime.startsWith("audio")) return FileType.AUDIO;

        if(mime.startsWith("image")) return FileType.IMAGE;

        if(mime.startsWith("video")) return FileType.VIDEO;

        if(mime.startsWith("application/ogg"))
            return FileType.AUDIO;


        if(mime.startsWith("application/msword"))
            return FileType.DOC;

        if(mime.startsWith("application/vnd.ms-word"))
            return FileType.DOC;

        if(mime.startsWith("application/vnd.openxmlformats-officedocument.wordprocessingml"))
            return FileType.DOC;

        if(mime.startsWith("application/vnd.ms-powerpoint"))
            return FileType.PPT;

        if(mime.startsWith("application/vnd.openxmlformats-officedocument.presentationml"))
            return FileType.PPT;

        if(mime.startsWith("application/vnd.ms-excel"))
            return FileType.XLS;

        if(mime.startsWith("application/vnd.openxmlformats-officedocument.spreadsheetml"))
            return FileType.XLS;

        if(mime.startsWith("application/pdf"))
            return FileType.PDF;

        if(mime.startsWith("text"))
            return FileType.TXT;

        if(mime.startsWith("application/zip"))
            return FileType.ZIP;

        return FileType.MISC_FILE;
    }
}
