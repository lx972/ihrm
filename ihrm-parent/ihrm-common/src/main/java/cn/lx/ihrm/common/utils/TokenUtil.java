package cn.lx.ihrm.common.utils;

import cn.lx.ihrm.common.entity.ResultCode;
import cn.lx.ihrm.common.exception.CommonException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

/**
 * cn.lx.ihrm.common.utils
 *
 * @Author Administrator
 * @date 16:00
 */
public class TokenUtil {


    public static Claims decodeToken(String token, String base64key) throws CommonException{
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(base64key)
                    .build()
                    .parseClaimsJws(token);

            //String userId = (String) claimsJws.getBody().get("userId");
            Claims claims = claimsJws.getBody();
            //OK, we can trust this JWT


            return claims;
        } catch (JwtException e) {
            e.printStackTrace();
            //don't trust the JWT!
            throw new CommonException(ResultCode.JWT_ERROR);
        }
    }
}
