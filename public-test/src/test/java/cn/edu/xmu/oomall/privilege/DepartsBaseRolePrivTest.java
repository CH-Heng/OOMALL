package cn.edu.xmu.oomall.privilege;

import cn.edu.xmu.oomall.BaseTestOomall;
import cn.edu.xmu.oomall.PublicTestApp;
import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnNo;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 权限模块公开测试
 * 测试需添加相应的数据库数据
 * @author 张湘君 24320182203327
 * @date 2020/12/13 20:15
 */
@SpringBootTest(classes = PublicTestApp.class)   //标识本类是一个SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DepartsBaseRolePrivTest extends BaseTestOomall {

    private static final String TESTURL = "/privilege/departs/{did}/baseroles/{id}/privileges";
    private static final String BASEROLEURL = "/privilege/departs/{did}/baseroles";
    private static final String IDURL = "/privilege/departs/{did}/baseroles/{id}/privileges/{pid}";


    private static Long roleId = null;
    /**
     * 7
     * 获得角色所有权限，成功获取
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    public void getRolePrivsTest1() throws Exception{
        String token =this.adminLogin("13088admin", "123456");
        this.gatewayClient.get().uri(TESTURL+"?page=1&pageSize=100",0,88)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '96')]").exists()
                .jsonPath("$.data.list[?(@.id == '95')]").exists()
                .jsonPath("$.data.list[?(@.id == '108')]").exists()
                .jsonPath("$.data.list[?(@.id == '109')]").exists()
                .jsonPath("$.data.list[?(@.id == '111')]").exists()
                .jsonPath("$.data.list[?(@.id == '110')]").exists()
                .jsonPath("$.data.list[?(@.id == '107')]").exists()
                .jsonPath("$.data.list.length()").isEqualTo(7);

    }

    /**
     * 8
     * 获得角色所有权限，id不存在
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    public void getRolePrivsTest2() throws Exception{
        String token =this.adminLogin("13088admin", "123456");
        this.gatewayClient.get().uri(TESTURL,0,12088).header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }

    /**
     * 9
     * 增加角色的权限，增加成功
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    @Order(1)
    public void addRolePrivTest1() throws Exception{
        String token =this.adminLogin("13088admin", "123456");
        String roleJson = "{\"descr\": \"testU\",\"name\": \"测试11\"}";
        String result = new String(Objects.requireNonNull(this.gatewayClient.post().uri(BASEROLEURL, 0)
                .header("authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.name").isEqualTo("测试11")
                .returnResult()
                .getResponseBodyContent()), "UTF-8");

        this.roleId = JacksonUtil.parseSubnodeToObject(result, "/data/id", Long.class);

        this.gatewayClient.post().uri(IDURL,0, roleId, 2)
                .header("authorization",token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(TESTURL,0,roleId)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '2')]").exists();
    }

    /**
     * 10
     * 增加角色的权限，roleId不存在
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    public void addRolePrivTest2() throws Exception{
        String token =this.adminLogin("13088admin", "123456");
        this.gatewayClient.post().uri(IDURL,0, 2000,2)
                .header("authorization",token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());

    }

    /**
     * 11
     * 增加角色的权限，privilegeId不存在
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    public void addRolePrivTest3() throws Exception{
        String token =this.adminLogin("13088admin", "123456");
        this.gatewayClient.post().uri(IDURL,0,88,10090)
                .header("authorization",token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }

    /**
     * 12
     * 非平台管理员
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    public void addRolePrivTest4() throws Exception{
        String token =this.adminLogin("8131600001", "123456");
        this.gatewayClient.post().uri(IDURL,0, 88, 2)
                .header("authorization",token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());

    }

    /**
     * 12
     * 非功能角色
     * @throws Exception
     */
    @Test
    public void addRolePrivTest5() throws Exception{
        String token =this.adminLogin("13088admin", "123456");
        this.gatewayClient.post().uri(IDURL,0, 2, 2)
                .header("authorization",token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());

    }

    /**
     * 9
     * 删除角色的权限，成功
     * @throws Exception
     */
    @Test
    @Order(2)
    public void delRolePrivTest1() throws Exception{
        String token =this.adminLogin("13088admin", "123456");
        assertNotNull(this.roleId);

        this.gatewayClient.get().uri(TESTURL,0,roleId)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '2')]").exists();


        this.gatewayClient.delete().uri(IDURL,0, roleId, 2)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode());

        this.gatewayClient.get().uri(TESTURL,0,roleId)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.OK.getCode())
                .jsonPath("$.data.list[?(@.id == '2')]").doesNotExist();
    }

    /**
     * 10
     * 删除角色的权限，roleId不存在
     * @throws Exception
     */
    @Test
    public void delRolePrivTest2() throws Exception{
        String token =this.adminLogin("13088admin", "123456");
        this.gatewayClient.delete().uri(IDURL,0, 2000,2)
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());

    }

    /**
     * 11
     * 删除角色的权限，privilegeId不存在
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    public void delRolePrivTest3() throws Exception{
        String token =this.adminLogin("13088admin", "123456");
        this.gatewayClient.delete().uri(IDURL,0,88,3)
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_NOTEXIST.getCode());
    }

    /**
     * 12
     * 非平台管理员
     * @throws Exception
     */
    @Test
    public void delRolePrivTest4() throws Exception{
        String token =this.adminLogin("8131600001", "123456");
        this.gatewayClient.post().uri(IDURL,0, 88, 2)
                .header("authorization",token)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());

    }

    /**
     * 12
     * 非功能角色
     * @throws Exception
     */
    @Test
    public void delRolePrivTest5() throws Exception{
        String token =this.adminLogin("13088admin", "123456");
        this.gatewayClient.post().uri(IDURL,0, 2, 2)
                .header("authorization",token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE.getCode());
    }

}
