package common.leyou.auth.test;

import com.leyou.commom.utils.JwtUtils;
import com.leyou.commom.utils.RsaUtils;
import com.leyou.pojo.UserInfo;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

public class JwtTest {

    private static final String pubKeyPath = "C:\\tmp\\rsa\\rsa.pub";

    private static final String priKeyPath = "C:\\tmp\\rsa\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        // 生成token
        String token = JwtUtils.generateToken(new UserInfo(20L, "jack"), privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiamFjayIsImV4cCI6MTYyODk5NjQzN30.NgJeGnfSf_6nXYLW3VkAK0ujzU2fkiA8sjOx7IQWW66kPnk76sd3ZYdzy0Nftsg7hhq4qXIN501Bdnsg7EPfv80KXSEGAtEcFescW5W_6RmNRKU7wmK-x_yxiQ2bsVfyLFxTN7pvYrzxrX2PNEod-5yL_jrSmLHP6YV-AWByRus";

        // 解析token
        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }
}