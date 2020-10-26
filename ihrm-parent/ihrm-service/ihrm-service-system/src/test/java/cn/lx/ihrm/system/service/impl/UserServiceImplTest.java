package cn.lx.ihrm.system.service.impl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * cn.lx.ihrm.system.service.impl
 *
 * @Author Administrator
 * @date 11:45
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class UserServiceImplTest {

    @Test
    public void encode() {
        //使用jjwt创建令牌
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String secretString = Encoders.BASE64.encode(key.getEncoded());
        SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretString));
        log.info("secretString:{}",secretString);
        Map<String, Object> claims = new HashMap<>();
        claims.put("companyId", "24324");
        claims.put("companyName", "测试");
        String jws = Jwts.builder()
                //.setIssuer("me")
                //.setSubject("Bob")
                //.setAudience("you")
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30)) //a java.util.Date
                //.setNotBefore(notBefore) //a java.util.Date
                //.setIssuedAt(new Date()) // for example, now
                //.setId(UUID.randomUUID()) //just an example id
                .signWith(secretKey)
                .addClaims(claims)
                .compact();

        System.out.println(jws);
    }

    @Test
    public void decode() {
        try {
            String compactJws = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2MDM2OTkxNTAsImNvbXBhbnlJZCI6IjI0MzI0IiwiY29tcGFueU5hbWUiOiLmtYvor5UifQ.Bxi8-njYCkBYOET-MQ4g2Z3nHCyM_D68Etrv47eliaM";
            //Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
            String key = "nz6K2p5Ylf884nlH0BN3D1KJsd6Cbwtpmcp4HBAjIaU=";
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(compactJws);
            String s = claimsJws.getBody().toString();
            //OK, we can trust this JWT
            System.out.println(s);
        } catch (JwtException e) {
            e.printStackTrace();
            //don't trust the JWT!
        }


    }
}
