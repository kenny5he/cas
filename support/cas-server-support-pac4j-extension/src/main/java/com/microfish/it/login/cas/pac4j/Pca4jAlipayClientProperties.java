/**
 * Copyright 2022 - Ren Jian Yan Huo
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
 */

package com.microfish.it.login.cas.pac4j;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apereo.cas.configuration.model.support.pac4j.Pac4jIdentifiableClientProperties;

import java.io.Serial;

/**
 * 支付宝
 * @author kenny.he
 * @since 2026/06/10
 */
@Getter
@Setter
@Accessors(chain = true)
public class Pca4jAlipayClientProperties extends Pac4jIdentifiableClientProperties {
    @Serial
    private static final long serialVersionUID = 5089432545960571401L;

}
