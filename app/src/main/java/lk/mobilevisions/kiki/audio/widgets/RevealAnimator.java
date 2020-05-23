package lk.mobilevisions.kiki.audio.widgets;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.graphics.Rect;
import android.os.Build;
import android.util.Property;
import android.view.View;

import java.lang.ref.WeakReference;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

/**
 * @hide
 */
public interface RevealAnimator {

    RevealRadius CLIP_RADIUS = new RevealRadius();

    /**
     * Listen when animation start/end/cancel
     * and setup view for it
     */
    void onRevealAnimationStart();
    void onRevealAnimationEnd();
    void onRevealAnimationCancel();

    /**
     * Used with animator to animate view clipping
     *
     * @param value clip radius
     */
    void setRevealRadius(float value);
    void setBackourdColor(int value);

    /**
     * Used with animator to animate view clipping
     *
     * @return current radius
     */
    float getRevealRadius();

    /**
     * Invalidate certain rectangle
     *
     * @param bounds bounds to redraw
     * @see View#invalidate(Rect)
     */
    void invalidate(Rect bounds);

    /**
     * {@link ViewAnimationUtils#createCircularReveal(View, int, int, float, float)} is
     * called it creates new {@link io.codetail.animation.RevealAnimator.RevealInfo}
     * and attaches to parent, here is necessary data about animation
     *
     * @param info reveal information
     *
     * @see io.codetail.animation.RevealAnimator.RevealInfo
     */
    void attachRevealInfo(RevealInfo info);

    /**
     * Returns new {@link SupportAnimator} that plays
     * reversed animation of current one
     *
     * This method might be temporary, you should call
     * {@link SupportAnimator#reverse()} instead
     *
     * @hide
     * @return reverse {@link SupportAnimator}
     *
     * @see SupportAnimator#reverse()
     */
    SupportAnimator startReverseAnimation();

    class RevealInfo{
        public final int centerX;
        public final int centerY;
        public final float startRadius;
        public final float endRadius;
        public final WeakReference<View> target;

        public RevealInfo(int centerX, int centerY, float startRadius, float endRadius,
                          WeakReference<View> target) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.startRadius = startRadius;
            this.endRadius = endRadius;
            this.target = target;
        }

        public View getTarget(){
            return target.get();
        }

        public boolean hasTarget(){
            return getTarget() != null;
        }
    }

    class RevealFinishedGingerbread extends SimpleAnimationListener {
        WeakReference<io.codetail.animation.RevealAnimator> mReference;

        RevealFinishedGingerbread(io.codetail.animation.RevealAnimator target) {
            mReference = new WeakReference<>(target);
        }

        @Override
        public void onAnimationStart(Animator animation) {
            io.codetail.animation.RevealAnimator target = mReference.get();
            target.onRevealAnimationStart();
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            io.codetail.animation.RevealAnimator target = mReference.get();
            target.onRevealAnimationCancel();
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            io.codetail.animation.RevealAnimator target = mReference.get();
            target.onRevealAnimationEnd();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class RevealFinishedIceCreamSandwich extends RevealFinishedGingerbread {
        int mFeaturedLayerType;
        int mLayerType;

        RevealFinishedIceCreamSandwich(io.codetail.animation.RevealAnimator target) {
            super(target);

            mLayerType = ((View) target).getLayerType();
            mFeaturedLayerType = View.LAYER_TYPE_SOFTWARE;
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            ((View) mReference.get()).setLayerType(mLayerType, null);
            super.onAnimationEnd(animation);
        }

        @Override
        public void onAnimationStart(Animator animation) {
            ((View) mReference.get()).setLayerType(mFeaturedLayerType, null);
            super.onAnimationStart(animation);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            ((View) mReference.get()).setLayerType(mLayerType, null);
            super.onAnimationEnd(animation);
        }
    }

    class RevealFinishedJellyBeanMr2 extends RevealFinishedIceCreamSandwich {

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        RevealFinishedJellyBeanMr2(io.codetail.animation.RevealAnimator target) {
            super(target);

            mFeaturedLayerType = View.LAYER_TYPE_HARDWARE;
        }
    }

    class RevealRadius extends Property<io.codetail.animation.RevealAnimator, Float> {

        public RevealRadius() {
            super(Float.TYPE, "revealRadius");
        }

        @Override
        public void set(io.codetail.animation.RevealAnimator object, Float value) {
            object.setRevealRadius(value);
        }

        @Override
        public Float get(io.codetail.animation.RevealAnimator object) {
            return object.getRevealRadius();
        }
    }
}