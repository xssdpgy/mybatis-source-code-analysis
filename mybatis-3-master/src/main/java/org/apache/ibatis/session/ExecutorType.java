/**
 *    Copyright 2009-2015 the original author or authors.
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
 * 执行器的类型 枚举类型
 * @author Clinton Begin
 */
public enum ExecutorType {
  /**
   * SIMPLE：这个执行器类型不做特殊的事情。它为每个语句的执行创建一个新的预处理语句。
   *          每执行一次update或select，就开启一个Statement对象，用完立刻关闭Statement对象。（可以是Statement或PrepareStatement对象）
   *
   * REUSE：这个执行器类型会复用预处理语句。
   *          执行update或select，以sql作为key查找Statement对象，存在就使用，不存在就创建，用完后，不关闭Statement对象，
   *          而是放置于Map<String, Statement>内，供下一次使用。（可以是Statement或PrepareStatement对象）
   *
   * BATCH：这个执行器会批量执行所有更新语句，如果SELECT在它们中间执行还会标定它们是必须的，来保证一个简单并易于理解的行为。
   *          执行update（没有select，JDBC批处理不支持select），将所有sql都添加到批处理中（addBatch()），等待统一执行（executeBatch()），
   *          它缓存了多个Statement对象，每个Statement对象都是addBatch()完毕后，等待逐一执行executeBatch()批处理的；
   *          BatchExecutor相当于维护了多个桶，每个桶里都装了很多属于自己的SQL，就像苹果篮里装了很多苹果，番茄篮里装了很多番茄，
   *          最后，再统一倒进仓库。（可以是Statement或PrepareStatement对象）
   */

  SIMPLE, REUSE, BATCH
}
