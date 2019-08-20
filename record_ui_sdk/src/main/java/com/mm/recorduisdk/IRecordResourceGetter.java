package com.mm.recorduisdk;

import android.support.annotation.WorkerThread;

import com.mm.recorduisdk.bean.CommonMomentFaceBean;
import com.mm.recorduisdk.bean.MomentFace;
import com.mm.recorduisdk.bean.MomentSticker;
import com.mm.recorduisdk.recorder.model.MusicContent;
import com.mm.recorduisdk.recorder.sticker.DynamicSticker;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created on 2019/7/19.
 *
 * @author jianxi[mabeijianxi@gmail.com]
 */
public interface IRecordResourceGetter {

    File getFiltersImgHomeDir();

    File getLivePhotoHomeDir();

    File getMakeUpHomeDir();

    @WorkerThread
    List<DynamicSticker> getDynamicStickerList();

    @WorkerThread
    List<MomentSticker> getStaticStickerList();

    @WorkerThread
    List<MusicContent> getRecommendMusic();

    Map<String, List<MomentFace>> getFaceMomentData();

    CommonMomentFaceBean fetchMomentFaceData() throws Exception;
}
