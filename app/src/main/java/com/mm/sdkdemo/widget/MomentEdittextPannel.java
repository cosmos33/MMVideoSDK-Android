package com.mm.sdkdemo.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mm.mediasdk.utils.ImageUtil;
import com.mm.mmutil.log.Log4Android;
import com.mm.mediasdk.utils.UIUtils;
import com.mm.sdkdemo.R;
import com.mm.sdkdemo.recorder.helper.AndroidBug5497Workaround;
import com.mm.sdkdemo.utils.KeyBoardUtil;
import com.mm.sdkdemo.widget.sticker.text.ColorEditText;
import com.mm.sdkdemo.widget.sticker.text.ColorTextView;

/**
 * Created by XiongFangyu on 17/2/20.
 */
public class MomentEdittextPannel extends FrameLayout implements View.OnClickListener {
    private AndroidBug5497Workaround workaround;
    private View btnOk;
    private ColorEditText editText;
    private ColorTextView textView;
    private AnimCheckableGroupView acgv;

    private int[] colors;
    private int[] firstColor;
    private int[] secondColor;

    private ChangeTextListener changeTextListener;
    private int checkedIndex = 0;
    private String startText;

    public MomentEdittextPannel(Context context) {
        this(context, null);
    }

    public MomentEdittextPannel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MomentEdittextPannel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MomentEdittextPannel(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        btnOk = findViewById(R.id.moment_edittext_panel_ok);
        editText = (ColorEditText) findViewById(R.id.moment_edittext_text);
        textView = (ColorTextView) findViewById(R.id.moment_edittext_textview);
        acgv = (AnimCheckableGroupView) findViewById(R.id.moment_edittext_panel_acgview);

        editText.setOnClickListener(this);
        btnOk.setOnClickListener(this);
        setOnClickListener(this);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            boolean down = false;

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == 0 || actionId == 3) && event != null) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        down = true;
                    } else if (event.getAction() == KeyEvent.ACTION_UP && down) {
                        onOkClick();
                        down = false;
                    }
                    return true;
                }
                return false;
            }
        });

        colors = new int[]{
                Color.WHITE,
                0xfff52824,
                0xffffbe00,
                0xff8834ff,
                0xff00c0ff,
                0xff10df7a,
        };

        firstColor = new int[]{
                0xff3023ae,
                0xffc96dd8
        };
        secondColor = new int[]{
                0xfff76b1c,
                0xfffbda61
        };

        acgv.addColors(colors);
        acgv.addDrawables(
                new GradientDrawable(GradientDrawable.Orientation.TL_BR, firstColor),
                new GradientDrawable(GradientDrawable.Orientation.TL_BR, secondColor));
        acgv.setCheckOnce(true);
        acgv.setChildCheckListener(new AnimCheckableGroupView.ChildCheckListener() {
            @Override
            public void onChecked(IAnimView v, boolean checked, int pos) {
                onCheckColor(v.getIndex());
            }
        });

        if (startText != null) {
            editText.setText(startText);
        }
        acgv.setCheck(checkedIndex, true, false);
        onCheckColor(checkedIndex);

        if (!isInEditMode()) {
            final int vh = UIUtils.getVirtualBarHeight();
            acgv.setPadding(acgv.getPaddingLeft(),
                    acgv.getPaddingTop(),
                    acgv.getPaddingRight(),
                    acgv.getPaddingBottom() + vh);
        }
    }

    private void init(Context context) {

    }

    @Override
    public void onClick(View v) {
        if (v == btnOk) {
            onOkClick();
        } else if (v == editText) {
            KeyBoardUtil.showSoftKeyboardForce((Activity) getContext(), editText);
        } else if (v == this) {
            onOkClick();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void release() {
        if (workaround != null)
            workaround.release();
        workaround = null;
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == View.VISIBLE) {
            acgv.setVisibility(View.VISIBLE);
        } else {
            acgv.setVisibility(View.GONE, false);
            hideSoftKeyboard();
        }
    }

    private AndroidBug5497Workaround.keyboardHeightChangedListener keyboardHeightChangedListener = new AndroidBug5497Workaround.keyboardHeightChangedListener() {
        @Override
        public void heightChanged(int previewHeight, int nowHeight) {
            if (getVisibility() != VISIBLE)
                return;
            int dh = UIUtils.getScreenHeight() - nowHeight;
            if (dh > UIUtils.getPixels(100)) {
                keyBoradShow(dh);
            } else {
                keyBoardHidden();
            }
        }
    };

    private void keyBoradShow(int h) {
        MarginLayoutParams params = (MarginLayoutParams) acgv.getLayoutParams();
        params.bottomMargin = h;
        MarginLayoutParams etp = (MarginLayoutParams) editText.getLayoutParams();
        etp.bottomMargin = h >> 1;
        MarginLayoutParams tvp = (MarginLayoutParams) textView.getLayoutParams();
        tvp.bottomMargin = etp.bottomMargin;
        requestLayout();
    }

    private void keyBoardHidden() {
        MarginLayoutParams params = (MarginLayoutParams) acgv.getLayoutParams();
        MarginLayoutParams etp = (MarginLayoutParams) editText.getLayoutParams();
        MarginLayoutParams tvp = (MarginLayoutParams) textView.getLayoutParams();
        params.bottomMargin = etp.bottomMargin = tvp.bottomMargin = 0;
        requestLayout();
    }

    private void onCheckColor(int index) {
        checkedIndex = index;
        if (index >= colors.length) {
            index -= colors.length;
            int[] gradient = index == 0 ? firstColor : secondColor;
            editText.setLinearGradient(gradient[0], gradient[1], 45);
            textView.setLinearGradient(gradient[0], gradient[1], 45);
        } else {
            int color = colors[index];
            editText.setTextColor(color);
            textView.setTextColor(color);
        }
    }

    private void onOkClick() {
        String text = editText.getText().toString();
        if (TextUtils.isEmpty(text)) {
            onBackPressed();
            return;
        }
        float size = editText.getTextSize();
        Log4Android.getInstance().d("onOkClick " + size);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        textView.setText(text);
        textView.setVisibility(VISIBLE);
        post(new Runnable() {
            @Override
            public void run() {
                if (changeTextListener != null) {
                    final Bitmap bitmap = ImageUtil.createBitmapByView(textView, 1);
                    changeTextListener.onChange(bitmap, textView.getText().toString(), checkedIndex);
                }
                hideSoftKeyboard();
                setVisibility(GONE);
            }
        });
    }

    private void hideSoftKeyboard() {
        if (editText.isFocused()) {
            KeyBoardUtil.hideSoftKeyboardNotAlways((Activity) editText.getContext(), editText.getWindowToken());
        }
    }

    public void setCheckedIndex(int index) {
        checkedIndex = index;
        if (acgv != null) {
            acgv.setCheck(index, true, false);
            onCheckColor(index);
        }
    }

    public void setHint(String hint) {
        if (editText != null && hint != null) {
            editText.setHint(hint);
        }
    }

    public void setText(String text) {
        startText = text;
        if (editText != null)
            editText.setText(text);
        if (textView != null)
            textView.setText(text);
    }

    public void beginEdit(Activity a) {
        if (workaround == null)
            workaround = AndroidBug5497Workaround.assistActivity(a, keyboardHeightChangedListener);
        setVisibility(VISIBLE);
        KeyBoardUtil.showSoftKeyboardForce(a, editText);
        textView.setVisibility(INVISIBLE);
        String text = editText.getText().toString();
        if (!TextUtils.isEmpty(text)) {
            int length = text.length();
            editText.setSelection(length);
        }
    }

    public void onBackPressed() {
        hideSoftKeyboard();
        setVisibility(GONE);
        if (changeTextListener != null)
            changeTextListener.onChange(null, null, 0);
    }

    public void setChangeTextListener(ChangeTextListener changeTextListener) {
        this.changeTextListener = changeTextListener;
    }

    public interface ChangeTextListener {
        void onChange(Bitmap textBitmap, String text, int checkedIndex);
    }
}
