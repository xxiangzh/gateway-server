import org.springframework.util.AntPathMatcher;

/**
 * @author 向振华
 * @date 2020/09/08 14:44
 */
public class Test2 {
    public static void main(String[] args) {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        System.out.println(antPathMatcher.match("/*/x/z", "/notify-service/x/z"));
        System.out.println(antPathMatcher.match("/notify-service/x/z", "/*/x/z"));
    }
}
