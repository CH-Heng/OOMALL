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

package cn.edu.xmu.privilegegateway.annotation.util.coder.imp;

import cn.edu.xmu.privilegegateway.annotation.util.coder.BaseSign;
import cn.edu.xmu.privilegegateway.annotation.util.encript.SHA256;

/**
 * @author wang zhongyu
 * @date 2021-11-22
 * modifiedBy Ming Qiu 2021-11-24 19:04
 */
public class SHA256Sign extends BaseSign {

    @Override
    protected String encrypt(String content) {
        return SHA256.getSHA256(content);
    }
}
