package com.mm.base_business.utils.toolbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.mm.base_business.R;


/**
 * Project momodev
 * Package com.mm.momo.view.toolbar
 * Created by tangyuchun on 4/8/16.
 */
public class CompatAppbarLayout extends RelativeLayout {
    private boolean showShadow = false;

    private Paint linePaint;

    public CompatAppbarLayout(Context context) {
        super(context);
        init(context, null);
    }

    public CompatAppbarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CompatAppbarLayout);
            showShadow = typedArray.getBoolean(R.styleable.CompatAppbarLayout_showShadow, showShadow);
            typedArray.recycle();
        }
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(getResources().getColor(R.color.toolbar_shadow_color));
        linePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (showShadow && linePaint != null) {
            canvas.drawLine(0, getHeight() - 1, getWidth(), getHeight(), linePaint);
        }
    }

    public void toggleShadow() {
        showShadow(!this.showShadow);
    }

    public void showShadow(boolean flag) {
        if (this.showShadow == flag) {
            return;
        }
        this.showShadow = flag;

        invalidate();
    }

}
