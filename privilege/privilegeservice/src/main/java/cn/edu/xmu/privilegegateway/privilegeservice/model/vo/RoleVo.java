/**
 * Copyright School of Informatics Xiamen University
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package cn.edu.xmu.privilegegateway.privilegeservice.model.vo;

import cn.edu.xmu.privilegegateway.privilegeservice.model.bo.Role;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 角色视图
 *
 * @author 24320182203281 王纬策
 * createdBy 王纬策 2020/11/04 13:57
 * modifiedBy 王纬策 2020/11/7 19:20
 **/
@Data
@NoArgsConstructor
@ApiModel(description = "角色视图对象")
public class RoleVo {
    @NotBlank(message = "角色名不能为空")
    @ApiModelProperty(value = "角色名称")
    private String name;

    @ApiModelProperty(value = "角色描述")
    private String descr;

    /**
     * 构造函数
     *
     * @author 24320182203281 王纬策
     * @return Role
     * createdBy 王纬策 2020/11/04 13:57
     * modifiedBy 王纬策 2020/11/7 19:20
     */
    public Role createRole() {
        Role role = new Role();
        role.setDescr(this.descr);
        role.setName(this.name);
        return role;
    }
}
