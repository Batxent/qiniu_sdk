import 'dart:async';

import 'package:flutter/services.dart';

class QiniuSdk {
  static const MethodChannel _channel = const MethodChannel('qiniu_sdk');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<String> uploadFile(
      String filePath, String key, String token) async {
    Map param = Map();
    param["filePath"] = filePath;
    param["key"] = key;
    param["token"] = token;
    return await _channel.invokeMethod("uploadFile", param);
  }
}
