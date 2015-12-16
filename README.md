<img src="https://github.com/GcsSloop/LeafLoading/blob/master/Art/title.png" width = "300" height = "75" alt="title" align=center />  

[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Maven Central](https://img.shields.io/bintray/v/gcssloop/maven/leafloding.svg)](https://bintray.com/gcssloop/maven/leafloding/view)
### 作者微博: [@攻城师sloop](http://weibo.com/5459430586)
## 声明
  由于该项目的创意来自于网络，并未寻找到原作者，所以可能存在侵权行为，使用前请慎重。如果您是该创意原作者，感觉侵犯了您的权利，可以在微博上联系我，侵删。
  
  另外，该项目核心实现代码来参考了一位CSDN大神的博客：详戳->[【一个绚丽的loading动效分析与实现！】](http://blog.csdn.net/tianjian4592/article/details/44538605)。
```
我在原有代码基础上进行了部分内容的修改，主要包括以下方面:
  0.添加风扇部分绘制
  1.修改了View测量逻辑
  2.对视图大小进行了适配
  3.添加了监听回调接口
  
目前仍存在的问题(待完善或者添加的功能)
  0.绑定风扇转速 树叶数量 和进度快慢之间的关系

如果你对此有好的想法欢迎在Issues中提交。
```
  
  
## 创意原型

#### 原型效果图如下：
![LeafLoading](https://github.com/GcsSloop/LeafLoading/blob/master/Art/model.gif)
#### 实现效果图如下：
![LeafLoadingDemo](https://github.com/GcsSloop/LeafLoading/blob/master/Art/loadingTest.gif)

目前实现了原型中百分之九十左右的内容。

---
## 如何使用
### 1.在布局文件中添加LeafLoding
``` xml
    <com.sloop.view.loading.LeafLoading
        android:id="@+id/loading"
        android:layout_centerInParent="true"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/
```
### 2.在Activity中找到组件
``` java
    //找到组件
    LeafLoading loading = (LeafLoading) findViewById(R.id.loading);
    //设置进度
    loading.setProgress(50);
    //PS：作为一个这么高大上的自定义View，当然不会只有一个setProgress方法了，关于其他用法，请看后续的说明文档
```

---
## 如何添加进项目中
### Android Studio
#### 1.在Project的build.gradle中添加仓库地址
```
 //sloop的仓库地址
  maven {url "http://dl.bintray.com/gcssloop/maven"}
```
示例：
```
allprojects {
    repositories {
        jcenter()
        //sloop的仓库地址
        maven {url "http://dl.bintray.com/gcssloop/maven"}
    }
}
```
#### 2.在Module目录下的build.gradle中添加依赖
```
  //leafloding
    compile 'com.sloop.view.loading:leafloading:1.0.1'
```
示例：
```
  dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    testCompile 'junit:junit:4.12'
    compile 'com.android.support:appcompat-v7:23.0.1'
    //leafloding
    compile 'com.sloop.view.loading:leafloading:1.0.0'
}
```

### Eclipse
#### 1.下载Library
#### 2.将资源文件分别拷贝进工程中
```
资源文件包括：
/res/drawable-xxxhdpi文件夹下的所有图片
代码包括：
com.sloop.view.loding.LeafLoding.java
com.sloop.view.utils.UiUtils.java
```
---
## 更新内容：
版本号 | 更新内容
 ---   |  ---
v1.0.0 | 完善基本功能，进行大小适配
v1.0.1 | 完善注释文档，添加监听回调

---
## 致谢：
  感谢原作者 [学问积年而成](http://blog.csdn.net/tianjian4592?viewmode=list) 提供的实现原型。



