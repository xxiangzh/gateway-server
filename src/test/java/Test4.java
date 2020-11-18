import com.xzh.gateway.utils.AesUtils;

/**
 * @author 向振华
 * @date 2020/09/11 15:08
 */
public class Test4 {
    public static void main(String[] args) {
        String x = AesUtils.encrypt("kBqmUdQV5s0eR8VF93U8Lw==\n" +
                "vU77ZAX1Bwuvd8775Jgxf2U8vhvyBlvZjnIoakpVT/SIZigSmnJBQsvPT9/ZHkzRuhWwA90o+nhWDuzuKaQM3L/UO/jQ9ypulG5qrsC4vKM/gsj6Vp70QLAFVkdt5twmEduCYkNSueWcxqlR07KeiIln+acSDjKsmODmSEQZ6CgpWjOXbI4JL2EYpB1qJybsnzuLGuk8wu19mKmxJkPMsoPkVFF1Iq6O6Zj6xShU+2nJZ2eX+64EJYUcIMuZsPyUugOLD0pqb5NtbNGuV9xxbInUJuIIOM0QGMPnXBaRdeTbG6rWAcyfAu6GJNF4+5MOjctgyWKHfPNUcuaQttTecjSLX8jmCRPNxCxlHnpz0CfdECwp7zDt4rRrJ6/4jhmMGNrnoVhc31UxSEbZ4reSSXT05kZw5w9t8Xy2OFQzbberiZjKAjLZgMoAjBrIs+4HgBSrbqB+uUyMgFfwfF5+1mb35tNTHKM1hoBVChmZ4U0bWm4PGGpm8OXwswjXc5ecOWKWyI0t+TImVUbLoMvHYteQzhwulNbTew4UB7eVlU3E+dPCD8FIjSdGLb6bvF/FAqOEdHyh2zKFNIYP+CZ4WnAySZUCm+3w4g7olkgqn9XcGHZAgYTEP7ITPhtzcyux\n");
        System.out.println(x);
//        System.out.println(AesUtils.decrypt("kBqmUdQV5s0eR8VF93U8Lw==\n" +
//                "vU77ZAX1Bwuvd8775Jgxf2U8vhvyBlvZjnIoakpVT/SIZigSmnJBQsvPT9/ZHkzRuhWwA90o+nhWDuzuKaQM3L/UO/jQ9ypulG5qrsC4vKM/gsj6Vp70QLAFVkdt5twmEduCYkNSueWcxqlR07KeiIln+acSDjKsmODmSEQZ6CgpWjOXbI4JL2EYpB1qJybsnzuLGuk8wu19mKmxJkPMsoPkVFF1Iq6O6Zj6xShU+2nJZ2eX+64EJYUcIMuZsPyUugOLD0pqb5NtbNGuV9xxbInUJuIIOM0QGMPnXBaRdeTbG6rWAcyfAu6GJNF4+5MOjctgyWKHfPNUcuaQttTecjSLX8jmCRPNxCxlHnpz0CfdECwp7zDt4rRrJ6/4jhmMGNrnoVhc31UxSEbZ4reSSXT05kZw5w9t8Xy2OFQzbberiZjKAjLZgMoAjBrIs+4HgBSrbqB+uUyMgFfwfF5+1mb35tNTHKM1hoBVChmZ4U0bWm4PGGpm8OXwswjXc5ecOWKWyI0t+TImVUbLoMvHYteQzhwulNbTew4UB7eVlU3E+dPCD8FIjSdGLb6bvF/FAqOEdHyh2zKFNIYP+CZ4WnAySZUCm+3w4g7olkgqn9XcGHZAgYTEP7ITPhtzcyux\n"));
    }
}
