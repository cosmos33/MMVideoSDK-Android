package com.mm.sdkdemo.utils;

import android.os.Environment;
import android.os.StatFs;

import com.mm.mmutil.log.Log4Android;
import com.mm.mmutil.toast.Toaster;

import java.io.File;

public class SDCardUtils {
	public static boolean checkSDCardStatus(){
		
		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){ // 表明对象存在并具有读/写权限 -- 可正常使用
			if(hasStorageUsage()){
				return true;
			}else{
				Toaster.show("储存卡可用空间不足");
				return false;
			}
		}
		
		if(Environment.MEDIA_REMOVED.equals(Environment.getExternalStorageState())){ // SD卡不可用
			Toaster.show("储存卡不可用");
		}else if(Environment.MEDIA_BAD_REMOVAL.equals(Environment.getExternalStorageState())){ // 表明SDCard 被卸载前己被移除 
			Toaster.show("储存卡已经被移除");
		}else if(Environment.MEDIA_CHECKING.equals(Environment.getExternalStorageState())){ // 表明对象正在磁盘检查
			Toaster.show("正在检查储存卡");
		}else if(Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState())){ // 表明对象权限为只读 
			Toaster.show("储存卡当前为只读状态");
		}else if(Environment.MEDIA_NOFS.equals(Environment.getExternalStorageState())){ // 表明对象为空白或正在使用不受支持的文件系统
			Toaster.show("储存卡为空白或正在使用不受支持的文件系统");
		}else if(Environment.MEDIA_SHARED.equals(Environment.getExternalStorageState())){ // 如果 SDCard 未安装 ，并通过 USB 大容量存储共享
			Toaster.show("正在使用大容量共享模式");
		}else if(Environment.MEDIA_UNMOUNTABLE.equals(Environment.getExternalStorageState())){ // SDCard 是存在但不可以被安装
			Toaster.show("储存卡读取失败");
		}else if(Environment.MEDIA_UNMOUNTED.equals(Environment.getExternalStorageState())){ // SDCard 已卸掉如果 SDCard  是存在但是没有被安装
			Toaster.show("储存卡已经被卸载");
		}else{
			Toaster.show("储存卡不可用");
		}
	
		return false;
	
	} 
	
	/**
	 * 检查内存卡是否还有可用空间
	 * @return
	 */
	public static boolean hasStorageUsage() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        float freeStorage = (availableBlocks * blockSize)/(1024 * 1024);
        if (freeStorage >= 1) { // 剩余空间大于1M
               return true;
        }
        return false;
    }

	/**
	 * 检查内存卡是否还有可用空间
	 * @param applySize 申请的大小
	 * @return
	 */
	public static boolean hasStorageUsage(long applySize) {
		try {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();
			long freeStorage = availableBlocks * blockSize;
			if (freeStorage > applySize) { // 剩余空间大于1M
				return true;
			}
		}catch (Exception e) {
			Log4Android.getInstance().e(e);
		}
		return false;
	}
}

