package com.mm.sdkdemo.widget.videorangebar;

/**
 * 选中的选框切换时
 * Project MomoDemo
 * Package com.mm.momo.videorangebar
 * Created by tangyuchun on 2/15/17.
 */
public interface RangeBarListener {
    /**
     * 当选中的选框发生切换时
     *
     * @param selectedRange 可能为空，当 ==null 时，代表没有选中的选框
     */
    void onSelectedRangeSwitched(VideoRange selectedRange);

    /**
     * 当选中的选框发生变化时  包括位置移动，时间戳变化都会触发
     * @param range
     */
    void onRangeMoving(VideoRange range);

    /**
     * 当停止拖动（包括整体移动，拖拽首尾 ）时，触发此回调
     * @param range
     */
    void onRangeMoveStopped(VideoRange range);
}
