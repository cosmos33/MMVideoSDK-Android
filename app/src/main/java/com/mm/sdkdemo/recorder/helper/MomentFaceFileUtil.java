package com.mm.sdkdemo.recorder.helper;

import com.immomo.mmutil.FileUtil;
import com.immomo.mmutil.StringUtils;
import com.mm.sdkdemo.bean.MomentFace;
import com.mm.sdkdemo.config.Configs;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by chenwangwang on 2018/3/27.
 * 管理时刻变脸的文件相关的逻辑
 */
public class MomentFaceFileUtil {

    /**
     * 临时文件的后缀
     */
    private static final String SUFFIX_TMP_FILE = "_tmp";

    /**
     * 变脸素材在存放的目录名称
     */
    private static final String DIR_NAME_FACE_HOME = "faces";

    /**
     * 用来存放处理中的文件
     */
    private static final String DIR_WORKING_NAME = "working";

    private static File sMomentHomeFile;
    private static File sWorkingDirFile;

    /**
     * 文件过滤器
     */
    public static final FileFilter sDirFilter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return pathname.isDirectory();
        }
    };

    /**
     * 获取某一个变脸素材的文件对象
     *
     * @param face 变脸素材信息
     * @return 返回null如果传入的face对象为空
     */
    public static File getFaceResourceDir(MomentFace face) {
        return getFaceResourceDir(getMomentFaceHomeDir(), face);
    }

    /**
     * 获取某一个变脸素材的文件对象
     *
     * @param parentDir 父文件夹
     * @param face      变脸素材信息
     * @return 返回null如果传入的face对象为空
     */
    private static File getFaceResourceDir(File parentDir, MomentFace face) {
        if (face == null) {
            return null;
        }
        return new File(parentDir, String.format("%s_%s", face.getId(), face.getVersion()));
    }

    /**
     * 变脸素材存放的目录
     */
    public static File getMomentFaceHomeDir() {
        if (sMomentHomeFile != null && sMomentHomeFile.exists()) {
            return sMomentHomeFile;
        }
        File dir = new File(Configs.PATH_MOMENT, DIR_NAME_FACE_HOME);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // note 防止mediaScanner 扫描到
        File nomedia = new File(dir, ".nomedia");
        if (!nomedia.exists()) {
            try {
                nomedia.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sMomentHomeFile = dir;
        return dir;
    }

    /**
     * 获取用于记录最后一次修改时间的文件
     *
     * @param dir 变脸素材解压后的目录文件
     */
    public static File getRecordLTFile(File dir) {
        if (dir == null)
            return null;
        return new File(dir, MomentFaceConstants.LAST_MODIFY_NAME);
    }

    /**
     * 获取某一个资源对应的下载文件
     *
     * @param taskID 任务ID
     * @see #getDownloadTaskID(MomentFace)
     */
    public static File getDownloadFile(String taskID) {
        return new File(getMomentFaceHomeDir(), taskID + SUFFIX_TMP_FILE);
    }

    /**
     * 获取某一个资源在本地的临时存放文件
     *
     * @see #getDownloadTaskID(MomentFace)
     */
    public static File getDownloadFile(MomentFace face) {
        return getDownloadFile(getDownloadTaskID(face));
    }

    /**
     * 对每一个下载任务都生成一个唯一的ID值
     */
    public static String getDownloadTaskID(MomentFace face) {
        return StringUtils.md5(face.getZip_url());
    }

    /**
     * 获取临时存放文件的目录
     */
    public static File getWorkingDirFile() {
        if (sWorkingDirFile != null) {
            return sWorkingDirFile;
        }
        sWorkingDirFile = new File(getMomentFaceHomeDir(), DIR_WORKING_NAME);
        if (!sWorkingDirFile.exists()) {
            sWorkingDirFile.mkdirs();
        }
        return sWorkingDirFile;
    }

    /**
     * 获取用于临时存放解压文件的目录
     */
    public static File getUnzipFile(MomentFace face) {
        return getFaceResourceDir(getWorkingDirFile(), face);
    }

    /**
     * 删除目标文件，在不知道是文件还是目录的情况下
     *
     * @return true 删除成功，false 删除失败
     */
    public static boolean deleteFileOrDir(File file) {
        if (!file.exists()) {
            return true;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (!deleteFileOrDir(f)) {
                        return false;
                    }
                }
            }
        }
        return file.delete();
    }

    public static void deleteMomentFace() {
        try {
            File f = getMomentFaceHomeDir();
            FileUtil.deleteDir(f);
        } catch (Exception e) {
        }
    }

}
