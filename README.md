# library
一些常用库

在根目录的build.gradle加入仓库地址
```Java
allprojects {
    repositories {
        google()
        jcenter()
        maven { url "https://jitpack.io" }
    }
}
```

在引用处添加('?'替换最新版本号)
```Java
implementation 'com.github.zy7:library:1.?.?'
```

TitleBar，
BottomBar，
CountDownTextView，
SuperRadioGroup
