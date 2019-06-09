/**
 *    Copyright 2009-2018 the original author or authors.
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

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * 检测自动映射目标的未知列（或未知属性类型）时指定行为
 * Specify the behavior when detects an unknown column (or unknown property type) of automatic mapping target.
 *
 * @since 3.4.0
 * @author Kazuki Shimizu
 */
public enum AutoMappingUnknownColumnBehavior {

  /**
   * 默认什么都不做
   * Do nothing (Default).
   */
  NONE {
    @Override
    public void doAction(MappedStatement mappedStatement, String columnName, String property, Class<?> propertyType) {
      // do nothing
    }
  },

  /**
   * 输出警告日志
   * 注意：日志级别'org.apache.ibatis.session.AutoMappingUnknownColumnBehavior'必须设置为WARN。
   * Output warning log.
   * Note: The log level of {@code 'org.apache.ibatis.session.AutoMappingUnknownColumnBehavior'} must be set to {@code WARN}.
   */
  WARNING {
    @Override
    public void doAction(MappedStatement mappedStatement, String columnName, String property, Class<?> propertyType) {
      LogHolder.log.warn(buildMessage(mappedStatement, columnName, property, propertyType));
    }
  },

  /**
   * 失败映射 抛出SqlSessionException
   * Fail mapping.
   * Note: throw {@link SqlSessionException}.
   */
  FAILING {
    @Override
    public void doAction(MappedStatement mappedStatement, String columnName, String property, Class<?> propertyType) {
      throw new SqlSessionException(buildMessage(mappedStatement, columnName, property, propertyType));
    }
  };

  /**
   * 检测到自动映射目标的未知列（或未知属性类型）时执行操作。
   * Perform the action when detects an unknown column (or unknown property type) of automatic mapping target.
   * @param mappedStatement current mapped statement  当前映射语句
   * @param columnName column name for mapping target  映射目标的列名称
   * @param propertyName property name for mapping target   映射目标的属性名称
   * @param propertyType property type for mapping target (If this argument is not null,
   *          {@link org.apache.ibatis.type.TypeHandler} for property type is not registered)  映射目标的属性类型（如果此参数不为null，TypeHandler则表示未注册属性类型）
     */
  public abstract void doAction(MappedStatement mappedStatement, String columnName, String propertyName, Class<?> propertyType);

  /**
   * 构建错误信息，用于警告输出及失败映射
   * build error message.
   */
  private static String buildMessage(MappedStatement mappedStatement, String columnName, String property, Class<?> propertyType) {
    return new StringBuilder("Unknown column is detected on '")
      .append(mappedStatement.getId())
      .append("' auto-mapping. Mapping parameters are ")
      .append("[")
      .append("columnName=").append(columnName)
      .append(",").append("propertyName=").append(property)
      .append(",").append("propertyType=").append(propertyType != null ? propertyType.getName() : null)
      .append("]")
      .toString();
  }

  private static class LogHolder {
    private static final Log log = LogFactory.getLog(AutoMappingUnknownColumnBehavior.class);
  }

}
