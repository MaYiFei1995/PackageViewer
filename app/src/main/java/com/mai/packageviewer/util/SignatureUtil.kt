package com.mai.packageviewer.util

import android.content.pm.Signature
import com.mai.packageviewer.data.BaseKVObject
import java.io.ByteArrayInputStream
import java.security.MessageDigest
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.*

/**
 * 应用签名信息工具
 */
object SignatureUtil {

    /**
     * 入口
     */
    fun getSignatureInfo(signature: Signature): List<BaseKVObject<String>> {
        val ret = LinkedList<BaseKVObject<String>>()
        ret.add(BaseKVObject("HashCode", "${signature.hashCode()}"))    // 有用签名hashcode做密钥的

        val certFactory = CertificateFactory.getInstance("X.509")
        val cert =
            certFactory.generateCertificate(ByteArrayInputStream(signature.toByteArray())) as X509Certificate

        ret.add(BaseKVObject("MD5", getAppSign(cert, "MD5")))
        ret.add(BaseKVObject("Sha1", getAppSign(cert, "Sha1")))
        ret.add(BaseKVObject("Sha256", getAppSign(cert, "Sha256")))

        return ret
    }

    /**
     * 获取签名的信息
     * @param algorithm type
     */
    private fun getAppSign(cert: X509Certificate, algorithm: String): String {
        try {
            val md = MessageDigest.getInstance(algorithm)
            val b = md.digest(cert.encoded)
            return byte2Hex(b)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "ERR"
    }

    private fun byte2Hex(bytes: ByteArray): String {
        val stringBuffer = StringBuffer()
        var temp: String
        for (i in bytes.indices) {
            temp = Integer.toHexString(bytes[i].toInt() and 0XFF)
            if (temp.length == 1) {
                //
                stringBuffer.append("0")
            }
            stringBuffer.append(temp)
        }
        return stringBuffer.toString()
    }
}