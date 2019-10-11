### CommonLib
该库是日常高频使用的工具类的封装，减少重复的工作。

#### gradle依赖
> Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:
``` gradle
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

> Step 2. Add the dependency

``` gradle
	dependencies {
	        implementation 'com.github.AlucardSama:LibraryDemo:1.0.0'
	}
```

#### 使用帮助
1. Tasty 优雅的Toast，使用方式与Toast一致
``` java
	Tasty.s("测试数据");
	Tasty.w("测试数据");
	Tasty.e("测试数据");
``` 

2. ApplicationCrashHandler 全局的异常捕获处理
需要在Application中进行初始化，并支持异常日志路径设置、奔溃后重启设置；
 ``` java
    /**
     * 奔溃日志捕获
     */
    private void initCrashHandler() {
        ApplicationCrashHandler
                .getInstance()
                .init(getApplicationContext())
                .setCrashDir(DIR_CRASH)
                .setRestartActivity(MainActivity.class);

    }
```
需要在AndroidManifest文件中添加sd卡权限
``` xml
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```
