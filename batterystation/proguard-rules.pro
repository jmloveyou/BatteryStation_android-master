# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}

-keep   class com.amap.api.services.**{*;}

-keep class com.amap.api.maps2d.**{*;}
-keep class com.amap.api.mapcore2d.**{*;}

-keep class com.tencent.mm.sdk.** {
   *;
}

 -keep class com.amap.api.navi.**{*;}
 -keep class com.autonavi.**{*;}


# 支付宝混淆处理
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}
-keep class com.alipay.sdk.app.H5PayCallback {
    <fields>;
    <methods>;
}
-keep class com.alipay.android.phone.mrpc.core.** { *; }
-keep class com.alipay.apmobilesecuritysdk.** { *; }
-keep class com.alipay.mobile.framework.service.annotation.** { *; }
-keep class com.alipay.mobilesecuritysdk.face.** { *; }
-keep class com.alipay.tscenter.biz.rpc.** { *; }
-keep class org.json.alipay.** { *; }
-keep class com.alipay.tscenter.** { *; }
-keep class com.ta.utdid2.** { *;}
-keep class com.ut.device.** { *;}

-dontwarn android.net.**
-keep class android.net.SSLCertificateSocketFactory{*;}

-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-keepattributes EnclosingMetho

#指定代码的压缩级别
-optimizationpasses 5

#包明不混合大小写
-dontusemixedcaseclassnames

#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses

 #优化  不优化输入的类文件
-dontoptimize

 #预校验
-dontpreverify

 #混淆时是否记录日志
-verbose

 # 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#保护注解
-keepattributes *Annotation*
 #如果引用了v4或者v7包
    -dontwarn android.support.**
#保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

#保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

#保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

#保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

#保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable

#保持 Serializable 不被混淆并且enum 类也不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#保持枚举 enum 类不被混淆 如果混淆报错，建议直接使用上面的 -keepclassmembers class * implements java.io.Serializable即可
#-keepclassmembers enum * {
#  public static **[] values();
#  public static ** valueOf(java.lang.String);
#}

-keepclassmembers class * {
    public void *ButtonClicked(android.view.View);
}
# retrofit不混淆
-dontwarn okio.**
-dontwarn javax.annotation.**

#不混淆资源类
-keepclassmembers class **.R$* {
    public static <fields>;
}

-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

-keepattributes EnclosingMethod

-dontwarn com.immotor.batterystation.android.database.**
-keep class com.immotor.batterystation.android.database.**{*;}

-dontwarn com.immotor.batterystation.android.entity.**
-keep class com.immotor.batterystation.android.entity.**{*;}

-dontwarn com.immotor.batterystation.android.http.**
-keep class com.immotor.batterystation.android.http.**{*;}

-dontwarn com.immotor.batterystation.android.utils.**
-keep class com.immotor.batterystation.android.**{*;}


#避免混淆泛型 如果混淆报错建议关掉

-dontwarn com.google.zxing.client.android.camera.**
-keep class com.google.zxing.client.android.camera.**{*;}

-dontwarn android.support.graphics.drawable.**
-keep class android.support.graphics.drawable.**{*;}

-dontwarn android.support.v7.**
-keep class android.support.v7.**{*;}

-dontwarn android.support.v4.**
-keep class android.support.v4.**{*;}

-dontwarn me.relex.circleindicator.**
-keep class me.relex.circleindicator.**{*;}

-dontwarn com.google.zxing.**
-keep class com.google.zxing.**{*;}

-dontwarn android.support.design.**
-keep class android.support.design.**{*;}

-dontwarn com.google.**
-keep class com.google.**{*;}

-dontwarn freemarker.cache.**
-keep class freemarker.cache.**{*;}

-dontwarn com.bumptech.glide.**
-keep class com.bumptech.glide.**{*;}

-dontwarn de.greenrobot.dao.**
-keep class de.greenrobot.dao.**{*;}

-dontwarn de.greenrobot.daogenerator.**
-keep class de.greenrobot.daogenerator.**{*;}

-dontwarn com.google.gson.**
-keep class com.google.gson.**{*;}

-dontwarn org.hamcrest.**
-keep class org.hamcrest.**{*;}

-dontwarn lecho.lib.hellocharts.**
-keep class lecho.lib.hellocharts.**{*;}

-dontwarn junit.**
-keep class junit.**{*;}

-dontwarn com.liulishuo.filedownloader.**
-keep class com.liulishuo.filedownloader.**{*;}

-dontwarn com.viewpagerindicator.**
-keep class com.viewpagerindicator.**{*;}

-dontwarn okhttp3.logging.**
-keep class okhttp3.logging.**{*;}

-dontwarn internal.**
-keep class internal.**{*;}

-dontwarn com.zhy.http.okhttp.**
-keep class com.zhy.http.okhttp.**{*;}

-dontwarn okio.**
-keep class okio.**{*;}

-dontwarn android.support.percent.**
-keep class android.support.percent.**{*;}

-dontwarn com.makeramen.roundedimageview.**
-keep class com.makeramen.roundedimageview.**{*;}

-dontwarn rx.**
-keep class rx.**{*;}


-dontwarn android.support.annotation.**
-keep class android.support.annotation.**{*;}

-dontwarn android.support.graphics.drawable.**
-keep class android.support.graphics.drawable.**{*;}

-dontwarn com.github.yoojia.**
-keep class com.github.yoojia.**{*;}

-dontwarn com.amap.api.**
-keep class com.amap.api.**{*;}

-dontwarn com.autonavi.aps.amapapi.model.**
-keep class com.autonavi.aps.amapapi.model.**{*;}

-dontwarn com.loc.**
-keep class com.loc.**{*;}

-dontwarn org.jcaki.**
-keep class org.jcaki.**{*;}

-dontwarn simplesound.**
-keep class simplesound.**{*;}

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-keep public class com.immotor.batterystation.android.R$*{
public static final int *;
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keepattributes *Annotation*
-keepclassmembers class ** {
@org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
<init>(java.lang.Throwable);
}