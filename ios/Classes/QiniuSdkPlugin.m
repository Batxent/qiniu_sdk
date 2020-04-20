#import "QiniuSdkPlugin.h"
#import <Qiniu/QiniuSDK.h>

@implementation QiniuSdkPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar
{
    FlutterMethodChannel* channel = [FlutterMethodChannel
                                     methodChannelWithName:@"qiniu_sdk"
                                     binaryMessenger:[registrar messenger]];
    QiniuSdkPlugin* instance = [[QiniuSdkPlugin alloc] init];
    [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result
{
    if ([@"getPlatformVersion" isEqualToString:call.method]) {
        result([@"iOS " stringByAppendingString:[[UIDevice currentDevice] systemVersion]]);
    } else if ([@"uploadFile" isEqualToString:call.method]) {
        NSString *filePath = call.arguments[@"filePath"];
        NSString *key = call.arguments[@"key"];
        NSString *token = call.arguments[@"token"];
        [self uploadFile:filePath key:key token:token result:result];
    }else {
        result(FlutterMethodNotImplemented);
    }
}

- (void)uploadFile:(NSString *)filePath key:(NSString *)key token:(NSString *)token result:(FlutterResult)result
{

    QNUploadManager *manager = [[QNUploadManager alloc]init];
    [manager putFile:filePath key:key token:token complete:^(QNResponseInfo *info, NSString *key, NSDictionary *resp) {
        result(resp[@"key"]);
    } option:nil];
    
}



@end
