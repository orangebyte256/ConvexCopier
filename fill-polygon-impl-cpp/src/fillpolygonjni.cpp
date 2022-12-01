#include "fillpolygonjni.h"

#include "fillpolygonimpl.h"

#include <vector>

static void transform_to_pointer(JNIEnv *env, jintArray array, int **buf, int *length = NULL) {
    jboolean isCopy;
    if (length != NULL) {
      *length = env->GetArrayLength(array);
    }
    *buf = (jint*) env->GetPrimitiveArrayCritical(array, &isCopy);
    assert (!isCopy);
}

static void release_pointer(JNIEnv *env, jintArray array, int *buf) {
    env->ReleasePrimitiveArrayCritical(array, buf, JNI_ABORT);
}

JNIEXPORT void JNICALL Java_com_orangebyte256_convexcopier_imageeditor_ImageEditor_fillPolygonJNI
  (JNIEnv *env, jobject, jintArray image, jint imageWidth, jintArray coords, jintArray pattern, jint patternWidth, jint parallelism, int anchorX, int anchorY) {
    int *imagePixels, *patternPixels, coordsSize, *coordsArray;
    transform_to_pointer(env, image, &imagePixels);
    transform_to_pointer(env, pattern, &patternPixels);
    transform_to_pointer(env, coords, &coordsArray, &coordsSize);

    FillPolygonImpl fillPolygonImpl(imagePixels, imageWidth, patternPixels, patternWidth);
    fillPolygonImpl.fillPolygon(coordsSize, coordsArray, parallelism, anchorX, anchorY);

    release_pointer(env, image, imagePixels);
    release_pointer(env, pattern, patternPixels);
    release_pointer(env, coords, coordsArray);
  }