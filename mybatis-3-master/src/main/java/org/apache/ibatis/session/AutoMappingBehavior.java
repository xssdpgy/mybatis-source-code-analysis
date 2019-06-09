/**
 *    Copyright 2009-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.session;

/**
 * 指定MyBatis是否启用自动映射以及如何自动将列映射到fields / properties。
 * Specifies if and how MyBatis should automatically map columns to fields/properties.
 *
 * @author Eduardo Macarron
 */
public enum AutoMappingBehavior {

  /**
   * 禁用自动映射
   * Disables auto-mapping.
   */
  NONE,

  /**
   * 仅自动映射结果，而不在内部定义嵌套结果映射
   * Will only auto-map results with no nested result mappings defined inside.
   */
  PARTIAL,

  /**
   * 自动映射任何复杂性的结果映射（包含嵌套或其他）
   * Will auto-map result mappings of any complexity (containing nested or otherwise).
   */
  FULL
}
