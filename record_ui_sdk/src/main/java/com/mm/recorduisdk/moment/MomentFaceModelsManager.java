package com.mm.recorduisdk.moment;


import android.support.v4.util.ArrayMap;

import com.mm.mmutil.StringUtils;
import com.mm.recorduisdk.bean.CommonMomentFaceBean;
import com.mm.recorduisdk.bean.FaceClass;
import com.mm.recorduisdk.bean.MomentFace;
import com.mm.recorduisdk.moment.model.MomentFaceItemModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by chenxin on 2019/4/19.
 */
public class MomentFaceModelsManager {

    private CommonMomentFaceBean mData;
    private Map<String,List<MomentFaceItemModel>> mClassModels = new ArrayMap<>();
    /**
     * 用来存放所有的变脸信息，加快查找
     */
    private Map<String, List<MomentFaceItemModel>> mModels = new ArrayMap<>();

    private OnItemChangedListener mOnItemChangedListener;

    public MomentFaceModelsManager(CommonMomentFaceBean data) {
        mData = data;
        addFaceModels();
    }

    //初始化分类中的变脸model
    private void addFaceModels() {
        List<FaceClass> faceClasses = mData.getFaceClasses();
        if (faceClasses == null) {
            return;
        }
        List<MomentFaceItemModel> list;
        for (FaceClass faceClass : faceClasses) {
            if (faceClass == null) {
                continue;
            }
            list = new ArrayList<>();
            for (MomentFace momentFace : faceClass.getFaces()) {
                MomentFaceItemModel model = new MomentFaceItemModel(momentFace);

                addToKinModelList(model, momentFace);

                list.add(model);
            }
            mClassModels.put(faceClass.getId(), list);
        }
    }

    /**
     * 添加到同类变脸集合中
     * @param model 变脸model
     * @param momentFace 变脸信息
     */
    private void addToKinModelList(MomentFaceItemModel model, MomentFace momentFace) {
        List<MomentFaceItemModel> models = mModels.get(momentFace.getId());
        if (models == null) {
            models = new ArrayList<>();
            mModels.put(momentFace.getId(), models);
        }
        models.add(model);
    }

    /**
     * 从同类集合中移除指定model
     */
    private void removeFromKinModelList(MomentFaceItemModel itemModel, MomentFace face) {
        List<MomentFaceItemModel> models = mModels.get(face.getId());
        if (models == null) {
            return;
        }
        models.remove(itemModel);
    }

    /**
     * 根据类别ID获取当前分类下的所有models集合
     * @param faceClassId 类别ID
     */
    public List<MomentFaceItemModel> findMomentFaceModels(String faceClassId) {
        return mClassModels.get(faceClassId);
    }

    /**
     * 根据变脸信息获取变脸分类
     */
    public FaceClass findFaceClass(MomentFace face) {
        for (FaceClass faceClass : mData.getFaceClasses()) {
            if (faceClass.getId().equals(face.getClassId())) {
                return faceClass;
            }
        }
        return null;
    }

    /**
     * 根据变脸素材信息获取model集合，因为同一个id的变脸素材可能存在与多个分类下，所以是一个集合
     */
    public List<MomentFaceItemModel> findModelsByFace(MomentFace face) {
        return mModels.get(face.getId());
    }

    /**
     * 根据类别ID和变脸素材信息，获取对应的model对象
     */
    public MomentFaceItemModel findMomentFaceModel(String classId, MomentFace face) {
        if (classId == null || face == null) {
            return null;
        }
        List<MomentFaceItemModel> models = findModelsByFace(face);
        if (models == null) {
            return null;
        }
        for (MomentFaceItemModel model : models) {
            if (face.getId().equals(model.getFace().getId())) {
                return model;
            }
        }
        return null;
    }

    public void setOnItemChangedListener(OnItemChangedListener onItemChangedListener) {
        mOnItemChangedListener = onItemChangedListener;
    }

    /**
     * 往制定分类中添加变脸项model
     * @param face 变脸信息
     * @param classId 所属分类
     */
    public void addMomentFace(MomentFace face, String classId) {
        if (mData == null || face == null || StringUtils.isEmpty(classId)) {
            return;
        }
        List<MomentFaceItemModel> momentFaceModels = findMomentFaceModels(classId);
        if (momentFaceModels == null) {
            return;
        }
        for (MomentFaceItemModel momentFaceModel : momentFaceModels) {
            if (momentFaceModel.getFace().getZip_url().equals(face.getZip_url())) {
                // 已经存在
                return;
            }
        }
        MomentFaceItemModel itemModel = new MomentFaceItemModel(face);
        momentFaceModels.add(itemModel);
        addToKinModelList(itemModel, face);
        if (mOnItemChangedListener != null) {
            List<FaceClass> faceClasses = mData.getFaceClasses();
            for (FaceClass faceClass : faceClasses) {
                if (faceClass.getId().equals(classId)) {
                    mOnItemChangedListener.onChanged(faceClass, face, itemModel, momentFaceModels);
                    break;
                }
            }
        }
    }

    /**
     * 替换model集合中最后一个
     */
    public void updateLastMomentFace(MomentFace face, String classId) {
        if (mData == null || face == null || StringUtils.isEmpty(classId)) {
            return;
        }
        List<MomentFaceItemModel> momentFaceModels = findMomentFaceModels(classId);
        if (momentFaceModels == null || momentFaceModels.isEmpty()) {
            return;
        }

        for (MomentFaceItemModel momentFaceModel : momentFaceModels) {
            if (momentFaceModel.getFace().getZip_url().equals(face.getZip_url())) {
                // 已经存在
                return;
            }
        }
        MomentFaceItemModel itemModel = new MomentFaceItemModel(face);

        // 移除最后一个
        MomentFaceItemModel remove = momentFaceModels.remove(momentFaceModels.size() - 1);
        if (remove == null) {
            return;
        }
        removeFromKinModelList(remove, remove.getFace());

        momentFaceModels.add(itemModel);
        addToKinModelList(itemModel, face);
        if (mOnItemChangedListener != null) {
            List<FaceClass> faceClasses = mData.getFaceClasses();
            for (FaceClass faceClass : faceClasses) {
                if (faceClass.getId().equals(classId)) {
                    mOnItemChangedListener.onChanged(faceClass, face, itemModel, momentFaceModels);
                    break;
                }
            }
        }
    }


    /**
     * 新增或者删除model之后的回调
     */
    public interface OnItemChangedListener {

        void onChanged(FaceClass faceClass, MomentFace face, MomentFaceItemModel itemModel, List<MomentFaceItemModel> modelList);

    }
}
