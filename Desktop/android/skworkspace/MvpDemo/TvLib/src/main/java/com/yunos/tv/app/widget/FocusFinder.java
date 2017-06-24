package com.yunos.tv.app.widget;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Comparator;

public class FocusFinder {
    private static ThreadLocal<FocusFinder> tlFocusFinder = new ThreadLocal() {
        protected FocusFinder initialValue() {
            return new FocusFinder();
        }
    };
    Rect mFocusedRect = new Rect();
    Rect mOtherRect = new Rect();
    Rect mBestCandidateRect = new Rect();
    ArrayList<View> mFocusables = new ArrayList(24);
    FocusFinder.SequentialFocusComparator mSequentialFocusComparator = new FocusFinder.SequentialFocusComparator();
    ViewGroup mRoot;

    public static FocusFinder getInstance() {
        return (FocusFinder)tlFocusFinder.get();
    }

    public FocusFinder() {
    }

    public void initFocusables(ViewGroup root) {
        for(int index = 0; index < root.getChildCount(); ++index) {
            root.getChildAt(index);
            this.mFocusables.add(root.getChildAt(index));
        }

    }

    public void addFocusable(View child) {
        if(child.isFocusable()) {
            this.mFocusables.add(child);
        }

    }

    public void removeFocusable(View child) {
        this.mFocusables.remove(child);
    }

    public void clearFocusables() {
        this.mFocusables.clear();
    }

    public ViewGroup getRoot() {
        return this.mSequentialFocusComparator.getRoot();
    }

    public final View findNextFocus(ViewGroup root, View focused, int direction) {
        if(focused != null) {
            View rootTop = this.findUserSetNextFocus(root, direction, focused);
            if(rootTop != null && rootTop.isFocusable() && (!rootTop.isInTouchMode() || rootTop.isFocusableInTouchMode())) {
                return rootTop;
            }

            focused.getFocusedRect(this.mFocusedRect);
            root.offsetDescendantRectToMyCoords(focused, this.mFocusedRect);
        } else {
            switch(direction) {
                case 1:
                case 17:
                case 33:
                    int rootBottom = root.getScrollY() + root.getHeight();
                    int rootRight = root.getScrollX() + root.getWidth();
                    this.mFocusedRect.set(rootRight, rootBottom, rootRight, rootBottom);
                    break;
                case 2:
                case 66:
                case 130:
                    int rootTop1 = root.getScrollY();
                    int rootLeft = root.getScrollX();
                    this.mFocusedRect.set(rootLeft, rootTop1, rootLeft, rootTop1);
            }
        }

        return this.findNextFocus(root, focused, this.mFocusedRect, direction);
    }

    Rect getRect(View focused) {
        Rect rect = new Rect();
        int[] location = new int[2];
        focused.getLocationOnScreen(location);
        rect.set(location[0], location[1], location[0] + focused.getWidth(), location[1] + focused.getHeight());
        return rect;
    }

    View findUserSetNextFocus(View root, int direction, View v) {
        switch(direction) {
            case 2:
            case 17:
                if(v.getNextFocusLeftId() == -1) {
                    return null;
                }

                return root.findViewById(v.getNextFocusLeftId());
            case 33:
                if(v.getNextFocusUpId() == -1) {
                    return null;
                }

                return root.findViewById(v.getNextFocusUpId());
            case 66:
                if(v.getNextFocusRightId() == -1) {
                    return null;
                }

                return root.findViewById(v.getNextFocusRightId());
            case 130:
                if(v.getNextFocusDownId() == -1) {
                    return null;
                }

                return root.findViewById(v.getNextFocusDownId());
            default:
                return null;
        }
    }

    public View findNextFocusFromRect(ViewGroup root, Rect focusedRect, int direction) {
        return this.findNextFocus(root, (View)null, focusedRect, direction);
    }

    private View findNextFocus(ViewGroup root, View focused, Rect focusedRect, int direction) {
        ArrayList focusables = this.mFocusables;
        if(focusables.isEmpty()) {
            return null;
        } else {
            if(this.mSequentialFocusComparator.getRoot() == null) {
                this.mSequentialFocusComparator.setRoot((ViewGroup)root.getRootView());
            }

            this.mBestCandidateRect.set(focusedRect);
            switch(direction) {
                case 1:
                case 66:
                    direction = 66;
                    this.mBestCandidateRect.offset(-(focusedRect.width() + 1), 0);
                    break;
                case 2:
                case 17:
                    direction = 17;
                    this.mBestCandidateRect.offset(focusedRect.width() + 1, 0);
                    break;
                case 33:
                    this.mBestCandidateRect.offset(0, focusedRect.height() + 1);
                    break;
                case 130:
                    this.mBestCandidateRect.offset(0, -(focusedRect.height() + 1));
            }

            View closest = null;
            int numFocusables = focusables.size();

            for(int i = 0; i < numFocusables; ++i) {
                View focusable = (View)focusables.get(i);
                if(focusable.isFocusable() && focusable != focused && focusable != root && focusable.getVisibility() == View.VISIBLE) {
                    focusable.getFocusedRect(this.mOtherRect);
                    root.offsetDescendantRectToMyCoords(focusable, this.mOtherRect);
                    if(this.isBetterCandidate(direction, focusedRect, this.mOtherRect, this.mBestCandidateRect)) {
                        this.mBestCandidateRect.set(this.mOtherRect);
                        closest = focusable;
                    }
                }
            }

            return closest;
        }
    }

    boolean isBetterCandidate(int direction, Rect source, Rect rect1, Rect rect2) {
        return !this.isCandidate(source, rect1, direction)?false:(!this.isCandidate(source, rect2, direction)?true:(this.beamBeats(direction, source, rect1, rect2)?true:(this.beamBeats(direction, source, rect2, rect1)?false:this.getWeightedDistanceFor(majorAxisDistance(direction, source, rect1), minorAxisDistance(direction, source, rect1)) < this.getWeightedDistanceFor(majorAxisDistance(direction, source, rect2), minorAxisDistance(direction, source, rect2)))));
    }

    boolean beamBeats(int direction, Rect source, Rect rect1, Rect rect2) {
        boolean rect1InSrcBeam = this.beamsOverlap(direction, source, rect1);
        boolean rect2InSrcBeam = this.beamsOverlap(direction, source, rect2);
        return !rect2InSrcBeam && rect1InSrcBeam?(!this.isToDirectionOf(direction, source, rect2)?true:(direction != 17 && direction != 66?majorAxisDistance(direction, source, rect1) < majorAxisDistanceToFarEdge(direction, source, rect2):true)):false;
    }

    float getWeightedDistanceFor(int majorAxisDistance, int minorAxisDistance) {
        float floatAxisDistance = (float)majorAxisDistance / 100.0F;
        float floatMinorAxisDistance = (float)minorAxisDistance / 100.0F;
        return 13.0F * floatAxisDistance * floatAxisDistance + floatMinorAxisDistance * floatMinorAxisDistance;
    }

    boolean isCandidate(Rect srcRect, Rect destRect, int direction) {
        boolean diff = true;
        switch(direction) {
            case 1:
            case 66:
                return (srcRect.left < destRect.left || srcRect.right <= destRect.left) && srcRect.right < destRect.right && (srcRect.top <= destRect.top && srcRect.bottom >= destRect.top || srcRect.top <= destRect.bottom && srcRect.bottom >= destRect.bottom || srcRect.top >= destRect.top && srcRect.bottom <= destRect.bottom);
            case 2:
            case 17:
                if(srcRect.right <= destRect.right && srcRect.left < destRect.right || srcRect.left <= destRect.left || (srcRect.top > destRect.top || srcRect.bottom < destRect.top) && (srcRect.top > destRect.bottom || srcRect.bottom < destRect.bottom) && (srcRect.top < destRect.top || srcRect.bottom > destRect.bottom)) {
                    return false;
                }

                return true;
            case 33:
                if((srcRect.bottom > destRect.bottom || srcRect.top >= destRect.bottom) && srcRect.top > destRect.top && (srcRect.left <= destRect.left && srcRect.right >= destRect.left || srcRect.left <= destRect.right && srcRect.right >= destRect.right || srcRect.left >= destRect.left && srcRect.right <= destRect.right)) {
                    return true;
                }

                return false;
            case 130:
                if(srcRect.top >= destRect.top && srcRect.bottom > destRect.top || srcRect.bottom >= destRect.bottom || (srcRect.left > destRect.left || srcRect.right < destRect.left) && (srcRect.left > destRect.right || srcRect.right < destRect.right) && (srcRect.left < destRect.left || srcRect.right > destRect.right)) {
                    return false;
                }

                return true;
            default:
                throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
        }
    }

    boolean beamsOverlap(int direction, Rect rect1, Rect rect2) {
        switch(direction) {
            case 1:
            case 2:
            case 17:
            case 66:
                if(rect2.bottom >= rect1.top && rect2.top <= rect1.bottom) {
                    return true;
                }

                return false;
            case 33:
            case 130:
                if(rect2.right >= rect1.left && rect2.left <= rect1.right) {
                    return true;
                }

                return false;
            default:
                throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
        }
    }

    boolean isToDirectionOf(int direction, Rect src, Rect dest) {
        switch(direction) {
            case 1:
            case 66:
                if(src.right <= dest.left) {
                    return true;
                }

                return false;
            case 2:
            case 17:
                if(src.left >= dest.right) {
                    return true;
                }

                return false;
            case 33:
                if(src.top >= dest.bottom) {
                    return true;
                }

                return false;
            case 130:
                if(src.bottom <= dest.top) {
                    return true;
                }

                return false;
            default:
                throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
        }
    }

    static int majorAxisDistance(int direction, Rect source, Rect dest) {
        return Math.max(0, majorAxisDistanceRaw(direction, source, dest));
    }

    static int majorAxisDistanceRaw(int direction, Rect source, Rect dest) {
        switch(direction) {
            case 1:
            case 66:
                return dest.left - source.right;
            case 2:
            case 17:
                return source.left - dest.right;
            case 33:
                return source.top - dest.bottom;
            case 130:
                return dest.top - source.bottom;
            default:
                throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
        }
    }

    static int majorAxisDistanceToFarEdge(int direction, Rect source, Rect dest) {
        return Math.max(1, majorAxisDistanceToFarEdgeRaw(direction, source, dest));
    }

    static int majorAxisDistanceToFarEdgeRaw(int direction, Rect source, Rect dest) {
        switch(direction) {
            case 1:
            case 66:
                return dest.right - source.right;
            case 2:
            case 17:
                return source.left - dest.left;
            case 33:
                return source.top - dest.top;
            case 130:
                return dest.bottom - source.bottom;
            default:
                throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
        }
    }

    static int minorAxisDistance(int direction, Rect source, Rect dest) {
        switch(direction) {
            case 1:
            case 2:
            case 17:
            case 66:
                return Math.abs(source.top + source.height() / 2 - (dest.top + dest.height() / 2));
            case 33:
            case 130:
                return Math.abs(source.left + source.width() / 2 - (dest.left + dest.width() / 2));
            default:
                throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
        }
    }

    private boolean isTouchCandidate(int x, int y, Rect destRect, int direction) {
        switch(direction) {
            case 17:
                if(destRect.left <= x && destRect.top <= y && y <= destRect.bottom) {
                    return true;
                }

                return false;
            case 33:
                if(destRect.top <= y && destRect.left <= x && x <= destRect.right) {
                    return true;
                }

                return false;
            case 66:
                if(destRect.left >= x && destRect.top <= y && y <= destRect.bottom) {
                    return true;
                }

                return false;
            case 130:
                if(destRect.top >= y && destRect.left <= x && x <= destRect.right) {
                    return true;
                }

                return false;
            default:
                throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
        }
    }

    private static final class SequentialFocusComparator implements Comparator<View> {
        private final Rect mFirstRect;
        private final Rect mSecondRect;
        private ViewGroup mRoot;

        private SequentialFocusComparator() {
            this.mFirstRect = new Rect();
            this.mSecondRect = new Rect();
        }

        public void recycle() {
            this.mRoot = null;
        }

        public void setRoot(ViewGroup root) {
            this.mRoot = root;
        }

        public ViewGroup getRoot() {
            return this.mRoot;
        }

        public int compare(View first, View second) {
            if(first == second) {
                return 0;
            } else {
                this.getRect(first, this.mFirstRect);
                this.getRect(second, this.mSecondRect);
                return this.mFirstRect.top < this.mSecondRect.top?-1:(this.mFirstRect.top > this.mSecondRect.top?1:(this.mFirstRect.left < this.mSecondRect.left?-1:(this.mFirstRect.left > this.mSecondRect.left?1:(this.mFirstRect.bottom < this.mSecondRect.bottom?-1:(this.mFirstRect.bottom > this.mSecondRect.bottom?1:(this.mFirstRect.right < this.mSecondRect.right?-1:(this.mFirstRect.right > this.mSecondRect.right?1:0)))))));
            }
        }

        private void getRect(View view, Rect rect) {
            view.getDrawingRect(rect);
            this.mRoot.offsetDescendantRectToMyCoords(view, rect);
        }
    }
}
