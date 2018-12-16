/*
 * Copyright (c) 2017 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hcmus.mdsd.itusgallery;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import hcmus.mdsd.itusgallery.camera.GraphicOverlay;
class FaceGraphic extends GraphicOverlay.Graphic {
    private static final String TAG = "FaceGraphic";
    private static final float DOT_RADIUS = 3.0f;
    private static final float TEXT_OFFSET_Y = -30.0f;
    private boolean mIsFrontFacing;
    private Integer mChoice;
    // This variable may be written to by one of many threads. By declaring it as volatile,
    // we guarantee that when we read its contents, we're reading the most recent "write"
    // by any thread.
    private volatile FaceData mFaceData;
    private Drawable mPigNoseGraphic;
    private Drawable mMustacheGraphic;
    private Drawable mHappyStarGraphic;
    private Drawable mHatGraphic;
    private Drawable mGlassesGraphic;
    private Drawable mThuglifeGlassesGraphic;
    FaceGraphic(GraphicOverlay overlay, Context context, boolean isFrontFacing, Integer choice) {
        super(overlay);
        mIsFrontFacing = isFrontFacing;
        mChoice = choice;
        Resources resources = context.getResources();
        initializeGraphics(resources);
    }
    private void initializeGraphics(Resources resources) {
        mPigNoseGraphic = resources.getDrawable(R.drawable.pig_nose_emoji);
        mMustacheGraphic = resources.getDrawable(R.drawable.mustache);
        mHappyStarGraphic = resources.getDrawable(R.drawable.happy_star);
        mHatGraphic = resources.getDrawable(R.drawable.red_hat);
        mGlassesGraphic= resources.getDrawable(R.drawable.glasses);
        mThuglifeGlassesGraphic= resources.getDrawable(R.drawable.thug_life_glasses);
    }
    void update(FaceData faceData) {
        mFaceData = faceData;
        postInvalidate(); // Trigger a redraw of the graphic (i.e. cause draw() to be called).
    }
    @Override
    public void draw(Canvas canvas) {
        final float DOT_RADIUS = 3.0f;
        final float TEXT_OFFSET_Y = -30.0f;
        // Confirm that the face and its features are still visible
        // before drawing any graphics over it.
        if (mFaceData == null) {
            return;
        }
        PointF detectPosition = mFaceData.getPosition();
        PointF detectLeftEyePosition = mFaceData.getLeftEyePosition();
        PointF detectRightEyePosition = mFaceData.getRightEyePosition();
        PointF detectNoseBasePosition = mFaceData.getNoseBasePosition();
        PointF detectMouthLeftPosition = mFaceData.getMouthLeftPosition();
        PointF detectMouthBottomPosition = mFaceData.getMouthBottomPosition();
        PointF detectMouthRightPosition = mFaceData.getMouthRightPosition();
        PointF detectLeftCheekPosition = mFaceData.getLeftCheekPosition();
        PointF detectRightCheekPosition = mFaceData.getRightCheekPosition();
        if ((detectPosition == null) ||
                (detectLeftEyePosition == null) ||
                (detectRightEyePosition == null) ||
                (detectNoseBasePosition == null) ||
                (detectMouthLeftPosition == null) ||
                (detectMouthBottomPosition == null) ||
                (detectMouthRightPosition == null) ||
                (detectLeftCheekPosition == null) ||
                (detectRightCheekPosition == null)){
            return;
        }
        // Face position and dimensions
        PointF position = new PointF(translateX(detectPosition.x),
                translateY(detectPosition.y));
        float width = scaleX(mFaceData.getWidth());
        float height = scaleY(mFaceData.getHeight());
        // Eye coordinates
        PointF leftEyePosition = new PointF(translateX(detectLeftEyePosition.x),
                translateY(detectLeftEyePosition.y));
        PointF rightEyePosition = new PointF(translateX(detectRightEyePosition.x),
                translateY(detectRightEyePosition.y));
        PointF leftCheekPosition = new PointF(translateX(detectLeftCheekPosition.x),
                translateY(detectLeftCheekPosition.y));
        PointF rightCheekPosition = new PointF(translateX(detectRightCheekPosition.x),
                translateY(detectRightCheekPosition.y));
        // Eye state
        boolean leftEyeOpen = mFaceData.isLeftEyeOpen();
        boolean rightEyeOpen = mFaceData.isRightEyeOpen();
        // Nose coordinates
        PointF noseBasePosition = new PointF(translateX(detectNoseBasePosition.x),
                translateY(detectNoseBasePosition.y));
        // Mouth coordinates
        PointF mouthLeftPosition = new PointF(translateX(detectMouthLeftPosition.x),
                translateY(detectMouthLeftPosition.y));
        PointF mouthRightPosition = new PointF(translateX(detectMouthRightPosition.x),
                translateY(detectMouthRightPosition.y));
        PointF mouthBottomPosition = new PointF(translateX(detectMouthBottomPosition.x),
                translateY(detectMouthBottomPosition.y));
        // Calculate the distance between the eyes using Pythagoras' formula,
        // and we'll use that distance to set the size of the eyes and irises.
        final float EYE_RADIUS_PROPORTION = 0.45f;
        final float IRIS_RADIUS_PROPORTION = EYE_RADIUS_PROPORTION / 2.0f;
        float distance = (float) Math.sqrt(
                (rightEyePosition.x - leftEyePosition.x) * (rightEyePosition.x - leftEyePosition.x) +
                        (rightEyePosition.y - leftEyePosition.y) * (rightEyePosition.y - leftEyePosition.y));
        float eyeRadius = EYE_RADIUS_PROPORTION * distance;
        float irisRadius = IRIS_RADIUS_PROPORTION * distance;
        // Draw the eyes.
        if (mChoice == 1) {
            drawEye(canvas, leftEyePosition, eyeRadius, leftEyeOpen);
            drawEye(canvas, rightEyePosition, eyeRadius, rightEyeOpen);
        } else if (mChoice == 2) {
            drawGlasses(canvas,noseBasePosition,leftCheekPosition,rightCheekPosition,leftEyePosition,rightEyePosition);
        } else if (mChoice == 3) {
            drawThuglifeGlasses(canvas,noseBasePosition,leftCheekPosition,rightCheekPosition,leftEyePosition,rightEyePosition);
        } else if (mChoice == 4) {
            // Draw the nose.
            drawNose(canvas, noseBasePosition, leftEyePosition, rightEyePosition, width);
        } else if (mChoice == 5) {
            // Draw the mustache.
            drawMustache(canvas, noseBasePosition, mouthLeftPosition, mouthRightPosition, leftCheekPosition, rightCheekPosition);
        }
        else if(mChoice ==6){
            // Head tilt
            float eulerY = mFaceData.getEulerY();
            float eulerZ = mFaceData.getEulerZ();
            // Draw the hat only if the subject's head is titled at a sufficiently jaunty angle.
            final float HEAD_TILT_HAT_THRESHOLD = 20.0f;
            //if (Math.abs(eulerZ) > HEAD_TILT_HAT_THRESHOLD) {
            drawHat(canvas, position, width, height, noseBasePosition);
            //}
        }
    }
    private void drawEye(Canvas canvas,
                         PointF eyePosition, float eyeRadius,
                         boolean eyeOpen) {
        if (eyeOpen) {
            mHappyStarGraphic.setBounds(
                    (int)(eyePosition.x - eyeRadius),
                    (int)(eyePosition.y - eyeRadius),
                    (int)(eyePosition.x + eyeRadius),
                    (int)(eyePosition.y + eyeRadius));
            mHappyStarGraphic.draw(canvas);
        }
    }
    private void drawNose(Canvas canvas,
                          PointF noseBasePosition,
                          PointF leftEyePosition, PointF rightEyePosition,
                          float faceWidth) {
        final float NOSE_FACE_WIDTH_RATIO = (float)(1 / 4.5);
        float noseWidth = faceWidth * NOSE_FACE_WIDTH_RATIO;
        int left = (int)(noseBasePosition.x - (noseWidth / 2));
        int right = (int)(noseBasePosition.x + (noseWidth / 2));
        int top = (int)((leftEyePosition.y + rightEyePosition.y) / 2);
        int bottom = (int)noseBasePosition.y;
        mPigNoseGraphic.setBounds(left-5, top+30, right+5, bottom+45);
        mPigNoseGraphic.draw(canvas);
    }
    private void drawMustache(Canvas canvas,
                              PointF noseBasePosition,
                              PointF mouthLeftPosition, PointF mouthRightPosition,
                              PointF leftCheekPosition, PointF rightCheekPosition) {
        int left = (int)leftCheekPosition.x;
        int top = (int)noseBasePosition.y;
        int right = (int)rightCheekPosition.x;
        int bottom = (int)Math.min(mouthLeftPosition.y, mouthRightPosition.y);
        if (mIsFrontFacing) {
            mMustacheGraphic.setBounds(left, top, right, bottom);
        } else {
            mMustacheGraphic.setBounds(right, top, left, bottom);
        }
        mMustacheGraphic.draw(canvas);
    }
    private void drawGlasses(Canvas canvas,
                             PointF noseBasePosition,
                             PointF leftCheekPosition,PointF rightCheekPosition,
                             PointF leftEyePosition,PointF rightEyePosition) {
        int left = (int)leftCheekPosition.x-115;
        int top = (int)leftEyePosition.y-120;
        int right = (int)rightCheekPosition.x+120;
        int bottom = (int)noseBasePosition.y-50;
        mGlassesGraphic.setBounds(left, top, right, bottom);
        mGlassesGraphic.draw(canvas);
    }
    private void drawThuglifeGlasses(Canvas canvas,
                                     PointF noseBasePosition,
                                     PointF leftCheekPosition,PointF rightCheekPosition,
                                     PointF leftEyePosition,PointF rightEyePosition) {
        int left = (int)leftCheekPosition.x-115;
        int top = (int)leftEyePosition.y-120;
        int right = (int)rightCheekPosition.x+120;
        int bottom = (int)noseBasePosition.y+10;
        mThuglifeGlassesGraphic.setBounds(left, top, right, bottom);
        mThuglifeGlassesGraphic.draw(canvas);
    }
    private void drawHat(Canvas canvas, PointF facePosition, float faceWidth, float faceHeight, PointF noseBasePosition) {
        final float HAT_FACE_WIDTH_RATIO = (float)(1.0 / 4.0);
        final float HAT_FACE_HEIGHT_RATIO = (float)(1.0 / 6.0);
        final float HAT_CENTER_Y_OFFSET_FACTOR = (float)(1.0 / 8.0);
        float hatCenterY = facePosition.y + (faceHeight * HAT_CENTER_Y_OFFSET_FACTOR);
        float hatWidth = faceWidth * HAT_FACE_WIDTH_RATIO;
        float hatHeight = faceHeight * HAT_FACE_HEIGHT_RATIO;
        int left = (int)(noseBasePosition.x - (hatWidth / 2));
        int right = (int)(noseBasePosition.x + (hatWidth / 2));
        int top = (int)(hatCenterY - (hatHeight / 2));
        int bottom = (int)(hatCenterY + (hatHeight / 2));
        mHatGraphic.setBounds(left, top, right, bottom);
        mHatGraphic.draw(canvas);
    }
}