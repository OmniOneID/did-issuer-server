/*
 * Copyright 2025 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.omnione.did.issuer.v1.admin.utils;

import org.omnione.did.issuer.v1.admin.dto.ResponseDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description...
 *
 */
public class ResponseUtil {
    public static ResponseDto generateBodyWithPage(List<?> list, long totalCount) {
        Map<String, Object> data = new HashMap<>();
        data.put("totalCount", totalCount);
        data.put("list", list);
        return new ResponseDto(data);
    }
}
