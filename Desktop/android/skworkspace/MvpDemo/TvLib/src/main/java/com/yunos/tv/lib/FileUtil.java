package com.yunos.tv.lib;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

public class FileUtil {
    private static final String TAG = FileUtil.class.getSimpleName();
    private static final String[] FILE_SYSTEM_UNSAFE = new String[]{"/", "\\", "..", ":", "\"", "?", "*", "<", ">"};
    private static final String[] FILE_SYSTEM_UNSAFE_DIR = new String[]{"\\", "..", ":", "\"", "?", "*", "<", ">"};

    public FileUtil() {
    }

    public static void createDirectoryForParent(File file) {
        File dir = file.getParentFile();
        if(!dir.exists() && !dir.mkdirs()) {
            Log.e(TAG, "Failed to create directory " + dir);
        }

    }

    public static boolean hasSpace(long fileLength, long reservedSpace) {
        if(hasSdcard()) {
            if(fileLength > getSDCardIdleSpace() - reservedSpace) {
                return false;
            }
        } else if(fileLength > getDataIdleSapce() - reservedSpace) {
            return false;
        }

        return true;
    }

    public static boolean hasSpace(long fileLength, long reservedSpace, String filePath) {
        boolean isSdcardPath = filePath.startsWith(getSDCardpath());
        if(isSdcardPath) {
            if(!hasSdcard()) {
                return false;
            }

            if(fileLength > getSDCardIdleSpace() - reservedSpace) {
                return false;
            }
        } else if(fileLength > getDataIdleSapce() - reservedSpace) {
            return false;
        }

        return true;
    }

    public static String mendPath(String path) {
        assert path != null : "path!=null";

        String newPath = path;
        if(!path.endsWith("/")) {
            newPath = path + '/';
        }

        return newPath;
    }

    public static boolean ensureDirectoryExistsAndIsReadWritable(File dir) {
        if(dir == null) {
            return false;
        } else {
            if(dir.exists()) {
                if(!dir.isDirectory()) {
                    Log.w(TAG, dir + " exists but is not a directory.");
                    return false;
                }
            } else {
                if(!dir.mkdirs()) {
                    Log.w(TAG, "Failed to create directory " + dir);
                    return false;
                }

                Log.i(TAG, "Created directory " + dir);
            }

            if(!dir.canRead()) {
                Log.w(TAG, "No read permission for directory " + dir);
                return false;
            } else if(!dir.canWrite()) {
                Log.w(TAG, "No write permission for directory " + dir);
                return false;
            } else {
                return true;
            }
        }
    }

    private static String fileSystemSafe(String filename) {
        if(filename != null && filename.trim().length() != 0) {
            String[] var4 = FILE_SYSTEM_UNSAFE;
            int var3 = FILE_SYSTEM_UNSAFE.length;

            for(int var2 = 0; var2 < var3; ++var2) {
                String s = var4[var2];
                filename = filename.replace(s, "-");
            }

            return filename;
        } else {
            return "unnamed";
        }
    }

    private static String fileSystemSafeDir(String path) {
        if(path != null && path.trim().length() != 0) {
            String[] var4 = FILE_SYSTEM_UNSAFE_DIR;
            int var3 = FILE_SYSTEM_UNSAFE_DIR.length;

            for(int var2 = 0; var2 < var3; ++var2) {
                String s = var4[var2];
                path = path.replace(s, "-");
            }

            return path;
        } else {
            return "";
        }
    }

    public static SortedSet<File> listFiles(File dir) {
        File[] files = dir.listFiles();
        if(files == null) {
            Log.w(TAG, "Failed to list children for " + dir.getPath());
            return new TreeSet();
        } else {
            return new TreeSet(Arrays.asList(files));
        }
    }

    public static String getExtension(String name) {
        int index = name.lastIndexOf(46);
        return index == -1?"":name.substring(index + 1).toLowerCase();
    }

    public static String getBaseName(String name) {
        int index = name.lastIndexOf(46);
        return index == -1?name:name.substring(0, index);
    }

    public static <T extends Serializable> boolean serialize(Context context, T obj, String fileName) {
        File file = new File(context.getCacheDir(), fileName);
        ObjectOutputStream out = null;

        try {
            out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(obj);
            Log.i(TAG, "Serialized object to " + file);
            return true;
        } catch (Throwable var9) {
            Log.d(TAG, "Caught: " + var9, var9);
        } finally {
            close(out);
        }

        return false;
    }

    public static <T extends Serializable> T deserialize(Context context, String fileName) {
        File file = new File(context.getCacheDir(), fileName);
        if(file.exists() && file.isFile()) {
            ObjectInputStream in = null;

            try {
                in = new ObjectInputStream(new FileInputStream(file));
                T x = (T)in.readObject();
                Log.i(TAG, "Deserialized object from " + file);
                return x;
            } catch (Throwable var9) {
                Log.w(TAG, "Caught: " + var9, var9);
            } finally {
                close(in);
            }

            return null;
        } else {
            return null;
        }
    }

    public static void close(Closeable closeable) {
        try {
            if(closeable != null) {
                closeable.close();
            }
        } catch (Throwable var2) {
            Log.w(TAG, "Caught: " + var2, var2);
        }

    }

    public static boolean delete(File file) {
        if(file != null && file.exists()) {
            if(!file.delete()) {
                Log.w(TAG, "Failed to delete file " + file);
                return false;
            }

            Log.i(TAG, "Deleted file " + file);
        }

        return true;
    }

    public static void atomicCopy(File from, File to) throws IOException {
        FileInputStream in = null;
        FileOutputStream out = null;
        File tmp = null;

        try {
            tmp = new File(to.getPath() + ".tmp");
            in = new FileInputStream(from);
            out = new FileOutputStream(tmp);
            in.getChannel().transferTo(0L, from.length(), out.getChannel());
            out.close();
            if(!tmp.renameTo(to)) {
                throw new IOException("Failed to rename " + tmp + " to " + to);
            }

            Log.i(TAG, "Copied " + from + " to " + to);
        } catch (IOException var9) {
            close(out);
            delete(to);
            throw var9;
        } finally {
            close(in);
            close(out);
            delete(tmp);
        }

    }

    public static boolean isUrl(String path) {
        if(path != null) {
            if(path.startsWith("file://")) {
                return false;
            }

            if(path.contains("://")) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static long getDataIdleSapce() {
        String path = "/data";
        StatFs fileStats = new StatFs(path);
        fileStats.restat(path);
        return (long)fileStats.getAvailableBlocks() * (long)fileStats.getBlockSize();
    }

    public static long getSDCardIdleSpace() {
        if(!hasSdcard()) {
            return 0L;
        } else {
            String sdcard = getSDCardpath();
            StatFs statFs = new StatFs(sdcard);
            return (long)statFs.getBlockSize() * (long)statFs.getAvailableBlocks();
        }
    }

    public static String getSDCardpath() {
        return Environment.getExternalStorageDirectory().getPath();
    }
}
