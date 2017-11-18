 ## 加密视频
 ### 1.AES解密视频
  1.实例化解密数据源
```
public class EnctyptDataSource implements DataSourceListener {
    public static final String TAG = "DataSource";
    private Context context;
    private Cipher cipher;
    private SecretKeySpec mSecretKeySpec;
    private IvParameterSpec mIvParameterSpec;

      /***
         * @param context context
         * @param cipher cipher
         *  @param mSecretKeySpec mSecretKeySpec
         *  @param   mIvParameterSpec     mSecretKeySpec
         * **/
    public EnctyptDataSource(Context context, Cipher cipher, SecretKeySpec mSecretKeySpec, IvParameterSpec mIvParameterSpec) {
        this.context = context;
        this.cipher = cipher;
        this.mSecretKeySpec = mSecretKeySpec;
        this.mIvParameterSpec = mIvParameterSpec;
    }
    @Override
    public DataSource.Factory getDataSourceFactory() {
      //初始化解密工厂类
        return new EncryptedFileDataSourceFactory(context,cipher, mSecretKeySpec, mIvParameterSpec);
    }
}

```
2.使用
````
//实例化你加密解密类
 exoPlayerManager = new ExoUserPlayer(this, mSimpleExoPlayerView, new EnctyptDataSource(this, mCipher, mSecretKeySpec, mIvParameterSpec));
 //传入你加密视频路径
  Uri uri = Uri.fromFile(mEncryptedFile);
  exoPlayerManager.setPlayUri(uri);
  exoPlayerManager.startPlayer();
````
3.加密处理
>> 注意:原来FileOutputStream 换成CipherOutputStream 流 进行写加密文件操作,其他操作不变。
````
//写文件输出流 
 FileOutputStream fos = new FileOutputStream(mFile.getAbsolutePath(), false);
//加密输出流 
  CipherOutputStream cipherOutputStream = new CipherOutputStream(fos, mCipher);
````
 ### 二.简单加密
  1.实例化解密数据源
```
public class EnctyptDataSource3 implements DataSourceListener {
    public static final String TAG = "DataSource";

    private Context context;
    private String  keyBody;

    /***
     * @param context context
     * @param    keyBody 你的视频文件key内容
     * ***/
    public EnctyptDataSource3(Context context, String  keyBody) {
        this.context = context;
        this. keyBody=  keyBody;
    }

    @Override
    public com.google.android.exoplayer2.upstream.DataSource.Factory getDataSourceFactory() {
        //初始化解密工厂类
        return new EncryptedFileDataSourceFactory(context,keyBody);
    }
}

```
2.使用
````
//实例化你加密解密类
 exoPlayerManager = new ExoUserPlayer(this, mSimpleExoPlayerView, new EnctyptDataSource3(this,keyBody));
 //传入你加密视频路径
  Uri uri = Uri.fromFile(mEncryptedFile);
  exoPlayerManager.setPlayUri(uri);
  exoPlayerManager.startPlayer();
````
3.加密处理注意
>>注意：必须放在创建文件时将你key写入文件里。
````
   //生成一个带有key视频文件
   Test.writeFile(mFile.getAbsolutePath(), keyBodys.getBytes("UTF-8"), 0, keyBodys.getBytes("UTF-8").length, false);
   //下载视频 
    while ((readCount = inputStream.read(buffer)) != -1) {
                   // 处理下载的数据
                   Test.writeFile(mFile.getAbsolutePath(), buffer, 0, readCount, true);
                   total = total + readCount;
                   pro=(int) (total * 100 / length);
                   System.out.println("字节" + total + "总共长度" + length + "--进度：" + pro);
    publishProgress(pro);
    }
   
````
### 三.Base64 加密处理和详情用法参考demo
 
 
 
  
 