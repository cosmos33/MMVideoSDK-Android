package com.mm.recorduisdk.moment;


import com.mm.recorduisdk.bean.CommonMomentFaceBean;
import com.mm.recorduisdk.bean.FaceClass;
import com.mm.recorduisdk.bean.MomentFace;
import com.mm.recorduisdk.moment.utils.MyFaceClassHelper;
import com.mm.recorduisdk.recorder.helper.MomentFaceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenwangwang on 2018/3/28.
 * 往集合中加入"我的"分类
 * @see MyFaceClassHelper
 */
public class MyClassDataProcessor extends MomentFaceDataProcessor {

    private FaceClass mFaceClass;

    @Override
    public <T extends CommonMomentFaceBean> void process(T data) {
        if (data == null) {
            return;
        }

        List<FaceClass> faceClasses = data.getFaceClasses();

        if (faceClasses == null || faceClasses.isEmpty()) {
            return;
        }

        if (mFaceClass == null) {
            mFaceClass = MyFaceClassHelper.buildMyFaceClass(faceClasses);
        }

        faceClasses.add(0, mFaceClass);
    }


    @Override
    public void onMomentFaceDownloadSuccess(MomentFace face, MomentFaceModelsManager modelsManager) {
        super.onMomentFaceDownloadSuccess(face, modelsManager);
        if (mFaceClass == null) {
            return;
        }
        if (face == null) {
            return;
        }
        if (!MomentFaceUtil.simpleCheckFaceResource(face)) {
            return;
        }
        boolean isFully = updateMyFacesListInMyFaceClass(face, mFaceClass);
        if (modelsManager != null) {
            MomentFace clone = face.clone();
            clone.setClassId(MyFaceClassHelper.MY_CATE_ID);
            if (isFully) {
                modelsManager.updateLastMomentFace(clone, MyFaceClassHelper.MY_CATE_ID);
            } else {
                modelsManager.addMomentFace(clone, MyFaceClassHelper.MY_CATE_ID);
            }
        }
    }


    /**
     * @return 是否已经满了，{@link MyFaceClassHelper#DEFAULT_MAX_SIZE}
     */
    private boolean updateMyFacesListInMyFaceClass(MomentFace face, FaceClass myFaceClass) {
        boolean isFully = false;
        final List<MomentFace> faces = myFaceClass.getFaces();
        if (faces == null) {
            return false;
        }
        while (faces.size() >= MyFaceClassHelper.DEFAULT_MAX_SIZE) {
            isFully = true;
            faces.remove(faces.size() - 1);
        }
        faces.add(0, face);

        final List<String> ids = new ArrayList<>();
        for (MomentFace f : faces) {
            ids.add(f.getId());
        }
        MyFaceClassHelper.setMyFacesOrders(ids);
        return isFully;
    }
}
