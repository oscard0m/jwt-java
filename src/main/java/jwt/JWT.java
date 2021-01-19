package main.java.jwt;

import io.jsonwebtoken.Jwts;

import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

class JWT {
    static final String beginPrivateKey = "-----BEGIN RSA PRIVATE KEY-----";
    static final String endPrivateKey = "-----END RSA PRIVATE KEY-----";

    public static String cleanPrivateKey(String privateKeyString)  {
        // Strip off delimiters, if they exist.
        if (privateKeyString.contains(beginPrivateKey) && privateKeyString.contains(endPrivateKey)) {
            privateKeyString = privateKeyString.substring(beginPrivateKey.length(), privateKeyString.lastIndexOf(endPrivateKey));
        }

        if(privateKeyString.startsWith(beginPrivateKey)) {
            privateKeyString = privateKeyString.substring(beginPrivateKey.length());
        }

        return privateKeyString;
    }

    public static PrivateKey getPrivateKeyRSA2(String base64PrivateKey) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeySpecException {
        String cleanPrivatekey = base64PrivateKey;
        System.out.println(cleanPrivatekey);
        cleanPrivatekey = cleanPrivateKey(base64PrivateKey);
        System.out.println(cleanPrivatekey);
        cleanPrivatekey = cleanPrivatekey.replace("\n", "");
        System.out.println(cleanPrivatekey);

        //byte[] keyBytes = cleanPrivatekey.getBytes("utf-8");
        //byte[] keyBytes = Base64.getDecoder().decode(cleanPrivatekey);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(DatatypeConverter.parseBase64Binary(cleanPrivatekey));

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);

    }

   /*public static PrivateKey getPrivateKeyCastle(String privateKey) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        String cleanPrivateKey = cleanPrivateKey(privateKey);
        PemReader reader = new PemReader(new StringReader(cleanPrivateKey));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(reader.readPemObject().getContent());
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }*/


        public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, NoSuchPaddingException, InvalidKeyException {
        String PRIVATE_KEY = "-----BEGIN RSA PRIVATE KEY-----\n" +
                "bla\n" +
                "bla\n" +
                "-----END RSA PRIVATE KEY-----";

        Instant now = Instant.now();
        Instant expiration = now.plus(10, ChronoUnit.MINUTES);
        Date nowDate = Date.from(now);
        Date expirationDate = Date.from(expiration);

        PrivateKey privateKeyRSA = getPrivateKeyRSA2(PRIVATE_KEY);
        //PrivateKey privateKeyRSACastle = getPrivateKeyCastle(PRIVATE_KEY);

        String jws = Jwts.builder()
                        .setSubject("Github Auth Token")
                        .setIssuedAt(nowDate)
                        .setIssuer("+41990")
                        .setExpiration(expirationDate)
                        .signWith(privateKeyRSA)
                        .compact();

        System.out.println(jws);
    }
}