import static com.xzh.gateway.utils.AesUtils.encrypt;

/**
 * @author 向振华
 * @date 2020/09/04 17:07
 */
public class Test1 {

    public static void main(String[] args) {

//        System.out.println(encrypt("[]"));

//        System.out.println(encrypt(String.valueOf(System.currentTimeMillis())));

        String x = "   {\n" +
                "    \"id\":\"统一门户\",\n" +
                "    \"predicates\":[\n" +
                "      {\n" +
                "        \"name\":\"Path\",\n" +
                "        \"args\":{\n" +
                "          \"_genkey_0\":\"/security-auth2/**\"\n" +
                "        }\n" +
                "      }\n" +
                "    ],\n" +
                "    \"filters\":[\n" +
                "      {\n" +
                "        \"name\":\"Login\",\n" +
                "        \"args\":{\n" +
                "          \"_genkey_0\":\"/*/oauth/*;/*/item/syn;\"\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\":\"Resource\",\n" +
                "        \"args\":{\n" +
                "          \"_genkey_0\":\"/*/oauth/*;/*/item/syn\"\n" +
                "        }\n" +
                "      }\n" +
                "    ],\n" +
                "    \"uri\":\"lb://security-auth2\",\n" +
                "    \"order\":0\n" +
                "  }";
        System.out.println(encrypt(x));
//        System.out.println(encrypt("小程序e"));

    }
}
