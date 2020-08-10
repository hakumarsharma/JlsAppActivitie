/*************************************************************
 *
 * Reliance Digital Platform & Product Services Ltd.

 * CONFIDENTIAL
 * __________________
 *
 *  Copyright (C) 2020 Reliance Digital Platform & Product Services Ltd.–
 *
 *  ALL RIGHTS RESERVED.
 *
 * NOTICE:  All information including computer software along with source code and associated *documentation contained herein is, and
 * remains the property of Reliance Digital Platform & Product Services Ltd..  The
 * intellectual and technical concepts contained herein are
 * proprietary to Reliance Digital Platform & Product Services Ltd. and are protected by
 * copyright law or as trade secret under confidentiality obligations.

 * Dissemination, storage, transmission or reproduction of this information
 * in any part or full is strictly forbidden unless prior written
 * permission along with agreement for any usage right is obtained from Reliance Digital Platform & *Product Services Ltd.
 **************************************************************/

package com.jio.devicetracker.util;

import android.util.Base64;

import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AESEncrypt {

    public static byte[] SALT = {
            (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
            (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03
    };

    private static final int ITERATION_COUNT = 65536;
    private static final int KEY_LENGTH = 256;
    private Cipher encryptCipher;
    private Cipher decryptCipher;

    /**
     * Initiates AESEncryption with password key
     * @param passwordKey
     * @throws Exception
     */
    public AESEncrypt(String passwordKey) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(Constant.AES_SECRET_KEY);
        KeySpec spec = new PBEKeySpec(passwordKey.toCharArray(), SALT, ITERATION_COUNT, KEY_LENGTH);
        SecretKey tempKey = factory.generateSecret(spec);
        SecretKey secret = new SecretKeySpec(tempKey.getEncoded(), "AES");

        encryptCipher = Cipher.getInstance(Constant.CIPHER_KEY);
        encryptCipher.init(Cipher.ENCRYPT_MODE, secret);

        decryptCipher = Cipher.getInstance(Constant.CIPHER_KEY);
        byte[] iv = encryptCipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV();
        decryptCipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
    }

    /**
     * Encrypts given text
     * @param encrypt
     * @return
     * @throws Exception
     */
    public String encrypt(String encrypt) throws Exception {
        byte[] bytes = encrypt.getBytes("UTF8");
        byte[] encrypted = encrypt(bytes);
        return Base64.encodeToString(encrypted, Base64.DEFAULT);
    }

    /**
     * Encrypts given byte array
     * @param plain
     * @return
     * @throws Exception
     */
    public byte[] encrypt(byte[] plain) throws Exception {
        return encryptCipher.doFinal(plain);
    }

    /**
     * Decrypts given text
     * @param encrypt
     * @return
     * @throws Exception
     */
    public String decrypt(String encrypt) throws Exception {
        byte[] bytes = Base64.decode(encrypt, Base64.DEFAULT);
        byte[] decrypted = decrypt(bytes);
        return new String(decrypted, "UTF8");
    }

    /**
     * Decrypts given byte array
     * @param encrypt
     * @return
     * @throws Exception
     */
    public byte[] decrypt(byte[] encrypt) throws Exception {
        return decryptCipher.doFinal(encrypt);
    }
}
