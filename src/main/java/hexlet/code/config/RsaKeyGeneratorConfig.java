package hexlet.code.config;


import hexlet.code.component.RsaKeyProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
public class RsaKeyGeneratorConfig {
    @Bean
    public RsaKeyProperties rsaKeyProperties() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        RsaKeyProperties rsaKeys = new RsaKeyProperties();
        rsaKeys.setPublicKey((RSAPublicKey) keyPair.getPublic());
        rsaKeys.setPrivateKey((RSAPrivateKey) keyPair.getPrivate());
        return rsaKeys;
    }
}
