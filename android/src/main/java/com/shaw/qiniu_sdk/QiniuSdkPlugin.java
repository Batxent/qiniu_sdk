package com.shaw.qiniu_sdk;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONException;
import org.json.JSONObject;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * QiniuSdkPlugin
 */
public class QiniuSdkPlugin implements FlutterPlugin, MethodCallHandler {
    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        final MethodChannel channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "qiniu_sdk");
        channel.setMethodCallHandler(new QiniuSdkPlugin());
    }

    // This static function is optional and equivalent to onAttachedToEngine. It supports the old
    // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
    // plugin registration via this function while apps migrate to use the new Android APIs
    // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
    //
    // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
    // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
    // depending on the user's project. onAttachedToEngine or registerWith must both be defined
    // in the same class.
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "qiniu_sdk");
        channel.setMethodCallHandler(new QiniuSdkPlugin());
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        if (call.method.equals("getPlatformVersion")) {
            result.success("Android " + android.os.Build.VERSION.RELEASE);
        } else if (call.method.equals("uploadFile")) {
            String filePath = call.argument("filePath");
            String key = call.argument("key");
            String token = call.argument("token");
            Log.v("QINIU_SDK", "start: upload " + filePath + " " + key + " " + token);
            uploadFile(filePath, key, token, result);
        } else {
            result.notImplemented();
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    }

    private void uploadFile(String filePath, String customKey, String token, final Result result) {
        Configuration config = new Configuration.Builder()
                .useHttps(true)               // 是否使用https上传域名
                .build();
        UploadManager uploadManager = new UploadManager(config);
        uploadManager.put(filePath, customKey, token, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                Log.v("QINIU_SDK", "complete: upload" + key);
                Log.v("QINIU_SDK", "complete: upload" + response);
                try {
                    Log.v("QINIU_SDK", "complete: upload" + response.get("key"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                new MainThreadResult(result).success(key);
            }
        }, null);
    }

    private static class MainThreadResult implements MethodChannel.Result {
        private MethodChannel.Result result;
        private Handler handler;

        MainThreadResult(MethodChannel.Result result) {
            this.result = result;
            handler = new Handler(Looper.getMainLooper());
        }

        @Override
        public void success(final Object _result) {
            handler.post(
                    new Runnable() {
                        @Override
                        public void run() {
                          result.success(_result);
                        }
                    });
        }

        @Override
        public void error(
                final String errorCode, final String errorMessage, final Object errorDetails) {
            handler.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            result.error(errorCode, errorMessage, errorDetails);
                        }
                    });
        }

        @Override
        public void notImplemented() {
            handler.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            result.notImplemented();
                        }
                    });
        }
    }

}
