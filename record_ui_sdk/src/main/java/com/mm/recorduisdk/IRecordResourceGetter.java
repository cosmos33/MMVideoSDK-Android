package com.mm.recorduisdk;

import com.mm.recorduisdk.bean.CommonMomentFaceBean;
import com.mm.recorduisdk.bean.MomentSticker;
import com.mm.recorduisdk.recorder.model.MusicContent;
import com.mm.recorduisdk.recorder.sticker.DynamicSticker;

import java.io.File;
import java.util.List;

/**
 * Created on 2019/7/19.
 *
 * @author jianxi[mabeijianxi@gmail.com]
 */
public interface IRecordResourceGetter {

    /**
     * @return 滤镜资源配置
     */
    IRecordResourceConfig<File> getFiltersImgHomeDirConfig();

    /**
     * @return 相册中图片合成视频时秀动效果资源配置
     */
    IRecordResourceConfig<File> getLivePhotoHomeDirConfig();

    /**
     * @return 美妆资源配置
     */
    IRecordResourceConfig<File> getMakeUpHomeDirConfig();

    /**
     * @return 视频编辑中贴纸资源配置
     */
    IRecordResourceConfig<List<DynamicSticker>> getDynamicStickerListConfig();

    /**
     * @return 图片编辑中贴纸资源配置
     */
    IRecordResourceConfig<List<MomentSticker>> getStaticStickerListConfig();

    /**
     * @return 拍摄器与视频编辑中网络音乐资源配置
     */
    IRecordResourceConfig<List<MusicContent>> getRecommendMusicConfig();

    /**
     * @return 道具资源配置
     */
    IRecordResourceConfig<CommonMomentFaceBean> getMomentFaceDataConfig();
}
