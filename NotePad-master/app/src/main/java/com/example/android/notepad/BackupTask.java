package com.example.android.notepad;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Date;

import java.text.SimpleDateFormat;

public class BackupTask {
    private static final String COMMAND_BACKUP = "backupDatabase";
    private static final String COMMAND_RESTORE = "restoreDatabase";
    public String backup_version;
    @SuppressLint("StaticFieldLeak")
    private Context mContext;

    public BackupTask(Context context) {
        this.mContext = context;
    }


    public String doInBackground(String command) {
        //File dbFile = mContext.getDatabasePath("note_pad.db");// 路径是 /data/user/0/(包名)/databases/*
        //File dbFile =  mContext.getFileDirs("note_pad");
        //Log.i("myLog", "getDatabasePath路径："+mContext.getDatabasePath("note_pad.db"));

        //这个好像是安卓新版本，修改了这个方法，以前版本getDatabasePath直接指向/data/data
        //现在该路径可能是为了适用多用户的，其中的0就是用户号，相当于对/data/data的一个链接
        //但是这里我要修改的是/data/data
        Log.i("myLog", "包名："+mContext.getPackageName());
        File dbFile = new File("/data/data/"+mContext.getPackageName()+"/databases/note_pad.db");//路径 /data/data
        //Log.i("myLog", "dbFiles数据库源："+dbFile.toString());//数据库的路径   /data/data/com.example.android.notepad/databases/note_pad.db
        if (!dbFile.exists()){
            Log.i("myLog", "未找到数据库：");
        }

        //临时文件也一起复制
        //File dbFile_shm = mContext.getDatabasePath("note_pad.db-shm");// 路径是 /data/user/0/(包名)/databases/*
        File dbFile_shm = new File("/data/data/"+mContext.getPackageName()+"/databases/note_pad.db-shm");//路径 /data/data
        //File dbFile_wal = mContext.getDatabasePath("note_pad.db-wal");// 路径是 /data/user/0/(包名)/databases/*
        File dbFile_wal =new File("/data/data/"+mContext.getPackageName()+"/databases/note_pad.db-wal");//路径 /data/data

        //复制的目标路径     /storage/emulated/0/Android/data/包名/files
        String path = mContext.getExternalFilesDir(null).getAbsolutePath();
        File exportDir = new File(path);
        Log.i("dirFile", "backup 文件路径："+exportDir.toString());//    /storage/emulated/0/Android/data/com.example.android.notepad/files
        if (!exportDir.exists()) {
            boolean mkdirs = exportDir.mkdirs();
            if (!mkdirs) {
                Log.i("原来不存在，新建createFile", "创建：" + mkdirs);
            } else {
                Log.i("createFile", "创建成功");
            }
        }
        //安卓后面的版本很少对SD卡进行操作，大多都是操作机身里的外部存储，且现在手机，大多都没有SD卡插口了
        //之前定位到SD的方法如getExternalStorageDirectory()都是指向机身里的外部存储
        //如需要或可以自己定一个位置？
        //File exportDir = new File("/storage/emulated/0/Android/data/com.example.android.notepad/files");
        //File exportDir = new File("/sdcard/Android/data/com.example.android.notepad/files");

        File backup = new File(exportDir, dbFile.getName());//备份文件与原数据库文件名一致，name为note_pad.db
        File backup_shm = new File(exportDir, dbFile_shm.getName());//备份文件与原数据库文件名一致
        File backup_wal = new File(exportDir, dbFile_wal.getName());//备份文件与原数据库文件名一致
        //命令backup备份
        Log.i("myLog", "backup 文件："+backup.toString());//    /storage/emulated/0/Android/data/com.example.android.notepad/files
        if (command.equals(COMMAND_BACKUP)) {
            try {
                backup.createNewFile();
                backup_shm.createNewFile();
                backup_wal.createNewFile();
                fileCopy(dbFile, backup);//数据库文件拷贝至备份文件
                fileCopy(dbFile_shm, backup_shm);//数据库文件拷贝至备份文件
                fileCopy(dbFile_wal, backup_wal);//数据库文件拷贝至备份文件

                Date date = new Date();
                SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                backup_version = sdf.format(date);//修改时间

                Log.d("myLog", "backup ok! 备份文件名："+backup.getName()+"\t"+backup_version);
                return backup_version;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("myLog", "backup fail! 备份文件名："+backup.getName());
                return null;
            }
            //命令恢复
        } else if (command.equals(COMMAND_RESTORE)) {
            try {
                fileCopy(backup, dbFile);//备份文件拷贝至数据库文件
                fileCopy(backup_shm, dbFile_shm);//备份文件拷贝至数据库文件
                fileCopy(backup_wal, dbFile_wal);//备份文件拷贝至数据库文件

                SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                backup_version = sdf.format(backup.lastModified());//最后修改时间

                Log.d("myLog", "restore success! 数据库文件名："+dbFile.getName()+"\t"+backup_version);
                return backup_version;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("myLog", "restore fail! 数据库文件名："+dbFile.getName());
                return null;
            }
        } else {
            return null;
        }
    }


    //文件复制操作
    private void fileCopy(File fromFile, File toFile) throws IOException {
        FileChannel inChannel = new FileInputStream(fromFile).getChannel();
        FileChannel outChannel = new FileOutputStream(toFile).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        }
    }

}

