import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author 向振华
 * @date 2021/01/18 17:53
 */
public class TokenTest {

    /**
     * 创建token
     *
     * @param json
     * @return
     */
    public static String build(String json) {
        return Jwts.builder()
                .setSubject(json)
                .setExpiration(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(365)))
                .signWith(SignatureAlgorithm.HS512, "cf7031334895495582bf05fa6d2f57e1")
                .compact();
    }

    public static void main(String[] args) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("permissionKey", "permission:sys:1");
        jsonObject.put("userId", 1L);
        jsonObject.put("sasTenantId", 1L);
        jsonObject.put("orgFrameId", 1L);
        String build = build(jsonObject.toJSONString());
        System.out.println(build);
    }
}