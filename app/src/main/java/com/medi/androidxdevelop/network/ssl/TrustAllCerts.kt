package com.medi.comm.network.ssl

import java.math.BigInteger
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.security.interfaces.RSAPublicKey
import javax.net.ssl.*


/**
 * 证书检查
 * Created by lixiang on 2019/12/19.
 */

class TrustAllCerts : X509TrustManager {

    @Throws(CertificateException::class)
    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {

    }

    @Throws(CertificateException::class)
    override fun checkServerTrusted(chain: Array<X509Certificate>?, authType: String?) {
        if (false) {
            if (chain == null) {
                throw IllegalArgumentException("checkServerTrusted:x509Certificate array isnull")
            }

            if (chain.isEmpty()) {
                throw IllegalArgumentException("checkServerTrusted: X509Certificate is empty")
            }
            if (!(null != authType && authType.equals("ECDHE_RSA", ignoreCase = true))) {
                throw CertificateException("checkServerTrusted: AuthType is not RSA")
            }

            // Perform customary SSL/TLS checks
            try {
                val tmf = TrustManagerFactory.getInstance("X509")
                tmf.init(null as KeyStore?)
                for (trustManager in tmf.trustManagers) {
                    (trustManager as X509TrustManager).checkServerTrusted(chain, authType)
                }
            } catch (e: Exception) {
                throw CertificateException(e)
            }

            // Hack ahead: BigInteger and toString(). We know a DER encoded Public Key begins
            // with 0×30 (ASN.1 SEQUENCE and CONSTRUCTED), so there is no leading 0×00 to drop.
            val pubKey = chain[0].publicKey as RSAPublicKey

            val encoded = BigInteger(1 /* positive */, pubKey.encoded).toString(16)
            // Pin it!
            val expected = PUB_KEY.equals(encoded, ignoreCase = true) || PUB_KEY_NEW.equals(encoded,ignoreCase = true)

            if (!expected) {
                throw CertificateException("checkServerTrusted: Expected public key: "
                        + PUB_KEY + ", got public key:" + encoded)
            }
        }
    }

    override fun getAcceptedIssuers(): Array<X509Certificate?> {
        return arrayOfNulls(0)
    }

    companion object {

        private const val PUB_KEY = "30820122300d06092a864886f70d01010105000382010f003082010a0282010100bc523d3c9a3384c73b20e64e7fe77902aba732da2564b521dd98b399fad3816ab80e316c7b56f7d8d8a564914cbf7ff4b4855132b1a01535a9deb829bc212d9782751fbdee0e5ea1ba474c1abe222d3e49f475db0cc67e7dc2e52503d83e3ab6e323418a7798d102811c71afc83f99f40c8a51fb783e5815907dba7a199acfba3203b9559e58bef5273c2c55b178b4b58f0e04a38b0a8afa634b9977bd92e5f7b7c94a9e7cf0c5706deda8120b1858d446ea874274a54a4e352b02c98374b899c7d12db2d8f59f19762bd2db878276dce150ae6378b2a3095c582f3f8492ad434057505e801da08d48f227e116c61412f2242ec754e60b54f950d706141fe4a90203010001"
        private const val PUB_KEY_NEW = "30820122300d06092a864886f70d01010105000382010f003082010a0282010100efc113a3c19fb90f97b2f59b709b85da091ef88c6b22ef2cf1b0026584074cf68928c59392929637367692463811ae500a890a3692154c30bb85f4561130be7e8f5637d56d8f7ee80abbce3c74235205660d73da648f4d84b6b5f851fb88429f8d6dd97445bfde81aff73abe3acefd16f89fd1168193a57619c17f86159ba1b5d89033b345ef9ffea1057ffe82b4ed7106a6257aba468cea8e66624cef046bc249e0c3fd3dffb48fb37eda8ba62042d356fe1a387340b979a2c9b304ebfb41abced0e5d828b13b70fcbdf51ebb7ca65adc4b0a51da750b258d6afde54c9d213e1d79802cc6f060a1fd58fea43a220671ce8acdce669f664337c4a1e90a82d7670203010001"
        fun createSSLSocketFactory(): SSLSocketFactory? {
            var ssfFactory: SSLSocketFactory? = null

            try {
                val sc = SSLContext.getInstance("TLS")
                sc.init(null, arrayOf<TrustManager>(TrustAllCerts()), SecureRandom())

                ssfFactory = sc.socketFactory
            } catch (ignored: Exception) {
            }

            return ssfFactory
        }
    }

}
