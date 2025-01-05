package tron.wallet.util;

import org.web3j.utils.Numeric;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.UUID;

/** AES 加解密 */
public class AES {

  public static final String algorithm = "AES";
  // AES/CBC/NOPaddin
  // AES 默认模式
  // 使用CBC模式, 在初始化Cipher对象时, 需要增加参数, 初始化向量IV : IvParameterSpec iv = new
  // IvParameterSpec(key.getBytes());
  // NOPadding: 使用NOPadding模式时, 原文长度必须是8byte的整数倍   ECB模式是可重复解密的
  public static final String transformation = "AES/ECB/NOPadding";

  public static void main(String[] args) throws Exception {
    String key = UUID.randomUUID().toString(); // 16位字符密钥 建议16位
    String pwd = HashAndSalt(key.getBytes());
    //    String key = "theBugMaker_0007"; // 16位字符密钥 建议16位
    String str = "2023-01-01 23:59:59"; // 一个需要加解密的字符串
    System.out.println(key + "->" + pwd);
    String reEnStr = encryptAes(str, pwd); // 结果是AES加密后又base64加密之后的字符
    System.out.println(reEnStr);

    String reDeStr = decryptAes(reEnStr, pwd);
    System.out.println(reDeStr);
    //    for (int i = 0; i < 256; i++) {
    //      System.out.println((char) te4[i]);
    //    }
  }

  /**
   * AES加密
   *
   * @param str 将要加密的内容
   * @param key 密钥
   * @return 已经加密的字节数组内容 再 base64 之后的字符串
   */
  public static String encryptAes(String str, String key) throws Exception {
    byte[] data = str.getBytes("UTF-8");
    byte[] keyByte = key.getBytes("UTF-8");
    // 不足16字节，补齐内容为差值
    int len = 16 - data.length % 16;
    for (int i = 0; i < len; i++) {
      byte[] bytes = {(byte) len};
      data = concat(data, bytes);
    }
    try {
      SecretKeySpec skeySpec = new SecretKeySpec(keyByte, "AES");
      Cipher cipher = Cipher.getInstance(transformation);
      cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
      byte[] resultByte = cipher.doFinal(data);
      return Base64.getEncoder().encodeToString(resultByte);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * AES解密
   *
   * @param base64Str 将要解密的字节数组内容 的 base64编码后的字符串
   * @param key 密钥
   * @return 已经解密的内容
   */
  public static String decryptAes(String base64Str, String key) throws Exception {

    try {
      byte[] data = Base64.getDecoder().decode(base64Str);
      //      byte[] data = Base64Util.decryBASE64ToByteArr(base64Str);
      data = noPadding(data, -1);
      byte[] keyByte = key.getBytes("UTF-8");
      SecretKeySpec skeySpec = new SecretKeySpec(keyByte, "AES");
      Cipher cipher = Cipher.getInstance(transformation);
      cipher.init(Cipher.DECRYPT_MODE, skeySpec);
      byte[] decryptData = cipher.doFinal(data);
      int len = 2 + byteToInt(decryptData[4]) + 3;
      byte[] resultByte = noPadding(decryptData, len);
      String resultStr = new String(resultByte);
      return resultStr.trim();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 合并数组
   *
   * @param firstArray 第一个数组
   * @param secondArray 第二个数组
   * @return 合并后的数组
   */
  public static byte[] concat(byte[] firstArray, byte[] secondArray) {
    if (firstArray == null || secondArray == null) {
      return null;
    }
    byte[] bytes = new byte[firstArray.length + secondArray.length];
    System.arraycopy(firstArray, 0, bytes, 0, firstArray.length);
    System.arraycopy(secondArray, 0, bytes, firstArray.length, secondArray.length);
    return bytes;
  }

  /**
   * 去除数组中的补齐
   *
   * @param paddingBytes 源数组
   * @param dataLength 去除补齐后的数据长度
   * @return 去除补齐后的数组
   */
  public static byte[] noPadding(byte[] paddingBytes, int dataLength) {
    if (paddingBytes == null) {
      return null;
    }

    byte[] noPaddingBytes = null;
    if (dataLength > 0) {
      if (paddingBytes.length > dataLength) {
        noPaddingBytes = new byte[dataLength];
        System.arraycopy(paddingBytes, 0, noPaddingBytes, 0, dataLength);
      } else {
        noPaddingBytes = paddingBytes;
      }
    } else {
      int index = paddingIndex(paddingBytes);
      if (index > 0) {
        noPaddingBytes = new byte[index];
        System.arraycopy(paddingBytes, 0, noPaddingBytes, 0, index);
      }
    }

    return noPaddingBytes;
  }

  /**
   * 获取补齐的位置
   *
   * @param paddingBytes 源数组
   * @return 补齐的位置
   */
  private static int paddingIndex(byte[] paddingBytes) {
    for (int i = paddingBytes.length - 1; i >= 0; i--) {
      if (paddingBytes[i] != 0) {
        return i + 1;
      }
    }
    return -1;
  }

  public static int byteToInt(byte b) {
    return (b) & 0xff;
  }

  // 原始密码加密处理
  public static String HashAndSalt(byte[] pwd) {
    int lens = pwd.length;
    int num = 0;
    for (int i = 0; i < lens; i++) {
      int tmp = (int) (pwd[i]);
      if (tmp != 45) {
        num += tmp;
      }
    }
    num = num % 15 + 1;
    int k = num;
    byte[] pass = new byte[16];

    for (int i = 0; i < 16; i++) {
      pass[i] = te4[k];
      k += num;
    }
    String t = Numeric.toHexString(pass);
    //    return t.substring(t.length() - 16);
    return t.substring(t.length() - 32);
  }

  private static byte[] te4 = {
    (byte) 0x63,
    (byte) 0x7c,
    (byte) 0x77,
    (byte) 0x7b,
    (byte) 0xf2,
    (byte) 0x6b,
    (byte) 0x6f,
    (byte) 0xc5,
    (byte) 0x30,
    (byte) 0x01,
    (byte) 0x67,
    (byte) 0x2b,
    (byte) 0xfe,
    (byte) 0xd7,
    (byte) 0xab,
    (byte) 0x76,
    (byte) 0xca,
    (byte) 0x82,
    (byte) 0xc9,
    (byte) 0x7d,
    (byte) 0xfa,
    (byte) 0x59,
    (byte) 0x47,
    (byte) 0xf0,
    (byte) 0xad,
    (byte) 0xd4,
    (byte) 0xa2,
    (byte) 0xaf,
    (byte) 0x9c,
    (byte) 0xa4,
    (byte) 0x72,
    (byte) 0xc0,
    (byte) 0xb7,
    (byte) 0xfd,
    (byte) 0x93,
    (byte) 0x26,
    (byte) 0x36,
    (byte) 0x3f,
    (byte) 0xf7,
    (byte) 0xcc,
    (byte) 0x34,
    (byte) 0xa5,
    (byte) 0xe5,
    (byte) 0xf1,
    (byte) 0x71,
    (byte) 0xd8,
    (byte) 0x31,
    (byte) 0x15,
    (byte) 0x04,
    (byte) 0xc7,
    (byte) 0x23,
    (byte) 0xc3,
    (byte) 0x18,
    (byte) 0x96,
    (byte) 0x05,
    (byte) 0x9a,
    (byte) 0x07,
    (byte) 0x12,
    (byte) 0x80,
    (byte) 0xe2,
    (byte) 0xeb,
    (byte) 0x27,
    (byte) 0xb2,
    (byte) 0x75,
    (byte) 0x09,
    (byte) 0x83,
    (byte) 0x2c,
    (byte) 0x1a,
    (byte) 0x1b,
    (byte) 0x6e,
    (byte) 0x5a,
    (byte) 0xa0,
    (byte) 0x52,
    (byte) 0x3b,
    (byte) 0xd6,
    (byte) 0xb3,
    (byte) 0x29,
    (byte) 0xe3,
    (byte) 0x2f,
    (byte) 0x84,
    (byte) 0x53,
    (byte) 0xd1,
    (byte) 0x00,
    (byte) 0xed,
    (byte) 0x20,
    (byte) 0xfc,
    (byte) 0xb1,
    (byte) 0x5b,
    (byte) 0x6a,
    (byte) 0xcb,
    (byte) 0xbe,
    (byte) 0x39,
    (byte) 0x4a,
    (byte) 0x4c,
    (byte) 0x58,
    (byte) 0xcf,
    (byte) 0xd0,
    (byte) 0xef,
    (byte) 0xaa,
    (byte) 0xfb,
    (byte) 0x43,
    (byte) 0x4d,
    (byte) 0x33,
    (byte) 0x85,
    (byte) 0x45,
    (byte) 0xf9,
    (byte) 0x02,
    (byte) 0x7f,
    (byte) 0x50,
    (byte) 0x3c,
    (byte) 0x9f,
    (byte) 0xa8,
    (byte) 0x51,
    (byte) 0xa3,
    (byte) 0x40,
    (byte) 0x8f,
    (byte) 0x92,
    (byte) 0x9d,
    (byte) 0x38,
    (byte) 0xf5,
    (byte) 0xbc,
    (byte) 0xb6,
    (byte) 0xda,
    (byte) 0x21,
    (byte) 0x10,
    (byte) 0xff,
    (byte) 0xf3,
    (byte) 0xd2,
    (byte) 0xcd,
    (byte) 0x0c,
    (byte) 0x13,
    (byte) 0xec,
    (byte) 0x5f,
    (byte) 0x97,
    (byte) 0x44,
    (byte) 0x17,
    (byte) 0xc4,
    (byte) 0xa7,
    (byte) 0x7e,
    (byte) 0x3d,
    (byte) 0x64,
    (byte) 0x5d,
    (byte) 0x19,
    (byte) 0x73,
    (byte) 0x60,
    (byte) 0x81,
    (byte) 0x4f,
    (byte) 0xdc,
    (byte) 0x22,
    (byte) 0x2a,
    (byte) 0x90,
    (byte) 0x88,
    (byte) 0x46,
    (byte) 0xee,
    (byte) 0xb8,
    (byte) 0x14,
    (byte) 0xde,
    (byte) 0x5e,
    (byte) 0x0b,
    (byte) 0xdb,
    (byte) 0xe0,
    (byte) 0x32,
    (byte) 0x3a,
    (byte) 0x0a,
    (byte) 0x49,
    (byte) 0x06,
    (byte) 0x24,
    (byte) 0x5c,
    (byte) 0xc2,
    (byte) 0xd3,
    (byte) 0xac,
    (byte) 0x62,
    (byte) 0x91,
    (byte) 0x95,
    (byte) 0xe4,
    (byte) 0x79,
    (byte) 0xe7,
    (byte) 0xc8,
    (byte) 0x37,
    (byte) 0x6d,
    (byte) 0x8d,
    (byte) 0xd5,
    (byte) 0x4e,
    (byte) 0xa9,
    (byte) 0x6c,
    (byte) 0x56,
    (byte) 0xf4,
    (byte) 0xea,
    (byte) 0x65,
    (byte) 0x7a,
    (byte) 0xae,
    (byte) 0x08,
    (byte) 0xba,
    (byte) 0x78,
    (byte) 0x25,
    (byte) 0x2e,
    (byte) 0x1c,
    (byte) 0xa6,
    (byte) 0xb4,
    (byte) 0xc6,
    (byte) 0xe8,
    (byte) 0xdd,
    (byte) 0x74,
    (byte) 0x1f,
    (byte) 0x4b,
    (byte) 0xbd,
    (byte) 0x8b,
    (byte) 0x8a,
    (byte) 0x70,
    (byte) 0x3e,
    (byte) 0xb5,
    (byte) 0x66,
    (byte) 0x48,
    (byte) 0x03,
    (byte) 0xf6,
    (byte) 0x0e,
    (byte) 0x61,
    (byte) 0x35,
    (byte) 0x57,
    (byte) 0xb9,
    (byte) 0x86,
    (byte) 0xc1,
    (byte) 0x1d,
    (byte) 0x9e,
    (byte) 0xe1,
    (byte) 0xf8,
    (byte) 0x98,
    (byte) 0x11,
    (byte) 0x69,
    (byte) 0xd9,
    (byte) 0x8e,
    (byte) 0x94,
    (byte) 0x9b,
    (byte) 0x1e,
    (byte) 0x87,
    (byte) 0xe9,
    (byte) 0xce,
    (byte) 0x55,
    (byte) 0x28,
    (byte) 0xdf,
    (byte) 0x8c,
    (byte) 0xa1,
    (byte) 0x89,
    (byte) 0x0d,
    (byte) 0xbf,
    (byte) 0xe6,
    (byte) 0x42,
    (byte) 0x68,
    (byte) 0x41,
    (byte) 0x99,
    (byte) 0x2d,
    (byte) 0x0f,
    (byte) 0xb0,
    (byte) 0x54,
    (byte) 0xbb,
    (byte) 0x16
  };
}
