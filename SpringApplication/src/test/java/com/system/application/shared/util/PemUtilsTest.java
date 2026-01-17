package com.system.application.shared.util;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class PemUtilsTest {

    private final PemUtils pemUtils = PemUtils.getInstance();

    @Test
    void deveLerChavePublicaComSucesso() throws Exception {
        KeyPair keyPair = gerarParDeChavesRSA();
        String publicKeyPem = gerarPublicKeyPem(keyPair.getPublic());
        InputStream input = new ByteArrayInputStream(publicKeyPem.getBytes());
        PublicKey publicKey = pemUtils.readPublicKey(input);
        assertNotNull(publicKey);
        assertEquals("RSA", publicKey.getAlgorithm());
    }

    @Test
    void deveLerChavePrivadaComSucesso() throws Exception {
        KeyPair keyPair = gerarParDeChavesRSA();
        String privateKeyPem = gerarPrivateKeyPem(keyPair.getPrivate());
        InputStream input = new ByteArrayInputStream(privateKeyPem.getBytes());
        PrivateKey privateKey = pemUtils.readPrivateKey(input);
        assertNotNull(privateKey);
        assertEquals("RSA", privateKey.getAlgorithm());
    }

    @Test
    void deveLancarExcecaoParaChavePublicaInvalida() {
        InputStream input = new ByteArrayInputStream("INVALID_KEY".getBytes());
        assertThrows(Exception.class, () -> pemUtils.readPublicKey(input));
    }

    @Test
    void deveLancarExcecaoParaChavePrivadaInvalida() {
        InputStream input = new ByteArrayInputStream("INVALID_KEY".getBytes());
        assertThrows(Exception.class, () -> pemUtils.readPrivateKey(input));
    }

    // =========================
    // Helpers
    // =========================

    private KeyPair gerarParDeChavesRSA() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

    private String gerarPublicKeyPem(PublicKey publicKey) {
        String base64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        return """
                -----BEGIN PUBLIC KEY-----
                %s
                -----END PUBLIC KEY-----
                """.formatted(base64);
    }

    private String gerarPrivateKeyPem(PrivateKey privateKey) {
        String base64 = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        return """
                -----BEGIN PRIVATE KEY-----
                %s
                -----END PRIVATE KEY-----
                """.formatted(base64);
    }
}

