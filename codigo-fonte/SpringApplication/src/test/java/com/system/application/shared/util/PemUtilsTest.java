package com.system.application.shared.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PemUtilsTest {
    private PemUtils pemUtils;

    // Chaves RSA reais geradas para os testes
    private static final String PUBLIC_KEY_PEM =
            "-----BEGIN PUBLIC KEY-----\n" +
                    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1FGkV7MByHqyihb8Aw1E\n" +
                    "N5FTZJOujahsJPMVsBTw4fIAnSW7r+Myfuy4m4p/E22R9vb23jm1VJ/bFsDYskJ0\n" +
                    "vaQWcXVbsBOc79rIBb4snrDLiDw6mLzNemAtlmJFhGmzKxFx4FhHyx6EdylCEDtM\n" +
                    "yaPzOsLU2UYJdItsGl+KsvTmmEzVJd4yUJTY5FPrLpynkbkQVFNqwPDl6MOkbWR9\n" +
                    "CgvZVM0YTWbS2WkyDwSnJin6hB1RCmWG0a0GBAXDqNM+c1zAK7+lQINbxCfUGFnz\n" +
                    "bpKgn5Afgu3ZTJGqtexmWrqYgHwruVvrEIO6uWeHWf2+6vdCrIAWyiJO0t1YekKb\n" +
                    "xwIDAQAB\n" +
                    "-----END PUBLIC KEY-----";

    private static final String PRIVATE_KEY_PEM =
            "-----BEGIN PRIVATE KEY-----\n" +
                    "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDUUaRXswHIerKK\n" +
                    "FvwDDUQ3kVNkk66NqGwk8xWwFPDh8gCdJbuv4zJ+7Libin8TbZH29vbeObVUn9sW\n" +
                    "wNiyQnS9pBZxdVuwE5zv2sgFviyesMuIPDqYvM16YC2WYkWEabMrEXHgWEfLHoR3\n" +
                    "KUIQO0zJo/M6wtTZRgl0i2waX4qy9OaYTNUl3jJQlNjkU+sunKeRuRBUU2rA8OXo\n" +
                    "w6RtZH0KC9lUzRhNZtLZaTIPBKcmKfqEHVEKZYbRrQYEBcOo0z5zXMArv6VAg1vE\n" +
                    "J9QYWfNukqCfkB+C7dlMkaq17GZaupiAfCu5W+sQg7q5Z4dZ/b7q90KsgBbKIk7S\n" +
                    "3Vh6QpvHAgMBAAECggEAIFmOKkSejW7QfETTQh3H2pWceLMdwfy6+YhGMi2GpE6M\n" +
                    "tGT26245F+i2mhBom0uzFUd8xtBRJiMzLQzWF5cTlX+GYeQpQ7NBUrRpjKJ0xvsW\n" +
                    "D9xpazM9NZuo5oRzpiC6yWGfkbixu6ubMFLLe5ClewWtfjMIXlCTCTxjDMOo7Oon\n" +
                    "fhqfX/oMEYbmw6loc71LemgLu8ZOYxoWLzAk+/vA5/M9lB2C+gw7hY5tex9RS6oh\n" +
                    "PUoDCGFisoBWwbavy5Sb0Ypy/sGKwk0bxZ5jH01OWy/4ZO/s8v8ryAdm5bEyF6Qv\n" +
                    "UHhDhWCMUF7KRrT4Aqut3QWn7M5E6cT70xBMvnFS7QKBgQD2nsTGdTs7cKVQfCbq\n" +
                    "yuvKLJ0USYD9iC3Tswfdm4siUKprBdG8i3LnSXvB+Z6JJDsSG2nDliPRnVGLayAY\n" +
                    "lifnh1GMXtOk8d17ScpoU46nCx1iGYA3VrrO9Y4qMOUnkVWfyo+DRQyVQGeptm9O\n" +
                    "ToWLUi98z8KRUbSKzmnmHDRWlQKBgQDcZOWiNChDgJrIHRsQvf3ScVZvB5PukEm5\n" +
                    "GdGqlwS49g9dlX6eT2nS4Qgwc2pjTieK9qxxW8m/31s6jeMuZHrgA1QJYOYQ2OMS\n" +
                    "KDGWihzLf26+PNWL+732Cv6ok0e6cGADCdiB571QVCNu8j92hOhwsqXUpKDdwERL\n" +
                    "j1ZQpfdd6wKBgQCtWm7xHWiUYJsjlxF4C0lw5KlJoF1WWYn0/Cx0PqivZ0pTTlTe\n" +
                    "lpsfLs0aTY990QTgtnpCrF/jMRUnzhZSkgHcwoa1B9b3Y7gOuJDZgpqMmsh9s3Un\n" +
                    "8QQBBCR5TcNWa9dnKeh1Gm084He9mOpmfdVrWErfDd8zbN5ej7dqs7qqfQKBgQDS\n" +
                    "h9+ETuaXdOZmesc7+wHYYUP70/VQUqzaEPvkRVbRiNusvf2yJJImS6iS/+2E50eG\n" +
                    "y5R2xQO4MhjnmlqGGp32F7fTLakYNtbSpWqZL13KVvTm7R3hFp2jx2T0i5xdULOm\n" +
                    "s2UK0uBZN8aRWP+eq9+OqFkZBBpXOi8DLu15JNFj+QKBgQCeNHrV0ZWDKq8Sl/kd\n" +
                    "vKCDQRsYEPMSyEThw/Dwfynl+OxEYIuhb2JWhZMlNsyXObi5of/Zerqc1BozGWSW\n" +
                    "2AMGsYpFV5uvC1pX0vPxKUyo32R9sCdq9pjHo2TBHkLkNR9Xc8He2M7YXF6mo/QY\n" +
                    "WsvV+WT4B/Rr9RL8F9VqyNTQHA==\n" +
                    "-----END PRIVATE KEY-----";

    @BeforeEach
    void setUp() {
        pemUtils = PemUtils.getInstance();
    }

    @Test
    @DisplayName("getInstance() deve retornar uma instância não nula")
    void testGetInstanceNotNull() {
        PemUtils instance = PemUtils.getInstance();
        assertNotNull(instance);
    }

    @Test
    @DisplayName("getInstance() deve retornar instâncias diferentes a cada chamada")
    void testGetInstanceReturnsDifferentInstances() {
        PemUtils instance1 = PemUtils.getInstance();
        PemUtils instance2 = PemUtils.getInstance();
        assertNotSame(instance1, instance2);
    }

    @Test
    @DisplayName("readPublicKey() deve retornar uma PublicKey válida a partir de PEM correto")
    void testReadPublicKeySuccess() throws Exception {
        InputStream inputStream = new ByteArrayInputStream(PUBLIC_KEY_PEM.getBytes());
        PublicKey publicKey = pemUtils.readPublicKey(inputStream);

        assertNotNull(publicKey);
        assertEquals("RSA", publicKey.getAlgorithm());
        assertEquals("X.509", publicKey.getFormat());
    }

    @Test
    @DisplayName("readPublicKey() deve lançar exceção ao receber conteúdo inválido")
    void testReadPublicKeyInvalidContent() {
        InputStream inputStream = new ByteArrayInputStream("CONTEUDO_INVALIDO".getBytes());
        assertThrows(Exception.class, () -> pemUtils.readPublicKey(inputStream));
    }

    @Test
    @DisplayName("readPublicKey() deve lançar IOException quando o InputStream lança IOException (usando Mockito)")
    void testReadPublicKeyThrowsIOException() throws Exception {
        InputStream mockStream = mock(InputStream.class);
        when(mockStream.readAllBytes()).thenThrow(new IOException("Erro de leitura simulado"));

        assertThrows(IOException.class, () -> pemUtils.readPublicKey(mockStream));
        verify(mockStream, times(1)).readAllBytes();
    }

    @Test
    @DisplayName("readPublicKey() deve funcionar com PEM sem espaços extras")
    void testReadPublicKeyWithoutExtraSpaces() throws Exception {
        String compactPem = PUBLIC_KEY_PEM.replace("\n", "");
        InputStream inputStream = new ByteArrayInputStream(compactPem.getBytes());
        PublicKey publicKey = pemUtils.readPublicKey(inputStream);

        assertNotNull(publicKey);
        assertEquals("RSA", publicKey.getAlgorithm());
    }

    @Test
    @DisplayName("readPrivateKey() deve retornar uma PrivateKey válida a partir de PEM correto")
    void testReadPrivateKeySuccess() throws Exception {
        InputStream inputStream = new ByteArrayInputStream(PRIVATE_KEY_PEM.getBytes());
        PrivateKey privateKey = pemUtils.readPrivateKey(inputStream);

        assertNotNull(privateKey);
        assertEquals("RSA", privateKey.getAlgorithm());
        assertEquals("PKCS#8", privateKey.getFormat());
    }

    @Test
    @DisplayName("readPrivateKey() deve lançar exceção ao receber conteúdo inválido")
    void testReadPrivateKeyInvalidContent() {
        InputStream inputStream = new ByteArrayInputStream("CONTEUDO_INVALIDO".getBytes());
        assertThrows(Exception.class, () -> pemUtils.readPrivateKey(inputStream));
    }

    @Test
    @DisplayName("readPrivateKey() deve lançar IOException quando o InputStream lança IOException (usando Mockito)")
    void testReadPrivateKeyThrowsIOException() throws Exception {
        InputStream mockStream = mock(InputStream.class);
        when(mockStream.readAllBytes()).thenThrow(new IOException("Erro de leitura simulado"));

        assertThrows(IOException.class, () -> pemUtils.readPrivateKey(mockStream));
        verify(mockStream, times(1)).readAllBytes();
    }

    @Test
    @DisplayName("readPrivateKey() deve funcionar com PEM sem espaços extras")
    void testReadPrivateKeyWithoutExtraSpaces() throws Exception {
        String compactPem = PRIVATE_KEY_PEM.replace("\n", "");
        InputStream inputStream = new ByteArrayInputStream(compactPem.getBytes());
        PrivateKey privateKey = pemUtils.readPrivateKey(inputStream);

        assertNotNull(privateKey);
        assertEquals("RSA", privateKey.getAlgorithm());
    }

    @Test
    @DisplayName("PublicKey e PrivateKey lidas devem formar um par RSA compatível")
    void testPublicAndPrivateKeyAreCompatible() throws Exception {
        PublicKey publicKey = pemUtils.readPublicKey(new ByteArrayInputStream(PUBLIC_KEY_PEM.getBytes()));
        PrivateKey privateKey = pemUtils.readPrivateKey(new ByteArrayInputStream(PRIVATE_KEY_PEM.getBytes()));

        // Assina dados com a chave privada e verifica com a pública
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update("dados de teste".getBytes());
        byte[] signed = signature.sign();

        signature.initVerify(publicKey);
        signature.update("dados de teste".getBytes());
        assertTrue(signature.verify(signed));
    }
}

