#include "FillPolygonImpl.h"

#include "Line.h"
#include "Point.h"

#include <iostream>
#include <unordered_map>
#include <unordered_set>
#include <set>
#include <vector>

static bool transform_to_pointer(JNIEnv *env, jintArray array, int **buf, int *length = NULL) {
    jboolean isCopy;
    if (length != NULL) {
      *length = env->GetArrayLength(array);
    }
    *buf = (jint*) env->GetPrimitiveArrayCritical(array, &isCopy);
    if (!isCopy) {
      return false;
    }
    return true;
}

static void release_pointer(JNIEnv *env, jintArray array, int *buf) {
    env->ReleasePrimitiveArrayCritical(array, buf, JNI_ABORT);
}

static std::set<int> calcCrossingPoints(int y, const std::unordered_set<Line> &crossingSet) {
    std::set<int> crossPoints;
    for (const Line& elem: crossingSet) {
        int x = elem.getXByY(y);
        if (crossPoints.find(x) != crossPoints.end()) {
            crossPoints.erase(x);
        } else {
            crossPoints.insert(x);
        }
    }
    assert (crossPoints.size() % 2 == 0);
    return crossPoints;
}

JNIEXPORT void JNICALL Java_com_orangebyte256_convexcopier_fillpolygonimpl_FillPolygonImpl_fillPolygonJNI
  (JNIEnv *env, jobject clz, jintArray image, jint imageWidth, jintArray coords, jintArray pattern, jint patternWidth, jint parallelism) {
    int *imagePixels, *patternPixels, coordsSize, *coordsArray;
    transform_to_pointer(env, image, &imagePixels);
    transform_to_pointer(env, pattern, &patternPixels);
    transform_to_pointer(env, coords, &coordsArray, &coordsSize);
    std::unordered_map<int, std::vector<Line> > linesPerHorizonUpperPoint;
    std::unordered_map<int, std::vector<Line> > linesPerHorizonBottomPoint;

    int minY = INT_MAX, maxY = INT_MIN;
    int pointsCount = coordsSize / 2;
    for (int i = 0; i < pointsCount; i++) {
      minY = std::min(minY, coordsArray[i * 2 + 1]);
      maxY = std::max(maxY, coordsArray[i * 2 + 1]);
      Point upper = Point(coordsArray[i * 2], coordsArray[i * 2 + 1]);
      Point bottom = Point(coordsArray[((i + 1) % pointsCount) * 2], coordsArray[((i + 1) % pointsCount) * 2 + 1]);
      if (upper.y < bottom.y) {
        std::swap(upper, bottom);
      }
      Line line = Line(upper, bottom);
      linesPerHorizonUpperPoint[upper.y].push_back(line);
      linesPerHorizonBottomPoint[bottom.y].push_back(line);
    }

    std::unordered_set<Line> crossingSet;
    for (int y = maxY; y >= minY; y--) {
      if (linesPerHorizonUpperPoint.find(y) != linesPerHorizonUpperPoint.end()) {
        for (const Line& elem: linesPerHorizonUpperPoint[y]) {
          crossingSet.insert(elem);
        }
      }
      if (linesPerHorizonBottomPoint.find(y) != linesPerHorizonBottomPoint.end()) {
        for (const Line& elem: linesPerHorizonBottomPoint[y]) {
          crossingSet.erase(elem);
        }
      }

      std::set<int> crossPoints = calcCrossingPoints(y, crossingSet);
      auto orderedPoints = crossPoints.begin();
      while (orderedPoints != crossPoints.end()) {
        int left = *orderedPoints++;
        int right = *orderedPoints++;
        std::memcpy(imagePixels + y * imageWidth + left, patternPixels + y * patternWidth + left, (right - left) * sizeof(int));
      }
    }

    release_pointer(env, image, imagePixels);
    release_pointer(env, pattern, patternPixels);
    release_pointer(env, coords, coordsArray);
  }