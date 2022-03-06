package cn.edu.xmu.oomall.privilege;

import cn.edu.xmu.oomall.BaseTestOomall;
import cn.edu.xmu.oomall.PublicTestApp;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnNo;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author XQChen
 * @version 创建时间：2020/12/7 下午1:35
 */
@SpringBootTest(classes = PublicTestApp.class)   //标识本类是一个SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DepartUsersTest extends BaseTestOomall {

    private static final String TESTURL ="/privilege/departs/{did}/users";
    private static final String NEWURL ="/privilege/departs/{did}/users/new";
    /***
     * 正确查找用户
     * @throws Exception
     */
    @Test
    public void findAllUsers1() throws Exception {

        String token = this.adminLogin("13088admin", "123456");

        this.gatewayClient
                .get()
                .uri(TESTURL+"?userName=13088admin", 0)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(1)
                .jsonPath("$.data.list[?(@.id == '1')]").exists();
    }

    /***
     * 正确查找用户
     * @throws Exception
     */
    @Test
    public void findAllUsers2() throws Exception {

        String token = this.adminLogin("13088admin", "123456");

        this.gatewayClient
                .get()
                .uri(TESTURL+"?userName=8131600001",1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '46')].email").isEqualTo("ees@123.com")
                .jsonPath("$.data.list[?(@.id == '46')].mobile").isEqualTo("15983424556")
                .jsonPath("$.data.list[?(@.id == '46')].departId").isEqualTo(1)
                .jsonPath("$.data.list[?(@.id == '46')].sign").isEqualTo(0)
                .jsonPath("$.data.list.length()").isEqualTo(1);
    }

    /***
     * 查找签名错误用户信息
     * @throws Exception
     */
    @Test
    public void findAllUsers3() throws Exception {

        String token = this.adminLogin("13088admin", "123456");

        this.gatewayClient.get()
                .uri(TESTURL+"?userName=wrong_sign",1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_FALSIFY.getCode())
                .jsonPath("$.data.list[?(@.name == '签名错误' && @.sign == '1' && @.id == '17341')]").exists();
    }

    /***
     * 查找其他店铺用户信息
     * @throws Exception
     */
    @Test
    public void findAllUsers4() throws Exception {

        String token = this.adminLogin("2721900002", "123456");

        this.gatewayClient.get()
                .uri(TESTURL+"?userName=change_user",1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

    /***
     * 正确查找用户
     * @throws Exception
     */
    @Test
    public void findNewUsers1() throws Exception {

        String token = this.adminLogin("13088admin", "123456");

        this.gatewayClient
                .get()
                .uri(NEWURL+"?userName=departuser1", 1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(1)
                .jsonPath("$.data.list[?(@.id == '24')]").exists();
    }


    /***
     * 店铺管理员查找
     * @throws Exception
     */
    @Test
    public void findNewUsers2() throws Exception {

        String token = this.adminLogin("2721900002", "123456");

        this.gatewayClient
                .get()
                .uri(NEWURL+"?userName=departuser2", 2)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list.length()").isEqualTo(1)
                .jsonPath("$.data.list[?(@.id == '25')]").exists();
    }

    /***
     * 查找其他店铺用户信息
     * @throws Exception
     */
    @Test
    public void findNewUsers3() throws Exception {

        String token = this.adminLogin("2721900002", "123456");

        this.gatewayClient
                .get()
                .uri(NEWURL+"?userName=departuser1", 1)
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }
}
