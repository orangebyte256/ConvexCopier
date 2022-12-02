# ConvexCopier
### Overview:
Current application allows to copy selected polygon from one image to another.

### Setup:
Current application use JNI so firstly you need to build shared library.
```
cd %ROOT_DIR%
cd fill-polygon-impl-cpp
mkdir build
cd build
cmake ../src
make
./tests ; to ensure everything OK
```
After that you will see `libfill_polygon_impl_cpp.dylib` in build dir
Now we will build jar with application itself.
```
cd %ROOT_DIR%
mvn package ; it will automaticly starts test
```
Now in directory `image-editor/target` you will have `image-editor-1.0-jar-with-dependencies.jar`.

### Using:
For testing support you can use images inside example folder. Let's start: <br>
Application contains from two modules. <br>
First modules allows to create polygon. You can form polygon by adding vertex via mouse clicks. When you will have finished forming polygon press Enter. 
```
cd example
java -Djava.library.path=../fill-polygon-impl-cpp/build -jar ../image-editor/target/image-editor-1.0-jar-with-dependencies.jar -create penguins.jpg coords.txt
```
Such command will create `coords.txt` with polygon's coords. If everything correct, you will get such message:
<br>
<p align="center">
<img src="https://github.com/orangebyte256/ConvexCopier/blob/d8967c17e6cb0efb0d0318c64daaaf376293f22a/readme/screenshot.png" alt="drawing" width="500"/>
</p>
Second part makes copy. You choose source and pattern files, coords file, anchore point(offset for insertion), and enabling/disabling of using JNI implementation

```
java -Djava.library.path=../fill-polygon-impl-cpp/build -jar ../image-editor/target/image-editor-1.0-jar-with-dependencies.jar -copy green.jpg penguins.jpg coords.txt 0 0 jni
```
As result you will get `result.png` file with created image.
<p align="center">
<img src="https://github.com/orangebyte256/ConvexCopier/blob/d8967c17e6cb0efb0d0318c64daaaf376293f22a/readme/result.png" alt="drawing" width="500"/>
</p>

That's all! Enjoy!
