package com.mm.recorduisdk.moment.utils;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.mm.recorduisdk.bean.FaceClass;
import com.mm.recorduisdk.bean.MomentFace;
import com.mm.recorduisdk.recorder.helper.MomentFaceFileUtil;
import com.mm.recorduisdk.recorder.helper.MomentFaceUtil;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;


/**
 * @author shidefeng
 * @since 2017/8/4.
 */

public class MyFaceClassHelper {

    // 产品限定最多展示4page: 4 * 4 * 2
    public static final int DEFAULT_MAX_SIZE = 32;

    public static final String MY_CATE_ID = "my_cate_id";

    private String[] mMyFacesOrders;
    private HashMap<String, MomentFace> mTempFacesMap = new HashMap<>();

    private MyFaceClassHelper() {
    }

    private FaceClass getMyFaceClass() {
        final FaceClass my = new FaceClass();
        my.setId(MY_CATE_ID);
        return my;
    }

    private void filterDownloadFaces(@NonNull List<FaceClass> faceClasses) {
        for (FaceClass faceClass : faceClasses) {
            final List<MomentFace> faces = faceClass.getFaces();
            for (MomentFace face : faces) {
                addToTempFacesMap(face);
            }
        }
    }

    private void addToTempFacesMap(MomentFace face) {
        final long start = System.currentTimeMillis();
        if (face == null || !MomentFaceUtil.simpleCheckFaceResource(face)) {
            return;
        }
        if (mTempFacesMap.containsKey(face.getId())) {
            return;
        }
        mTempFacesMap.put(face.getId(), face);
    }

    private List<MomentFace> getMyFacesList() {
        final long start = System.currentTimeMillis();
        if (mTempFacesMap == null || mTempFacesMap.isEmpty()) {
            return new LinkedList<>();
        }
        if (mMyFacesOrders == null) {
            mMyFacesOrders = getMyFacesOrders();
        }

        List<String> tempIds = new ArrayList<>(mTempFacesMap.keySet());
        List<String> saveIds;

        if (mMyFacesOrders == null) {
            saveIds = tempIds;
        } else {
            saveIds = new ArrayList<>(Arrays.asList(mMyFacesOrders));
            saveIds.retainAll(tempIds);
            tempIds.removeAll(saveIds);
            saveIds.addAll(tempIds);
        }

        while (saveIds.size() > DEFAULT_MAX_SIZE) {
            saveIds = saveIds.subList(0, DEFAULT_MAX_SIZE);
        }

        setMyFacesOrders(saveIds);

        final ArrayList<MomentFace> myFacesList = new ArrayList<>();
        for (int i = 0, size = saveIds.size(); i < size; i++) {
            final MomentFace face = mTempFacesMap.get(saveIds.get(i));
            if (face == null) {
                continue;
            }
            myFacesList.add(face);
        }
        return myFacesList;
    }

    public static FaceClass buildMyFaceClass(@NonNull List<FaceClass> faceClasses) {
        final MyFaceClassHelper myHelper = new MyFaceClassHelper();
        myHelper.filterDownloadFaces(faceClasses);
        FaceClass myFaceClass = myHelper.getMyFaceClass();
        myFaceClass.setFaces(myHelper.getMyFacesList());
        return myFaceClass;
    }

    public static String[] getMyFacesOrders() {
        final Properties properties = new Properties();
        try {
            final File file = getMyFacesOrdersFile();
            final FileReader reader = new FileReader(file);

            properties.load(reader);

            final Set<String> keys = properties.stringPropertyNames();
            if (keys == null || keys.isEmpty()) {
                reader.close();
                return null;
            }
            final String[] ids = new String[keys.size()];
            for (String id : keys) {
                final String value = properties.getProperty(id);
                int index = TextUtils.isEmpty(value) ? -1 : Integer.parseInt(value);
                if (index >= 0 && index < ids.length) {
                    ids[index] = id;
                }
            }
            reader.close();
            return ids;
        } catch (Exception e) {
        }
        return null;
    }

    public static void setMyFacesOrders(List<String> ids) {
        final Properties properties = new Properties();
        try {
            final File file = getMyFacesOrdersFile();
            final FileWriter writer = new FileWriter(file);

            for (int i = 0, size = ids.size(); i < size; i++) {
                properties.setProperty(ids.get(i), i + "");
            }
            properties.store(writer, "");
            writer.close();
        } catch (Exception e) {
        }
    }

    public static File getMyFacesOrdersFile() throws Exception {
        final File file = new File(MomentFaceFileUtil.getMomentFaceHomeDir(), "my_faces_orders");
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

}
