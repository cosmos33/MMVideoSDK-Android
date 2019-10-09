package com.mm.recorduisdk;

/**
 * Created on 2019-09-27.
 *
 * @author jianxi[mabeijianxi@gmail.com]
 */
public interface IRecordResourceConfig<Resource> {
    boolean isOpen();

    Resource getResource();
}
