import com.xzh.gateway.common.route.RouteDefinitionDTO;
import com.xzh.gateway.utils.FileLoaderUtils;
import com.xzh.gateway.utils.JsonUtils;

import java.util.List;

/**
 * @author 向振华
 * @date 2020/09/09 18:07
 */
public class Test3 {

    public static void main(String[] args) {
        String s = FileLoaderUtils.read("route.json");
        List<RouteDefinitionDTO> dtos = JsonUtils.convertJson(s, RouteDefinitionDTO.class);
        for (RouteDefinitionDTO dto : dtos) {
            System.out.println(dto);
        }
    }

}
